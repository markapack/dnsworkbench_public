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
		// logger.info("Request for /dnsresolver from IP: " + request.getRemoteAddr());
		// logger.info("Request for /dnsresolver from Host: " + request.getRemoteHost());

		// JSONObject resolverCities = getMapCities(resolvers);

		// model.addAttribute("resolvers", resolvers);
		// model.addAttribute("resolverCities", resolverCities);
		logger.debug("name right before addAttribute: " + name);
		logger.debug("rr right before addAttribute: " + rr);
		model.addAttribute("rr", name);
		logger.debug("rr right after addAttribute: " + rr);

		logger.debug("name right after addAttribute: " + name);
		logger.debug("rr is: " + rr);
		logger.debug("resolver is: " + resolver);
		logger.debug("rrtype is: " + rrtype);

		// 1 per second rate limiting
		RateLimiter rateLimiter = RateLimiter.create(1);
		logger.debug("Acquiring rate limiting permit");
		long startTime = System.nanoTime();
		rateLimiter.acquire(1);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		logger.debug("Acquired after " + duration + " milliseconds");

		// query = model.getAttribute("rr").toString();
		query = rr;

		logger.debug("query = " + query);

		DnsResolver dnsr = new DnsResolver();
		// System.out.println("rr is " + rr);
		// System.out.println("resolver is " + resolver);

		Map<String, ArrayList<String>> answers = new HashMap<>();
		ArrayList<String> rrset = new ArrayList<>();

		try {
			answers = dnsr.UseDig(query, rrtype, resolver);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// rrset.add("1.2.3.4");
		// rrset.add("2.3.4.5");

		// answers.put("8.8.8.8", rrset);

		logger.debug("answers: " + answers);

		return answers;
	}
}
