package com.dnsmonks.dnsworkbench;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.*;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;

@Controller
public class QueryController {

	private static final Logger logger = LogManager.getLogger(QueryController.class);

	@GetMapping("/query")
	public String query(@RequestParam(name="rr", required=false, defaultValue="example.com") String name,
						   @RequestParam(name="rrtype", required = false) String rrtype,
						   Model model,
						   HttpServletRequest request)
			throws IOException, ExecutionException, InterruptedException, TimeoutException {

		logger.debug("===== QUERYCONTROLLER =====");

		logger.info("Remote IP: " + request.getRemoteAddr());
		logger.info("Remote Host: " + request.getRemoteHost());
		DisplayOrder resolverorder = new DisplayOrder();

		model.addAttribute("rr", name);
		model.addAttribute("query", name);
		model.addAttribute("qtype", rrtype);
		model.addAttribute("resolverorder", resolverorder.getOrder());
		DnsResolver dnsr = new DnsResolver();

		ArrayList<String> resolvers = new ArrayList<>();

		String resolversFile = "/resolvers.json";
		InputStreamReader filePath = new InputStreamReader(getClass().getResourceAsStream(resolversFile));

		JSONParser jsonParser = new JSONParser();
		JSONObject resolversobj = new JSONObject();
		try (InputStreamReader reader = filePath) {
		    resolversobj = (JSONObject) jsonParser.parse(reader);
            JSONObject resolversobj2 = (JSONObject) resolversobj.get("resolvers");

            for (Iterator iterator = resolversobj2.keySet().iterator(); iterator.hasNext();) {
            	String key = (String) iterator.next();
            	resolvers.add(key);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// Note some additional information about the client
		Enumeration<String> headerNames = request.getHeaderNames();
		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				logger.info("Header: " + request.getHeader(headerNames.nextElement()));
			}
		}

		Map<String, ArrayList<String>> answers = new LinkedHashMap<String, ArrayList<String>>();

		RateLimiter rateLimiter = RateLimiter.create(1);
		long startTime = System.nanoTime();
		rateLimiter.acquire(1);
		long endTime = System.nanoTime();

		// Duration for debugging
		long duration = (endTime - startTime) / 1000000;

		ExecutorService executor = Executors.newWorkStealingPool();

		String rr = model.getAttribute("rr").toString();

		List<Callable<Map<String, ArrayList<String>>>> queryResponses = new ArrayList<Callable<Map<String, ArrayList<String>>>>();

		for (String resolver: resolvers) {
			queryResponses.add(dnsQuery(rr, rrtype, resolver));
		}

		startTime = System.nanoTime();
		List<Future<Map<String, ArrayList<String>>>> result4 = executor.invokeAll(queryResponses);
		endTime = System.nanoTime();
		duration = (endTime - startTime) / 1000000;

		for (Future<Map<String, ArrayList<String>>> future: result4) {
			Map<String, ArrayList<String>> ans = future.get();
			// TODO fix this:
            String key = "";
			ArrayList<String> value = new ArrayList<>();
			if (ans.isEmpty()) {
				ArrayList<String> nores = new ArrayList<String>();
				nores.add("No Resolution");
				ans.put(ans.entrySet().iterator().next().getKey(), nores);
			}
			else {
				key = ans.entrySet().iterator().next().getKey();
				value = ans.entrySet().iterator().next().getValue();
				ans.put(key, value);
			}
			answers.put(key, value);
		}

		model.addAttribute("rrset2", answers);
		model.addAttribute("resolvers", resolversobj);

		return "query";
	}

	Callable dnsQuery(String rr, String rrtype, String resolver) {
		return () -> {
			DnsResolver dnsr = new DnsResolver();
			return dnsr.UseDig(rr, rrtype, resolver);
		};
	};
}
