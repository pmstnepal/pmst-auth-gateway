package com.pmst.gateway.controller;

import com.pmst.gateway.config.GatewayConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Enumeration;

@RestController
@RequestMapping("/api")
public class ProxyController {

    @Autowired
    private GatewayConfig gatewayConfig;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/**")
    public ResponseEntity<?> proxyRequest(HttpServletRequest request) throws IOException {
        String backendUrl = gatewayConfig.getBackendUrl();
        String path = request.getRequestURI();
        String queryString = request.getQueryString();

        String targetUrl = backendUrl + path + (queryString != null ? "?" + queryString : "");

        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            // Skip certain headers that should be regenerated
            if (!headerName.equalsIgnoreCase("host") && 
                !headerName.equalsIgnoreCase("content-length")) {
                headers.add(headerName, headerValue);
            }
        }

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<?> response = restTemplate.exchange(
                targetUrl,
                method,
                entity,
                Object.class
        );

        return ResponseEntity
                .status(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody());
    }
}
