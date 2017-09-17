package com.swapnilhk.competition.ph.model;

import com.opencsv.bean.CsvBindByName;

public class Review {
	@CsvBindByName(column = "User_ID") String userId;
	@CsvBindByName(column = "Description") String description;
	@CsvBindByName(column = "Browser_Used") String browserUsed;
	@CsvBindByName(column = "Device_Used") String deviceUsed;
	@CsvBindByName(column = "Is_Response")String isResponse;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBrowserUsed() {
		return browserUsed;
	}

	public void setBrowserUsed(String browserUsed) {
		this.browserUsed = browserUsed;
	}

	public String getDeviceUsed() {
		return deviceUsed;
	}

	public void setDeviceUsed(String deviceUsed) {
		this.deviceUsed = deviceUsed;
	}

	public String getIsResponse() {
		return isResponse;
	}

	public void setIsResponse(String isResponse) {
		this.isResponse = isResponse;
	}
}
