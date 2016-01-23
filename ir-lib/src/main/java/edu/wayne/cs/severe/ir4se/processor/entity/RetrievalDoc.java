package edu.wayne.cs.severe.ir4se.processor.entity;

import java.util.Comparator;

public class RetrievalDoc {

	private String id;
	private String text;
	private String name;
	private Integer rank;
	private float score;

	public RetrievalDoc() {
	}

	public RetrievalDoc(String docId) {
		super();
		this.id = docId;
	}

	public RetrievalDoc(String docId, String docText, String docName) {
		this.id = docId;
		this.text = docText;
		this.name = docName;
	}

	public String getId() {
		return id;
	}

	public void setId(String docId) {
		this.id = docId;
	}

	public String getText() {
		return text;
	}

	public void setText(String docText) {
		this.text = docText;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		RetrievalDoc other = (RetrievalDoc) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer docRank) {
		this.rank = docRank;
	}

	@Override
	public String toString() {
		return id + " - " + score;
	}

	public String getName() {
		return name;
	}

	public void setName(String docName) {
		this.name = docName;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public float getScore() {
		return this.score;
	}

	public static Comparator<RetrievalDoc> ScoreRetrievalDocComparator = new Comparator<RetrievalDoc>() {

		@Override
		public int compare(RetrievalDoc o1, RetrievalDoc o2) {
			return Float.compare(o2.getScore(), o1.getScore());
		}

	};

}