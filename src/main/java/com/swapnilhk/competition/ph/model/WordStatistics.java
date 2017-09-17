package com.swapnilhk.competition.ph.model;

public class WordStatistics {
	private int positiveCount;
	private int negativeCount;
	
	public WordStatistics(int positiveCount, int negativeCount) {
		this.positiveCount = positiveCount;
		this.negativeCount = negativeCount;
	}
	public int getPositiveCount() {
		return positiveCount;
	}
	public void incrementPositiveCount() {
		this.positiveCount++;
	}
	public int getNegativeCount() {
		return negativeCount;
	}
	public void incrementNegativeCount() {
		this.negativeCount++;
	}
}
