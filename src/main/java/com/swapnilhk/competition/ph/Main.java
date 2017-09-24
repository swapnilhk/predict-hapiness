package com.swapnilhk.competition.ph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.bean.CsvToBeanBuilder;
import com.swapnilhk.competition.ph.model.Review;
import com.swapnilhk.competition.ph.util.SearchUtil;
import com.swapnilhk.competition.ph.util.Stemmer;
import com.swapnilhk.competition.ph.util.StopWordRemover;

public class Main {
	public final static String TRAIN = "E:\\Development\\eclipse-workspace\\predict_happiness\\src\\main\\resources\\f2c2f440-8-dataset_he\\train.csv";
	public final static String TEST = "E:\\Development\\eclipse-workspace\\predict_happiness\\src\\main\\resources\\f2c2f440-8-dataset_he\\test.csv";
	public final static String SUBMISSION = "E:\\Development\\eclipse-workspace\\predict_happiness\\src\\main\\resources\\f2c2f440-8-dataset_he\\submission.csv";
	
	public static void main(String[] args) {
		useMachine2();
	}
	
	public static void useMachine2() {
		Map<String, String> result = new HashMap<String, String>();
		try {
			// For printing to the console
			int countForPrintingToConsole = 0;

			Machine2 machine = new Machine2(TRAIN);
			System.out.println("machine.getpOfWord() : " + machine.getpOfWord());
			System.out.println("machine.getpOfWordGivenHappy() : " + machine.getpOfWordGivenHappy());
			System.out.println("machine.getpOfWordGivenUnhappy() : " + machine.getpOfWordGivenUnhappy());
			System.out.println("machine.getpOfWordInRecommendation() : " + machine.getpOfWordInRecommendation());
			System.out.println("machine.getpOfWordInRecommendationGivenHappy() : "
					+ machine.getpOfWordInRecommendationGivenHappy());
			System.out.println("machine.getpOfWordInRecommendationGivenUnhappy() : "
					+ machine.getpOfWordInRecommendationGivenUnhappy());

			List<Review> reviewListForTest = new CsvToBeanBuilder<Review>(new FileReader(TEST)).withType(Review.class)
					.build().parse();
			for (Review review : reviewListForTest) {

				countForPrintingToConsole++;

				double pOfHappy = 0;
				double pOfUnhappy = 0;
				// Find probability of happiness and unhappiness using browser
				double pOfHappyGivenBowser = 0;
				double pOfUnhappyGivenBowser = 0;
				if (machine.getpOfBrowserGivenHappy().get(review.getBrowserUsed()) != null) {
					pOfHappyGivenBowser = machine.getpOfBrowserGivenHappy().get(review.getBrowserUsed())
							* machine.getpOfHappy() / machine.getpOfBrowser().get(review.getBrowserUsed());
				}
				if (machine.getpOfBrowserGivenUnhappy().get(review.getBrowserUsed()) != null) {
					pOfUnhappyGivenBowser = machine.getpOfBrowserGivenUnhappy().get(review.getBrowserUsed())
							* machine.getpOfUnhappy() / machine.getpOfBrowser().get(review.getBrowserUsed());
				}
				// Find probability of happiness and unhappiness using device
				double pOfHappyGivenDevice = 0;
				double pOfUnhappyGivenDevice = 0;
				if (machine.getpOfDeviceGivenHappy().get(review.getDeviceUsed()) != null) {
					pOfHappyGivenDevice = machine.getpOfDeviceGivenHappy().get(review.getDeviceUsed())
							* machine.getpOfHappy() / machine.getpOfDevice().get(review.getDeviceUsed());
				}
				if (machine.getpOfDeviceGivenUnhappy().get(review.getDeviceUsed()) != null) {
					pOfUnhappyGivenDevice = machine.getpOfDeviceGivenUnhappy().get(review.getDeviceUsed())
							* machine.getpOfUnhappy() / machine.getpOfDevice().get(review.getDeviceUsed());
				}
				// Find probability of happiness and unhappiness using words
				double pOfHappyGivenWords = 0;
				double pOfUnhappyGivenWords = 0;
				String description = StopWordRemover.removeStopWords(review.getDescription());// Remove
																								// stop
																								// words
				description = Stemmer.stem(description);// Stem
				String[] wordArray = description.split("\\s+");
				for (int i = 0; i < wordArray.length; i++) {
					if (machine.getpOfWordGivenHappy().containsKey(wordArray[i])) {
						pOfHappyGivenWords += machine.getpOfWordGivenHappy().get(wordArray[i]) * machine.getpOfHappy()
								/ machine.getpOfWord().get(wordArray[i]);
					}
					if (machine.getpOfWordGivenUnhappy().get(wordArray[i]) != null) {
						pOfUnhappyGivenWords += machine.getpOfWordGivenUnhappy().get(wordArray[i])
								* machine.getpOfUnhappy() / machine.getpOfWord().get(wordArray[i]);
					}
				}
				pOfHappyGivenWords /= wordArray.length;
				pOfUnhappyGivenWords /= wordArray.length;

				// Find probability of happiness and unhappiness using words in
				// recommendation
				double pOfHappyGivenWordsInRecommendation = 0;
				double pOfUnhappyGivenWordsInRecommendation = 0;
				Pattern pattern = Pattern.compile("[\\w+ ]*recommend[\\w+ ]*");
				Matcher matcher = pattern.matcher(description);
				if (matcher.find()) {
					String lineContainingRecommend = matcher.group();
					String[] wordsInLineContainingRecommend = lineContainingRecommend.split("\\s+");
					for (int i = 0; i < wordsInLineContainingRecommend.length; i++) {
						if (machine.getpOfWordInRecommendationGivenHappy()
								.containsKey(wordsInLineContainingRecommend[i])) {
							pOfHappyGivenWordsInRecommendation += machine.getpOfWordInRecommendationGivenHappy()
									.get(wordsInLineContainingRecommend[i]) * machine.getpOfHappy()
									/ machine.getpOfWordInRecommendation().get(wordsInLineContainingRecommend[i]);
						}
						if (machine.getpOfWordInRecommendationGivenUnhappy()
								.get(wordsInLineContainingRecommend[i]) != null) {
							pOfUnhappyGivenWordsInRecommendation += machine.getpOfWordInRecommendationGivenUnhappy()
									.get(wordsInLineContainingRecommend[i]) * machine.getpOfUnhappy()
									/ machine.getpOfWordInRecommendation().get(wordsInLineContainingRecommend[i]);
						}
					}
					pOfHappyGivenWordsInRecommendation /= wordsInLineContainingRecommend.length;
					pOfUnhappyGivenWordsInRecommendation /= wordsInLineContainingRecommend.length;
				}
				pOfHappy = (pOfHappyGivenBowser + pOfHappyGivenDevice + pOfHappyGivenWords
						+ pOfHappyGivenWordsInRecommendation) / 4;
				pOfUnhappy = (pOfUnhappyGivenBowser + pOfUnhappyGivenDevice + pOfUnhappyGivenWords
						+ pOfUnhappyGivenWordsInRecommendation) / 4;
				if (pOfHappy >= pOfUnhappy) {
					result.put(review.getUserId(), Machine2.HAPPY);
					System.out.println(countForPrintingToConsole + " : " + Machine2.HAPPY + " pOfHappy : " + pOfHappy
							+ " pOfUnhappy : " + pOfUnhappy);
				} else {
					result.put(review.getUserId(), Machine2.NOT_HAPPY);
					System.out.println(countForPrintingToConsole + " : " + Machine2.NOT_HAPPY + " pOfHappy : "
							+ pOfHappy + " pOfUnhappy : " + pOfUnhappy);
				}
			}
			writeResults(result);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void useMachine1() {
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
								} else if(machine.getNoRecommendation().containsKey(wordsInLineContainingRecommend[i])){
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
				StringBuilder overallHapinessIndexString = new StringBuilder();
				// Happiness according to capitalization
				if(SearchUtil.containsCapitals(review.getDescription())){
					overallHapinessIndex += machine.isHappyWhenContainsCapitalization() ? 0 : -1;
					overallHapinessIndexString.append(machine.isHappyWhenContainsCapitalization() ? "0" : "-1");
				}
				/*else{
					overallHapinessIndex += machine.isHappyWhenContainsCapitalization() ? -1 : 0;
					overallHapinessIndexString.append(machine.isHappyWhenContainsCapitalization() ? "-1" : "0");
				}*/
				
				// Happiness according to exclamation
				/*if(SearchUtil.containsExclamation(review.getDescription())){
					overallHapinessIndex += machine.isHappyWhenContainsExclamation() ? 0 : -1;
					overallHapinessIndexString.append(machine.isHappyWhenContainsExclamation() ? "0" : "-1");
				}
				else{
					overallHapinessIndex += machine.isHappyWhenContainsExclamation() ? -1 : 0;
					overallHapinessIndexString.append(machine.isHappyWhenContainsExclamation() ? "-1" : "0");
				}*/
				
				// Happiness according to browser
//				overallHapinessIndex += machine.getBrowserStatistics().get(review.getBrowserUsed()) ? 1 : -1;
//				overallHapinessIndexString.append(machine.getBrowserStatistics().get(review.getBrowserUsed()) ? "1" : "-1");
				// Happiness according to device
//				overallHapinessIndex += machine.getDeviceStatistics().get(review.getDeviceUsed()) ? 1 : -1;
//				overallHapinessIndexString.append(machine.getDeviceStatistics().get(review.getDeviceUsed()) ? "1" : "-1");
				// Happiness predicted by all words in description
				overallHapinessIndex += happinessIndex > 0 ? 1 : -1;
				overallHapinessIndexString.append(happinessIndex > 0 ? "1" : "-1");
				// Happiness predicted by recommendation
				overallHapinessIndex += happinessRecommendationIndex > 0 ? 1 : (happinessRecommendationIndex < 0 ? -1 : 0);
				overallHapinessIndexString.append(happinessRecommendationIndex);
				
				if(overallHapinessIndex >= 0){
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
	private static void writeResults(Map<String, String> result){
		try {
			int happyCustomerCount = 0;
			int unhappyCustomerCount = 0;
			Set<String> resultSet = result.keySet();
			Iterator<String> it = resultSet.iterator();
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File(SUBMISSION)));
			pw.write("User_ID,Is_Response\n");
			while(it.hasNext()){
				String key = it.next();
				pw.write(key + "," + result.get(key) + "\n");
				if(result.get(key).equals(Machine2.NOT_HAPPY)){
					unhappyCustomerCount++;
				}
				else{
					happyCustomerCount++;
				}
			}
			pw.close();
			System.out.println("Final count: HAPPY : " + happyCustomerCount);
			System.out.println("Final count: UN-HAPPY : " + unhappyCustomerCount);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
