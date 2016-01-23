package edu.wayne.cs.severe.ir4se.processor.controllers;

import java.util.List;

import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;

public abstract class RetrievalProcessor {

	public abstract void processSystem(String filepath) throws Exception;

	protected void setNamesResults(List<RetrievalDoc> results, List<RetrievalDoc> docs) {

		for (RetrievalDoc res : results) {
			int indexOf = docs.indexOf(res);
			res.setName(docs.get(indexOf).getName());
		}

	}

}