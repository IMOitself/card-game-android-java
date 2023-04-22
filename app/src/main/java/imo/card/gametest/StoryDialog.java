package imo.card.gametest;

import android.app.Dialog;
import android.content.Context;
import android.view.WindowManager;
import android.view.Window;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
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
	
	public int screenWidth;
	public ArrayList<Map<String, String>> routeData = new ArrayList<>();
	public ArrayList<Map<String, String>> sceneData = new ArrayList<>();
	public String[] sceneSequencesArray;
	public int sequenceIndex = 1;
	
	public boolean stillDisplaySequence = true;
	public boolean hasChosen = false;
	public int forChoice = 0;
	
    public StoryDialog(final Context context) {
        super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
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
		
		int strokeColor = Color.WHITE;
		int strokeWidth = 2;
		float strokeAlpha = 0.3f;
		int cornerRadius = 10;
		int bgColor = Color.TRANSPARENT;
		Tools.setCustomBgWithStroke(choice1Layout, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
		Tools.setCustomBgWithStroke(choice2Layout, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
		
		onDialogCreate(context);
		
		mainStoryLayout.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v){
				mainOnclick(context);
			}
		});
		View.OnClickListener choicesOnClick = new View.OnClickListener(){
			@Override public void onClick(View view){
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
					
				} else if (view.getId() == choice2LayoutId) {
					Tools.setCustomBgWithStroke(choice2Layout, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
				}		
			}
		};
		choice1Layout.setOnClickListener(choicesOnClick);
		choice2Layout.setOnClickListener(choicesOnClick);
    }

	
	
	
	
	public void onDialogCreate(Context context){
		choicesParentLayout.setVisibility(View.GONE);
		
		routeData = Tools.getArrayListFromSharedPrefs(context, "route_data");
		sceneData = Tools.getArrayListFromSharedPrefs(context, "scene_data");
		Map<String, String> startingSceneMap =
		Tools.findMapFromArraylist(sceneData, "scene_id", "initial_scene");
		String getSceneSequences = startingSceneMap.get("scene_sequences");
		getSceneSequences = getSceneSequences.replaceFirst("»", "");
		sceneSequencesArray = getSceneSequences.split("»");
		String firstSequence = sceneSequencesArray[0];
		storyTxt.setText(firstSequence.substring(5).trim());
	}
	
	
	
	public void mainOnclick(Context context){
		int sceneSequencesLength = sceneSequencesArray.length - 1;
		
		if(sequenceIndex <= sceneSequencesLength){
			if(stillDisplaySequence){
				if(hasChosen){
					choicesParentLayout.setVisibility(View.GONE);
					choicesHintTxt.setVisibility(View.GONE);
					hintTxt.setVisibility(View.VISIBLE);
					storyTxt.setText("");
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
					choicesSelection(stringSequence);
				}
				sequenceIndex++;
			}
		}else{
			dismiss();//temporary
		}
	}
	
	
	
	public void choicesSelection(String stringSequence){
		//usually stringSequence has "pick:" at the start.
		//remove it by doing substring(5)
		stringSequence = stringSequence.substring(5).trim();
        //typically it has "," in between two text. split it by that into 2 parts
		String[] choiceArray = stringSequence.split(",");
		Map<String, String> outputMap = new HashMap<>();
		Random random = new Random();

		for(String choiceString : choiceArray){
			forChoice++;
			//for picking route by its level
			String routeLevelKey = null;
			String routeLevelValue = null;

			if (choiceString.equals("route_level_1")) {
				routeLevelKey = "route_level";
				routeLevelValue = "1";
			} else if (choiceString.equals("route_level_2")) {
				routeLevelKey = "route_level";
				routeLevelValue = "2";
			}
			if (routeLevelKey != null||routeLevelValue != null) {//this will run only if its populated
				List<Map<String, String>> levelMaps = new ArrayList<>();
				//this will search a map with desired level from routeData into levelMaps
				for (Map<String, String> map : routeData) {
					if (map.get(routeLevelKey).equals(routeLevelValue)) {
						levelMaps.add(map);
					}
				}
				if (!levelMaps.isEmpty()) {
					//only execute if populated
					//randomly pick a map from the levelMaps
					outputMap = levelMaps.get(random.nextInt(levelMaps.size()));
				}
				if (forChoice == 1){
					choiceArray[0] = outputMap.get("route_name");
				}else if (forChoice == 2){
					choiceArray[1] = outputMap.get("route_name");
				}
			}
			choice1Txt.setText(choiceArray[0]);
			choice2Txt.setText(choiceArray[1]);
		}
	}
}