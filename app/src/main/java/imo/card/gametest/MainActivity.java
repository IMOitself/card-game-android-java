package imo.card.gametest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.view.MotionEvent;
import android.util.DisplayMetrics;
import android.graphics.Color;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends Activity 
{
	public TextView titleTxt;
	public LinearLayout playerLayout;
	public TextView playerLivesTxt;
	public TextView playerEnergyTxt;
	public LinearLayout enemyLayout;
	public TextView enemyLivesTxt;
	public TextView enemyEnergyTxt;
	public ImageView movesImg;
	public TextView movesTxt;
	public TextView useIndicatorTxt;
	public TextView skipIndicatorTxt;
	public View cardParentLayout;
	public LinearLayout cardBackLayout;
	public LinearLayout cardLayout;
	public ImageView cardTypeImg;
	public TextView cardCostTxt;
	public TextView cardNameTxt;
	public TextView cardInfoTxt;
	public LinearLayout buttonsLayout;
	public Button skipBtn;
	public Button useBtn;
	public TextView debugTxt;

	public int screenWidth = 0;

	public List<Map<String, String>> cardsData = new ArrayList<>();
	public Map<String, String> drawnCardMap;

	public int moves = 3;
	public int playerLives = 10;
	public int playerEnergy = 5;
	public int enemyLives = 10;
	public int enemyEnergy = 5;

	public int moves_old = moves;
	public int playerLives_old = playerLives;
	public int playerEnergy_old = playerEnergy;
	public int enemyLives_old = enemyLives;
	public int enemyEnergy_old = enemyEnergy;

	public String userTurn = "player";
	public boolean isEnemyTurn = false;
	
	//use for properly ending the game while recording data
	public int playerMovesMade = 0;
	public int enemyMovesMade = 0;
	public boolean isGameFinished = false;
	public Map<String, String> gameResult = new HashMap<>();

	public List<Map<String, String>> playerCardsStock = new ArrayList<>();
	public List<Map<String, String>> enemyCardsStock = new ArrayList<>();

	public List<Map<String, String>> playerCardsCurrent = new ArrayList<>();
	public List<Map<String, String>> enemyCardsCurrent = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		titleTxt = findViewById(R.id.title_txt);
		playerLayout = findViewById(R.id.player_layout);
		playerLivesTxt = findViewById(R.id.player_lives_txt);
		playerEnergyTxt = findViewById(R.id.player_energy_txt);
		enemyLayout = findViewById(R.id.enemy_layout);
		enemyLivesTxt = findViewById(R.id.enemy_lives_txt);
		enemyEnergyTxt = findViewById(R.id.enemy_energy_txt);
		movesImg = findViewById(R.id.moves_img);
		movesTxt = findViewById(R.id.moves_txt);
		useIndicatorTxt = findViewById(R.id.use_indicator_txt);
		skipIndicatorTxt = findViewById(R.id.skip_indicator_txt);
		cardParentLayout = findViewById(R.id.cardParentLayout);
		cardBackLayout = findViewById(R.id.card_back_layout);
		cardLayout = findViewById(R.id.card_layout);
		cardTypeImg = findViewById(R.id.card_type_img);
		cardCostTxt = findViewById(R.id.card_cost_txt);
		cardNameTxt = findViewById(R.id.card_name_txt);
		cardInfoTxt = findViewById(R.id.card_info_txt);
		buttonsLayout = findViewById(R.id.buttons_layout);
		skipBtn = findViewById(R.id.skip_btn);
		useBtn = findViewById(R.id.use_btn);
		debugTxt = findViewById(R.id.debug_txt);

		//buttons are not necessary anymore.
		buttonsLayout.setVisibility(View.GONE);
		//to bring them back remove this code above

		onCreateLogic();

		skipBtn.setOnClickListener( new View.OnClickListener(){
				@Override public void onClick(View v){
					skipCard();
				}
			});
		useBtn.setOnClickListener( new View.OnClickListener(){
				@Override public void onClick(View v){
					useCard();
				}
			});
		cardParentLayout.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					swipeCardLogic(cardParentLayout, event);
					return true;
				}
			});
    }







	public void onCreateLogic(){
		//get the screen width
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		screenWidth = displayMetrics.widthPixels;
		//set the view size to 1/3 of the screen
		Tools.setViewSize(playerLayout, screenWidth / 3, screenWidth / 3);
		Tools.setViewSize(enemyLayout, screenWidth / 3, screenWidth / 3);

		Tools.setViewSize(cardLayout, screenWidth / 3, screenWidth / 3);
		Tools.setViewSize(cardBackLayout, screenWidth / 3, screenWidth / 3);
		//card is on its back by default then it will be flipped later when drawn
		cardLayout.setVisibility(View.GONE);

		//will only be visible when the card is touch
		skipIndicatorTxt.setVisibility(View.GONE);
		Tools.setViewSize(skipIndicatorTxt, screenWidth/3, 0);
		useIndicatorTxt.setVisibility(View.GONE);
		Tools.setViewSize(useIndicatorTxt, screenWidth/3, 0);

		//this void method is used only to minimize the code here
		setViewsCustomBgDrawable();

		//Transform the string to a Arraylist that holds a hashmap. 
		//Each Hashmap holds key-value pairs that makes it easier to search for a specific item.
		//If i search for the key "name" it will retrieve a value of "John" for example.
		String sample_cardpack = getResources().getString(R.string.sample_cardpack);
        Tools.importDataToArraylist(cardsData, sample_cardpack, ";", "》");//arraylist, string, splitItemsBy, splitContentsBy
		//Stock the lists with cards. Will be use to restock playerCardsCurrent and enemyCardsCurrent
		//TODO: make a seperate set of cards for the player and enemy
		playerCardsStock.addAll(cardsData);
		enemyCardsStock.addAll(cardsData);
		//Start the game by populating views with datas
		//and also draw a card.
		updateGame();
		drawCard();
	}



	public void swipeCardLogic(View cardParentLayout, MotionEvent event){
		//The card's width is set to 1/3 of the screen on onCreateLogic().
		//Get the view width's center by dividing it to 2.
		//Find the center of the screen by dividing it to 2.
		//screenWidthCenter then subtracted by viewWidthCenter because trust me it works.
		final int viewWidth = screenWidth/3;
		final int viewWidthCenter = viewWidth/2;
		final int screenWidthCenter = screenWidth/2;
		final int centerX = screenWidthCenter - viewWidthCenter;
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				//When your finger touched the screen.
				//Make these views visible. This views tells where the user should swipe
				skipIndicatorTxt.setVisibility(View.VISIBLE);
				useIndicatorTxt.setVisibility(View.VISIBLE);
				break;

			case MotionEvent.ACTION_MOVE:
				//When your finger moved while touching the screen
				//Get the current location horizontally of your touch
				float currentX = event.getRawX() - viewWidthCenter;
				//Set the location of the view to your touch
				cardParentLayout.setX(currentX);

				//Get how far your touch moved to the center of the screen
				//then convert it to decimal e.g. 150 / 100 = 1.50
				//add 0.5f coz why not
				float recordAddedX = Math.abs(currentX - centerX);
				float scaleBy = (recordAddedX/100)+0.5f;
				if (currentX < centerX) {
					//if its on the left
					if(scaleBy >= 1 && scaleBy <= 2){
						//to prevent getting smaller than its default scale 
						//or getting higher than 2
						//Now set the view's X and Y to scaleBy
						skipIndicatorTxt.setScaleX(scaleBy);
						skipIndicatorTxt.setScaleY(scaleBy);
					}
				}else if (currentX > centerX) {
					//if its on the right
					if(scaleBy >= 1 && scaleBy <= 2){
						//to prevent getting smaller than its default scale 
						//or getting higher than 2
						//Now set the view's X and Y to scaleBy
						useIndicatorTxt.setScaleX(scaleBy);
						useIndicatorTxt.setScaleY(scaleBy);
					}
				}
				//this code is unnecessary but its here anyway
				if(!isGameFinished){
					debugTxt.setText("event.getRawX(): " + currentX + "   centerX: " + centerX + " recordAddedX: " + recordAddedX + "\n" + "skipIndicatorTxt: " + skipIndicatorTxt.getScaleX() + " useIndicatorTxt: " + useIndicatorTxt.getScaleX());
				}
				//remove this code above if its annoying
				break;

			case MotionEvent.ACTION_UP:
				//When your finger is released.
				//remove these views and set them back to normal size.
				skipIndicatorTxt.setVisibility(View.GONE);
				skipIndicatorTxt.setScaleX(1);
				skipIndicatorTxt.setScaleY(1);
				useIndicatorTxt.setVisibility(View.GONE);
				useIndicatorTxt.setScaleX(1);
				useIndicatorTxt.setScaleY(1);

				//bring the card to its original position which is the center
				cardParentLayout.setX(centerX);
				//get your last touch's position
				float finalX = event.getRawX() - viewWidthCenter;
				//safeArea is a certain distance from the center
				//that will not gonna do anything when your touch is on there
				int safeArea = 80;
				if (finalX < centerX - safeArea) {
					//left side of the center
					skipCard();

				}else if (finalX > centerX + safeArea) {
					//right side of the center
					useCard();
				}
				break;
		}
	}



	public void drawCard(){
		if(!isGameFinished){
			//if the game is not finished yet
			Random random = new Random();
			int randomInt = 0;

			if(userTurn.equals("player")){
				//Restock playerCardsCurrent if empty. might add a logic when cards are restocked
				if(playerCardsCurrent.isEmpty()) playerCardsCurrent.addAll(playerCardsStock);
				//Randomly pick a map on the arraylist.
				randomInt = random.nextInt(playerCardsCurrent.size());
				drawnCardMap = playerCardsCurrent.get(randomInt);
				//Remove the map on the list to not get drawn again unless the list restocked again
				playerCardsCurrent.remove(drawnCardMap);

			}else if(userTurn.equals("enemy")){
				//Restock enemyCardsCurrent if empty.
				if(enemyCardsCurrent.isEmpty()) enemyCardsCurrent.addAll(enemyCardsStock);
				//Randomly pick a map on the arraylist.
				randomInt = random.nextInt(enemyCardsCurrent.size());
				drawnCardMap = enemyCardsCurrent.get(randomInt);
				//Remove the map on the list to not get drawn again unless the list restocked again
				enemyCardsCurrent.remove(drawnCardMap);
			}
			//After a map is picked, use its attributes.
			String cardName = drawnCardMap.get("name");
			String cardInfo = drawnCardMap.get("info");
			String cardType = drawnCardMap.get("type");
			//Based on cardType, display a specific image corresponding to its card type
			setImageByCardType(cardType, cardTypeImg);
			//Populate the textviews
			cardNameTxt.setText(cardName);
			cardInfoTxt.setText(cardInfo);

			//Get the cardCost. usually its a negative number.
			int cardCost = Integer.parseInt(drawnCardMap.get("cost"));
			//Make it positive
			cardCost = Math.abs(cardCost);
			//The code below is like in the updateEnergy() void method 
			//but the difference is it reach 0 and below. See updateEnergy for info.
			String energyString = "";
			energyString = cardCost <= 0 ? energyString + "----" : energyString + "";
			energyString = cardCost >= 1 ? energyString + "■" : energyString + "";
			energyString = cardCost >= 2 ? energyString + "■" : energyString + "";
			energyString = cardCost >= 3 ? energyString + "■" : energyString + "";
			energyString = cardCost >= 4 ? energyString + "■" : energyString + "";
			energyString = cardCost >= 5 ? energyString + "■" : energyString + "";
			cardCostTxt.setText(energyString);

			//animate the card as if its doing intro upwards then flipping on its back
			Animations.cardAnim(cardLayout, cardBackLayout, cardNameTxt, isEnemyTurn, cardParentLayout, skipBtn, useBtn);

		}
	}



	public void skipCard(){
		if(!isGameFinished){
			//if the game is not finished yet
            //skipping a card gives you 1 energy.
			if (userTurn.equals("player")){
				playerEnergy = playerEnergy + 1;

			}else if (userTurn.equals("enemy")){
				enemyEnergy = enemyEnergy + 1;
			}
			useMoves();
			drawCard();
			updateGame();
		}
	}



	public void useCard(){
		if(!isGameFinished){
			//if the game is not finished yet
            //The drawnCardMap is a map populated by drawnCard() void method
			//Some map doesn't have a key of "lives" or "energy"
			//"lives" and "energy" key contain a value of e.g "0, 0"
			//We split the "0, 0" by "," thus resulting in ["0", " 0"]
			//The first value of the array populates how many should be added on self.
			//The second value of the array populates how many should be added on target.
			String cardLives = drawnCardMap.get("lives");
			String cardEnergy = drawnCardMap.get("energy");
			String cardType = drawnCardMap.get("type");

			int editSelfLives = 0;
			int editTargetLives = 0;
			if (drawnCardMap.containsKey("lives")){
				String[] valueArray = cardLives.split(",");
				editSelfLives = Integer.parseInt(valueArray[0].trim());
				editTargetLives = Integer.parseInt(valueArray[1].trim());
			}
			int editSelfEnergy = 0;
			int editTargetEnergy = 0;
			if (drawnCardMap.containsKey("energy")){
				String[] valueArray = cardEnergy.split(",");
				editSelfEnergy = Integer.parseInt(valueArray[0].trim());
				editTargetEnergy = Integer.parseInt(valueArray[1].trim());
			}
			//drawnCardCost is the energy cost of the card.
			//this int always have negative value.
			int drawnCardCost = Integer.parseInt(drawnCardMap.get("cost"));
			//the code below detects if you can use the card or not.
			//e.g. if i have 3 energy and the card needs 4. The card cant be use.
			//therefore my only option is to skip.
			//Else if i have enough energy, use the card and move onto next card.

			//for example, if the user is player. 
			//In that case, self is referring to player and target is referring to enemy.
			//same as if the enemy is the user but vice versa.
			//Negative value subtracts instead of addition.
			if (userTurn.equals("player")){
				if (Math.abs(drawnCardCost) > playerEnergy){
					//If theres not enough energy. Indicate it by making the text red.
					Animations.redTextAnim(playerEnergyTxt);
				}else{
					//If theres enough energy. subtract the cost to playerEnergy
					//It might not seem its subtraction, but the drawnCardCost is always negative
					playerEnergy = playerEnergy + drawnCardCost;

					playerLives = playerLives + editSelfLives;
					enemyLives = enemyLives + editTargetLives;

					playerEnergy = playerEnergy + editSelfEnergy;//overwrites drawnCardCost
					enemyEnergy = enemyEnergy + editTargetEnergy;

					if(cardType.contains("attack")){
						//contains() method because it will reach any string with "attack"
						Animations.attackAnim(-5, playerLayout, enemyLayout);
					}
					useMoves();
					drawCard();
				}
			}else if (userTurn.equals("enemy")){
				//Enemy will play on its own. The decision of enemy is made on updateTurns() on enemyRandomPlay()
				if (Math.abs(drawnCardCost) > enemyEnergy){
					//if theres not enough energy. Skip the card.
					skipCard();
				}else{
					//If theres enough energy. subtract the cost to enemyEnergy
					//It might not seem its subtraction, but the drawnCardCost is always negative
					enemyEnergy = enemyEnergy + drawnCardCost;

					enemyLives = enemyLives + editSelfLives;
					playerLives = playerLives + editTargetLives;

					enemyEnergy = enemyEnergy + editSelfEnergy;//overwrites drawnCardCost
					playerEnergy = playerEnergy + editTargetEnergy;

					if(cardType.contains("attack")){
						//contains() method because it will reach any string with "attack"
						Animations.attackAnim(5, enemyLayout, playerLayout);
					}

					useMoves();
					drawCard();
				}
			}
			//whether or not the card can be used, still update the game.
			updateGame();
		}
	}



	public void useMoves(){
		if(!isGameFinished){
			//if game is not finished yet
			//The code below keep count how many moves you make
			//either by using the card or skipping it.
			//Once moves int reach zero, move on to the next user
			if (moves - 1 > 0){
				moves--;
				//rotate the movesImg like a sand or hour glass
				Animations.rotateAnim(movesImg, userTurn);
				if(isEnemyTurn){
					//if its still enemy turn, do some random decisions 
					//whether to skip or use a card
					enemyRandomPlay(enemyEnergy);
				}
			}else{
				if (userTurn.equals("player")){
					//player turn has ended
					userTurn = "enemy";
					Animations.hoverAnim(enemyLayout, playerLayout, Color.parseColor("#696969"));
					enemyRandomPlay(enemyEnergy);
					isEnemyTurn = true;

				}else if (userTurn.equals("enemy")){
					//enemy turn has ended
					userTurn = "player";
					Animations.hoverAnim(playerLayout, enemyLayout, Color.parseColor("#696969"));
					isEnemyTurn = false;
				}
				moves = 3;
			}
			//record how many moves has been made
			if(userTurn.equals("player")){ playerMovesMade++;
			}else if(userTurn.equals("enemy")) enemyMovesMade++;
		}
	}



	public void updateGame(){
		//The max energy a player and enemy could have is 5. 
		//This code prevents going beyond 5.
		if (playerEnergy > 5){
			playerEnergy = 5;
			playerEnergy_old = 0;//to prevent not showing changes
		}
		if (enemyEnergy > 5){
			enemyEnergy = 5;
			enemyEnergy_old = 0;//to prevent not showing changes
		}
		//Variables are populated on useCard() void method.
		//Display the current datas on textviews
		titleTxt.setText(userTurn + "");
		movesTxt.setText(moves + "");

		playerLivesTxt.setText("❤" + playerLives);
		updateEnergy(playerEnergyTxt, playerEnergy, true);

		enemyLivesTxt.setText("❤" + enemyLives);
		updateEnergy(enemyEnergyTxt, enemyEnergy, true);

		//Detect any differences on datas
		//do an animation if theres any
		if (playerLives_old != playerLives){
			Animations.popAnim(playerLivesTxt);
		}
		if (enemyLives_old != enemyLives){
			Animations.popAnim(enemyLivesTxt);
		}
		if (playerEnergy_old != playerEnergy){
			Animations.popAnim(playerEnergyTxt);
		}
		if (enemyEnergy_old != enemyEnergy){
			Animations.popAnim(enemyEnergyTxt);
		}
		//Record the data to be compared with later ones
		moves_old = moves;
		playerLives_old = playerLives;
		playerEnergy_old = playerEnergy;
		enemyLives_old = enemyLives;
		enemyEnergy_old = enemyEnergy;

		//Winning and losing system
		//if enemy dies then you win
		if(enemyLives <= 0){
			titleTxt.setText(R.string.you_win);
			recordGameResult("player");
		}
		//if player dies then you lose
		if(playerLives <= 0){
			titleTxt.setText(R.string.you_lose);
			recordGameResult("enemy");
		}
	}



	public void enemyRandomPlay(final int enemyEnergy){
		if(!isGameFinished){
			//if the game is not finished yet
            //dummy view can be anything that are not gonna be animated by other animattions
			//its not gonna be manipulated or animated or whatsoever
			//its sole purpose is to delay a code
			View dummy = titleTxt;
			dummy.animate().setDuration(Animations.duration * 3)
				.withEndAction(new Runnable() { @Override public void run() {
						//This code is delayed because its too fast to run this code again
						//and also make sure to only run after all of the animations are played
						//Pick random int between 0 and 1.
						//if 0 then skip the card
						//if 1 then use the card
						//some decisions is also made on useCard().
						Random random = new Random();
						int randomInt = random.nextInt(2);
						if (randomInt == 0){
							skipCard();
						}else if (randomInt == 1){
							useCard();
						}
					} })
				.start();
		}
	}



	public void setImageByCardType(String cardType, ImageView img){
		//Based on the cardType display specific image corresponding to it.
		//thats pretty much the explanation:/
		switch(cardType){
			case "attack":
				img.setImageResource(R.drawable.attack_type);
				break;
			case "rest":
				img.setImageResource(R.drawable.rest_type);
				break;
			case "heal":
				img.setImageResource(R.drawable.heal_type);
				break;
			case "mega_attack":
				img.setImageResource(R.drawable.mega_attack_type);
				break;
		}
	}



	public void setViewsCustomBgDrawable(){
		//customize the views with custom background drawable
		//set the values first then configure it based on the view
		//each view has specific background color that is needed to be kept that way
		//because changing background drawable also change the background color
		int bgColor;
		int strokeColor = Color.WHITE;
		int strokeWidth = 1;
		float strokeAlpha = 0.3f;
		int cornerRadius = 0;
		//cardLayout's default color is white
		bgColor = Color.WHITE;
		Tools.setCustomBgWithStroke(cardLayout, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
		//cardBackLayout's default color is grey
		bgColor = Color.parseColor("#696969");
		Tools.setCustomBgWithStroke(cardBackLayout, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
		//cardTypeImg and cardCostTxt has no color
		bgColor = Color.TRANSPARENT;
		Tools.setCustomBgWithStroke(cardTypeImg, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
		Tools.setCustomBgWithStroke(cardCostTxt, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
		//playerLayout will be outlined first by making its stroke thick.
		strokeWidth = 2;
		bgColor = Color.parseColor("#696969");
		Tools.setCustomBgWithStroke(playerLayout, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
	}



	public void updateEnergy(final TextView energyTxt, final int energyInt, final boolean showEnergyEmoji){
		//imagine there's 5 light bulbs
		//1 light bulb must be on and everything to off to indicate number 1
		//2 light bulbs must be on to indicate number 2
		//3 light bulbs must be on to indicate number 3...so on and so forth
		//anything that the energyInt doesnt reach e.g. 5, it must be off
		String energyString = "";
		if (showEnergyEmoji){
			energyString = "⚡";
		}
		energyString = energyInt >= 1 ? energyString + "■" : energyString + "□";
		energyString = energyInt >= 2 ? energyString + "■" : energyString + "□";
		energyString = energyInt >= 3 ? energyString + "■" : energyString + "□";
		energyString = energyInt >= 4 ? energyString + "■" : energyString + "□";
		energyString = energyInt >= 5 ? energyString + "■" : energyString + "□";
		energyTxt.setText(energyString);
	}



	public void recordGameResult(String winner){
		//finish the game
		isGameFinished = true;
		//set the winner to enemy
		gameResult.put("winner", winner);
		//get how many moves the player has been made
		gameResult.put("player_moves_made", playerMovesMade + "");
		//get how many moves the enemy has been made
		gameResult.put("enemy_moves_made", enemyMovesMade + "");
		//get how much extra damage has been made
		//if none then stay zero
		gameResult.put("overkill", "0");
		//if the playerLives is negative 
		//then get it to determine how much extra damage has been made
		if(playerLives < 0) gameResult.put("overkill", playerLives + "");

		String debugString = "isGameFinished: " + isGameFinished + " ";
		debugString = debugString + "winner: " + gameResult.get("winner") + "\n";
		debugString = debugString + "player_moves_made: " + gameResult.get("player_moves_made") + " ";
		debugString = debugString + "enemy_moves_made: " + gameResult.get("enemy_moves_made") + " ";
		debugString = debugString + "overkill: " + gameResult.get("overkill") + " ";
		debugTxt.setText(debugString);
	}
}