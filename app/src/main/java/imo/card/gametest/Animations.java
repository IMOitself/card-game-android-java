package imo.card.gametest;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;

public class Animations
{
	private static int duration = 500;
	
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
	public static void cardAnim(final Button btn1, final Button btn2, final View cardLayout, final View cardBackLayout, final View optionalView){
		int translateDistance = 100;
		btn1.setEnabled(false);
		btn2.setEnabled(false);
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
								cardLayout.animate().scaleX(1).setDuration(duration).start();
								btn1.setEnabled(true);
								btn2.setEnabled(true);
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
}
