package imo.card.gametest;

import android.app.Activity;
import android.widget.TextView;
import android.view.ViewGroup;
import android.view.View;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tools
{
	public static void setViewSize(View view, int width, int height){
		if (width != 0) view.getLayoutParams().width = width;
		if (height != 0) view.getLayoutParams().height = height;
	}
	public static void setRoundedViewWithStroke(View layout, int backgroundColor, int strokeColor, int strokeWidth, int cornerRadius, float opacity) {
		GradientDrawable shape = new GradientDrawable();
		shape.setShape(GradientDrawable.RECTANGLE);
		shape.setColor(backgroundColor);
		shape.setCornerRadius(cornerRadius);
		shape.setStroke(strokeWidth, strokeColor & 0xFFFFFF | ((int)(opacity * 255)) << 24);
		layout.setBackgroundDrawable(shape);//display the shape as background of the view
	}
	
	public static void importDataToArraylist(List<Map<String, String>> arraylist,
											 String textFile,
											 String splitItemsBy,
											 String splitContentsBy){
		String[] rawItems = textFile.split(splitItemsBy);//seperate the contents to individual sections
		for (String rawItem : rawItems) {//loop to every sections made
			String rawContent = rawItem.replaceFirst(splitContentsBy, "");//remove the leading desired char before seperating
			String[] keyValues = rawContent.split(splitContentsBy);//seperate into pieces by the desired char
			Map<String, String> newMap = new HashMap<>();
			for (String keyValue : keyValues) {//get the seperated pieces
				if(keyValue.contains("=")){
					String[] splitKeyValue = keyValue.split("=");//seperate into 2 pieces by =
					newMap.put(splitKeyValue[0].trim(), splitKeyValue[1].trim());//put the 1st piece into the key and 2nd piece to the value of a map
				}
			}
			System.out.println("uwu");
			arraylist.add(newMap);//add the maps
		}
		System.out.println(arraylist);
	}
	public static Map<String, String> searchMapFromArraylist(
		List<Map<String, String>> arrayList,
		String key, String value){
		Map<String, String> output = new HashMap<>();
		output.put("nope", "nope");//should not happen
		for (Map<String, String> map : arrayList) {// Loop through the ArrayList and find the map with the desired key-value pair
			if (map.containsKey(key) && map.get(key).equals(value)) {
				output = map;// Found the map with the desired key-value pair
			}
		}
		return output;
	}
}
