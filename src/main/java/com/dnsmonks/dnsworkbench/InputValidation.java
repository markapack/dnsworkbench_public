package com.dnsmonks.dnsworkbench;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InputValidation {
    static final Logger logger = LogManager.getLogger(InputValidation.class.getName());

    // public String validate(String input) {
    public boolean validate(String input) {
        logger.debug("Validating " + input);

        if (input.matches("^[a-zA-Z0-9.-]*$")) {
            return true;
        }
        else {
            return false;
        }
    }
}
