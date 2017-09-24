package com.swapnilhk.competition.ph;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.bean.CsvToBeanBuilder;
import com.swapnilhk.competition.ph.model.ProbabiliyEntry;
import com.swapnilhk.competition.ph.model.Review;
import com.swapnilhk.competition.ph.model.Statistics;
import com.swapnilhk.competition.ph.util.Stemmer;
import com.swapnilhk.competition.ph.util.StopWordRemover;

public class Machine2 {
	public static final String NOT_HAPPY = "not happy";
	public static final String HAPPY = "happy";
	public static final String RECOMMEND = "recommend";
	/**
	 * Limit of the number of positive words to be generated
	 * */
	private static final int WORD_LIMIT_POSITIVE = 100;
	/**
	 * Limit of the number of negative words to be generated
	 * */
	private static final int WORD_LIMIT_NEGATIVE = 100;
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
	 * List of words in reviews of happy customers along with their probability
	 * of occurrence
	 */
	private Map<String, Double> pOfWordGivenHappy;
	/**
	 * List of words in reviews of unhappy customers along with their
	 * probability of occurrence
	 */
	private Map<String, Double> pOfWordGivenUnhappy;
	/**
	 * List of words in reviews along with their probability of occurrence
	 */
	private Map<String, Double> pOfWord;
	/**
	 * List of words around the word 'recommend' in the review of of happy
	 * customers along with their probability of occurrence
	 */
	private Map<String, Double> pOfWordInRecommendationGivenHappy;
	/**
	 * List of words around the word 'recommend' in the review of of unhappy
	 * customers along with their probability of occurrence
	 */
	private Map<String, Double> pOfWordInRecommendationGivenUnhappy;
	/**
	 * List of words around the word 'recommend' in the review along with their
	 * probability of occurrence
	 */
	private Map<String, Double> pOfWordInRecommendation;
	/**
	 * The browser used while writing the review by happy customers along with
	 * the probability of it being used
	 */
	private Map<String, Double> pOfBrowserGivenHappy;
	/**
	 * The browser used while writing the review by unhappy customers along with
	 * the probability of it being used
	 */
	private Map<String, Double> pOfBrowserGivenUnhappy;
	/**
	 * The browser used while writing the review along with the probability of
	 * it being used
	 */
	private Map<String, Double> pOfBrowser;
	/**
	 * The device used while writing the review by happy customers along with
	 * the probability of it being used
	 */
	private Map<String, Double> pOfDeviceGivenHappy;
	/**
	 * The device used while writing the review by unhappy customers along with
	 * the probability of it being used
	 */
	private Map<String, Double> pOfDeviceGivenUnhappy;
	/**
	 * The device used while writing the review along with the probability of it
	 * being used
	 */
	private Map<String, Double> pOfDevice;
	/**
	 * Filename for training data-set
	 */
	List<Review> reviewList;
	private Map<String, Statistics> wordStatisticsMap;
	private Map<String, Statistics> recommendStatisticsMap;
	private Map<String, Statistics> browserStatisticsMap;
	private Map<String, Statistics> deviceStatisticsMap;
	private long happyCount;
	private long unhappyCount;
	private double pOfHappy;
	private double pOfUnhappy;
	private long totalNumberOfWords;
	private long totalNumberOfRecommendationWords;

	public Machine2(String dataset) throws IllegalStateException, FileNotFoundException {
		this.pOfWordGivenHappy = new HashMap<String, Double>();
		this.pOfWordGivenUnhappy = new HashMap<String, Double>();
		this.pOfWord = new HashMap<String, Double>();
		this.pOfWordInRecommendationGivenHappy = new HashMap<String, Double>();
		this.pOfWordInRecommendationGivenUnhappy = new HashMap<String, Double>();
		this.pOfWordInRecommendation = new HashMap<String, Double>();
		this.pOfBrowserGivenHappy = new HashMap<String, Double>();
		this.pOfBrowserGivenUnhappy = new HashMap<String, Double>();
		this.pOfBrowser = new HashMap<String, Double>();
		this.pOfDeviceGivenHappy = new HashMap<String, Double>();
		this.pOfDeviceGivenUnhappy = new HashMap<String, Double>();
		this.pOfDevice = new HashMap<String, Double>();
		this.wordStatisticsMap = new HashMap<String, Statistics>();
		this.recommendStatisticsMap = new HashMap<String, Statistics>();
		this.browserStatisticsMap = new HashMap<String, Statistics>();
		this.deviceStatisticsMap = new HashMap<String, Statistics>();
		this.reviewList = new CsvToBeanBuilder<Review>(new FileReader(dataset)).withType(Review.class).build().parse();
		train();
	}

	public Map<String, Double> getpOfWordGivenHappy() {
		return pOfWordGivenHappy;
	}

	public Map<String, Double> getpOfWordGivenUnhappy() {
		return pOfWordGivenUnhappy;
	}

	public Map<String, Double> getpOfWordInRecommendationGivenHappy() {
		return pOfWordInRecommendationGivenHappy;
	}

	public Map<String, Double> getpOfWordInRecommendationGivenUnhappy() {
		return pOfWordInRecommendationGivenUnhappy;
	}

	public Map<String, Double> getpOfBrowserGivenHappy() {
		return pOfBrowserGivenHappy;
	}

	public Map<String, Double> getpOfBrowserGivenUnhappy() {
		return pOfBrowserGivenUnhappy;
	}

	public Map<String, Double> getpOfDeviceGivenHappy() {
		return pOfDeviceGivenHappy;
	}

	public Map<String, Double> getpOfDeviceGivenUnhappy() {
		return pOfDeviceGivenUnhappy;
	}

	public double getpOfHappy() {
		return pOfHappy;
	}

	public double getpOfUnhappy() {
		return pOfUnhappy;
	}

	public Map<String, Double> getpOfWord() {
		return pOfWord;
	}

	public Map<String, Double> getpOfWordInRecommendation() {
		return pOfWordInRecommendation;
	}

	public Map<String, Double> getpOfBrowser() {
		return pOfBrowser;
	}

	public Map<String, Double> getpOfDevice() {
		return pOfDevice;
	}

	public void train() {
		generateStatistics(reviewList, reviewList.size());
		generateProbalilities();
	}

	/**
	 * For a given review, this method will generate various statistics
	 */
	private void generateStatistics(List<Review> reviewList, int reviewListSize) {

		// For printing to the console
		int countForPrintingToConsole = 0;
		
		for (Review review : reviewList) {
			
			if (countForPrintingToConsole % 100 == 0)
				System.out.println(countForPrintingToConsole);
			countForPrintingToConsole++;
			
			// Keep track of number of happy and unhappy customers
			if (review.getIsResponse().equals(NOT_HAPPY)) {
				unhappyCount += 1;
			} else {
				happyCount += 1;
			}

			// Generate browser statistics
			if (!browserStatisticsMap.containsKey(review.getBrowserUsed())) {
				browserStatisticsMap.put(review.getBrowserUsed(), new Statistics());
			}
			if (review.getIsResponse().equals(NOT_HAPPY)) {
				browserStatisticsMap.get(review.getBrowserUsed()).incrementUnhappyCount();
			} else {
				browserStatisticsMap.get(review.getBrowserUsed()).incrementHappyCount();
			}

			// Generate device statistics
			if (!deviceStatisticsMap.containsKey(review.getDeviceUsed())) {
				deviceStatisticsMap.put(review.getDeviceUsed(), new Statistics());
			}
			if (review.getIsResponse().equals(NOT_HAPPY)) {
				deviceStatisticsMap.get(review.getDeviceUsed()).incrementUnhappyCount();
			} else {
				deviceStatisticsMap.get(review.getDeviceUsed()).incrementHappyCount();
			}

			// Remove stop words
			String description = StopWordRemover.removeStopWords(review.getDescription());
			// Stem
			description = Stemmer.stem(description);
			// Check if the review contains the word 'recommend'
			if (description.contains(RECOMMEND)) {
				// If yes. check words in the line containing the word
				Pattern pattern = Pattern.compile("[\\w+ ]*" + RECOMMEND + "[\\w+ ]*");
				Matcher matcher = pattern.matcher(description);
				if (matcher.find()) {
					String lineContainingRecommend = matcher.group();
					String[] wordsInLineContainingRecommend = lineContainingRecommend.split("\\s+");
					totalNumberOfRecommendationWords += wordsInLineContainingRecommend.length;
					if (review.getIsResponse().equals(NOT_HAPPY)) {
						for (int i = 0; i < wordsInLineContainingRecommend.length; i++) {
							if (!recommendStatisticsMap.containsKey(wordsInLineContainingRecommend[i])) {
								recommendStatisticsMap.put(wordsInLineContainingRecommend[i], new Statistics());
							}
							recommendStatisticsMap.get(wordsInLineContainingRecommend[i]).incrementUnhappyCount();
						}
					} else {
						for (int i = 0; i < wordsInLineContainingRecommend.length; i++) {
							if (!recommendStatisticsMap.containsKey(wordsInLineContainingRecommend[i])) {
								recommendStatisticsMap.put(wordsInLineContainingRecommend[i], new Statistics());
							}
							recommendStatisticsMap.get(wordsInLineContainingRecommend[i]).incrementHappyCount();
						}
					}
				}
			}
			// Get word list
			String[] wordArray = description.split("\\s+");
			totalNumberOfWords += wordArray.length;
			// Generate statistics
			if (review.getIsResponse().equals(NOT_HAPPY)) {
				for (int i = 0; i < wordArray.length; i++) {
					if (!wordStatisticsMap.containsKey(wordArray[i])) {
						wordStatisticsMap.put(wordArray[i], new Statistics());
					}
					wordStatisticsMap.get(wordArray[i]).incrementUnhappyCount();
				}
			} else {
				for (int i = 0; i < wordArray.length; i++) {
					if (!wordStatisticsMap.containsKey(wordArray[i])) {
						wordStatisticsMap.put(wordArray[i], new Statistics());
					}
					wordStatisticsMap.get(wordArray[i]).incrementHappyCount();
				}
			}
		}
	}

	private void generateProbalilities() {
		Iterator<String> it;
		Iterator<ProbabiliyEntry> itProb;
		Statistics st;
		Set<String> keySet;
		int count;
		TreeSet<ProbabiliyEntry> pOfWordGivenHappySet = new TreeSet<ProbabiliyEntry>(new ProbablityComparator<ProbabiliyEntry>());
		TreeSet<ProbabiliyEntry> pOfWordGivenUnhappySet = new TreeSet<ProbabiliyEntry>(new ProbablityComparator<ProbabiliyEntry>());
		TreeSet<ProbabiliyEntry> pOfWordSet = new TreeSet<ProbabiliyEntry>(new ProbablityComparator<ProbabiliyEntry>());
		TreeSet<ProbabiliyEntry> pOfWordInRecommendationGivenHappySet = new TreeSet<ProbabiliyEntry>(new ProbablityComparator<ProbabiliyEntry>());
		TreeSet<ProbabiliyEntry> pOfWordInRecommendationGivenUnhappySet = new TreeSet<ProbabiliyEntry>(new ProbablityComparator<ProbabiliyEntry>());
		TreeSet<ProbabiliyEntry> pOfWordInRecommendationSet = new TreeSet<ProbabiliyEntry>(new ProbablityComparator<ProbabiliyEntry>());
		
		// Probability of happiness and unhappiness depending on words
		keySet = wordStatisticsMap.keySet();
		it = keySet.iterator();
		while (it.hasNext()) {
			String word = it.next();
			st = wordStatisticsMap.get(word);
			pOfWordGivenHappySet.add(new ProbabiliyEntry(word, ((double) st.getHappyCount()) / totalNumberOfWords));
			pOfWordGivenUnhappySet.add(new ProbabiliyEntry(word, ((double) st.getUnhappyCount()) / totalNumberOfWords));
			pOfWordSet.add(new ProbabiliyEntry(word, ((double) st.getHappyCount() + st.getUnhappyCount()) / totalNumberOfWords));
		}
		// Take only top values
		itProb = pOfWordGivenHappySet.iterator();
		count = 0;
		while(itProb.hasNext() && count < WORD_LIMIT_POSITIVE){
			ProbabiliyEntry p = itProb.next();
			pOfWordGivenHappy.put(p.getItemName(), p.getProbablity());
			count++;
		}
		itProb = pOfWordGivenUnhappySet.iterator();
		count = 0;
		while(itProb.hasNext() && count < WORD_LIMIT_NEGATIVE){
			ProbabiliyEntry p = itProb.next();
			pOfWordGivenUnhappy.put(p.getItemName(), p.getProbablity());
			count++;
		}
		itProb = pOfWordSet.iterator();
		while(itProb.hasNext()){
			ProbabiliyEntry p = itProb.next();
			if(pOfWordGivenHappy.containsKey(p.getItemName()) || pOfWordGivenUnhappy.containsKey(p.getItemName())){
				pOfWord.put(p.getItemName(), p.getProbablity());
			}
		}
		// Probability of happiness and unhappiness depending on recommendation
		keySet = recommendStatisticsMap.keySet();
		it = keySet.iterator();
		while (it.hasNext()) {
			String word = it.next();
			st = recommendStatisticsMap.get(word);
			pOfWordInRecommendationGivenHappySet.add(new ProbabiliyEntry(word, ((double) st.getHappyCount()) / totalNumberOfRecommendationWords));
			pOfWordInRecommendationGivenUnhappySet.add(new ProbabiliyEntry(word, ((double) st.getUnhappyCount()) / totalNumberOfRecommendationWords));
			pOfWordInRecommendationSet.add(new ProbabiliyEntry(word, ((double) st.getHappyCount()+ st.getUnhappyCount()) / totalNumberOfRecommendationWords));
		}
		// Take only top values
		itProb = pOfWordInRecommendationGivenHappySet.iterator();
		count = 0;
		while(itProb.hasNext() && count < RECOMMENDATION_WORD_LIMIT_POSITIVE){
			ProbabiliyEntry p = itProb.next();
			pOfWordInRecommendationGivenHappy.put(p.getItemName(), p.getProbablity());
			count++;
		}
		itProb = pOfWordInRecommendationGivenUnhappySet.iterator();
		count = 0;
		while(itProb.hasNext() && count < RECOMMENDATION_WORD_LIMIT_NEGATIVE){
			ProbabiliyEntry p = itProb.next();
			pOfWordInRecommendationGivenUnhappy.put(p.getItemName(), p.getProbablity());
			count++;
		}
		itProb = pOfWordInRecommendationSet.iterator();
		while(itProb.hasNext()){
			ProbabiliyEntry p = itProb.next();
			if(pOfWordInRecommendationGivenHappy.containsKey(p.getItemName()) || pOfWordInRecommendationGivenUnhappy.containsKey(p.getItemName())){
				pOfWordInRecommendation.put(p.getItemName(), p.getProbablity());
			}
		}
		// Probability of happiness and unhappiness depending on browser
		keySet = browserStatisticsMap.keySet();
		it = keySet.iterator();
		while (it.hasNext()) {
			String word = it.next();
			st = browserStatisticsMap.get(word);
			pOfBrowserGivenHappy.put(word, ((double) st.getHappyCount()) / happyCount);
			pOfBrowserGivenUnhappy.put(word, ((double) st.getUnhappyCount()) / unhappyCount);
			pOfBrowser.put(word, ((double) st.getUnhappyCount()) / reviewList.size());
		}
		// Probability of happiness and unhappiness depending on device
		keySet = deviceStatisticsMap.keySet();
		it = keySet.iterator();
		while (it.hasNext()) {
			String word = it.next();
			st = deviceStatisticsMap.get(word);
			pOfDeviceGivenHappy.put(word, ((double) st.getHappyCount()) / happyCount);
			pOfDeviceGivenUnhappy.put(word, ((double) st.getUnhappyCount()) / unhappyCount);
			pOfDevice.put(word, ((double) st.getUnhappyCount()) / reviewList.size());
		}
		// Probability of customer being happy
		pOfHappy = ((double)happyCount) / reviewList.size();
		// Probability of customer being unhappy
		pOfUnhappy = ((double)unhappyCount) / reviewList.size();
	}
	
	public static class ProbablityComparator<T extends ProbabiliyEntry> implements Comparator<T> {

		public int compare(T o1, T o2) {
			if (o1.getProbablity() - o2.getProbablity() > 0)
				return -1;
			else if (o1.getProbablity() - o2.getProbablity() < 0)
				return +1;
			else
				return 0;
		}
	}
}
