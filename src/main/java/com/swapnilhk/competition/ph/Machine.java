package com.swapnilhk.competition.ph;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.bean.CsvToBeanBuilder;
import com.swapnilhk.competition.ph.model.Review;
import com.swapnilhk.competition.ph.model.WordStatistics;
import com.swapnilhk.competition.ph.util.SearchUtil;
import com.swapnilhk.competition.ph.util.Stemmer;
import com.swapnilhk.competition.ph.util.StopWordRemover;

public class Machine {
	/**
	 * Limit of the number of positive words to be generated
	 * */
	private static final int WORD_LIMIT_POSITIVE = 250;
	/**
	 * Limit of the number of negative words to be generated
	 * */
	private static final int WORD_LIMIT_NEGATIVE = 8000;
	/**
	 * Limit of the number of positive words to be generated
	 * in the sentence containing the words "recommend"
	 * */
	private static final int RECOMMENDATION_WORD_LIMIT_POSITIVE = 10;
	/**
	 * Limit of the number of negative words to be generated
	 * in the sentence containing the words "recommend"
	 * */
	private static final int RECOMMENDATION_WORD_LIMIT_NEGATIVE = 10;
	/**
	 * List of positive words
	 * */
	private Map<String, Double> positiveWords;
	/**
	 * List of negative words
	 * */
	private Map<String, Double> negativeWords;
	/**
	 * List of words that appear in a sentence containing
	 * the word recommends when the customer is happy 
	 * */
	private Map<String, Double> recommendation;
	/**
	 * List of words that appear in a sentence containing
	 * the word recommends when the customer is NOT happy 
	 * */
	private Map<String, Double> noRecommendation;
	/**
	 * Filename for training data-set
	 * */
	private final String dataset;
	/**
	 * Happy when contains capitalization
	 * */
	private boolean happyWhenContainsCapitalization;
	/**
	 * Happy when contains exclamation
	 * */
	private boolean happyWhenContainsExclamation;
	/**
	 * Browser statistics
	 * */
	private Map<String, Boolean> browserStatistics;
	/**
	 * Device statistics
	 * */
	private Map<String, Boolean> deviceStatistics;
	
	public Machine(String dataset) {
		this.positiveWords = new HashMap<String, Double>();
		this.negativeWords = new HashMap<String, Double>();
		this.recommendation = new HashMap<String, Double>();
		this.noRecommendation = new HashMap<String, Double>();
		this.browserStatistics = new HashMap<String, Boolean>();
		this.deviceStatistics = new HashMap<String, Boolean>();
		this.dataset = dataset;
	}

	public Map<String, Double> getPositiveWords() {
		return positiveWords;
	}

	public void setPositiveWords(Map<String, Double> positiveWords) {
		this.positiveWords = positiveWords;
	}

	public Map<String, Double> getNegativeWords() {
		return negativeWords;
	}

	public void setNegativeWords(Map<String, Double> negativeWords) {
		this.negativeWords = negativeWords;
	}

	public Map<String, Double> getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(Map<String, Double> recommendation) {
		this.recommendation = recommendation;
	}

	public Map<String, Double> getNoRecommendation() {
		return noRecommendation;
	}

	public void setNoRecommendation(Map<String, Double> noRecommendation) {
		this.noRecommendation = noRecommendation;
	}

	public boolean isHappyWhenContainsCapitalization() {
		return happyWhenContainsCapitalization;
	}

	public void setHappyWhenContainsCapitalization(boolean happyWhenContainsCapitalization) {
		this.happyWhenContainsCapitalization = happyWhenContainsCapitalization;
	}

	public boolean isHappyWhenContainsExclamation() {
		return happyWhenContainsExclamation;
	}

	public void setHappyWhenContainsExclamation(boolean happyWhenContainsExclamation) {
		this.happyWhenContainsExclamation = happyWhenContainsExclamation;
	}

	public Map<String, Boolean> getBrowserStatistics() {
		return browserStatistics;
	}

	public void setBrowserStatistics(Map<String, Boolean> browserStatistics) {
		this.browserStatistics = browserStatistics;
	}

	public Map<String, Boolean> getDeviceStatistics() {
		return deviceStatistics;
	}

	public void setDeviceStatistics(Map<String, Boolean> deviceStatistics) {
		this.deviceStatistics = deviceStatistics;
	}

	public void train() {
		try {
			List<Review> reviewList = new CsvToBeanBuilder<Review>(new FileReader(dataset)).withType(Review.class)
					.build().parse();
			generateStatistics(reviewList, reviewList.size());

		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void compileStatistics(Map<String, WordStatistics> wordStatisticsMap, int sampleSize,
			Map<String, Double> positiveResultMap, Map<String, Double> negativeResultMap,
			int positiveWordLimit, int negativeWordLimit) {
		Set<String> keySet = wordStatisticsMap.keySet();
		Iterator<String> it = keySet.iterator();
		Map<String, Double> weightedWordMap = new HashMap<String, Double>();
		// Find weights for all words
		while (it.hasNext()) {
			String key = it.next();
			double weight = calcuateWeight(wordStatisticsMap.get(key), sampleSize);
			weightedWordMap.put(key, weight);
		}
		// Sort the words by their weight
		Set<Entry<String, Double>> weightedWordSet = weightedWordMap.entrySet();
		TreeSet<Entry<String, Double>> sortedWordSetByWeight = new TreeSet(new Comparator<Entry<String, Double>>() {

			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				if (o1.getValue() - o2.getValue() > 0)
					return -1;
				else if (o1.getValue() - o2.getValue() < 0)
					return +1;
				else
					return 0;
			}
		});
		sortedWordSetByWeight.addAll(weightedWordSet);
		// Positive "positiveWordLimit" (number of) words
		int count = 0;
		Iterator<Entry<String, Double>> fwdIt = sortedWordSetByWeight.iterator();
		while (fwdIt.hasNext()) {
			Entry<String, Double> entry = fwdIt.next();
			positiveResultMap.put(entry.getKey(), entry.getValue());
			count++;
			if (count == positiveWordLimit)
				break;
		}
		System.out.println("Positive Words : " + positiveResultMap);

		// Negative "negativeWordLimit" (number of) words
		count = 0;
		Iterator<Entry<String, Double>> bwdIt = sortedWordSetByWeight.descendingIterator();
		while (bwdIt.hasNext()) {
			Entry<String, Double> entry = bwdIt.next();
			negativeResultMap.put(entry.getKey(), entry.getValue());
			count++;
			if (count == negativeWordLimit)
				break;
		}
		System.out.println("Negative Words : " + negativeResultMap);
	}

	private static double calcuateWeight(WordStatistics wordCount, int sampleSize) {
		return ((double) (wordCount.getPositiveCount() - wordCount.getNegativeCount())) / sampleSize;
	}

	/**
	 * At the end of this method, list of positive and negative words will be
	 * finalized
	 */
	private void generateStatistics(List<Review> reviewList, int reviewListSize) {
		Map<String, WordStatistics> wordStatisticsMap = new HashMap<String, WordStatistics>();
		Map<String, WordStatistics> recommendStatisticsMap = new HashMap<String, WordStatistics>();
		Map<Boolean, Integer> capitalizationStatisticsMap = new HashMap<Boolean, Integer>();
		Map<Boolean, Integer> exclamationStatisticsMap = new HashMap<Boolean, Integer>();
		Map<String, Integer> browserStatisticsMap = new HashMap<String, Integer>();
		Map<String, Integer> deviceStatisticsMap = new HashMap<String, Integer>();
		int count = 0;
		capitalizationStatisticsMap.put(true, 0);
		capitalizationStatisticsMap.put(false, 0);
		exclamationStatisticsMap.put(true, 0);
		exclamationStatisticsMap.put(false, 0);
		
		for (Review review : reviewList) {
			if (count % 100 == 0)
				System.out.println(count);
			count++;
			// Generate browser statistics
			if(browserStatisticsMap.containsKey(review.getBrowserUsed())){
				Integer value = browserStatisticsMap.get(review.getBrowserUsed());
				value += review.getIsResponse().equals("not happy") ? -1 : 1;
			}
			else{
				browserStatisticsMap.put(review.getBrowserUsed(), review.getIsResponse().equals("not happy") ? -1 : 1);
			}
			
			// Generate device statistics
			if(deviceStatisticsMap.containsKey(review.getDeviceUsed())){
				Integer value = deviceStatisticsMap.get(review.getDeviceUsed());
				value += review.getIsResponse().equals("not happy") ? -1 : 1;
			}
			else{
				deviceStatisticsMap.put(review.getDeviceUsed(), review.getIsResponse().equals("not happy") ? -1 : 1);
			}
			
			// Generate capitalization statistics
			if(SearchUtil.containsCapitals(review.getDescription())){
				Integer value = capitalizationStatisticsMap.get(review.getIsResponse().equals("happy"));
				value += 1;
			}
			
			// Generate exclamation statistics
			if(SearchUtil.containsExclamation(review.getDescription())){
				Integer value = exclamationStatisticsMap.get(review.getIsResponse().equals("happy"));
				value += 1;
			}
			
			// Remove stop words
			String description = StopWordRemover.removeStopWords(review.getDescription());
			// Stem
			description = Stemmer.stem(description);
			// Check if the review contains the word 'recommend'
			if (description.contains("recommend")) {
				// If yes. check words in the line containing the word
				Pattern pattern = Pattern.compile("[\\w+ ]*recommend[\\w+ ]*");
				Matcher matcher = pattern.matcher(description);
				if (matcher.find()) {
					String lineContainingRecommend = matcher.group();
					String[] wordsInLineContainingRecommend = lineContainingRecommend.split("\\s+");
					if (review.getIsResponse().equals("not happy")) {
						for (int i = 0; i < wordsInLineContainingRecommend.length; i++) {
							if (recommendStatisticsMap.containsKey(wordsInLineContainingRecommend[i])) {
								recommendStatisticsMap.get(wordsInLineContainingRecommend[i]).incrementNegativeCount();
							} else {
								recommendStatisticsMap.put(wordsInLineContainingRecommend[i], new WordStatistics(0, 1));
							}
						}
					} else {
						for (int i = 0; i < wordsInLineContainingRecommend.length; i++) {
							if (recommendStatisticsMap.containsKey(wordsInLineContainingRecommend[i])) {
								recommendStatisticsMap.get(wordsInLineContainingRecommend[i]).incrementPositiveCount();
							} else {
								recommendStatisticsMap.put(wordsInLineContainingRecommend[i], new WordStatistics(1, 0));
							}
						}
					}
				}
			}
			// Get word list
			String[] wordArray = description.split("\\s+");
			// Generate statistics
			if (review.getIsResponse().equals("not happy")) {
				for (int i = 0; i < wordArray.length; i++) {
					if (wordStatisticsMap.containsKey(wordArray[i])) {
						wordStatisticsMap.get(wordArray[i]).incrementNegativeCount();
					} else {
						wordStatisticsMap.put(wordArray[i], new WordStatistics(0, 1));
					}
				}
			} else {
				for (int i = 0; i < wordArray.length; i++) {
					if (wordStatisticsMap.containsKey(wordArray[i])) {
						wordStatisticsMap.get(wordArray[i]).incrementPositiveCount();
					} else {
						wordStatisticsMap.put(wordArray[i], new WordStatistics(1, 0));
					}
				}
			}
		}
		compileStatistics(wordStatisticsMap, reviewListSize,
				positiveWords, negativeWords, WORD_LIMIT_POSITIVE, WORD_LIMIT_NEGATIVE);
		compileStatistics(recommendStatisticsMap, reviewListSize,
				recommendation, noRecommendation, RECOMMENDATION_WORD_LIMIT_POSITIVE, RECOMMENDATION_WORD_LIMIT_NEGATIVE);
		// Compile simple statistics here itself
		happyWhenContainsCapitalization = capitalizationStatisticsMap.get(true) > capitalizationStatisticsMap.get(false);
		happyWhenContainsExclamation = exclamationStatisticsMap.get(true) > exclamationStatisticsMap.get(false);
		Set<String> keySet = browserStatisticsMap.keySet();
		Iterator<String> it = keySet.iterator();
		while(it.hasNext()){
			String key = it.next();
			browserStatistics.put(key, browserStatisticsMap.get(key) > 0);
		}
		keySet = deviceStatisticsMap.keySet();
		it = keySet.iterator();
		while(it.hasNext()){
			String key = it.next();
			deviceStatistics.put(key, deviceStatisticsMap.get(key) > 0);
		}
	}
}
