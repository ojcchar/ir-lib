package edu.wayne.cs.severe.ir4se.processor.controllers;

import java.util.List;

import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.exception.SearchException;

/*
 */
public abstract class RetrievalSearcher {

	public abstract List<RetrievalDoc> searchQuery(Query query) throws SearchException;

}