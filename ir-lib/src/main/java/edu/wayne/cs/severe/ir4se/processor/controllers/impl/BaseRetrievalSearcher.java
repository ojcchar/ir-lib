package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalSearcher;
import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.exception.SearchException;
import seers.appcore.utils.ExceptionUtils;

public abstract class BaseRetrievalSearcher extends RetrievalSearcher implements Cloneable {

	protected IndexSearcher searcher;
	private IndexReader reader;
	private QueryParser parser;
	private final int resultsNumber;
	private final Analyzer analyzer = new WhitespaceAnalyzer();

	static {
		org.apache.lucene.search.BooleanQuery.setMaxClauseCount(1000000);
	}

	public BaseRetrievalSearcher(String indexPath, int numResults) throws IOException, SearchException {
		String field = "text";

		parser = new QueryParser(field, analyzer);

		File fileIndex = new File(indexPath);

		if (!fileIndex.exists() || !fileIndex.isDirectory()) {
			throw new SearchException("Invalid index path!");
		}

		reader = DirectoryReader.open(FSDirectory.open(fileIndex.toPath()));
		resultsNumber = numResults;
		// reader.numDocs();
		searcher = new IndexSearcher(reader);
		setSimilarity();
	}

	public BaseRetrievalSearcher(Directory indexDir, int numResults) throws IOException, SearchException {
		String field = "text";

		parser = new QueryParser(field, analyzer);

		reader = DirectoryReader.open(indexDir);
		resultsNumber = numResults;
		// reader.numDocs();
		searcher = new IndexSearcher(reader);
		setSimilarity();
	}

	protected abstract void setSimilarity() throws SearchException;

	// public List<RetrievalDoc> searchQuery(org.apache.lucene.search.Query
	// luceneQuery) throws SearchException {
	//
	// List<RetrievalDoc> retrievedDocs = new ArrayList<RetrievalDoc>();
	// try {
	//
	// TopScoreDocCollector collector =
	// TopScoreDocCollector.create(resultsNumber);
	// searcher.search(luceneQuery, collector);
	// ScoreDoc[] hits = collector.topDocs().scoreDocs;
	//
	// for (int i = 0; i < hits.length; i++) {
	//
	// RetrievalDoc doc = new RetrievalDoc();
	// doc.setRank(i + 1);
	// doc.setScore(hits[i].score);
	// doc.setId(reader.document(hits[i].doc).getField("docNo").stringValue());
	// doc.setText(reader.document(hits[i].doc).getField("text").stringValue());
	//
	// retrievedDocs.add(doc);
	// }
	// } catch (IOException | NullPointerException e) {
	// SearchException e2 = new SearchException(e.getMessage());
	// ExceptionUtils.addStackTrace(e, e2);
	// throw e2;
	// }
	//
	// return retrievedDocs;
	// }

	@Override
	public List<RetrievalDoc> searchQuery(Query query) throws SearchException {

		try {

			String txtQuery = query.getTxt();
			txtQuery = QueryParser.escape(txtQuery);

			if (txtQuery == null || txtQuery.trim().isEmpty()) {
				throw new SearchException("The query [" + query.getQueryId() + "] is empty!");
			}

			org.apache.lucene.search.Query luceneQuery = parser.parse(txtQuery);

			return searchQuery(luceneQuery);
		} catch (IOException | ParseException | NullPointerException e) {
			SearchException e2 = new SearchException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		}

	}

	public List<RetrievalDoc> searchQuery(org.apache.lucene.search.Query query) throws IOException {
		List<RetrievalDoc> retrievedDocs = new ArrayList<RetrievalDoc>();

		TopScoreDocCollector collector = TopScoreDocCollector.create(resultsNumber);
		searcher.search(query, collector);

		ScoreDoc[] hits = collector.topDocs().scoreDocs;

		for (int i = 0; i < hits.length; i++) {

			RetrievalDoc doc = new RetrievalDoc();
			doc.setRank(i + 1);
			doc.setScore(hits[i].score);
			doc.setId(reader.document(hits[i].doc).getField("docNo").stringValue());
			doc.setText(reader.document(hits[i].doc).getField("text").stringValue());

			retrievedDocs.add(doc);
		}

		return retrievedDocs;
	}

	public void close() throws IOException {
		reader.close();
	}
}
