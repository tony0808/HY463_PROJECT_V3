package indexer;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class PostingList {

	private TreeMap<Integer, TreeMap<Integer, ArrayList<Integer>>> postingList;
	
	public PostingList() { this.postingList = new TreeMap<>(); }
	
	public TreeMap<Integer, TreeMap<Integer, ArrayList<Integer>>> getPostingList() { return this.postingList; }
	
	public boolean hasPositionsList(int docId) { return this.postingList.containsKey(docId); }
	
	public void addNewPostingList(int docId, int labelId, ArrayList<Integer> positions) {
		TreeMap<Integer, ArrayList<Integer>> labelWordPositionsMap = new TreeMap<>();
		labelWordPositionsMap.put(labelId, positions);
		this.postingList.put(docId, labelWordPositionsMap);
	}
	
	public void createNewPositionList(int docId) {
		TreeMap<Integer, ArrayList<Integer>> positionsList = new TreeMap<>();
		this.postingList.put(docId, positionsList);
	}
	
	public void addPositionsList(int docId, int labelId, ArrayList<Integer> positions) {
		this.postingList.get(docId).put(labelId, positions);
	}
	
	public int getTf(int docId) {
		int tf = 0;
		TreeMap<Integer, ArrayList<Integer>> labelWordPositionsMapMap = this.postingList.get(docId);
		for(Map.Entry<Integer, ArrayList<Integer>> labelWordPositionEntry : labelWordPositionsMapMap.entrySet()) {
			tf += labelWordPositionEntry.getValue().size();
		}
		return tf;
	}
	
	public long getSize() {
		long size = 0L;
		for(Integer docid : this.postingList.keySet()) {
			size += Integer.BYTES;
			TreeMap<Integer, ArrayList<Integer>> labelPositions = this.postingList.get(docid);
			for(Integer labelid : labelPositions.keySet()) {
				size += Integer.BYTES;
				ArrayList<Integer> positions = labelPositions.get(labelid);
				size += positions.size() * Integer.BYTES;
			}
		}
		return size;
	}
}
