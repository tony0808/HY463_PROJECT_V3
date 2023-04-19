package main;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Dummy {

	public static void main(String[] args) throws IOException {
		RandomAccessFile freader = new RandomAccessFile("C:\\CollectionIndex\\test.txt", "r");
		String line;
		freader.seek(15+3);
		while((line = freader.readLine()) != null) {
			print(line);
		}
		print(freader.getFilePointer());
		freader.close();
	} 
	
	public static void print(Object obj) {
		System.out.println(obj);
	}
}
