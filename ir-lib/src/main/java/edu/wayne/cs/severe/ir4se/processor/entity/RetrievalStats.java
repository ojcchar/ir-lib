package edu.wayne.cs.severe.ir4se.processor.entity;

import java.util.List;
import java.util.Map;

/*
 */
public class RetrievalStats {

	private Double meanRecipRank;
	private Double meanPrecision;
	private Double meanRecall;
	private Double meanF1score;
	private Double meanAvgPrecision;
	private Map<Query, List<Double>> queryStats;

	public Double getMeanRecipRank() {
		return meanRecipRank;
	}

	public void setMeanRecipRank(Double meanRecipRank) {
		this.meanRecipRank = meanRecipRank;
	}

	public Map<Query, List<Double>> getQueryStats() {
		return queryStats;
	}

	public void setQueryStats(Map<Query, List<Double>> queryStats) {
		this.queryStats = queryStats;
	}

	public Double getMeanPrecision() {
		return meanPrecision;
	}

	public void setMeanPrecision(Double meanPrecision) {
		this.meanPrecision = meanPrecision;
	}

	public Double getMeanRecall() {
		return meanRecall;
	}

	public void setMeanRecall(Double meanRecall) {
		this.meanRecall = meanRecall;
	}

	public Double getMeanF1score() {
		return meanF1score;
	}

	public void setMeanF1score(Double meanF1score) {
		this.meanF1score = meanF1score;
	}

	public Double getMeanAvgPrecision() {
		return meanAvgPrecision;
	}

	public void setMeanAvgPrecision(Double meanAvgPrecision) {
		this.meanAvgPrecision = meanAvgPrecision;
	}

}