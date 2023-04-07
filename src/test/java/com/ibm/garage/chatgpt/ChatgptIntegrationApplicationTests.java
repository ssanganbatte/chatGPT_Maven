package com.ibm.garage.chatgpt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;

import com.ibm.garage.chatgpt.controller.WorkOrderController;
import com.ibm.garage.chatgpt.model.WorkOrder;
import com.ibm.garage.chatgpt.service.WorkOrderService;

@SpringBootTest
class ChatgptIntegrationApplicationTests {

	@InjectMocks
	private WorkOrderController workOrderController;

	@Autowired
	private WorkOrderService workOrderService;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Test
	void testGetWorkOrderById_whenDataInRedis() {
		// Mock input data
		String workOrderId = "567013";
		WorkOrder expectedWorkOrder = new WorkOrder("567013", "Some description", "", "", null);
		String redisKey = "workorder:100";

		RedisTemplate<String, String> mockRedisTemplate = mock(RedisTemplate.class);
		ValueOperations<String, String> mockValueOps = mock(ValueOperations.class);
		when(mockRedisTemplate.opsForValue()).thenReturn(mockValueOps);
		
		// Mock Redis cache behavior
		Mockito.when(mockValueOps.get(redisKey)).thenReturn(expectedWorkOrder.toString());

		// Call the controller method
		ResponseEntity<WorkOrder> actualWorkOrder = workOrderService.fetchWorkOrderById(workOrderId);

		// Assert the result
		assertNotNull(actualWorkOrder.getBody());
		assertEquals(expectedWorkOrder.getWorkorderid(), actualWorkOrder.getBody().getWorkorderid());

		// Verify that Redis cache was accessed
//		Mockito.verify(redisTemplate, Mockito.times(1)).opsForValue().get(redisKey);

		// Verify that WorkOrderService was not called
//		Mockito.verify(workOrderService, Mockito.never()).fetchWorkOrderById(workOrderId);
	}

	@Test
	void testGetWorkOrderById_whenDataNotInRedis() {
		// Mock input data
		String workOrderId = "567013";
		WorkOrder expectedWorkOrder = new WorkOrder("567013", "Some description", "", "", null);
		String redisKey = "workorder:100";

		// Mock Redis cache behavior
		Mockito.when(redisTemplate.opsForValue().get(redisKey)).thenReturn(null);

		// Mock WorkOrderService behavior
		Mockito.when(workOrderService.fetchWorkOrderById(workOrderId).getBody()).thenReturn(expectedWorkOrder);

		// Call the controller method
		ResponseEntity<WorkOrder> actualWorkOrder = workOrderController.getWorkOrderById(workOrderId);

		// Assert the result
		assertNotNull(actualWorkOrder.getBody());
		assertEquals(expectedWorkOrder, actualWorkOrder.getBody());

		// Verify that Redis cache was accessed and updated
		Mockito.verify(redisTemplate, Mockito.times(1)).opsForValue().get(redisKey);
		Mockito.verify(redisTemplate, Mockito.times(1)).opsForValue().set(redisKey, expectedWorkOrder.toString());

		// Verify that WorkOrderService was called
		Mockito.verify(workOrderService, Mockito.times(1)).fetchWorkOrderById(workOrderId);

	}

}
