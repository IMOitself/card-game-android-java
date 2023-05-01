package imo.card.gametest;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class Animations
{
	//This int will be use to determine how fast an animation should be
	public static int duration = 500;
	
	public static void redTextAnim(final TextView textview){
		//Set the color of the textview to red
		textview.setTextColor(Color.parseColor("#FB1600"));
		//do a pop animation
		float scaleBy = 1.15f;
		textview.animate().scaleX(scaleBy).scaleY(scaleBy).setDuration(duration)
			.withEndAction(new Runnable() { @Override public void run() {
					textview.setTextColor(Color.parseColor("#FFFFFF"));
					textview.setScaleX(1);
					textview.setScaleY(1);
				} })
			.start();
	}
	public static void hoverAnim(View view1, View view2, int bgColor){
		//customize the views with custom background drawable
		//set the values first then configure it based on the view
		int strokeColor = Color.WHITE;
		int strokeWidth = 2;
		float strokeAlpha = 0.3f;
		int cornerRadius = 0;
		//view1 will be outlined
		Tools.setCustomBgWithStroke(view1, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
		//scale view1 to indicate its been highlighted
		float scaleBy = 1.05f;
		view1.animate().scaleX(scaleBy).scaleY(scaleBy ).setDuration(duration).start();
		
		//view2 will be removed of stroke/outline
		strokeWidth = 0;
		Tools.setCustomBgWithStroke(view2, bgColor, cornerRadius, strokeWidth, strokeColor, strokeAlpha);
		//set view2 to default size
		view2.setScaleX(1);
		view2.setScaleY(1);
	}
	public static void slideAnim(final View view, String direction){
		int viewWidth = view.getWidth();
	    int distance = 0;
		if(direction.equals("left")){
			distance = -viewWidth + -viewWidth/2;//1.5x width
		}else if (direction.equals("right")){
			distance = viewWidth + viewWidth/2;//1.5x width
		}
		final int moveBy = distance;
		view.animate().setDuration(duration*3).withEndAction(new Runnable() { @Override public void run() {
					view.animate().translationX(moveBy).alpha(0).setDuration(duration*3).start();
				} })
			.start();
	}
	
	public static void popAnim(final View view){
		//scale the view to a certain size then to default size
		//as if it just pop
		float scaleBy = 1.15f;
		view.animate().scaleX(scaleBy).scaleY(scaleBy).setDuration(duration)
			.withEndAction(new Runnable() { @Override public void run() {
					view.setScaleX(1);
					view.setScaleY(1);
				} })
			.start();
	}
	public static void flipCardAnim(
	final View frontLayout,
	final View backLayout,
	final View optionalView,
	final boolean isEnemyTurn,
	final View cardSwipe,
	final Button btn1, final Button btn2
	){
		//This disables the buttons and swiping action to prevent future issues
		cardSwipe.setEnabled(false);
		btn1.setEnabled(false);
		btn2.setEnabled(false);
		//If you want to spam using the card nonstop, delete the code above. Be careful tho.
		
		//make the card gone to indicate its flipped on its back
		frontLayout.setVisibility(View.GONE);
		backLayout.setVisibility(View.VISIBLE);
		//translate the card's back to a certain distance first
		int translateDistance = 100;
		backLayout.setTranslationY(translateDistance);
		//then go back to zero as if its moving upward
		backLayout.animate().translationY(0).setDuration(duration)
			.withEndAction(new Runnable() { @Override public void run() {
				    //after that scale the card' back to zero until its gone
					backLayout.animate().scaleX(0).setDuration(duration).withEndAction(new Runnable() { @Override public void run() {
						        //remove the card's back and set it to its default size when its gone
								backLayout.setVisibility(View.GONE);
								backLayout.setScaleX(1);
								//set the scale of the card to zero first
								frontLayout.setScaleX(0);
								//then make it visible
								frontLayout.setVisibility(View.VISIBLE);
								//scale the card back to its default size as if its being flipped
								frontLayout.animate().scaleX(1).setDuration(duration).withEndAction(new Runnable() { @Override public void run() {
									        //if its enemy turn still don't make the buttons and swiping action enabled
											if (isEnemyTurn == false){
												cardSwipe.setEnabled(true);
												btn1.setEnabled(true);
												btn2.setEnabled(true);
											}
										} })
									.start();
							} })
						.start();	
				} })
			.start();
			//optionalView's animation is in sync with the card's so that it also translates upward
			//the only thing it doesn't do is the flipping animation (i.e. scaleX)
			if (optionalView != null){
				//translate it to a distance first before translating it back
				//as if it moves upward with the card
				optionalView.setTranslationY(translateDistance);
				optionalView.animate().translationY(0).setDuration(duration).start();
			}
	}
	public static void sandClockAnim(final View view, String userTurn){
		//since remainingMovesImg looks like a hourglass
		//rotate it to whichever the target's direction
		//then seamlessly flip it back
		int num = 180;
		if (userTurn.equals("player")) {//player turn
			num = 180;
		}else{//enemy turn
			num = -180;
		}
		final int rotation = num;
		view.animate().rotation(rotation).setDuration(duration)
			.withEndAction(new Runnable() { @Override public void run() {
					view.setRotation(0);
				} })
			.start();
	}
	public static void attackAnim(final int moveBackBy, final View self, final View target){
		//moves back by 5dp,
		//Dash to the distance of half of its own view's length.
		//after that, animate the target's reaction to the dash
		//then the view will go back to its original position.
		int viewWidth = self.getWidth();
		if(moveBackBy > 0){//positive
			viewWidth = -self.getWidth();
		}
		final int moveBy = viewWidth/2;
		self.animate().translationX(moveBackBy).setDuration(duration/4)
			.withEndAction(new Runnable() { @Override public void run() {
					self.animate().translationX(moveBy).setDuration(duration*3/4)
						.withEndAction(new Runnable() { @Override public void run() {
								targetHurtAnim(target);
								self.animate().translationX(0).setDuration(duration).start();
							} })
						.start();
				} })
			.start();
	}
	public static void targetHurtAnim(final View target){
		//basically move upward as if its hurt then back to its original position.
		target.animate().translationY(-10).setDuration(duration/3)
			.withEndAction(new Runnable() { @Override public void run() {
					target.animate().translationY(0).setDuration(duration/4).start();
				} })
			.start();
	}
}

