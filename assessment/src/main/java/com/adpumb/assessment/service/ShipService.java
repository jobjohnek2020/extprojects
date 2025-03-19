package com.adpumb.assessment.service;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ShipService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ResponseEntity<?> processRequest(HttpServletRequest request) {

        try {
            List<String> httpMethodList = List.of("POST", "PUT", "GET", "PATCH");
            if (Optional.of(httpMethodList).stream().anyMatch(i -> !i.contains(request.getMethod().toUpperCase()))) {
                return ResponseEntity.status(405).build();
            }
            HttpHeaders headers = new HttpHeaders();
            Enumeration<String> headerNameEnum = request.getHeaderNames();
            while (headerNameEnum.hasMoreElements()) {
                String headerName = headerNameEnum.nextElement();
                String headerValue = request.getHeader(headerName);
                headers.add(headerName, headerValue);
            }

            Enumeration<String> parameterNameEnum = request.getParameterNames();
            Map<String, Object> querys = new HashMap<>();

            while (parameterNameEnum.hasMoreElements()) {
                String parameterName = parameterNameEnum.nextElement();
                String parameterValue = request.getParameter(parameterName);
                querys.put(parameterName, parameterValue);
            }

            HttpEntity<?> entity = new HttpEntity<>(headers);

            return new RestTemplate().exchange(request.getRequestURL().toString(),
                    HttpMethod.valueOf(request.getMethod()),
                    entity, byte[].class, querys);

        } catch (Exception e) {
            logger.error("Error processing request {}",
                    request.getRequestURL().toString(), e);
            return ResponseEntity.internalServerError().build();
        }

    }

}
