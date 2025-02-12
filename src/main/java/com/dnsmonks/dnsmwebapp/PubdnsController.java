package com.dnsmonks.dnsmwebapp;

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
		JSONArray testarray = new JSONArray();

		InputStreamReader filePath = new InputStreamReader(getClass().getResourceAsStream("/BOOT-INF/classes/resolvers.json"));

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
