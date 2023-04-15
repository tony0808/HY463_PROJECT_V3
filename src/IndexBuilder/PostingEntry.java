package indexbuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class PostingEntry {
	private int tf;
	private HashMap<String, ArrayList<Integer>> wordPositions;
	
	public PostingEntry(HashMap<String, ArrayList<Integer>> wordPositions, int tf) { this.tf = tf; this.wordPositions = wordPositions; }
	public HashMap<String, ArrayList<Integer>> getLabelPositionsMap() { return this.wordPositions; }
	public void addLabelPositions(String labelName, ArrayList<Integer> positions) { this.wordPositions.put(labelName, positions); }
	public void setTf(int tf) { this.tf = tf; } 
	public int getTf() { return this.tf; }
}


















