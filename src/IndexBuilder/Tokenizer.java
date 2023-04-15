package indexbuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.HashMap;

public class Tokenizer {
	
	private static final String STOPWORDFILENAME = "C:\\Users\\Admin\\Desktop\\earino_2023\\hy463\\project\\2023-3-EkftonisiResourcesSoftware\\3_Resources_Stoplists\\stopwordsEn.txt";
	private static String[] stopwords = (new FileWordReader(STOPWORDFILENAME)).getWords();
	private static final String WORD_REGEX = "\\b\\w+\\b";
	private String text;
	private HashMap<String, ArrayList<Integer>> wordPositionMap;
	
	public Tokenizer(String text) {  this.text = text; this.wordPositionMap = new HashMap<>(); tokenize(); }
	public HashMap<String, ArrayList<Integer>> get_tokens() { return this.wordPositionMap; }
 	
	private void tokenize() {
		Pattern pattern = Pattern.compile(WORD_REGEX);
		Matcher matcher = pattern.matcher(this.text);
		while(matcher.find()) {
			String word = matcher.group();
			if(isWordStopword(word)) continue;
			int position = matcher.start();
			if(!this.wordPositionMap.containsKey(word)) {
				ArrayList<Integer> positions = new ArrayList<Integer>();
				this.wordPositionMap.put(word, positions); 
			}
			this.wordPositionMap.get(word).add(position);
 		}
	}
	
	private boolean isWordStopword(String word) {
		int start = 0;
		int end = stopwords.length - 1;
		while(start <= end) {
			int mid = (start + end) / 2;
			int cmp = stopwords[mid].compareTo(word);
			if(cmp == 0) return true;
			else if(cmp < 0) start = mid + 1;
			else end = mid - 1;
		}
		return false;
	}
}	
















