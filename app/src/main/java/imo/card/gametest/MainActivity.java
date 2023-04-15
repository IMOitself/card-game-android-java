package imo.card.gametest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends Activity 
{
	public TextView movesTxt;
	public LinearLayout playerLayout;
	public TextView playerLivesTxt;
	public TextView playerEnergyTxt;
	public LinearLayout enemyLayout;
	public TextView enemyLivesTxt;
	public TextView enemyEnergyTxt;
	public TextView cardNameTxt;
	public TextView cardInfoTxt;
	public TextView cardCostTxt;
	public Button skipBtn;
	public Button useBtn;

	public List<Map<String, String>> cardsData;
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		movesTxt = findViewById(R.id.moves_txt);
		playerLayout = findViewById(R.id.player_layout);
		playerLivesTxt = findViewById(R.id.player_lives_txt);
		playerEnergyTxt = findViewById(R.id.player_energy_txt);
		enemyLayout = findViewById(R.id.enemy_layout);
		enemyLivesTxt = findViewById(R.id.enemy_lives_txt);
		enemyEnergyTxt = findViewById(R.id.enemy_energy_txt);
		cardNameTxt = findViewById(R.id.card_name_txt);
		cardInfoTxt = findViewById(R.id.card_info_txt);
		cardCostTxt = findViewById(R.id.card_cost_txt);
		skipBtn = findViewById(R.id.skip_btn);
		useBtn = findViewById(R.id.use_btn);
		
		System.out.println("mainLogic() check");
		
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
    }






	// TODO: enemy autoplay
	// TODO: card animation


	public void onCreateLogic(){
		System.out.println("uwu check");
		//Transform the string to a Arraylist that holds a hashmap. 
		//Each Hashmap holds key-value pairs that makes it easier to search for a specific attribute.
		//e.g. if i search for the key "name" it will retrieve a value of "Attack".
		String cards = getResources().getString(R.string.cards);
        Tools.importDataToArraylist(cardsData, cards, ";", "》");//arraylist, string, splitItemsBy, splitContentsBy
		System.out.println("uwu check");
		//Start the game by populating views with datas
		//and also draw a card.
		updateGame();
		drawCard();
		
		System.out.println("onCreateLogic() check");
	}



	public void useCard(){
		//The drawnCardMap is a map populated by drawnCard() void method
		//Some map doesn't have a key of "lives" or "energy"
		//"lives" key contain a value of e.g "0, 0"
		//"energy" key also contain a value of e.g "0, 0"
		//We split the "0, 0" by "," thus resulting in ["0", " 0"]
		//The first value of the array populates how many should be added on self.
		//The second value of the array populates how many should be added on target.
		//for example, if the user is player. 
		//In that case, self is referring to player and target is referring to enemy.
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
		//e.g. if i have 3 energy and the card needs 4.
		//im not gonna be able to use it since i don't have enough energy.
		//therefore my only option is to skip.
		//Else if i have enough energy, use the card and move onto next card.
		
		//if its player's turn,
		//editSelfLives and editSelfEnergy will be added to the player
		//editTargetLives and editTargetEnergy will be added to the enemy
		//same as the enemy's turn but vice versa.
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
				Animations.redTextAnim(enemyEnergyTxt);
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
		//The max energy a player and enemy could have is 5
		//This prevents going beyond 5.
		if (playerEnergy > 5){
			playerEnergy = 5;
			playerEnergy_old = 0;//to prevent not showing changes
		}
		if (enemyEnergy > 5){
			enemyEnergy = 5;
			enemyEnergy_old = 0;//to prevent not showing changes
		}
		
		//whether or not the card can be used. 
		//Still update and populates the views with datas.
		updateGame();
		System.out.println("useCard() check");
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
		System.out.println("skipCard() check");
	}



	public void drawCard(){
		//Randomly get a hashmap from the cardsData
		//drawnCardMap will be used on useCard() void method.
		Random random = new Random();
        drawnCardMap = cardsData.get(random.nextInt(cardsData.size()));
		
		//update which card you picked by populating some views
		cardNameTxt.setText(drawnCardMap.get("name"));
		cardCostTxt.setText("⚡" + drawnCardMap.get("cost"));
		cardInfoTxt.setText(drawnCardMap.get("info"));
		System.out.println("drawCard() check");
	}



	public void updateTurns(){
		//This keep count how many moves you make
		//either by using the card or skipping it.
		//Once it reach 0 move on to another user
		if (moves - 1 > 0){
			moves--;
		}else{
			if (userTurn.equals("player")){
				userTurn = "enemy";
				Animations.hoverAnim(enemyLayout, playerLayout);

			}else if (userTurn.equals("enemy")){
				userTurn = "player";
				Animations.hoverAnim(playerLayout, enemyLayout);
			}
			moves = 3;
		}
		System.out.println("updateTurns() check");
	}



	public void updateGame(){
		//Some datas are populated on useCard() void method.
		//Display the current datas on textviews
		movesTxt.setText(userTurn + "\n" + moves + "");
		playerLivesTxt.setText("❤" + playerLives);
		playerEnergyTxt.setText("⚡" + playerEnergy + "/5");

		enemyLivesTxt.setText("❤" + enemyLives);
		enemyEnergyTxt.setText("⚡" + enemyEnergy + "/5");

		//Detect any differences on datas
		//do an animation if theres any
		if (playerEnergy_old != playerEnergy){
			Animations.greenTextAnim(playerEnergyTxt);
		}
		if (enemyEnergy_old != enemyEnergy){
			Animations.greenTextAnim(enemyEnergyTxt);
		}
		if (playerLives_old != playerLives){
			Animations.popAnim(playerLivesTxt);
		}
		if (enemyLives_old != enemyLives){
			Animations.popAnim(enemyLivesTxt);
		}

		//Record the data to be compared with later ones
		playerLives_old = playerLives;
		playerEnergy_old = playerEnergy;
		enemyLives_old = enemyLives;
		enemyEnergy_old = enemyEnergy;
		
		//Winning and losing system
		if(playerLives <= 0){
			movesTxt.setText(R.string.you_lose);
		}
		if(enemyLives <= 0){
			movesTxt.setText(R.string.you_win);
		}
		System.out.println("updateGame() check");
	}




}
