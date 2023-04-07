package com.ibm.garage.chatgpt.service;

import org.springframework.http.ResponseEntity;

import com.ibm.garage.chatgpt.model.WorkOrder;

public interface WorkOrderService {

	ResponseEntity<WorkOrder> fetchWorkOrderById(String workorderId);

}