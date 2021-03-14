package com.example.demo.logging;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MDCFilter implements Filter { // Interface from Java - @Override doFilter(...
//public class MDCFilter extends OncePerRequestFilter { // Class from Spring - @Override doFilterInternal(...

	private static final String MDC_KEY = "requestId";
	private static final String REQUEST_ATTRIBUTE_NAME = "requestId";
	private static final String RESPONSE_HEADER_NAME = "X-Request-Id";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		// Sleuth may be a better option for tracing
		String requestId = UUID.randomUUID().toString();
		req.setAttribute(REQUEST_ATTRIBUTE_NAME, requestId);
		res.setHeader(RESPONSE_HEADER_NAME, requestId);
		// Request - headers, URI, Session all are valid candidates
		MDC.put(MDC_KEY, requestId);
		if (SecurityContextHolder.getContext().getAuthentication() != null) {
			MDC.put("userId", SecurityContextHolder.getContext().getAuthentication().getName());
		}
		MDC.put("RHOST", req.getRemoteAddr());
		MDC.put("LHOST", request.getLocalName());
		// Breadcrumb - to do
		// Masking logs - to do
		try {
			chain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}

	@Bean
	public FilterRegistrationBean<MDCFilter> filter() {
		FilterRegistrationBean<MDCFilter> bean = new FilterRegistrationBean<>();
		bean.setFilter(new MDCFilter());
		bean.addUrlPatterns("/execute/*"); // or use setUrlPatterns()
		return bean;
	}

}