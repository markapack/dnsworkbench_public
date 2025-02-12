package com.dnsmonks.dnsmwebapp;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class DisplayOrder {
    private static final Logger logger = LogManager.getLogger(DisplayOrder.class.getName());

    public JSONObject getResolvers() {
        JSONObject resolversobj = new JSONObject();

        // ArrayList<String> resolvers = new ArrayList<>();
        String resolversFile = "/resolvers.json";

        try (InputStreamReader reader = new InputStreamReader(getClass().getResourceAsStream(resolversFile))) {
            JSONParser jsonParser = new JSONParser();
            resolversobj = (JSONObject) jsonParser.parse(reader);
        } catch (Exception ex) {
            logger.error("Error reading resolvers.json", ex);
        }

        // return resolvers;
        return resolversobj;
    }
    public ArrayList<String> getOrder() {
        // Return an array with the order of the resolver IPs by ID
        JSONObject resolvers = getResolvers();
        JSONObject resolverDetails = (JSONObject) resolvers.get("resolvers");

        // Iterator<String> keys = resolverDetails.keySet().iterator();

        JSONObject orderedResolvers = new JSONObject();
        for (Iterator<String> iterator = resolverDetails.keySet().iterator(); iterator.hasNext();) {
            String key = iterator.next();
            JSONObject resolver = (JSONObject) resolverDetails.get(key);
            orderedResolvers.put(resolver.get("id"), key);
        }

        // JSONObject ordertmp = new JSONObject();
        // while(keys.hasNext()) {
            // String key = keys.next();
            // JSONObject r = (JSONObject) resolverDetails.get(key);
            // ordertmp.put(r.get("id"), key);
        // }

        ArrayList<String> finalorder = new ArrayList<>();
        for (int i = 1; i <= orderedResolvers.size(); i++) {
            finalorder.add(orderedResolvers.get("r" + i).toString());
        }

        return finalorder;
    }
}
