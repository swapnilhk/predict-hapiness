package com.swapnilhk.competition.ph.model;

public class ProbabiliyEntry {
	private String itemName;
	private double probablity;
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public double getProbablity() {
		return probablity;
	}
	public void setProbablity(double probablity) {
		this.probablity = probablity;
	}
	public ProbabiliyEntry(String itemName, double probablity) {
		this.itemName = itemName;
		this.probablity = probablity;
	}
}
