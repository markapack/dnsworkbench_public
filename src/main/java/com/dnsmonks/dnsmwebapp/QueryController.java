package com.dnsmonks.dnsmwebapp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.*;
import java.util.concurrent.*;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletRequest;

@Controller
public class QueryController {

	// private static final Logger logger = LogManager.getLogger(QueryController.class.getName());
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

		//model.addAttribute("name", name);
		model.addAttribute("rr", name);
		model.addAttribute("query", name);
		model.addAttribute("qtype", rrtype);
		model.addAttribute("resolverorder", resolverorder.getOrder());
		DnsResolver dnsr = new DnsResolver();

		ArrayList<String> resolvers = new ArrayList<>();

		String resolversFile = "/resolvers.json";
		// InputStreamReader filePath = new InputStreamReader(getClass().getResourceAsStream("/BOOT-INF/classes/resolvers.json"));
		InputStreamReader filePath = new InputStreamReader(getClass().getResourceAsStream(resolversFile));

		// try (FileReader reader = new FileReader(ClassLoader.getSystemResource(filePath).getFile())) {
		JSONParser jsonParser = new JSONParser();
		// JSONArray jsonArray = new JSONArray();
		// JSONObject resolverCities = new JSONObject();
		JSONObject resolversobj = new JSONObject();
		try (InputStreamReader reader = filePath) {
		    resolversobj = (JSONObject) jsonParser.parse(reader);
            JSONObject resolversobj2 = (JSONObject) resolversobj.get("resolvers");

            for (Iterator iterator = resolversobj2.keySet().iterator(); iterator.hasNext();) {
            	String key = (String) iterator.next();
            	resolvers.add(key);
			}

		    logger.debug("resolversobj: " + resolversobj);
		    logger.debug("resoversobj.get(resolvers)" + resolversobj.get("resolvers"));

			// JSONObject resolversobj2 = (JSONObject) resolversobj.get("resolvers");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// try (FileReader reader = new FileReader(filePath)) {
		/*
		try (InputStreamReader reader = filePath) {
			// JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
			jsonArray = (JSONArray) jsonParser.parse(reader);
			// logger.debug("FROM JSONO: " + jsonArray);

			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jo = (JSONObject) jsonArray.get(i);
				logger.debug("jsonArray " + jsonArray);

				// Gotta be a better way to do this.
				// Iterator<JSONObject> keys = jo.keySet().iterator();

				// logger.debug("This?" + jo.keySet().iterator().next().toString());

				// jo: {"8.8.8.8":{"id":"r1","City":"Mountain View, CA","Name":"Google","Location":[38,-122]}}
				// logger.debug("jo: " + jo);

				// logger.debug("i: " + jo.keySet().iterator());
				// logger.debug("FROM JSON: " + jo.get("IP"));
				String r = jo.keySet().iterator().next().toString();
				// logger.debug("r: " + r);
				// logger.debug("FROM JSON: " + r);
				// JSONObject res = (JSONObject) jo.get(r);
				// resolverCities.put(r, res.get("City"));
				// logger.debug("FROM JO: " + res.get("City"));
				// resolvers.add(jo.get("IP").toString());
				// resolvers.add(r);
			}

			// ArrayList resolversArray = (JSONArray) jsonObject.get("resolvers");

			// for (Iterator iterator = jsonObject.keySet().iterator(); iterator.hasNext();) {
				// String key = (String) iterator.next();
				// logger.debug("FROM JSON: " + jsonObject.get(key));
			// }
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		 */




		// Note some additional information about the client
		Enumeration<String> headerNames = request.getHeaderNames();
		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				logger.info("Header: " + request.getHeader(headerNames.nextElement()));
			}
		}

		// Map<String, Map<String, String>> answers = new HashMap<String, Map<String, String>>();
		// Map<String, Map<String, String>> answers = new HashMap<String, Map<String, String>>();
		// Map<String, ArrayList<String>> answers = new HashMap<String, ArrayList<String>>();
		Map<String, ArrayList<String>> answers = new LinkedHashMap<String, ArrayList<String>>();

		RateLimiter rateLimiter = RateLimiter.create(1);
		logger.debug("Acquiring rate limiting permit");
		long startTime = System.nanoTime();
		rateLimiter.acquire(1);
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		logger.debug("Acquired after " + duration + " milliseconds");

		ExecutorService executor = Executors.newWorkStealingPool();

		// Test t = new Test();
		// t.add();

		// List<Callable<String>> callables = Arrays.asList(
				// callable("task1", 2, t),
				// callable("task2", 1, t),
				// callable("task3", 15, t),
				// monitor("montask", t));

				// String result = executor.invokeAny(callables);
		// System.out.println("Here is the result: " + result);
		// String result2 = executor.invokeAny(callables);
		// System.out.println("Here is the result2: " + result2);
		// System.out.println("getStest is " + t.getStest());
		// List<Future<String>> result3 = executor.invokeAll(callables);
		// System.out.println("getStest is " + t.getStest());
		// System.out.println("Here is the result3: " + result3.size());
		// System.out.println("All have finished");

		String rr = model.getAttribute("rr").toString();

		List<Callable<Map<String, ArrayList<String>>>> qcallables2 = new ArrayList<Callable<Map<String, ArrayList<String>>>>();

		for (String resolver: resolvers) {
			qcallables2.add(qcallable(rr, rrtype, resolver));
		}

		startTime = System.nanoTime();
		List<Future<Map<String, ArrayList<String>>>> result4 = executor.invokeAll(qcallables2);
		endTime = System.nanoTime();
		duration = (endTime - startTime) / 1000000;
		logger.debug("Callables took " + duration + " milliseconds");

		for (Future<Map<String, ArrayList<String>>> future: result4) {
			Map<String, ArrayList<String>> ans = future.get();
			// TODO fix this:
            String key = "";
			ArrayList<String> value = new ArrayList<>();
			// logger.debug("HERE: " + ans.entrySet().iterator().next().getValue().size());
			// if (ans.entrySet().iterator().next().getValue().isEmpty()) {
			if (ans.isEmpty()) {
				ArrayList<String> nores = new ArrayList<String>();
				nores.add("No Resolution");
				// logger.info("Answer was empty");
				ans.put(ans.entrySet().iterator().next().getKey(), nores);
				// ans.put(s, nores);
			}
			else {
				key = ans.entrySet().iterator().next().getKey();
				value = ans.entrySet().iterator().next().getValue();
				ans.put(key, value);
			}
			// logger.debug("KEYSET iterator key: " + ans.entrySet().iterator().next().getKey());
			// logger.debug("KEYSET iterator value: " + ans.entrySet().iterator().next().getValue());
			answers.put(key, value);
		}

		/*
		startTime = System.nanoTime();
		for (String s: resolvers) {
			//ArrayList<String> answers = dnsr.UseDig(model.getAttribute("rr").toString(), s);
			// logger.info("INFO: About to try " + s);
			// Map<String, String> ans = dnsr.UseDig(model.getAttribute("rr").toString(), s);
			Map<String, ArrayList<String>> ans = dnsr.UseDig(model.getAttribute("rr").toString(), s);
			//answers.put(s, dnsr.UseDig(model.getAttribute("rr").toString(), s));
			if (ans.isEmpty()) {
				ArrayList<String> nores = new ArrayList<String>();
				nores.add("No Resolution");
				// logger.info("Answer was empty");
				// ans.put(s, "No Resolution");
				ans.put(s, nores);
			}
		}
		endTime = System.nanoTime();
		duration = (endTime - startTime) / 1000000;
		logger.debug("for loop took " + duration + " milliseconds");
		 */

		//String[] rrset = new String[ips.length];
		//for (int i = 0; i < ips.size(); i++) {
		//	model.addAttribute("name" +i, ips[i]);
		//}
		//int count = 0;
		//for (String result: ips) {
		//	model.addAttribute("name" + count, ips);
		//}
		// for (int i =0; i < ips.length; i++) {
		//	model.addAttribute("name" + i, ips[i].getHostAddress());
		//	rrset[i] = ips[i].getHostAddress();
		//}
		// model.addAttribute("rrset", rrset);
		//model.addAttribute("rrset", ips);
		// JSONObject jo = new JSONObject(answers);
		// logger.info("jo keys: " + jo.keys().toString());
		// logger.info("jo keys: " + jo);
		//model.addAttribute("rrset2", map);
		// model.addAttribute("rrset2", jo);
        // logger.debug("answers: " + answers);
		// TODO: Ideally what I'd do here is send 2 dictionaries.
		// TODO: 1) A dictionary of dictionaries { "resolvers": "{"8.8.8.8": {"City": "Mountain View", "Name": "Google"}} }
		// TODO: 2) A dictionary of answers { "8.8.8.8": [1.2.3.4, 2.3.4.5] }
		model.addAttribute("rrset2", answers);
		// model.addAttribute("resolvers", jsonArray);
		// model.addAttribute("resolverCities", resolverCities);
		model.addAttribute("resolvers", resolversobj);

		logger.debug("This is what I'm passing:");
		logger.debug("rrset2: " + answers);
		// logger.debug("resolvers: " + jsonArray);
		// logger.debug("resolverCities: " + resolverCities);
		// logger.debug("resolversobj: " + resolversobj);

		return "query";
	}

	Callable<String> callable(String result, long sleepSeconds, Test t) {
		return () -> {
			TimeUnit.SECONDS.sleep(sleepSeconds);
			t.add(result);
			System.out.println(result + " is about to return");
			return result;
		};
	}

	Callable<String> monitor(String result, Test t) throws InterruptedException {
	    return () -> {
			while (t.getStest().size() < 6) {
				// System.out.println("t is " + t.getStest().size());
				TimeUnit.SECONDS.sleep(1);
			}
			// System.out.println("monitor result: " + result);
			t.add();
			// System.out.println(test());
			return result;
		};
	}

	Callable qcallable(String rr, String rrtype, String resolver) {
		return () -> {
			DnsResolver dnsr = new DnsResolver();
			// System.out.println("rr is " + rr);
			// System.out.println("resolver is " + resolver);
			return dnsr.UseDig(rr, rrtype, resolver);
		};
	};

	class Test {
		List<String> stest = new ArrayList<>();

		void add() {
			stest.add("a");
			stest.add("b");
			stest.add("c");

		}

		void add(String s) {
			stest.add(s);
		}

		List<String> getStest() {
			return stest;
		}
	}
}
