package queryevaluator;

public class VocabData {
	private int df;
	private int dPtr;
	private int index;
	public VocabData(int df, int dPtr, int index) { this.df = df; this.dPtr = dPtr; this.index = index; }
	int getDF() { return this.df; }
	int getDPTR() { return this.dPtr; }
	int getIndex() { return this.index; }
}