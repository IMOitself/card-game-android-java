package imo.card.gametest;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

//TODO: Some comments

public class Animations
{
	public static int duration = 500;
	
	public static void redTextAnim(final TextView textview){
		textview.setTextColor(Color.parseColor("#FB1600"));
		float scaleBy = 1.15f;
		textview.animate().scaleX(scaleBy).scaleY(scaleBy).setDuration(duration)
			.withEndAction(new Runnable() { @Override public void run() {
					textview.setTextColor(Color.parseColor("#FFFFFF"));
					textview.setScaleX(1);
					textview.setScaleY(1);
				} })
			.start();
	}
	public static void hoverAnim(View view1, View view2){
		int bgColor = Color.parseColor("#696969");//grey
		int strokeColor = Color.parseColor("#FFFFFF");//white
		int strokeWidth = 2;
		int cornerRadius = 0;
		float opacity = 0.3f;
		Tools.setRoundedViewWithStroke(view1, bgColor, strokeColor, strokeWidth, cornerRadius, opacity);
		strokeWidth = 0;
		Tools.setRoundedViewWithStroke(view2, bgColor, strokeColor, strokeWidth, cornerRadius, opacity);
		
		float scaleBy = 1.05f;
		view1.animate().scaleX(scaleBy).scaleY(scaleBy ).setDuration(duration).start();
		if (view2 != null){
			view2.setScaleX(1);
			view2.setScaleY(1);
		}
	}
	public static void popAnim(final View view){
		float scaleBy = 1.15f;
		view.animate().scaleX(scaleBy).scaleY(scaleBy).setDuration(duration)
			.withEndAction(new Runnable() { @Override public void run() {
					view.setScaleX(1);
					view.setScaleY(1);
				} })
			.start();
	}
	public static void cardAnim(
	final View cardLayout,
	final View cardBackLayout,
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
		
		int translateDistance = 100;
		cardLayout.setVisibility(View.GONE);
		cardBackLayout.setTranslationY(translateDistance);
		cardBackLayout.setVisibility(View.VISIBLE);

		cardBackLayout.animate().translationY(0).setDuration(duration)
			.withEndAction(new Runnable() { @Override public void run() {
					cardBackLayout.animate().scaleX(0).setDuration(duration).withEndAction(new Runnable() { @Override public void run() {
								cardBackLayout.setVisibility(View.GONE);
								cardBackLayout.setScaleX(1);
								cardLayout.setScaleX(0);
								cardLayout.setVisibility(View.VISIBLE);
								cardLayout.animate().scaleX(1).setDuration(duration).withEndAction(new Runnable() { @Override public void run() {
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
			//optionalView's animation is in sync with the card's so that it also trnaslates upward
			//the only thing it doesnt do is the flipping animation (i.e. scaleX)
			if (optionalView != null){
				optionalView.setTranslationY(translateDistance);
				optionalView.animate().translationY(0).setDuration(duration).start();
			}
	}
	public static void rotateAnim(final View view, final String userTurn){
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
	public static void attackAnim (final int moveBackBy, final View self, final View target){
		//moves back by 5dp,
		//Dash to the distance of its own view's length times the value.
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
