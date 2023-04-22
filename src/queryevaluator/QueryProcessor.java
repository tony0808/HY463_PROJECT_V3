package queryevaluator;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import indexer.FileWordReader;
import mitos.stemmer.Stemmer;

public class QueryProcessor {
	private static final String STOPWORDFILENAME = "C:\\Users\\Admin\\Desktop\\earino_2023\\hy463\\project\\2023-3-EkftonisiResourcesSoftware\\3_Resources_Stoplists\\stopwordsEn.txt";
	private static String[] stopwords = (new FileWordReader(STOPWORDFILENAME)).getWords();
	private static final String WORD_REGEX = "\\b[a-zA-Z_]+\\b";
	
	private HashMap<String, VocabData> vocabulary;
	private String query;
	
	public QueryProcessor(HashMap<String, VocabData> vocabulary) { Stemmer.Initialize(); this.vocabulary = vocabulary; }
	public void setQuery(String query) { this.query = query; }
	
	public String getProcessedQuery() {
		Pattern pattern = Pattern.compile(WORD_REGEX);
		Matcher matcher = pattern.matcher(this.query);
		String word;
		StringBuilder sb = new StringBuilder();
		while(matcher.find()) {
			word = matcher.group();
			if(isWordStopword(word)) continue;
			if(!this.vocabulary.containsKey(word)) continue;
			word = Stemmer.Stem(word);
			sb.append(word).append(" ");
 		}
		return sb.toString();
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
