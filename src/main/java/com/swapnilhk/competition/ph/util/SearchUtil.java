package com.swapnilhk.competition.ph.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchUtil {

	public static boolean containsCapitals(String description){
		Pattern pattern = Pattern.compile("[A-Z ]+");
		Matcher matcher = pattern.matcher(description);
		return matcher.find();
	}
	
	public static boolean containsExclamation(String description){
		return description.contains("!");
	}
}
