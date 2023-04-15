package main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import indexbuilder.XmlLabelReader;
import java.util.ArrayList;

public class Tester {
	
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		String documentPath = "C:\\MiniCollection\\diagnosis\\Topic_1\\0\\1852545.nxml";
		XmlLabelReader reader = new XmlLabelReader(documentPath);
		ArrayList<String> labelNames = reader.getLabelNames();
		for(String labelName : labelNames) {
			System.out.println(labelName + " : " + reader.getLabelText(labelName));
		}
	}
}
