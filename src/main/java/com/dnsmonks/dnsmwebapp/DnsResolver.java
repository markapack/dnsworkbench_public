package com.dnsmonks.dnsmwebapp;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DnsResolver {
	private static final Logger logger = LogManager.getLogger(DnsResolver.class);

	// TODO: Change this to return an "answer" which includes errors, answers, response times, etc.

	public Map<String, ArrayList<String>> UseDig(String fqdn, String rrtype, String resolver) throws IOException {
		if (!isValidInput(fqdn)) {
			return generateErrorResponse(resolver, "Input validation error");
		}

		String s = null;

		String digCmd = "dig @" + resolver + " +short +time=5 +retry=1 " + fqdn + " " + rrtype;
		logger.info("Query: " + digCmd);

		try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(digCmd).getInputStream()))) {
			return processResponse(stdInput, resolver);
		} catch (IOException e) {
			logger.error("Exception occurred while executing dig command", e);
			throw e;
		}
	}

	private boolean isValidInput(String fqdn) {
		InputValidation validator = new InputValidation();
		return validator.validate(fqdn);
	}

	private Map<String, ArrayList<String>> generateErrorResponse(String resolver, String errorMessage) {
		Map<String, ArrayList<String>> errorResponse = new HashMap<>();
		ArrayList<String> errorMessages = new ArrayList<>();
		errorMessages.add(errorMessage);
		errorResponse.put(resolver, errorMessages);
		return errorResponse;
	}

	private Map<String, ArrayList<String>> processResponse(BufferedReader stdInput, String resolver) throws IOException {
		String line;
		ArrayList<String> responses = new ArrayList<>();
		Map<String, ArrayList<String>> responseMap = new HashMap<>();

		while ((line = stdInput.readLine()) != null) {
			responses.add(line);
			logger.info("Answer: {}", line);

			if (line.contains("connection timed out")) {
				return generateErrorResponse(resolver, "Connection timed out");
			}
		}
		if (responses.isEmpty()) {
			return generateErrorResponse(resolver, "No resolution");
		}

		responseMap.put(resolver, responses);
		return responseMap;
	}
}