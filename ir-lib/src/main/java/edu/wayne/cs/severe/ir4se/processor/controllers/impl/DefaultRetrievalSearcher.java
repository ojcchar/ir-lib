package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.io.Closeable;
import java.io.IOException;

import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;

import edu.wayne.cs.severe.ir4se.processor.exception.SearchException;

public class DefaultRetrievalSearcher extends BaseRetrievalSearcher implements Closeable {

	public DefaultRetrievalSearcher(String indexPath) throws IOException, SearchException {
		super(indexPath);
	}

	public DefaultRetrievalSearcher(Directory index) throws IOException, SearchException {
		super(index);
	}

	@Override
	protected void setSimilarity() throws SearchException {
		try {
			Similarity similarity = new ClassicSimilarity();
			searcher.setSimilarity(similarity);
		} catch (NullPointerException | NumberFormatException e) {
			throw new SearchException(e.getMessage());
		}
	}
}
