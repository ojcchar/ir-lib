package edu.wayne.cs.severe.ir4se.processor.controllers.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.wayne.cs.severe.ir4se.processor.controllers.RetrievalParser;
import edu.wayne.cs.severe.ir4se.processor.entity.Query;
import edu.wayne.cs.severe.ir4se.processor.entity.RelJudgment;
import edu.wayne.cs.severe.ir4se.processor.entity.RetrievalDoc;
import edu.wayne.cs.severe.ir4se.processor.exception.CorpusException;
import edu.wayne.cs.severe.ir4se.processor.exception.QueryException;
import edu.wayne.cs.severe.ir4se.processor.exception.RelJudgException;
import net.quux00.simplecsv.CsvParser;
import net.quux00.simplecsv.CsvParserBuilder;
import net.quux00.simplecsv.CsvReader;
import seers.appcore.utils.ExceptionUtils;

public class DefaultRetrievalParser implements RetrievalParser {

	protected static Logger LOGGER = LoggerFactory.getLogger(DefaultRetrievalParser.class);

	@Override
	public List<RetrievalDoc> readCorpus(String corpFilePath) throws CorpusException {
		List<RetrievalDoc> corpus = new ArrayList<>();

		CsvParser csvParser = new CsvParserBuilder().separator(';').build();
		try (CsvReader csvReader = new CsvReader(new FileReader(corpFilePath), csvParser)) {

			List<List<String>> allLines = csvReader.readAll();

			Integer docId = 0;
			for (List<String> line : allLines) {

				String docName = line.get(0);
				String docText = line.get(1);

				if (docName == null || docName.trim().isEmpty()) {
					LOGGER.warn("Doc " + docId + " has invalid name. Doc discarded.");
				} else if (docText == null || docText.trim().isEmpty()) {
					LOGGER.warn("Doc discarded. It has invalid text: " + docName);
				} else {
					RetrievalDoc doc = new RetrievalDoc(docId.toString(), docText, docName);
					corpus.add(doc);
				}
				docId++;
			}

		} catch (Exception e) {
			CorpusException e2 = new CorpusException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		}

		return corpus;
	}

	@Override
	public List<RetrievalDoc> readCorpus(String corpFilePath, String mapDocsPath) throws CorpusException {
		Integer docId = 0;
		String lineCorpus;
		List<RetrievalDoc> docList = new ArrayList<RetrievalDoc>();

		BufferedReader inCorpus = null;

		try {
			// add every document into the list
			inCorpus = new BufferedReader(new FileReader(corpFilePath));

			while ((lineCorpus = inCorpus.readLine()) != null) {

				String lineTrimmed = lineCorpus.trim();
				if (!lineTrimmed.isEmpty()) {

					RetrievalDoc doc = new RetrievalDoc();
					doc.setId(docId.toString());
					doc.setText(lineTrimmed);

					docList.add(doc);
					docId++;
				}
			}
			if (docId == 0) {
				throw new CorpusException();
			}
		} catch (Exception e) {
			CorpusException e2 = new CorpusException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			try {
				if (inCorpus != null) {
					inCorpus.close();
				}
			} catch (IOException e) {
				CorpusException e2 = new CorpusException(e.getMessage());
				ExceptionUtils.addStackTrace(e, e2);
				throw e2;
			}
		}

		setDocNames(docList, mapDocsPath);

		return docList;
	}

	protected void setDocNames(List<RetrievalDoc> docList, String mapDocsPath) throws CorpusException {
		String line;
		List<String> docNames = new ArrayList<String>();

		BufferedReader inMapping = null;

		try {
			inMapping = new BufferedReader(new FileReader(mapDocsPath));

			while ((line = inMapping.readLine()) != null) {

				String lineTrimmed = line.trim();
				if (!lineTrimmed.isEmpty()) {
					// docNames.add(lineTrimmed);
					docNames.add(getDocQueryStr(lineTrimmed));
				}
			}
		} catch (Exception e) {
			CorpusException e2 = new CorpusException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			try {
				if (inMapping != null) {
					inMapping.close();
				}
			} catch (IOException e) {
				CorpusException e2 = new CorpusException(e.getMessage());
				ExceptionUtils.addStackTrace(e, e2);
				throw e2;
			}
		}

		if (docList.size() != docNames.size()) {
			throw new CorpusException("Number of documents and names do not match!");
		}

		for (int i = 0; i < docList.size(); i++) {
			RetrievalDoc doc = docList.get(i);
			doc.setName(docNames.get(i));
		}

	}

	@Override
	public List<Query> readQueries(String queriesPath) throws QueryException {

		List<Query> queryList = new ArrayList<Query>();
		File fileQuery = new File(queriesPath);

		if (!fileQuery.isFile() || !fileQuery.exists()) {
			throw new QueryException("Query file (" + queriesPath + ") is not valid!");
		}

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileQuery));

			String line;
			int lineNumber = 0;
			Integer queryId = 0;

			Query query = new Query();
			while ((line = reader.readLine()) != null) {

				// it is not a blank line
				String lineTrimmed = line.trim();
				if (!lineTrimmed.isEmpty()) {
					lineNumber++;
					switch (lineNumber) {
					case 1:
						if (lineTrimmed.contains(" ")) {
							queryId = Integer.valueOf(lineTrimmed.split(" ")[0]);
						} else {
							queryId = Integer.valueOf(lineTrimmed);
						}

						query.setQueryId(queryId);
						// queryId++;
						break;
					case 2:
						query.setTxt(lineTrimmed);
						queryList.add(query);
						break;
					}
				} else {
					lineNumber = 0;
					query = new Query();
				}
			}

		} catch (Exception e) {
			QueryException e2 = new QueryException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new QueryException(e.getMessage());
				}
			}
		}
		return queryList;
	}

	@Override
	public List<Query> readQueries(String queriesFilePath, String queriesExcludedFilePath) throws QueryException {
		List<Query> queries = readQueries(queriesFilePath);
		List<Query> queriesExcluded = readExcludedQueries(queriesExcludedFilePath);
		for (Query query : queriesExcluded) {
			queries.remove(query);
		}
		return queries;
	}

	private List<Query> readExcludedQueries(String queriesExcludedFilePath) throws QueryException {

		List<Query> queryList = new ArrayList<Query>();
		File fileQuery = new File(queriesExcludedFilePath);

		if (!fileQuery.isFile() || !fileQuery.exists()) {
			return queryList;
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(fileQuery))) {

			String line;

			while ((line = reader.readLine()) != null) {

				String lineTrimmed = line.trim();
				// it is not a blank line
				if (!lineTrimmed.isEmpty()) {
					Integer queryId = Integer.valueOf(lineTrimmed);
					Query query = new Query(queryId);
					queryList.add(query);
				}
			}

		} catch (Exception e) {
			QueryException e2 = new QueryException(e.getMessage());
			ExceptionUtils.addStackTrace(e, e2);
			throw e2;
		}
		return queryList;
	}

	@Override
	public Map<Query, RelJudgment> readReleJudgments(String releJudgmentPath, String mapDocsPath)
			throws RelJudgException {

		Map<Query, RelJudgment> relJudgMap = new LinkedHashMap<Query, RelJudgment>();
		File fileRelJudg = new File(releJudgmentPath);

		if (!fileRelJudg.isFile() || !fileRelJudg.exists()) {
			throw new RelJudgException("The Relevance Judgments file (" + releJudgmentPath + ") is not valid!");
		}

		File mapDocsFile = new File(mapDocsPath);
		if (!mapDocsFile.isFile() || !mapDocsFile.exists()) {
			throw new RelJudgException("The Mappings file (" + releJudgmentPath + ") is not valid!");
		}

		List<String> mapDocsStr = readDocuments(mapDocsPath);

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileRelJudg));

			String line;
			int lineNumber = 0;
			int numberTargetDocs = -1;
			Integer queryId = 0;

			Query query = new Query();
			List<String> targetDocs = null;
			while ((line = reader.readLine()) != null) {

				// it is not a blank line
				String lineTrimmed = line.trim();
				if (!lineTrimmed.isEmpty()) {
					lineNumber++;
					switch (lineNumber) {
					case 1:
						if (lineTrimmed.contains(" ")) {
							queryId = Integer.valueOf(lineTrimmed.split(" ")[0]);
						} else {
							queryId = Integer.valueOf(lineTrimmed);
						}
						// System.out.println(queryId);
						query.setQueryId(queryId);
						// queryId++;
						break;
					// case 2:
					// query.setTxt(line.trim().toLowerCase());
					// break;
					case 3:
						numberTargetDocs = Integer.parseInt(lineTrimmed);
						targetDocs = new ArrayList<String>(numberTargetDocs);
						break;
					default:
						if (lineNumber >= 4) {
							targetDocs.add(lineNumber - 4, getDocQueryStr(line));
							// System.out.println(getDocQueryStr(line));
						}
						break;
					}
				} else {

					if (targetDocs != null) {
						RelJudgment relJud = new RelJudgment();
						List<RetrievalDoc> relevantDocs = getRelevantDocs(targetDocs, mapDocsStr);
						relJud.setRelevantDocs(relevantDocs);
						relJudgMap.put(query, relJud);
					}

					lineNumber = 0;
					numberTargetDocs = -1;
					query = new Query();
					targetDocs = null;
				}
			}

			if (targetDocs != null) {
				RelJudgment relJud = new RelJudgment();
				List<RetrievalDoc> relevantDocs = getRelevantDocs(targetDocs, mapDocsStr);
				relJud.setRelevantDocs(relevantDocs);
				relJudgMap.put(query, relJud);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RelJudgException(e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RelJudgException(e.getMessage());
				}
			}
		}
		return relJudgMap;
	}

	protected List<RetrievalDoc> getRelevantDocs(List<String> targetDocs, List<String> mapDocsStr)
			throws RelJudgException {

		List<RetrievalDoc> relJudgDocs = new ArrayList<>();

		for (String targetDoc : targetDocs) {
			// System.out.println("Searching " + targetDoc);
			Integer docId = mapDocsStr.indexOf(targetDoc);
			if (docId != -1) {
				RetrievalDoc relJudgDoc = new RetrievalDoc();
				relJudgDoc.setId(docId.toString());
				relJudgDoc.setName(targetDoc);
				relJudgDocs.add(relJudgDoc);
			} else {
				LOGGER.warn("Doc not found in corpus (rel. judg. not considered): " + targetDoc);
			}

		}

		// if (relJudgDocs.isEmpty()) {
		// System.out.println("vacio");
		// // throw new RelJudgException(
		// // "Could not find the relevant judgement documents");
		// }

		return relJudgDocs;
	}

	protected List<String> readDocuments(String mapDocsPath) throws RelJudgException {

		List<String> mapDocsStr = new ArrayList<>();

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(mapDocsPath));

			String line;
			while ((line = reader.readLine()) != null) {

				if (line.trim().isEmpty()) {
					continue;
				}
				// mapDocsStr.add();
				String docStr = getDocQueryStr(line);
				// System.out.println(docStr);
				mapDocsStr.add(docStr);

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RelJudgException(e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RelJudgException(e.getMessage());
				}
			}
		}
		return mapDocsStr;
	}

	protected String getDocQueryStr(String line) {
		String docStr = line.toLowerCase().trim();
		docStr = processPath(docStr);
		docStr = docStr.replaceAll(" ", ".");
		return docStr;
	}

	public static String processPath(String path) {
		String res = path.toLowerCase();
		res = res.replaceAll("\\$", ".");
		res = res.replaceAll("\n", "");
		res = res.replaceAll("\r", " ");
		res = res.replaceAll("\t", " ");
		res = res.replaceAll("/", ".");
		// res = res.replaceAll("::", ".");
		res = res.replaceAll(", ", ",");
		if (res.startsWith("."))
			res = path.replaceFirst("\\.", "");
		return res;
	}

	@Override
	public Map<Query, RelJudgment> readRelevantJudgments(String releJudgmentPath, List<RetrievalDoc> corpusDocs)
			throws RelJudgException {
		Map<Query, RelJudgment> relJudgMap = new LinkedHashMap<Query, RelJudgment>();
		File fileRelJudg = new File(releJudgmentPath);

		if (!fileRelJudg.isFile() || !fileRelJudg.exists()) {
			throw new RelJudgException("The Relevance Judgments file (" + releJudgmentPath + ") is not valid!");
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(fileRelJudg))) {

			String line;
			int lineNumber = 0;
			int numberTargetDocs = -1;
			Integer queryId = 0;

			Query query = new Query();
			List<String> targetDocs = null;
			while ((line = reader.readLine()) != null) {

				// it is not a blank line
				String lineTrimmed = line.trim();
				if (!lineTrimmed.isEmpty()) {
					lineNumber++;
					switch (lineNumber) {
					case 1:
						if (lineTrimmed.contains(" ")) {
							queryId = Integer.valueOf(lineTrimmed.split(" ")[0]);
						} else {
							queryId = Integer.valueOf(lineTrimmed);
						}
						// System.out.println(queryId);
						query.setQueryId(queryId);
						// queryId++;
						break;
					// case 2:
					// query.setTxt(line.trim().toLowerCase());
					// break;
					case 3:
						numberTargetDocs = Integer.parseInt(lineTrimmed);
						targetDocs = new ArrayList<String>(numberTargetDocs);
						break;
					default:
						if (lineNumber >= 4) {
							targetDocs.add(lineNumber - 4, line);
							// System.out.println(getDocQueryStr(line));
						}
						break;
					}
				} else {

					if (targetDocs != null) {
						RelJudgment relJud = new RelJudgment();
						List<RetrievalDoc> relevantDocs = findDocsByName(targetDocs, corpusDocs);
						relJud.setRelevantDocs(relevantDocs);
						relJudgMap.put(query, relJud);
					}

					lineNumber = 0;
					numberTargetDocs = -1;
					query = new Query();
					targetDocs = null;
				}
			}

			if (targetDocs != null) {
				RelJudgment relJud = new RelJudgment();
				List<RetrievalDoc> relevantDocs = findDocsByName(targetDocs, corpusDocs);
				relJud.setRelevantDocs(relevantDocs);
				relJudgMap.put(query, relJud);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RelJudgException(e.getMessage());
		}
		return relJudgMap;
	}

	private List<RetrievalDoc> findDocsByName(List<String> docNames, List<RetrievalDoc> corpusDocs) {
		List<RetrievalDoc> docs = new ArrayList<>();
		for (String docName : docNames) {
			for (RetrievalDoc doc : corpusDocs) {
				if (doc.getName().equals(docName)) {
					docs.add(doc);
					break;
				}
			}
		}

		return docs;
	}
}