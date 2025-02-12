package com.dnsmonks.dnsmwebapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

@Controller
public class ContactController {

	private static final Logger logger = LogManager.getLogger(ContactController.class);
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

	@GetMapping("/contact")
    public String contactForm(Model model) {
		model.addAttribute("contact", new Contact());
		return "contact";
	}

	@PostMapping("/contact")
	public String contactSubmit(HttpServletRequest request, Model model) throws IOException {
		SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date date = new Date(System.currentTimeMillis());

		String name = sanitize(request.getParameter("name"));
		String email = sanitize(request.getParameter("email"));
		String message = sanitize(request.getParameter("message"));

		if (!isValidEmail(email)) {
			logger.warn("Invalid email address: {}", email);
			// Return thankyou but don't write to messages
			return "thankyou";
		}

		logRequestDetails(request, name, email, message);

		writeMessageToFile(date, request, name, email, message);

		return "thankyou";
	}

	private String sanitize(String input) {
		return input == null ? "" : input.replaceAll("[\\n\\r\\t]", "_");
	}

	private boolean isValidEmail(String email) {
		return EMAIL_PATTERN.matcher(email).matches();
	}

	private void logRequestDetails(HttpServletRequest request, String name, String email, String message) {
		logger.info("Remote IP: {}", request.getRemoteAddr());
		logger.info("Remote Host: {}", request.getRemoteHost());
		logger.info("Contact Logger - Name: {}", name);
		logger.info("Contact Logger - Email: {}", email);
		logger.info("Contact Logger - message: {}", message);
	}

	private void writeMessageToFile(Date date, HttpServletRequest request, String name, String email, String message) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter("contactMessages.txt", true))) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			writer.append(formatter.format(date)).append("\n");
			writer.append("Remote IP: ").append(request.getRemoteAddr()).append("\n");
			writer.append("Remote Host: ").append(request.getRemoteHost()).append("\n");
			writer.append("Name: ").append(name).append("\n");
			writer.append("Email: ").append(email).append("\n");
			writer.append("Message: ").append(message).append("\n===\n");
		} catch (IOException e) {
			logger.error("Failed to write contact message to file", e);
		}
	}
}
