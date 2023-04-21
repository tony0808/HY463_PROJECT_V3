package queryevaluator;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import indexer.IndexBuilder;

public class Main {
	
	public static void main(String[] args) throws IOException {
		String parentDirectory = "C:\\CollectionIndex";
		String query = "A 2-year-old boy is brought to the emergency department by his parents for 5 days of high fever and irritability. The physical exam reveals conjunctivitis, strawberry tongue, inflammation of the hands and feet, desquamation of the skin of the fingers and toes, and cervical lymphadenopathy with the smallest node at 1.5 cm. The abdominal exam demonstrates tenderness and enlarged liver. Laboratory tests report elevated alanine aminotransferase, white blood cell count of 17,580/mm, albumin 2.1 g/dL, C-reactive protein 4.5 mg, erythrocyte sedimentation rate 60 mm/h, mild normochromic, normocytic anemia, and leukocytes in urine of 20/mL with no bacteria identified. The echocardiogram shows moderate dilation of the coronary arteries with possible coronary artery aneurysm.";
		QueryEvaluator qeval = new QueryEvaluator(query, parentDirectory);
		qeval.setQueryScores();
		qeval.printQueryScores();
	}
	
	public static void print(Object obj) {
		System.out.println(obj);
	}
}
