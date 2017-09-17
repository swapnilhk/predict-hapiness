package com.swapnilhk.competition.ph.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StopWordRemover {
    public static final String[] STOP_WORD = {
    		"a","about","also","am","among","an","and","are","as","at",
    		"be","because","been","by",
    		"can","could",
    		"did","do","does",
    		"ever",
    		"for","from",
    		"get","got",
    		"had","has","have","he","her","hers","him","his",
    		"i","if","in","into","is","it","its",
    		"let","likely",
    		"may","me","might","must","my",
    		"of","often","on","or","our","own",
    		"she","should","so",
    		"that","the","their","them","then","there","these","they","this","to","too",
    		"us",
    		"was","we","were","will","with","would",
    		"you","your"};

    /*public static void main(String[] args) {
		String input = "I stayed here on a Category - award. This is the first time I have been disappointed in a Category - property. I recognize it is a category - and have lower expectations, but housekeeping is not one of those lower expectations. I am Gold Elite with Marriott. Gold elite is certainly not a superstar, but I do expect that my preferences will be honored when I submit them timely. I requested a feather free room due to allergies. Upon arrival, I notice that has not been honored. I call down to the desk and ask if they can send up some feather free pillows, but I am told that is not possible as housekeeping is gone for the night and there are none at the front desk. (I also requested extra towels which was not honored either, but that is not an allergy issue at least). Recognizing that the hotel may be understaffed, I proceeded downstairs to the lobby to explain the situation in person. She was helpful and did her best to arrange a solution and ultimately found some pillows in a spare guest room. The second issue is minor, but also relates to Housekeeping. On the -nd day, the used bath products were removed, but no replacement new bath products were provided. Again, no extra towels were provided. These are somewhat minor things, but it seems to me that housekeeping may need a little more oversight to ensure a consistent service is provided. As far as the room, etc., it was fine. There was plenty of room, the pullout sofa seemed brand new (plastic still on the mattress), and we slept ok. I personally will choose a different property next time I need to stay in the area most likely.";
		input = removeStopWords(input);
		System.out.println(input);
	}*/
	public static String removeStopWords(String text){
		text = text.toLowerCase();
		String whiteSpacesAndPunctuations = "\\s`~!@#$%&*()-=+\\\\;':\",./<>?|\\[\\]{}";
		Pattern punctuations = Pattern.compile("["+whiteSpacesAndPunctuations+"]+");
		Matcher matcher = punctuations.matcher(text);
		if(matcher.find()){
			text = matcher.replaceAll(" ");
		}
		text = " " + text + " ";
    	for(int i = 0; i < STOP_WORD.length; i++){
    		
    		Pattern pattern = Pattern.compile(" "+STOP_WORD[i]+" ");
    		matcher = pattern.matcher(text);
			if(matcher.find()){
				text = matcher.replaceAll(" ");
			}
		}
    	return text.trim();
    }
}
