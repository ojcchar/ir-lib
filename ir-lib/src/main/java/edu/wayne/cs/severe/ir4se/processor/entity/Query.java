package edu.wayne.cs.severe.ir4se.processor.entity;

import java.util.LinkedHashMap;
import java.util.Map;

public class Query {

	public static final String ISSUE_ID = "ISSUE_ID";

	private Integer queryId;
	private String txt;
	private Map<String, Object> info = new LinkedHashMap<>();

	public Query() {
	}

	public Query(int qId) {
		this.queryId = qId;
	}

	public Query(int qId, String txt) {
		this.queryId = qId;
		this.txt = txt;
	}

	public Integer getQueryId() {
		return queryId;
	}

	public void setQueryId(Integer queryId) {
		this.queryId = queryId;
	}

	public String getTxt() {
		return txt;
	}

	public void setTxt(String txt) {
		this.txt = txt;
	}

	public void addInfoAttribute(String key, Object value) {
		info.put(key, value);
	}

	public Object getInfoAttribute(String key) {
		return info.get(key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((queryId == null) ? 0 : queryId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Query other = (Query) obj;
		if (queryId == null) {
			if (other.queryId != null)
				return false;
		} else if (!queryId.equals(other.queryId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Query [queryId=" + queryId + ", txt=" + txt + "]";
	}

	public void copyInfo(Query query) {
		this.info = query.info;
	}

}