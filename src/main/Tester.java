package main;

import java.io.IOException;
import indexbuilder.Tokenizer;
import java.io.UnsupportedEncodingException;
import indexbuilder.XmlLabelReader;
import indexbuilder.FileWordReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Tester {
	private static final String STOPWORDFILENAME = "C:\\Users\\Admin\\Desktop\\earino_2023\\hy463\\project\\2023-3-EkftonisiResourcesSoftware\\3_Resources_Stoplists\\stopwordsEn.txt";
	private static String[] stopwords = (new FileWordReader(STOPWORDFILENAME)).getWords();
	public static void main(String[] args) throws IOException {
		String documentPath = "C:\\MiniCollection\\diagnosis\\Topic_1\\0\\1852545.nxml";
		XmlLabelReader reader = new XmlLabelReader(documentPath);
		String text = reader.getLabelText(reader.getLabelNames().get(2));
		Tokenizer tokenizer = new Tokenizer(text);
		HashMap<String, ArrayList<Integer>> tokens = tokenizer.get_tokens();
		for(Map.Entry<String, ArrayList<Integer>> token : tokens.entrySet()) {
			System.out.println(token.getKey() + " : " + token.getValue());
		}
		//for(String word : stopwords) {System.out.println(word);}
		
		System.out.println(isWordStopword("the"));
	}
	
	private static boolean isWordStopword(String word) {
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








