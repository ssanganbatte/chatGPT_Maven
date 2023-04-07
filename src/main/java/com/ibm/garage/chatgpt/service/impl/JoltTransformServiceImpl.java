package com.ibm.garage.chatgpt.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.ibm.garage.chatgpt.service.JoltTransformService;

@Service
public class JoltTransformServiceImpl implements JoltTransformService {

	@Override
	public String transformJson(String inputJson) {
		try {
			// Load Jolt spec from resource folder
			ClassPathResource resource = new ClassPathResource("jolt-spec.json");
			InputStream inputStream = resource.getInputStream();
			String joltSpecJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

			// Parse Jolt spec as Chainr
			List<Object> joltSpec = JsonUtils.jsonToList(joltSpecJson);
			Chainr chainr = Chainr.fromSpec(joltSpec);

			// Transform input JSON using Jolt
			Object inputJsonObj = JsonUtils.jsonToObject(inputJson);
			Object transformedJsonObj = chainr.transform(inputJsonObj);
			return JsonUtils.toJsonString(transformedJsonObj);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public void transformJson(String inputFilePath, String joltSpecPath, String outputFilePath) throws IOException {
		String inputJson = Files.readString(Path.of(inputFilePath));
		String joltSpecJson = Files.readString(Path.of(joltSpecPath));

		Chainr chainr = Chainr.fromSpec(JsonUtils.jsonToObject(joltSpecJson));
		Object transformedOutput = chainr.transform(JsonUtils.jsonToObject(inputJson));

		String outputJson = JsonUtils.toJsonString(transformedOutput);

		Files.writeString(Path.of(outputFilePath), outputJson);
	}

}
