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

//TODO: Explain everything

public class StoryDialog extends Dialog {
	public LinearLayout mainStoryLayout;
	public TextView storyTxt;
	public TextView choicesHintTxt;
	public LinearLayout choicesParentLayout;
	public LinearLayout choice1Layout;
	public ImageView choice1Img;
	public TextView choice1Txt;
	public LinearLayout choice2Layout;
	public ImageView choice2Img;
	public TextView choice2Txt;
	public TextView hintTxt;
	
	public int screenWidth;
	
    public StoryDialog(Context context) {
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
		choice1Layout = findViewById(R.id.choice1_layout);
		choice1Img = findViewById(R.id.choice1_img);
		choice1Txt = findViewById(R.id.choice1_txt);
		choice2Layout = findViewById(R.id.choice2_layout);
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
		
		mainStoryLayout.setOnClickListener(new View.OnClickListener(){
			@Override public void onClick(View v){
				dismiss();//temporary
			}
		});
		View.OnClickListener choicesOnClick = new View.OnClickListener(){
			@Override public void onClick(View view){
				int strokeColor = Color.WHITE;
				int strokeWidth = 2;
				float strokeAlpha = 0.3f;
				int cornerRadius = 10;
				int bgColor = Color.TRANSPARENT;
				Tools.setCustomBgWithStroke(choice1Layout, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
				Tools.setCustomBgWithStroke(choice2Layout, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
				
				if (view.getId() == R.id.choice1_layout) {
					strokeWidth = 4;
					Tools.setCustomBgWithStroke(choice1Layout, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
					
				} else if (view.getId() == R.id.choice2_layout) {
					strokeWidth = 4;
					Tools.setCustomBgWithStroke(choice2Layout, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
				}		
			}
		};
		choice1Layout.setOnClickListener(choicesOnClick);
		choice2Layout.setOnClickListener(choicesOnClick);
    }

}

