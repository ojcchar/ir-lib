package edu.wayne.cs.severe.ir4se.processor.controllers;

import java.util.List;

import org.apache.lucene.store.Directory;

import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.exception.IndexerException;

public interface RetrievalIndexer {

	public Directory buildIndex(String indexPath, List<RetrievalDoc> docs) throws IndexerException;

	public Directory buildIndex(List<RetrievalDoc> docs) throws IndexerException;

}