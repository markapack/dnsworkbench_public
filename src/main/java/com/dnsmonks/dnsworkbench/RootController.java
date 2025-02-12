package com.dnsmonks.dnsworkbench;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import javax.servlet.http.HttpServletRequest;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Iterator;

@Controller
public class RootController {

	private static final Logger logger = LogManager.getLogger(RootController.class);

	@GetMapping("/")
	public String root(Model model, HttpServletRequest request) {
		logger.debug("===== RootController =====");
		logger.info("Request for / from IP: {}", request.getRemoteAddr());
		logger.info("Request for / from Host: {}", request.getRemoteHost());

		// Note some additional information about the client
		Enumeration<String> headerNames = request.getHeaderNames();
		if (headerNames != null) {
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();
				logger.info("Header: {}: {}", headerName, request.getHeader(headerName));
			}
		}

		DisplayOrder resolverorder = new DisplayOrder();
		model.addAttribute("resolverorder", resolverorder.getOrder());

		JSONObject resolvers = getMapData();
		model.addAttribute("resolvers", resolvers);

		return "query";
	}

	private JSONObject getMapData() {
		JSONArray resolversarr = new JSONArray();
		JSONArray testarray = new JSONArray();

		// InputStreamReader filePath = new InputStreamReader(getClass().getResourceAsStream("/BOOT-INF/classes/resolvers.json"));
		String resolversFile = "/resolvers.json";
		InputStreamReader filePath = new InputStreamReader(getClass().getResourceAsStream(resolversFile));

		JSONParser jsonParser = new JSONParser();
		JSONObject resolversobj = new JSONObject();

		try (InputStreamReader reader = filePath) {
			resolversobj = (JSONObject) jsonParser.parse(reader);
			JSONObject resolversobj2 = (JSONObject) resolversobj.get("resolvers");

			for (Iterator iterator = resolversobj2.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				resolversarr.add(resolversobj2.get(key));
				testarray.add(resolversobj2);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resolversobj;
	}
}
