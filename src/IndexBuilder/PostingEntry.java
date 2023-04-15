package indexbuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class PostingEntry {
	private int docId;
	private int tf;
	private HashMap<String, ArrayList<Integer>> wordPositions;
	
	public PostingEntry(int docId, int tf) {
		this.docId = docId;
		this.tf = tf;
		this.wordPositions = new HashMap<String, ArrayList<Integer>>();
	}
	
	public int getDocId() { return this.docId; }
	public int getTf() { return this.tf; }
	public HashMap<String, ArrayList<Integer>> getWordPositions() { return this.wordPositions; }
	
	public void setDocId(int docId) { this.docId = docId; }
	public void setTf(int tf) { this.tf = tf; } 
	public void incrementTf() { this.tf += 1; }
	
	public void addWordPosition(String labelName, int position) {
		if(!this.wordPositions.containsKey(labelName)) { this.wordPositions.put(labelName, new ArrayList<>()); }
		this.wordPositions.get(labelName).add(position);
	}
}


















