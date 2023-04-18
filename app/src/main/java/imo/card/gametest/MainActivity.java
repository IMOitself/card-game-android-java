package imo.card.gametest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.view.MotionEvent;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

// TODO: another textview or smth to display moves int
// TODO: attack animation
// TODO: stop the game when someone wins or lose
// TODO: Make "Skip" textview on the left and "Use" on the right. To indicate where should the user will swipe.
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
	public View cardParentLayout;
	public LinearLayout cardBackLayout;
	public LinearLayout cardLayout;
	public TextView cardNameTxt;
	public TextView cardInfoTxt;
	public TextView cardCostTxt;
	public LinearLayout buttonsLayout;
	public Button skipBtn;
	public Button useBtn;
	public TextView debugTxt;
	
	public int screenWidth = 0;

	public List<Map<String, String>> cardsData = new ArrayList<>();
	public Map<String, String> drawnCardMap;

	public int playerLives = 10;
	public int playerEnergy = 5;
	public int enemyLives = 10;
	public int enemyEnergy = 5;

	public int playerLives_old = playerLives;
	public int playerEnergy_old = playerEnergy;
	public int enemyLives_old = enemyLives;
	public int enemyEnergy_old = enemyEnergy;

	public int moves = 3;
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
		cardParentLayout = findViewById(R.id.cardParentLayout);
		cardBackLayout = findViewById(R.id.card_back_layout);
		cardLayout = findViewById(R.id.card_layout);
		cardNameTxt = findViewById(R.id.card_name_txt);
		cardInfoTxt = findViewById(R.id.card_info_txt);
		cardCostTxt = findViewById(R.id.card_cost_txt);
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
		Tools.setViewSize(cardLayout, screenWidth / 3, screenWidth / 3);
		Tools.setViewSize(cardBackLayout, screenWidth / 3, screenWidth / 3);
		
		cardBackLayout.setVisibility(View.GONE);//will be used later
		
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
			case MotionEvent.ACTION_MOVE:
				float currentX = event.getRawX();
				cardParentLayout.setX(currentX - viewWidthCenter);
				
				float debugFloat = event.getRawX() - viewWidthCenter;
				debugTxt.setText("event.getRawX(): " + debugFloat + "   centerX: " + centerX);
				
				break;
			case MotionEvent.ACTION_UP:
				cardParentLayout.setX(centerX);
				
				float finalX = event.getRawX() - viewWidthCenter;
				
				int safeZone = 120;
				if (finalX < centerX - safeZone) {
					skipCard();
					
				}else if (finalX > centerX + safeZone) {
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

		//update which card you picked by populating some views
		cardNameTxt.setText(drawnCardMap.get("name"));
		cardInfoTxt.setText(drawnCardMap.get("info"));
		//TODO: Some comments
		int cardCost = Math.abs(Integer.parseInt(drawnCardMap.get("cost")));
		String energyString = "";
		energyString = cardCost <= 0 ? energyString + "FREE" : energyString + "";
		energyString = cardCost >= 1 ? energyString + "■" : energyString + "";
		energyString = cardCost >= 2 ? energyString + "■" : energyString + "";
		energyString = cardCost >= 3 ? energyString + "■" : energyString + "";
		energyString = cardCost >= 4 ? energyString + "■" : energyString + "";
		energyString = cardCost >= 5 ? energyString + "■" : energyString + "";
		cardCostTxt.setText(energyString);

		Animations.cardAnim(cardLayout, cardBackLayout, cardNameTxt, cardParentLayout, skipBtn, useBtn);
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
		int editSelfLives = 0;
		int editTargetLives = 0;
		if (drawnCardMap.containsKey("lives")){
			String[] valueArray = drawnCardMap.get("lives").split(",");
			editSelfLives = Integer.parseInt(valueArray[0].trim());
			editTargetLives = Integer.parseInt(valueArray[1].trim());
		}
		int editSelfEnergy = 0;
		int editTargetEnergy = 0;
		if (drawnCardMap.containsKey("energy")){
			String[] valueArray = drawnCardMap.get("energy").split(",");
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

				updateTurns();
				drawCard();
			}
		}else if (userTurn.equals("enemy")){
			if (Math.abs(drawnCardCost) > enemyEnergy){
				skipCard();
			}else{
				enemyEnergy = enemyEnergy + drawnCardCost;

				enemyLives = enemyLives + editSelfLives;
				playerLives = playerLives + editTargetLives;

				enemyEnergy = enemyEnergy + editSelfEnergy;//overwrites drawnCardCost
				playerEnergy = playerEnergy + editTargetEnergy;

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
		titleTxt.setText(userTurn + "\n" + moves);
		
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
}