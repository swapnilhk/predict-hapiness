package com.swapnilhk.competition.ph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.bean.CsvToBeanBuilder;
import com.swapnilhk.competition.ph.model.Review;
import com.swapnilhk.competition.ph.model.WordStatistics;
import com.swapnilhk.competition.ph.util.SearchUtil;
import com.swapnilhk.competition.ph.util.Stemmer;
import com.swapnilhk.competition.ph.util.StopWordRemover;

public class Main {
	public final static String TRAIN = "E:\\Development\\eclipse-workspace\\predict_happiness\\src\\main\\resources\\f2c2f440-8-dataset_he\\train.csv";
	public final static String TEST = "E:\\Development\\eclipse-workspace\\predict_happiness\\src\\main\\resources\\f2c2f440-8-dataset_he\\test.csv";
	public final static String SUBMISSION = "E:\\Development\\eclipse-workspace\\predict_happiness\\src\\main\\resources\\f2c2f440-8-dataset_he\\submission.csv";
	
	public static void main(String[] args) {
		Machine machine = new Machine(TRAIN);
		machine.train();
		// Test
		int happyCustomerCount = 0;
		int unhappyCustomerCount = 0;
		Map<String, String> result = new HashMap<String, String>();
		
		try {
			List<Review> reviewListForTest = new CsvToBeanBuilder<Review>(new FileReader(TEST))
					.withType(Review.class).build().parse();
			for(Review review : reviewListForTest){
				double happinessIndex = 0;
				double happinessRecommendationIndex = 0;
				String[] lineArray = review.getDescription().split("\\.");
				for(int j = 0; j < lineArray.length; j++){
					double possitiveIndex = 0;
					double negativeIndex = 0;
					double positiveRecommendationIndex = 0;
					double negativeRecommendationIndex = 0;
					
					//int negativeCount = 0;
					
					// Remove stop words
					String description = StopWordRemover.removeStopWords(lineArray[j]);
					// Stem
					description = Stemmer.stem(description);
					if (description.contains("recommend")) {
						// If yes. check words in the line containing the word
						Pattern pattern = Pattern.compile("[\\w+ ]*recommend[\\w+ ]*");
						Matcher matcher = pattern.matcher(description);
						if (matcher.find()) {
							String lineContainingRecommend = matcher.group();
							String[] wordsInLineContainingRecommend = lineContainingRecommend.split("\\s+");
							for (int i = 0; i < wordsInLineContainingRecommend.length; i++) {
								if (machine.getRecommendation().containsKey(wordsInLineContainingRecommend[i])) {
									positiveRecommendationIndex += /*machine.getRecommendation().get(wordsInLineContainingRecommend[i])*/1;
								} else {
									negativeRecommendationIndex += /*machine.getNoRecommendation().get(wordsInLineContainingRecommend[i])*/-1;
								}
							}
						}
					}
					happinessRecommendationIndex += positiveRecommendationIndex;
					happinessRecommendationIndex += negativeRecommendationIndex;
					// Get word list
					String[] wordArray = description.split("\\s+");
					for (int i = 0; i < wordArray.length; i++) {
						if (machine.getPositiveWords().containsKey(wordArray[i])) {
							possitiveIndex += /*machine.getPositiveWords().get(wordArray[i])*/1;
						} else if (machine.getNegativeWords().containsKey(wordArray[i])) {
							negativeIndex += /*machine.getNegativeWords().get(wordArray[i])*/-1;
							//negativeCount++;
						}
					}
					// Odd no of negative words
					//if(negativeCount % 2 == 1){
						happinessIndex += negativeIndex;
					//}
					//else {
						happinessIndex += possitiveIndex;
					//}
				}
				int overallHapinessIndex = 0;
				
				// Happiness according to capitalization
				if(SearchUtil.containsCapitals(review.getDescription())){
					if(machine.isHappyWhenContainsCapitalization()){
						overallHapinessIndex += 1;
					}
					else{
						overallHapinessIndex -= 1;
					}
				}
				// Happiness according to exclamation
				if(SearchUtil.containsExclamation(review.getDescription())){
					if(machine.isHappyWhenContainsExclamation()){
						overallHapinessIndex += 1;
					}
					else{
						overallHapinessIndex -= 1;
					}
				}
				// Happiness according to browser
				overallHapinessIndex = machine.getBrowserStatistics().get(review.getBrowserUsed()) ? 1 : -1;
				// Happiness according to device
				overallHapinessIndex = machine.getDeviceStatistics().get(review.getDeviceUsed()) ? 1 : -1;
				// Happiness predicted by all words in description
				overallHapinessIndex = happinessIndex > 0 ? 1 : -1;
				// Happiness predicted by recommendation
				overallHapinessIndex = happinessRecommendationIndex > 0 ? 1 : -1;
				
				if(overallHapinessIndex > 0){
					happyCustomerCount++;
					System.out.println("HAPPY : " + happyCustomerCount);
					result.put(review.getUserId(), "happy");
				}
				else{
					unhappyCustomerCount++;
					System.out.println("UN-HAPPY : " + unhappyCustomerCount);
					result.put(review.getUserId(), "not_happy");
				}
			}
			System.out.println("Final count: HAPPY : " + happyCustomerCount);
			System.out.println("Final count: UN-HAPPY : " + unhappyCustomerCount);
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File(SUBMISSION)));
			Set<String> resultSet = result.keySet();
			Iterator<String> it = resultSet.iterator();
			pw.write("User_ID,Is_Response\n");
			while(it.hasNext()){
				String key = it.next();
				pw.write(key + "," + result.get(key) + "\n");
			}
			pw.close();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
