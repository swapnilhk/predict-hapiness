package com.swapnilhk.competition.ph.model;

public class Statistics {
	private int happyCount;
	private int unhappyCount;
	public int getHappyCount() {
		return happyCount;
	}
	public void incrementHappyCount() {
		this.happyCount += 1;
	}
	public int getUnhappyCount() {
		return unhappyCount;
	}
	public void incrementUnhappyCount() {
		this.unhappyCount += 1;
	}
	public Statistics() {
		this.happyCount = 0;
		this.unhappyCount = 0;
	}
}
