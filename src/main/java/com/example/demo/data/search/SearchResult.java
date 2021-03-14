package com.example.demo.data.search;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class SearchResult {
	private List<Map<String,String>> records;
	private int retrieved=0;
	private int available=0;
}
