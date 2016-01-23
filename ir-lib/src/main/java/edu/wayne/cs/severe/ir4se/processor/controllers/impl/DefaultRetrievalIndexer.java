package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.exception.IndexerException;
import seers.appcore.utils.ExceptionUtils;

public class DefaultRetrievalIndexer implements Closeable {

	private IndexWriter writer;
	private Directory directory;

	public DefaultRetrievalIndexer(String indexPath) throws IOException {
		File indexFile = new File(indexPath);

		if (!indexFile.exists() || !indexFile.isDirectory())
			throw new IOException("The index folder doesn't exist or is not a directory: " + indexPath);

		IndexWriterConfig config = new IndexWriterConfig(new WhitespaceAnalyzer());
		directory = FSDirectory.open(indexFile.toPath());
		writer = new IndexWriter(directory, config);
	}

	public DefaultRetrievalIndexer(Directory directory) throws IOException {
		IndexWriterConfig config = new IndexWriterConfig(new WhitespaceAnalyzer());
		writer = new IndexWriter(directory, config);
	}

	public Directory buildIndex(List<RetrievalDoc> docs) throws IndexerException {

		if (docs == null)
			throw new IndexerException("The document list is missing");

		try {
			for (RetrievalDoc retDoc : docs) {
				Document luceneDoc = getLuceneDocument(retDoc);
				writer.addDocument(luceneDoc);
			}
			return directory;
		} catch (IOException e) {
			IndexerException e2 = new IndexerException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		}
	}

	private Document getLuceneDocument(RetrievalDoc doc) {
		Document luceneDoc = new Document();

		FieldType type = new FieldType();
		type.setStoreTermVectors(true);
		type.setTokenized(true);
		type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		type.setStoreTermVectorPositions(true);
		type.setStored(true);

		luceneDoc.add(new Field("docNo", doc.getId().toString(), type));

		// -----------------------------------

		type = new FieldType();
		type.setStoreTermVectors(true);
		type.setTokenized(true);
		// type.setIndexed(true);
		type.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
		type.setStoreTermVectorPositions(true);
		type.setStored(true);

		String text = doc.getText();
		luceneDoc.add(new Field("text", text, type));

		return luceneDoc;

	}

	public void close() throws IOException {
		writer.close();
	}
}