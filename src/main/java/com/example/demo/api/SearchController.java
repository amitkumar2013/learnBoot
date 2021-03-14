package com.example.demo.api;

import java.security.InvalidParameterException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.data.search.LuceneSearcher;
import com.example.demo.data.search.SearchResult;

import lombok.extern.slf4j.Slf4j;

/**
 * The rest controller is for search.
 * 
 * @author amit.30.kumar
 */
@RestController
@Slf4j
public class SearchController {

	@Autowired
	private LuceneSearcher indexSearcher;

	@Value("${search.records.default}")
	private Integer SEARCH_DEFAULT_RECORDS;

	@Value("${search.records.max}")
	private Integer SEARCH_MAX_RECORDS;

	@GetMapping("/search")
	public SearchResult queryLucene(@RequestParam String query, @RequestParam String countStr) throws Exception {
		log.debug("Search service invoked with " + query);
		if (!query.trim().isEmpty()) {
			int count = SEARCH_DEFAULT_RECORDS;
			boolean showAvailable = false;
			if (countStr != null) {
				try {
					count = Integer.parseInt(countStr);
					count = Math.min(SEARCH_MAX_RECORDS, Math.abs(count));
					log.info("Requested count: " + count);
				} catch (NumberFormatException e) {
					if (!countStr.equalsIgnoreCase("all")) {
						log.warn(countStr + " invalid using default " + SEARCH_DEFAULT_RECORDS);
					} else {
						log.info("Requested all available records");
						showAvailable = true;
					}
				}
			} else {
				log.warn("Requesting default count " + SEARCH_DEFAULT_RECORDS);
			}
			SearchResult results = indexSearcher.searchIndex(query, count, showAvailable);
			log.info("Found " + results.getAvailable() + " and retrieved " + results.getRetrieved() + " records");
			return results;
		} else {
			throw new InvalidParameterException(query);
		}
	}
}
