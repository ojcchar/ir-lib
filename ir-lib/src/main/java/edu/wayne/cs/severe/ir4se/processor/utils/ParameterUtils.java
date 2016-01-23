package edu.wayne.cs.severe.ir4se.processor.utils;

import java.io.File;
import java.util.Map;

import seers.appcore.utils.ExceptionUtils;

public class ParameterUtils {

	public static final String FILE_SEP = File.separator;
	public static final String BASE_DIR = "base_dir";
	public static final String SYSTEM = "system";
	public static final String RET_MODEL = "ret_model";
	private static final String NUM_TOPICS = "num_topics";
	private static final String NUM_ITERS = "num_iters";
	public static final String NAME_CONFIG = "name_config";

	public static final String AUTHOR_HISTORY_TXT = "_Authorship_History.txt";
	public static final String AUTHOR_FIRST_TXT = "_Authorship_First.txt";
	public static final String AUTHOR_JAVADOC_TXT = "_Authorship_Javadoc.txt";

	public static String getSystem(Map<String, String> params) {
		String sys = params.get(SYSTEM);
		return sys;
	}

	public static String getCorpFilePath(Map<String, String> params) {
		return getFilePathPrefix(params) + "_Corpus.txt";
	}

	public static String getIndexFolderPath(Map<String, String> params) {
		String baseDir = params.get(BASE_DIR);
		String sys = params.get(SYSTEM);
		return baseDir + FILE_SEP + sys + FILE_SEP + "index";
	}

	public static String getTopicDistrPath(Map<String, String> params) {
		String baseDir = params.get(BASE_DIR);
		String sys = params.get(SYSTEM);
		return baseDir + FILE_SEP + sys + FILE_SEP + "plsa_data";
	}

	public static int getNumberOfTopics(Map<String, String> params) {
		String numOfTopics = params.get(NUM_TOPICS);
		return Integer.valueOf(numOfTopics);
	}

	public static Integer getNumberOfIterations(Map<String, String> params) {
		String numOfIters = params.get(NUM_ITERS);
		return Integer.valueOf(numOfIters);
	}

	public static void setNumberOfIterations(Map<String, String> params, String value) {
		params.put(NUM_ITERS, value);
	}

	public static void setNumberOfTopics(Map<String, String> params, String value) {
		params.put(NUM_TOPICS, value);
	}

	public static String getQueriesFilePath(Map<String, String> params) {
		return getFilePathPrefix(params) + "_Queries.txt";
	}

	public static String getRelJudFilePath(Map<String, String> params) {
		return getFilePathPrefix(params) + "_Queries.txt";
	}

	public static String getStatsFilePath(Map<String, String> params) {
		String baseDir = params.get(BASE_DIR);
		String sys = params.get(SYSTEM);
		String resultFileName = getConfigName(params);
		return baseDir + FILE_SEP + sys + FILE_SEP + resultFileName + "-stats.csv";

		// return baseDir + FILE_SEP + sys + "/E8C4/results.txt";
	}

	public static String getConfigName(Map<String, String> params) {
		return params.get(NAME_CONFIG);
	}

	public static String getRetrievalModel(Map<String, String> params) {
		return params.get(RET_MODEL);
	}

	public static String getDocMapPath(Map<String, String> params) {
		return getFilePathPrefix(params) + "_Mapping.txt";
	}

	public static String getLdaHelperPath(Map<String, String> params) {
		String distrPath = getTopicDistrPath(params);
		return distrPath + FILE_SEP + "ldaHelper.obj";
	}

	public static String getGraphFilePath(Map<String, String> params) {
		return getFilePathPrefix(params) + "_Graph.txt";
	}

	public static String getStackTraceFilePath(Map<String, String> params) {
		return getFilePathPrefix(params) + "_Stack.txt";
	}

	public static String[] getAuthorshipFilePaths(Map<String, String> params) {
		String filePathPrefix = getFilePathPrefix(params);
		String[] filePaths = { filePathPrefix + AUTHOR_HISTORY_TXT, filePathPrefix + AUTHOR_FIRST_TXT,
				filePathPrefix + AUTHOR_JAVADOC_TXT };
		return filePaths;
	}

	public static String getFilePathPrefix(Map<String, String> params) {
		String baseDir = params.get(BASE_DIR);
		String sys = params.get(SYSTEM);
		String filePathPrefix = baseDir + FILE_SEP + sys + FILE_SEP + sys;
		return filePathPrefix;
	}

	public static String getIndexAuthorFolderPath(Map<String, String> params) {
		String baseDir = params.get(BASE_DIR);
		String sys = params.get(SYSTEM);
		return baseDir + FILE_SEP + sys + FILE_SEP + "index_auth";
	}

	public static String getResultsFilePath(Map<String, String> params) {
		String baseDir = params.get(BASE_DIR);
		String sys = params.get(SYSTEM);
		String resultFileName = getConfigName(params);
		return baseDir + FILE_SEP + sys + FILE_SEP + resultFileName + "-results.csv";
	}

	public static String getResultsFilePath(Map<String, String> params, String suffix) {
		String baseDir = params.get(BASE_DIR);
		String sys = params.get(SYSTEM);
		String resultFileName = getConfigName(params);
		return baseDir + FILE_SEP + sys + FILE_SEP + resultFileName + "-" + suffix + ".csv";
	}

	public static boolean getBoolParam(String trueVal, String paramName, Map<String, String> params) {

		String val = params.get(paramName);

		if (trueVal.equalsIgnoreCase(val)) {
			return true;
		} else if (("N" + trueVal).equalsIgnoreCase(val)) {
			return false;
		}

		throw new RuntimeException("Value not valid for param " + paramName + ": " + val);
	}

	public static boolean getBoolParam(String paramName, Map<String, String> params) {
		String val = params.get(paramName);

		if ("Y".equalsIgnoreCase(val)) {
			return true;
		} else if (("N").equalsIgnoreCase(val)) {
			return false;
		}

		throw new RuntimeException("Value not valid for param " + paramName + ": " + val);
	}

	public static double getDoubleParam(String paramName, Map<String, String> params) throws Exception {

		String value = params.get(paramName);
		Double dVal;
		try {
			dVal = Double.valueOf(value);
			if (dVal < 0 || dVal > 1) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException | NullPointerException e) {
			Exception targetExc = new RuntimeException("Value not valid for param " + paramName + ": " + value);
			ExceptionUtils.addStackTrace(e, targetExc);
			throw targetExc;
		}
		return dVal.doubleValue();
	}

	public static <T extends Enum<T>> T getEnumParam(Map<String, String> params, String paramName, Class<T> clEnum)
			throws Exception {

		String value = params.get(paramName);
		T type = null;
		try {
			type = Enum.valueOf(clEnum, value);
		} catch (IllegalArgumentException | NullPointerException e) {
			Exception targetExc = new RuntimeException("Value not valid for param " + paramName + ": " + value);
			ExceptionUtils.addStackTrace(e, targetExc);
			throw targetExc;
		}

		return type;
	}

	public static Integer getIntegerParam(String paramName, Map<String, String> params) throws Exception {
		String value = params.get(paramName);
		Integer val;
		try {
			val = Integer.valueOf(value);
		} catch (NumberFormatException | NullPointerException e) {
			Exception targetExc = new RuntimeException("Value not valid for param " + paramName + ": " + value);
			ExceptionUtils.addStackTrace(e, targetExc);
			throw targetExc;
		}
		return val;
	}

	public static String getQueriesFileInfoPath(Map<String, String> params) {
		return getFilePathPrefix(params) + "_Queries_info.txt";
	}

	public static String getQueriesExcludedFilePath(Map<String, String> params) {
		return getFilePathPrefix(params) + "_Queries_excluded.txt";
	}

}
