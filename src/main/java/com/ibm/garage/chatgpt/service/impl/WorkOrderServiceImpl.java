package com.ibm.garage.chatgpt.service.impl;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ibm.garage.chatgpt.exception.MaximoAdapterException;
import com.ibm.garage.chatgpt.model.WorkOrder;
import com.ibm.garage.chatgpt.model.WorkOrderEntity;
import com.ibm.garage.chatgpt.service.JoltTransformService;
import com.ibm.garage.chatgpt.service.WorkOrderService;

@Service
public class WorkOrderServiceImpl implements WorkOrderService {

	private static final Logger LOGGER = Logger.getLogger(WorkOrderServiceImpl.class.getName());

	private static final String CACHE_PREFIX = "workorder:";
	private static final long CACHE_EXPIRY_IN_SECONDS = 60;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	private MongoTemplate mongoTemplate;

	private String maximoEndpoint = "https://run.mocky.io/v3/02041b0d-abac-4453-b638-494b08d92f51";

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private JoltTransformService joltTransformService;

	@Override
	public ResponseEntity<WorkOrder> fetchWorkOrderById(String workOrderId) {
		String cacheKey = CACHE_PREFIX + workOrderId;
		try {
			String cachedValue = redisTemplate.opsForValue().get(cacheKey);
			if (cachedValue != null) {
				LOGGER.log(Level.INFO, "Work order {0} found in Redis cache", new Object[] { workOrderId });
				WorkOrder workOrder = objectMapper.readValue(cachedValue, WorkOrder.class);
				saveToMongo(workOrder);
				return ResponseEntity.ok(workOrder);
			} else {
				LOGGER.log(Level.INFO, "Work order {0} not found in Redis cache, fetching from Maximo",
						new Object[] { workOrderId });
				String url = maximoEndpoint + "/" + workOrderId;
				WorkOrder workOrder = restTemplate.getForObject(url, WorkOrder.class);
				String workOrderJson = objectMapper.writeValueAsString(workOrder);
				redisTemplate.opsForValue().set(cacheKey, workOrderJson, CACHE_EXPIRY_IN_SECONDS, TimeUnit.SECONDS);
				LOGGER.log(Level.INFO, "Work order {0} fetched from Maximo and cached in Redis",
						new Object[] { workOrderId });
				saveToMongo(workOrder);
				return ResponseEntity.ok(workOrder);
			}
		} catch (RedisConnectionFailureException e) {
			LOGGER.log(Level.SEVERE, "Failed to connect to Redis", e);
			throw new MaximoAdapterException("Failed to connect to Redis", e);
		} catch (DataAccessException e) {
			LOGGER.log(Level.SEVERE, "Failed to access Redis", e);
			throw new MaximoAdapterException("Failed to access Redis", e);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to fetch work order for ID: {0}", new Object[] { workOrderId });
			throw new MaximoAdapterException("Failed to fetch work order for ID: " + workOrderId, e);
		}
	}

	private void saveToMongo(WorkOrder workOrder) {
		try {
			// Jolt Transformation
			String workOrderStr = joltTransformService.transformJson(objectMapper.writeValueAsString(workOrder));
			WorkOrderEntity workOrderEntity = objectMapper.readValue(workOrderStr, WorkOrderEntity.class);
			mongoTemplate.save(workOrderEntity);
			LOGGER.log(Level.SEVERE, "Work order {0} saved to MongoDB", new Object[] { workOrder.getWorkorderid() });
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to save work order {0} to MongoDB",
					new Object[] { workOrder.getWorkorderid() });
			throw new MaximoAdapterException("Failed to save work order to MongoDB", e);
		}
	}

}