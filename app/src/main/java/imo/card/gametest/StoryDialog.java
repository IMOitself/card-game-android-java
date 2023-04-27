package imo.card.gametest;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

//TODO: Explain everything

public class StoryDialog extends Dialog {
	public LinearLayout mainStoryLayout;
	public TextView storyTxt;
	public TextView choicesHintTxt;
	public LinearLayout choicesParentLayout;
	public LinearLayout choice1Layout;
	public int choice1LayoutId;
	public ImageView choice1Img;
	public TextView choice1Txt;
	public LinearLayout choice2Layout;
	public int choice2LayoutId;
	public ImageView choice2Img;
	public TextView choice2Txt;
	public TextView hintTxt;
	
	public ArrayList<Map<String, String>> routeList = new ArrayList<>();
	public ArrayList<Map<String, String>> initialSceneList = new ArrayList<>();
	public String[] sceneSequencesArray;
	public int sequenceIndex = 1;
	
	public boolean stillDisplaySequence = true;
	public boolean hasChosen = false;
	public Map<String, String> choice1Map = new HashMap<>();
	public Map<String, String> choice2Map = new HashMap<>();
	public Map<String, String> chosenMap = new HashMap<>();
	
    public StoryDialog(final Context context) {
        super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCancelable(false);
        setContentView(R.layout.story);
        //Set the dialog window to match the parent's width and height then set it to transparent
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

		mainStoryLayout = findViewById(R.id.main_story_layout);
		storyTxt = findViewById(R.id.story_txt);
		choicesHintTxt = findViewById(R.id.choices_hint_txt);
		choicesParentLayout = findViewById(R.id.choices_parent_layout);
		choice1LayoutId = R.id.choice1_layout;
		choice1Layout = findViewById(choice1LayoutId);
		choice1Img = findViewById(R.id.choice1_img);
		choice1Txt = findViewById(R.id.choice1_txt);
		choice2LayoutId = R.id.choice2_layout;
		choice2Layout = findViewById(choice2LayoutId);
		choice2Img = findViewById(R.id.choice2_img);
		choice2Txt = findViewById(R.id.choice2_txt);
		hintTxt = findViewById(R.id.hint_txt);
		
		onDialogCreate(context);
		
		mainStoryLayout.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v){
				mainOnClick(context);
			}
		});
		View.OnClickListener choicesClickListener = new View.OnClickListener(){
			@Override public void onClick(View view){
				choicesOnClick(context, view);
			}
		};
		choice1Layout.setOnClickListener(choicesClickListener);
		choice2Layout.setOnClickListener(choicesClickListener);
		
		choicesParentLayout.setOnClickListener(new View.OnClickListener(){
				@Override public void onClick(View v){
					//this is to prevent mainStoryLayout from clicking inside this layout
				}
			});
    }
	
	
	public void onDialogCreate(Context context){
		choicesParentLayout.setVisibility(View.GONE);
		int strokeColor = Color.WHITE;
		int strokeWidth = 2;
		float strokeAlpha = 0.3f;
		int cornerRadius = 10;
		int bgColor = Color.TRANSPARENT;
		Tools.setCustomBgWithStroke(choice1Layout, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
		Tools.setCustomBgWithStroke(choice2Layout, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);

		routeList = Data.routeData;
		initialSceneList = Data.initialSceneData;
		
		Map<String, String> startingSceneMap =
		Tools.findMapFromArraylist(initialSceneList, "scene_id", "initial_scene");
		String getSceneSequences = startingSceneMap.get("scene_sequences");
		if (getSceneSequences != null) {
			getSceneSequences = getSceneSequences.replaceFirst("»", "");
			sceneSequencesArray = getSceneSequences.split("»");
			String firstSequence = sceneSequencesArray[0];
			storyTxt.setText(firstSequence.substring(5).trim());
		}
	}
	
	
	public void mainOnClick(Context context){
		int sceneSequencesLength = sceneSequencesArray.length - 1;
		
		if(sequenceIndex <= sceneSequencesLength){
			if(stillDisplaySequence){
				if(hasChosen){
					choicesParentLayout.setVisibility(View.GONE);
					choicesHintTxt.setVisibility(View.GONE);
					hintTxt.setVisibility(View.VISIBLE);
					storyTxt.setText("");
					hasChosen = false;
				}
				String textviewString = storyTxt.getText().toString().trim();
				String stringSequence = sceneSequencesArray[sequenceIndex];

				if(stringSequence.startsWith("text:")){
					stringSequence = stringSequence.substring(5);
					storyTxt.setText(textviewString +"\n\n" + stringSequence.trim());

				}else if (stringSequence.startsWith("pick:")){
					choicesParentLayout.setVisibility(View.VISIBLE);
					choicesHintTxt.setVisibility(View.VISIBLE);
					hintTxt.setVisibility(View.INVISIBLE);
					stillDisplaySequence = false;
					hasChosen = false;
					makeChoicesSelection(stringSequence, choice1Txt, choice2Txt);
				}
				sequenceIndex++;
			}
		}else{
			dismiss();
		}
	}
	
	
	public void makeChoicesSelection(String stringSequence, TextView choice1Txt, TextView choice2Txt){
		//usually stringSequence has "pick:" at the start.
		//remove it by doing substring(5)
		stringSequence = stringSequence.substring(5).trim();
        //typically it has "," in between two text. split it by that into 2 parts
		String[] choiceArray = stringSequence.split(",");
		Map<String, String> outputMap = new HashMap<>();
		Random random = new Random();
		int forChoice = 0;
		for(String choiceString : choiceArray){
			forChoice++;
			//for picking route by its level
			String routeLevelKey = null;
			String routeLevelValue = null;

			if (choiceString.equals("route_level_1")) {
				routeLevelKey = "level";
				routeLevelValue = "1";
			} else if (choiceString.equals("route_level_2")) {
				routeLevelKey = "level";
				routeLevelValue = "2";
			}
			if (routeLevelKey != null||routeLevelValue != null) {//this will run only if its populated
				List<Map<String, String>> levelMaps = new ArrayList<>();
				//this will search a map with desired key and value from routeData
				//list maps that has the same key and value
				for (Map<String, String> map : routeList) {
					if (map.get(routeLevelKey).equals(routeLevelValue)) {
						levelMaps.add(map);
					}
				}
				if (!levelMaps.isEmpty()) {
					//only execute if there is any map added on levelMaps
					//randomly pick a map from the levelMaps
					outputMap = levelMaps.get(random.nextInt(levelMaps.size()));
				}
				if (forChoice == 1){
					choice1Map = outputMap;
					choiceArray[0] = choice1Map.get("name");
					
				}else if (forChoice == 2){
					choice2Map = outputMap;
					choiceArray[1] = choice2Map.get("name");
				}
			}
			choice1Txt.setText(choiceArray[0]);
			choice2Txt.setText(choiceArray[1]);
		}
	}
	
	
	public void choicesOnClick(Context context, View view){
		stillDisplaySequence = true;
		hasChosen = true;

		int strokeColor = Color.WHITE;
		int strokeWidth = 2;
		float strokeAlpha = 0.3f;
		int cornerRadius = 10;
		int bgColor = Color.TRANSPARENT;
		Tools.setCustomBgWithStroke(choice1Layout, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
		Tools.setCustomBgWithStroke(choice2Layout, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);

		strokeWidth = 4;
		bgColor = Color.parseColor("#1affffff");
		if (view.getId() == choice1LayoutId) {
			Tools.setCustomBgWithStroke(choice1Layout, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
			chosenMap = choice1Map;

		} else if (view.getId() == choice2LayoutId) {
			Tools.setCustomBgWithStroke(choice2Layout, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
			chosenMap = choice2Map;
		}
		if (!chosenMap.isEmpty()) {
			String hintString = "";
			if(chosenMap.containsKey("name")){
				hintString = hintString + chosenMap.get("name");
				if(chosenMap.containsKey("info")){
					hintString = hintString + ": " + chosenMap.get("info");
				}
			}
			choicesHintTxt.setText(hintString);
		}
	}
}

