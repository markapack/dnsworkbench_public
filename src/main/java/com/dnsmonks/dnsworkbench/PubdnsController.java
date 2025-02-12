package com.dnsmonks.dnsworkbench;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.io.InputStreamReader;
import java.util.Iterator;

@Controller
public class PubdnsController {

	static final Logger logger = LogManager.getLogger(PubdnsController.class.getName());

	@GetMapping("/pubdns")
	public String pubdns(Model model) {
		JSONObject resolvers = getMapData();
		DisplayOrder resolverorder = new DisplayOrder();

		model.addAttribute("resolvers", resolvers);
		model.addAttribute("resolverorder", resolverorder.getOrder());

		return "pubdns";
	}

	private JSONObject getMapData() {
		JSONArray resolversarr = new JSONArray();

		InputStreamReader filePath = new InputStreamReader(getClass().getResourceAsStream("/BOOT-INF/classes/resolvers.json"));

		JSONParser jsonParser = new JSONParser();
		JSONObject resolvers = new JSONObject();

		try (InputStreamReader reader = filePath) {
			resolvers = (JSONObject) jsonParser.parse(reader);
			JSONObject singleResolver = (JSONObject) resolvers.get("resolvers");

			for (Iterator iterator = singleResolver.keySet().iterator(); iterator.hasNext();) {
				String key = (String) iterator.next();
				resolversarr.add(singleResolver.get(key));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resolvers;
	}
}
