
public class Outcast {
	private final WordNet wordnet;
	// constructor takes a WordNet object
	public Outcast(WordNet wordnet) {
		this.wordnet = wordnet;
	}
	
	// given an array of WordNet nouns, return an outcast
	public String outcast(String[] nouns) {
		int[][] mat = new int[nouns.length][nouns.length];
		for(int i=0; i < nouns.length - 1; i++ ) {
			mat[i][i] = 0;
			for(int j = i+1; j < nouns.length; j++) {
				mat[i][j] = wordnet.distance(nouns[i], nouns[j]);
				mat[j][i] = mat[i][j];
			}
		}
		mat[nouns.length-1][nouns.length-1] = 0;
		
		int maxdist = Integer.MIN_VALUE;
		int maxidx = 0;
		for(int i = 0; i < nouns.length; i++ ) {
			int dist = 0;
			for(int j = 0; j < nouns.length; j++) {
				if(i == j) continue;
				dist += mat[i][j];
			}
			if(dist > maxdist) {
				maxdist = dist;
				maxidx = i;
			}
		}
		return nouns[maxidx];
	}
	
	public static void main(String[] args) {
		WordNet wordnet = new WordNet(args[0], args[1]);
		Outcast outcast = new Outcast(wordnet);
		for(int t = 2; t < args.length; t++) {
			In in = new In(args[t]);
			String[] nouns = in.readAllStrings();
			StdOut.println(args[t] + ": " + outcast.outcast(nouns));
		}
	}
}
