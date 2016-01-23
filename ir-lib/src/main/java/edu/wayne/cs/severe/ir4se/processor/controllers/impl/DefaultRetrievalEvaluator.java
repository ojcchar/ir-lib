package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalEvaluator;
import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RelJudgment;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalStats;
import edu.wayne.cs.severe.ir4se.processor.exception.EvaluationException;

public class DefaultRetrievalEvaluator implements RetrievalEvaluator {

	private int corpusSize;

	public DefaultRetrievalEvaluator(int corpusSize) {
		this.corpusSize = corpusSize;
	}

	// FIXME: Refactor this method, it should return a map of metrics
	@Override
	public List<Double> evaluateRelJudgment(RelJudgment relJudgment, List<RetrievalDoc> retrievedDocList)
			throws EvaluationException {

		if (relJudgment == null || retrievedDocList == null) {
			throw new EvaluationException("The parameters cannot be null");
		}

		if (relJudgment != null) {
			if (relJudgment.getRelevantDocs() == null) {
				throw new EvaluationException("The relevant judgements cannot be null");
			}
		}

		List<Double> queryStats = new ArrayList<Double>();
		List<RetrievalDoc> relJudgDocs = relJudgment.getRelevantDocs();
		int idxMiddleNonRetrieved = (corpusSize - retrievedDocList.size()) / 2;
		double rankFirst = retrievedDocList.size() + idxMiddleNonRetrieved;

		List<Integer> possibleRanks = new ArrayList<Integer>();
		// non retrieved ones or negatives
		// List<RetrievalDoc> negatives = getNegatives(corpus,
		// retrievedDocList);

		int numTruePos = 0;
		for (RetrievalDoc relJudgDoc : relJudgDocs) {

			int indexOf = retrievedDocList.indexOf(relJudgDoc);
			if (indexOf != -1) {
				possibleRanks.add(indexOf + 1);
				numTruePos++;
			}
		}

		int numFalseNeg = relJudgDocs.size() - numTruePos;

		Collections.sort(possibleRanks);

		if (!possibleRanks.isEmpty()) {
			rankFirst = possibleRanks.get(0);
		}

		// rank first relevant and retrieved doc
		queryStats.add(rankFirst);
		// reciprocal rank
		// if (rankFirst != Double.MAX_VALUE) {
		queryStats.add(1 / rankFirst);
		// } else {
		// queryStats.add(0.0);
		// }

		// true positives
		queryStats.add((double) numTruePos);
		// precision
		double precision = (retrievedDocList.size() == 0) ? 0.0 : ((double) numTruePos) / retrievedDocList.size();
		queryStats.add(precision);
		// false negatives
		queryStats.add((double) numFalseNeg);
		// recall
		double recall = ((numTruePos + numFalseNeg) == 0) ? 0.0 : ((double) numTruePos) / (numTruePos + numFalseNeg);
		queryStats.add(recall);
		// f1 score
		double f1Score = ((precision + recall) == 0.0) ? 0.0 : 2 * precision * recall / (precision + recall);
		queryStats.add(f1Score);

		// -------------------------------------------
		// average precision

		int numTP = 0;
		double sumPrecs = 0.0;
		for (Integer rk : possibleRanks) {
			numTP++;
			double precAtRk = ((double) numTP) / rk;
			sumPrecs += precAtRk;
		}
		double avgPrec = relJudgDocs.size() == 0.0 ? 0.0 : sumPrecs / relJudgDocs.size();
		queryStats.add(avgPrec);

		// --------------------------------------------------

		return queryStats;
	}

	// private List<RetrievalDoc> getNegatives(List<RetrievalDoc> corpus,
	// List<RetrievalDoc> retrievedDocList) {
	// List<RetrievalDoc> negatives = new ArrayList<>();
	// for (RetrievalDoc corpDoc : corpus) {
	// if (!retrievedDocList.contains(corpDoc)) {
	// negatives.add(corpDoc);
	// }
	// }
	// return negatives;
	// }

	@Override
	public RetrievalStats evaluateModel(Map<Query, List<Double>> queryEvals) throws EvaluationException {

		if (queryEvals == null) {
			throw new EvaluationException("No retrieval evaluation data");
		}

		RetrievalStats stats = new RetrievalStats();
		stats.setQueryStats(queryEvals);

		double sums[] = { 0.0, 0.0, 0.0, 0.0, 0.0 };
		Set<Entry<Query, List<Double>>> entrySet = queryEvals.entrySet();
		for (Entry<Query, List<Double>> entry : entrySet) {
			sums[0] += entry.getValue().get(1);
			sums[1] += entry.getValue().get(3);
			sums[2] += entry.getValue().get(5);
			sums[3] += entry.getValue().get(6);
			sums[4] += entry.getValue().get(7);
		}
		if (!queryEvals.isEmpty()) {
			stats.setMeanRecipRank(sums[0] / queryEvals.size());
			stats.setMeanPrecision(sums[1] / queryEvals.size());
			stats.setMeanRecall(sums[2] / queryEvals.size());
			stats.setMeanF1score(sums[3] / queryEvals.size());
			stats.setMeanAvgPrecision(sums[4] / queryEvals.size());
		} else {
			stats.setMeanRecipRank(0.0);
			stats.setMeanPrecision(0.0);
			stats.setMeanRecall(0.0);
			stats.setMeanF1score(0.0);
			stats.setMeanAvgPrecision(0.0);

		}

		return stats;
	}

	@Override
	public List<RetrievalDoc> filterRelevantDocs(RelJudgment relJudgment, List<RetrievalDoc> retrievedDocList)
			throws EvaluationException {

		if (relJudgment == null || retrievedDocList == null) {
			throw new EvaluationException("The parameters cannot be null");
		}

		if (relJudgment != null) {
			if (relJudgment.getRelevantDocs() == null) {
				throw new EvaluationException("The relevant judgements cannot be null");
			}
		}

		List<RetrievalDoc> filteredList = new ArrayList<RetrievalDoc>();
		List<RetrievalDoc> relJudgDocs = relJudgment.getRelevantDocs();

		for (RetrievalDoc relJudgDoc : relJudgDocs) {

			int indexOf = retrievedDocList.indexOf(relJudgDoc);
			if (indexOf != -1) {
				filteredList.add(retrievedDocList.get(indexOf));
			} else {
				relJudgDoc.setRank(0);
				filteredList.add(relJudgDoc);
				// System.err.println(relJudgDoc.getDocName());
			}
		}

		return filteredList;
	}
}