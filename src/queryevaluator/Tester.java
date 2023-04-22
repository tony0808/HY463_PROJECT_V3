package queryevaluator;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Tester {
	
	public static void main(String[] args) throws IOException {
		String targetDirectory = "C:\\CollectionIndex";
		VocabularyLoader voc = new VocabularyLoader(targetDirectory);
		QueryProcessor qp = new QueryProcessor(voc.getVocabulary());
		PostingFileScanner pf = new PostingFileScanner(targetDirectory);
		QueryEvaluator qeval = new QueryEvaluator(targetDirectory);
		String query = "abdhalah abbrevi aacucuggag";
		String[] words = query.split(" ");
		qeval.setQuery(query);
		qeval.evaluateQuery();
		Vectorizer vectorizer = new Vectorizer(voc.getVocabulary());
		vectorizer.setNumDocs(54);
		vectorizer.setWords(words);
		print(vectorizer.getVector());
		DocumentVectorFileScanner obj = new DocumentVectorFileScanner(targetDirectory);
		HashMap<Long, Double> vec = obj.getDocumentVector(6);
		
	}
	
	public static void print(Object obj) {
		System.out.println(obj);
	}
}
