package com.dnsmonks.dnsworkbench;

import com.google.common.util.concurrent.RateLimiter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DnsResolverController {

	private static final Logger logger = LogManager.getLogger(DnsResolverController.class);

	@GetMapping("/dnsresolver")
	@ResponseBody // Prevents Spring from trying to load a template
	public Map<String, ArrayList<String>> dnsresolver(
			@RequestParam String rr,
			@RequestParam String rrtype,
			@RequestParam String resolver, String query, String name, HttpServletRequest request, Model model) {
		model.addAttribute("rr", name);

		// 1 per second rate limiting
		RateLimiter rateLimiter = RateLimiter.create(1);
		long startTime = System.nanoTime();
		rateLimiter.acquire(1);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;

		query = rr;

		logger.debug("query = " + query);

		DnsResolver dnsr = new DnsResolver();

		Map<String, ArrayList<String>> answers = new HashMap<>();

		try {
			answers = dnsr.UseDig(query, rrtype, resolver);
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.debug("answers: " + answers);

		return answers;
	}
}
