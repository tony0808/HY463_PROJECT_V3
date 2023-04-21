package queryevaluator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import indexer.FileWordReader;
import mitos.stemmer.Stemmer;

public class QueryProcessor {
	
	private static final String STOPWORDFILENAME = "C:\\Users\\Admin\\Desktop\\earino_2023\\hy463\\project\\2023-3-EkftonisiResourcesSoftware\\3_Resources_Stoplists\\stopwordsEn.txt";
	private static String[] stopwords = (new FileWordReader(STOPWORDFILENAME)).getWords();
	private static final String WORD_REGEX = "\\b[a-zA-Z_]+\\b";
	
	private String query;
	private String processedQuery;
	private VocabularyLoader vocabLoader;
	
	public QueryProcessor(String query, VocabularyLoader vocabLoader) { 
		this.vocabLoader = vocabLoader;
		this.query = query; processQuery(); 
		Stemmer.Initialize(); 
	}
	public String getProcessedQuery() { return this.processedQuery; }
	
	private void processQuery() {
		Pattern pattern = Pattern.compile(WORD_REGEX);
		Matcher matcher = pattern.matcher(this.query);
		String word;
		StringBuilder sb = new StringBuilder();
		while(matcher.find()) {
			word = matcher.group();
			if(isWordStopword(word)) continue;
			if(this.vocabLoader.getDF(word) == -1) continue;
			word = Stemmer.Stem(word);
			sb.append(word).append(" ");
 		}
		this.processedQuery = sb.toString();
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
