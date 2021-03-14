package com.example.demo.data.search;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DataIndexer {

	public static final String CONTENTS = "contents";
	public static final String FILE_NAME = "filename";
	public static final String FILE_PATH = "filepath";
	@Value("${lucene.index.location}")
	private String indexLocation;
	String dataDirPath = "./data/raw";

	private IndexWriter writer;

	// create the indexer in separate dir than data
	public void init(boolean recreate) throws IOException {
		Path path = Paths.get(indexLocation);
		if (recreate) {
			final File[] files = path.toFile().listFiles();
			for (File f : files)
				f.delete();
			log.debug("Clean the previous index.");			
		}
		Directory indexDirectory = FSDirectory.open(path);
		IndexWriterConfig conf = new IndexWriterConfig(new StandardAnalyzer());
		writer = new IndexWriter(indexDirectory, conf);
	}

	public int createIndex() throws IOException {
		// get all files in the data directory
		File[] files = new File(dataDirPath).listFiles();
		TextFileFilter filter = new TextFileFilter();
		for (File file : files) {
			if (!file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && filter.accept(file)) {
				writer.addDocument(getDocument(file));
				// update and delete
				// writer.updateDocument(new Term(FILE_NAME, file.getName()), new Document());
				log.debug("Indexing " + file.getCanonicalPath());
			}
		}
		writer.commit();
		return writer.getDocStats().numDocs;
	}

	private Document getDocument(File file) throws IOException {
		Document document = new Document();
		// Text Field is tokenized whereas StringField is not
		document.add(new TextField(CONTENTS, new FileReader(file)));
		document.add(new StringField(FILE_NAME, file.getName(), Field.Store.YES));
		document.add(new StringField(FILE_PATH, file.getCanonicalPath(), Field.Store.YES));
		return document;
	}

	public void close() throws CorruptIndexException, IOException {
		writer.close();
	}
}

class TextFileFilter implements FileFilter {
	@Override
	public boolean accept(File pathname) {
		return pathname.getName().toLowerCase().endsWith(".txt");
	}
}