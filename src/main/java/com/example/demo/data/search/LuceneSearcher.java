package com.example.demo.data.search;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

/**
 * The class is more of a @Service rather than @Repository used by @SearchController 
 * 
 * @author amit.30.kumar
 */
@Repository
@Slf4j
public class LuceneSearcher {

	private Directory indexDirectory;
	private QueryParser queryParser;
	private boolean initialized = false;

	@Value("${lucene.index.location}")
	private String indexLocation;

	public void init() throws IOException {
		try {
			indexDirectory = FSDirectory.open(Paths.get(indexLocation));

			// Analyzer with specific stop words
			CharArraySet stopWordsOverride = new CharArraySet(Collections.emptySet(), true);
			Analyzer analyzer = new StandardAnalyzer(stopWordsOverride);

			// By default search the contents
			queryParser = new QueryParser(DataIndexer.CONTENTS, analyzer);
			log.info("Connected to Index at: " + indexLocation);

			IndexReader reader = DirectoryReader.open(indexDirectory);
			log.info("Number of docs: " + reader.numDocs());
			if (reader.numDocs() > 0) {
				log.debug("Getting fields for a sample document in the index. . .");
				reader.document(1).getFields();
				List<IndexableField> fields = reader.document(1).getFields();
				for (int i = 0; i < fields.size(); i++) {
					log.debug(i + 1 + ") " + fields.get(i).name() + ":" + fields.get(i).stringValue());
				}
			} else {
				log.warn("Index is empty!!");
			}
			reader.close();
		} catch (IOException ioe) {
			log.error("Could not open Lucene Index at: " + indexLocation + " : " + ioe.getMessage());
			throw new IOException("Could not open Lucene Index at: " + indexLocation + " : " + ioe.getMessage());
		}
	}

	@PreDestroy
	private void close() {
		try {
			indexDirectory.close();
			log.info("Lucene Index closed");
		} catch (IOException ioe) {
			log.warn("Issue closing Lucene Index: " + ioe.getMessage());
		}
	}

	public SearchResult searchIndex(String querystring, int numRecords, boolean showAvailable)
			throws Exception {
		try {
			IndexSearcher indexSearcher = new IndexSearcher(DirectoryReader.open(indexDirectory));
			if(!initialized) {
				init();
			}
			Query query = queryParser.parse(querystring); // Needs to bee initialised as in `init()`
			log.info("'" + querystring + "' ==> '" + query.toString() + "'");

			TotalHitCountCollector collector = null;
			if (showAvailable) {
				collector = new TotalHitCountCollector();
				indexSearcher.search(query, collector);
			}
			// Find documents
			TopDocs documents = indexSearcher.search(query, numRecords); // Sort is optional

			// Name of the document
			List<Map<String, String>> mapList = new LinkedList<Map<String, String>>();
			for (ScoreDoc scoreDoc : documents.scoreDocs) {
				Document document = indexSearcher.doc(scoreDoc.doc);
				Map<String, String> docMap = new HashMap<String, String>();
				List<IndexableField> fields = document.getFields();
				for (IndexableField field : fields) {
					docMap.put(field.name(), field.stringValue());
				}
				mapList.add(docMap);
			}
			SearchResult result = new SearchResult(mapList, mapList.size(),
					collector == null ? (mapList.size() < numRecords ? mapList.size() : -1) : collector.getTotalHits());
			log.debug("Found " + result);
			return result;
		} catch (ParseException pe) {
			throw new InvalidParameterException(pe.getMessage());
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
	}
}
