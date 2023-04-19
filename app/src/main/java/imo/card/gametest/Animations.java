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
											cardSwipe.setEnabled(true);
											if (isEnemyTurn == false){
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
}
