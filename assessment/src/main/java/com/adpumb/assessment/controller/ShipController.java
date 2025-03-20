package com.adpumb.assessment.controller;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adpumb.assessment.service.ShipService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ShipController {
	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ShipService shipService;
	private BlockingQueue<HttpServletRequest> queue = new LinkedBlockingDeque<>(3);

	@RequestMapping(value = "/**")
	public ResponseEntity<?> handleProxyRequest(HttpServletRequest request) {
		try {
			logger.info("Receive {} for url {}", request.getMethod(), request.getRequestURL().toString());
			queue.put(request);
			while (true) {
				return shipService.processRequest(queue.take());
			}
		} catch (Exception e) {
			logger.error("Unable to process Request {}", e);
			return ResponseEntity.internalServerError().build();
		}

	}
}
