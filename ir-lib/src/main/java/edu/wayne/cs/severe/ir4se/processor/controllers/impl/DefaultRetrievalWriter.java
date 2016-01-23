package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalStats;

public class DefaultRetrievalWriter extends SummaryStatsRetrievalWriter {

	@Override
	public void writeSummary(FileWriter writer, RetrievalStats stats) throws IOException {

		writer.write("mean rec. rank" + SEMI + stats.getMeanRecipRank() + SEMI);
		writer.write("mean precision" + SEMI + stats.getMeanPrecision() + SEMI);
		writer.write("mean recall" + SEMI + stats.getMeanRecall() + SEMI);
		writer.write("mean f1-score" + SEMI + stats.getMeanF1score() + SEMI);
		writer.write("mean average precision" + SEMI + stats.getMeanAvgPrecision());
		writer.write("\n");
	}

	@Override
	public void writeHeader(FileWriter writer, RetrievalStats stats) throws IOException {

		writer.write("query" + SEMI + "rank" + SEMI + "rec. rank" + SEMI + "true positives" + SEMI + "precision" + SEMI
				+ "false negatives" + SEMI + "recall" + SEMI + "f1 score" + SEMI + "avg precision");
		writer.write("\n");
	}

	@Override
	public void writeQueryStats(FileWriter writer, Query query, List<Double> stats) throws IOException {

		StringBuffer quResult = new StringBuffer(query.getQueryId().toString());
		quResult.append(SEMI + stats.get(0));
		quResult.append(SEMI + stats.get(1));
		quResult.append(SEMI + stats.get(2));
		quResult.append(SEMI + stats.get(3));
		quResult.append(SEMI + stats.get(4));
		quResult.append(SEMI + stats.get(5));
		quResult.append(SEMI + stats.get(6));
		quResult.append(SEMI + stats.get(7));

		writer.write(quResult.toString());
		writer.write("\n");
	}
}