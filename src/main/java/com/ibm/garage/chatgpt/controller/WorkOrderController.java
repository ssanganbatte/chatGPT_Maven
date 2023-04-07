package com.ibm.garage.chatgpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.garage.chatgpt.model.WorkOrder;
import com.ibm.garage.chatgpt.service.JoltTransformService;
import com.ibm.garage.chatgpt.service.WorkOrderService;

@RestController
@RequestMapping("/api")
public class WorkOrderController {

	@Autowired
	private WorkOrderService workOrderService;

	@Autowired
	private JoltTransformService joltTransformService;

	@GetMapping("/workorder/{id}")
	public ResponseEntity<WorkOrder> getWorkOrderById(@PathVariable String id) {
		return workOrderService.fetchWorkOrderById(id);
	}

	@PostMapping("/transform")
	public String transform(@RequestBody String inputJson) {
		return joltTransformService.transformJson(inputJson);
	}

}