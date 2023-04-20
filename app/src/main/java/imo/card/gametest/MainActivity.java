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

// TODO: finish all codes that are needed to be explained
// TODO: stop the game when someone wins or lose
// TODO: record the already drawn cards to not be use again until all of the card is drawn and refreshes

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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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
		//Start the game by populating views with datas
		//and also draw a card.
		updateGame();
		drawCard();
	}



	public void swipeCardLogic(View cardParentLayout, MotionEvent event){
		//TODO: Some comments
		final int viewWidth = screenWidth/3;
		final int viewWidthCenter = viewWidth/2;
		final int centerX = screenWidth/2 - viewWidth/2;
		switch (event.getAction()) {
			
			case MotionEvent.ACTION_DOWN:
				skipIndicatorTxt.setVisibility(View.VISIBLE);
				useIndicatorTxt.setVisibility(View.VISIBLE);
				break;

			case MotionEvent.ACTION_MOVE:
				float currentX = event.getRawX() - viewWidthCenter;
				cardParentLayout.setX(currentX);

				float recordAddedX = Math.abs(currentX - centerX);
				float scaleBy = (recordAddedX/100)+1;
				if (currentX < centerX) {
					skipIndicatorTxt.setScaleX(scaleBy);
					skipIndicatorTxt.setScaleY(scaleBy);

				}else if (currentX > centerX) {
					useIndicatorTxt.setScaleX(scaleBy);
					useIndicatorTxt.setScaleY(scaleBy);
				}

				debugTxt.setText("event.getRawX(): " + currentX + "   centerX: " + centerX + " recordAddedX: " + recordAddedX + "\n" + "skipIndicatorTxt: " + skipIndicatorTxt.getScaleX() + " useIndicatorTxt: " + useIndicatorTxt.getScaleX());
				break;

			case MotionEvent.ACTION_UP:
				skipIndicatorTxt.setVisibility(View.GONE);
				skipIndicatorTxt.setScaleX(1);
				skipIndicatorTxt.setScaleY(1);
				useIndicatorTxt.setVisibility(View.GONE);
				useIndicatorTxt.setScaleX(1);
				useIndicatorTxt.setScaleY(1);

				cardParentLayout.setX(centerX);
				float finalX = event.getRawX() - viewWidthCenter;
				int safeArea = 110;
				if (finalX < centerX - safeArea) {
					//if the card is on the left of the center of the screen
					skipCard();

				}else if (finalX > centerX + safeArea) {
					//if the card is on the right of the center of the screen
					useCard();
				}
				break;
		}
	}



	public void drawCard(){
		//Randomly get a hashmap from the cardsData
		//drawnCardMap will be used on useCard() void method.
		Random random = new Random();
        drawnCardMap = cardsData.get(random.nextInt(cardsData.size()));
		// TODO: Some comments
		String cardName = drawnCardMap.get("name");
		String cardInfo = drawnCardMap.get("info");
		String cardType = drawnCardMap.get("type");
		setImageByCardType(cardType, cardTypeImg);
		cardNameTxt.setText(cardName);
		cardInfoTxt.setText(cardInfo);

		//TODO: Some comments
		int cardCost = Integer.parseInt(drawnCardMap.get("cost"));
		cardCost = Math.abs(cardCost);
		String energyString = "";
		energyString = cardCost <= 0 ? energyString + "----" : energyString + "";
		energyString = cardCost >= 1 ? energyString + "■" : energyString + "";
		energyString = cardCost >= 2 ? energyString + "■" : energyString + "";
		energyString = cardCost >= 3 ? energyString + "■" : energyString + "";
		energyString = cardCost >= 4 ? energyString + "■" : energyString + "";
		energyString = cardCost >= 5 ? energyString + "■" : energyString + "";
		cardCostTxt.setText(energyString);

		Animations.cardAnim(cardLayout, cardBackLayout, cardNameTxt, isEnemyTurn, cardParentLayout, skipBtn, useBtn);

	}



	public void skipCard(){
		//skipping a card gives you 1 energy.
		if (userTurn.equals("player")){
			playerEnergy = playerEnergy + 1;

		}else if (userTurn.equals("enemy")){
			enemyEnergy = enemyEnergy + 1;

		}
		updateTurns();
		drawCard();
		updateGame();
	}



	public void useCard(){
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
		//negative value subtracts instead of addition.
		if (userTurn.equals("player")){
			if (Math.abs(drawnCardCost) > playerEnergy){
				Animations.redTextAnim(playerEnergyTxt);	
			}else{
				playerEnergy = playerEnergy + drawnCardCost;

				playerLives = playerLives + editSelfLives;
				enemyLives = enemyLives + editTargetLives;

				playerEnergy = playerEnergy + editSelfEnergy;//overwrites drawnCardCost
				enemyEnergy = enemyEnergy + editTargetEnergy;

				if(cardType.contains("attack")){//contains() method because it will reach any string with "attack"
					Animations.attackAnim(-5, playerLayout, enemyLayout);
				}
				updateTurns();
				drawCard();
			}
		}else if (userTurn.equals("enemy")){
			//Enemy will play on its own. The decision of enemy is made on updateTurns() on enemyRandomPlay()
			if (Math.abs(drawnCardCost) > enemyEnergy){
				skipCard();
			}else{
				enemyEnergy = enemyEnergy + drawnCardCost;

				enemyLives = enemyLives + editSelfLives;
				playerLives = playerLives + editTargetLives;

				enemyEnergy = enemyEnergy + editSelfEnergy;//overwrites drawnCardCost
				playerEnergy = playerEnergy + editTargetEnergy;

				if(cardType.contains("attack")){//contains() method because it will reach any string with "attack"
					Animations.attackAnim(5, enemyLayout, playerLayout);
				}

				updateTurns();
				drawCard();
			}
		}
		//whether or not the card can be used, still update the game.
		updateGame();
	}



	public void updateTurns(){
		//This keep count how many moves you make
		//either by using the card or skipping it.
		//Once it reach zero, move on to the next user
		if (moves - 1 > 0){
			moves--;
			if(isEnemyTurn){
				enemyRandomPlay(enemyEnergy);
			}
		}else{
			if (userTurn.equals("player")){
				//player turn has ended
				userTurn = "enemy";
				Animations.hoverAnim(enemyLayout, playerLayout);
				enemyRandomPlay(enemyEnergy);
				isEnemyTurn = true;

			}else if (userTurn.equals("enemy")){
				//enemy turn has ended
				userTurn = "player";
				Animations.hoverAnim(playerLayout, enemyLayout);
				isEnemyTurn = false;
			}
			moves = 3;
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
		if (moves_old != moves){
			Animations.rotateAnim(movesImg, userTurn);
		}
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
		//if player dies then you lose
		if(playerLives <= 0){
			titleTxt.setText(R.string.you_lose);
		}
		//if enemy dies then you win
		if(enemyLives <= 0){
			titleTxt.setText(R.string.you_win);
		}
	}



	public void enemyRandomPlay(final int enemyEnergy){
		View dummy = titleTxt;//not gonna be manipulated
		dummy.animate().setDuration(Animations.duration * 3)
			.withEndAction(new Runnable() { @Override public void run() {
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



	public void setImageByCardType(String cardType, ImageView img){
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
		int bgColor;
		int strokeColor = Color.parseColor("#FFFFFF");//white
		int strokeWidth = 1;
		int cornerRadius = 0;
		float opacity = 0.3f;

		//cardLayout has a white background so it should be kept that way
		bgColor = Color.parseColor("#FFFFFF");
		Tools.setRoundedViewWithStroke(cardLayout, bgColor, strokeColor, strokeWidth, cornerRadius, opacity);

		//cardBackLayout has a grey background so it should be kept that way
		bgColor = Color.parseColor("#696969");
		Tools.setRoundedViewWithStroke(cardBackLayout, bgColor, strokeColor, strokeWidth, cornerRadius, opacity);

		//cardTypeImg and cardCostTxt is transparent and should be kept that way
		bgColor = Color.parseColor("#00FFFFFF");
		Tools.setRoundedViewWithStroke(cardTypeImg, bgColor, strokeColor, strokeWidth, cornerRadius, opacity);
		Tools.setRoundedViewWithStroke(cardCostTxt, bgColor, strokeColor, strokeWidth, cornerRadius, opacity);

		//playerLayout will be outlined first
		bgColor = Color.parseColor("#696969");
		strokeWidth = 2;
		Tools.setRoundedViewWithStroke(playerLayout, bgColor, strokeColor, strokeWidth, cornerRadius, opacity);
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
}