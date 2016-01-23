package edu.wayne.cs.severe.ir4se.processor.controllers;

import java.util.List;
import java.util.Map;

import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalStats;
import edu.wayne.cs.severe.ir4se.processor.exception.WritingException;

public interface RetrievalWriter {

	void writeStats(RetrievalStats stats, String statsFilePath) throws WritingException;

	void writeQueryResults(Map<Query, List<RetrievalDoc>> queryRes, String outFilePath) throws WritingException;

}