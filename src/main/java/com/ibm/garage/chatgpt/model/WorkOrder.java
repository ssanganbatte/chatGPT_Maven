package com.ibm.garage.chatgpt.model;

import java.util.List;

public class WorkOrder {
	private String workorderid;
	private String jpnum;
	private String wostatus;
	private String crewId;
	private List<JobPlan> jobPlan;

	// default constructor
	public WorkOrder() {
	}

	// constructor with all fields
	public WorkOrder(String workorderid, String jpnum, String wostatus, String crewId, List<JobPlan> jobPlan) {
		this.workorderid = workorderid;
		this.jpnum = jpnum;
		this.wostatus = wostatus;
		this.crewId = crewId;
		this.jobPlan = jobPlan;
	}

	// getters and setters
	public String getWorkorderid() {
		return workorderid;
	}

	public void setWorkorderid(String workorderid) {
		this.workorderid = workorderid;
	}

	public String getJpnum() {
		return jpnum;
	}

	public void setJpnum(String jpnum) {
		this.jpnum = jpnum;
	}

	public String getWostatus() {
		return wostatus;
	}

	public void setWostatus(String wostatus) {
		this.wostatus = wostatus;
	}

	public String getCrewId() {
		return crewId;
	}

	public void setCrewId(String crewId) {
		this.crewId = crewId;
	}

	public List<JobPlan> getJobPlan() {
		return jobPlan;
	}

	public void setJobPlan(List<JobPlan> jobPlan) {
		this.jobPlan = jobPlan;
	}
	
}