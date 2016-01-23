package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalWriter;
import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalStats;
import edu.wayne.cs.severe.ir4se.processor.exception.WritingException;
import seers.appcore.utils.ExceptionUtils;

public abstract class SummaryStatsRetrievalWriter implements RetrievalWriter {

	public static final String SEMI = ";";

	@Override
	public void writeStats(RetrievalStats stats, String statsFilePath) throws WritingException {

		FileWriter writer = null;
		try {

			File stFile = new File(statsFilePath);
			if (stFile.isDirectory()) {
				throw new RuntimeException("Output file invalid: " + statsFilePath);
			}

			if (stats == null) {
				throw new RuntimeException("No statistics to write");
			}

			writer = new FileWriter(statsFilePath);

			// ---------------------------------------------------

			writeSummary(writer, stats);

			writeHeader(writer, stats);

			// ---------------------------------------------------

			Map<Query, List<Double>> queryStats = stats.getQueryStats();
			Set<Entry<Query, List<Double>>> qStatsSet = queryStats.entrySet();

			for (Entry<Query, List<Double>> entry : qStatsSet) {

				writeQueryStats(writer, entry.getKey(), entry.getValue());

			}

		} catch (Exception e) {
			WritingException e2 = new WritingException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new WritingException(e.getMessage());
				}
			}
		}

	}

	public abstract void writeSummary(FileWriter writer, RetrievalStats stats) throws IOException;

	public abstract void writeHeader(FileWriter writer, RetrievalStats stats) throws IOException;

	public abstract void writeQueryStats(FileWriter writer, Query key, List<Double> value) throws IOException;

	@Override
	public void writeQueryResults(Map<Query, List<RetrievalDoc>> queryRes, String outFilePath) throws WritingException {

		FileWriter writer = null;
		try {

			File stFile = new File(outFilePath);
			if (stFile.isDirectory()) {
				throw new RuntimeException("Output file invalid: " + outFilePath);
			}

			writer = new FileWriter(outFilePath);

			// ---------------------------------------------------

			writer.write("query id" + SEMI + "doc id" + SEMI + "rank" + SEMI + "score" + SEMI + "doc name");
			writer.write("\n");

			// ---------------------------------------------------

			Set<Entry<Query, List<RetrievalDoc>>> qStatsSet = queryRes.entrySet();

			for (Entry<Query, List<RetrievalDoc>> entry : qStatsSet) {

				List<RetrievalDoc> docs = entry.getValue();
				for (RetrievalDoc doc : docs) {

					StringBuffer quResult = new StringBuffer(entry.getKey().getQueryId().toString());
					quResult.append(SEMI + doc.getId());
					quResult.append(SEMI + doc.getRank());
					quResult.append(SEMI + doc.getScore());
					quResult.append(SEMI + doc.getName());

					writer.write(quResult.toString());
					writer.write("\n");

				}

			}

		} catch (Exception e) {
			WritingException e2 = new WritingException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					throw new WritingException(e.getMessage());
				}
			}
		}

	}
}