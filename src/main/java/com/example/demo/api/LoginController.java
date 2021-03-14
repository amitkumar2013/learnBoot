package com.example.demo.api;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.service.MiscService;

import lombok.extern.slf4j.Slf4j;

/**
 * Class shows URI based Access Control.
 */
@Controller
@Slf4j
public class LoginController {

	@Autowired
	@Qualifier("threadPoolExecutor") // Just in case there are variety of pools.
	private Executor executor;
	
	@Autowired
	private MiscService miscSvc;
	
	@RequestMapping(value = "/greet", method = RequestMethod.GET)
	public String showLoginPage(ModelMap model) {
		log.info("SERVING /greet - a new TRACE - Span");
		// @SpanName("my-span") else fallback to toString(...)
		Runnable runnable = () -> {
            log.info("New thread: Same TRACE - new span");
            try {
            	miscSvc.someWorkInNewTracingSpan();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        // Although ExecutorService is preferred for ability to return Future & cancel midway.
        // To-do TraceableExecutorService.
        executor.execute(runnable); 
		model.put("name", "world");
        log.info("Back to original TRACE - Span!");
		return "index";
	}

	@RequestMapping(value = "/about-us", method = RequestMethod.GET)
	public String aboutUsPage(ModelMap model) {
		log.info("In aboutUsPage");
		model.put("msg", "This is a demo spring boot application with Keycloak!");
		return "aboutus";
	}
}
