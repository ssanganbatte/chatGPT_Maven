package com.ibm.garage.chatgpt.model;

public class JobPlan {
	private String jobId;
	private String jobLocation;

	// default constructor
	public JobPlan() {
	}

	// constructor with all fields
	public JobPlan(String jobId, String jobLocation) {
		this.jobId = jobId;
		this.jobLocation = jobLocation;
	}

	// getters and setters
	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobLocation() {
		return jobLocation;
	}

	public void setJobLocation(String jobLocation) {
		this.jobLocation = jobLocation;
	}
	
}