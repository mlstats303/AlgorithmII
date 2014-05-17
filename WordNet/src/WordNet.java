import java.util.ArrayList;

public class WordNet {
	private final SeparateChainingHashST<String, Bag<Integer> > st;
	private final ArrayList<String> ls;
	private final Digraph g ;
	private final SAP sap;
	
	// constructor takes the name of the two input files
	public WordNet(String synsets, String hypernyms) {
		String line;
		final String csvSplitBy = ",";
		int i, id, V = 0;
		
		// Build hashtable from synset id to synset
		In in = new In(synsets);
		st = new SeparateChainingHashST<String, Bag<Integer>>();
		ls = new ArrayList<String>();
		
		while( (line = in.readLine()) != null ) {
			String[] s = line.split(csvSplitBy);
			String[] fields = s[1].split("\\s+");
			id = Integer.parseInt(s[0]);
			
			ls.add(s[1]);
			for(i=0; i < fields.length; i++) {
				Bag<Integer> c;
				if( !st.contains(fields[i]) ) c = new Bag<Integer>();
				else c = st.get(fields[i]);
				assert c != null;
				c.add(id);
				st.put(fields[i], c);
			}
			V++;
		}
		in.close();
		
		assert V >= 0;
		// Build WordNet digraph 
		g = new Digraph(V);
		
		in = new In(hypernyms);
		while( (line = in.readLine()) != null ) {
			String[] s = line.split(csvSplitBy);
			id = Integer.parseInt(s[0]);
			for(i = 1; i < s.length; i++ ) g.addEdge(id, Integer.parseInt(s[i]));
		}
		in.close();
		sap = new SAP(g);	
		
		// Check if it is a DAG
		DirectedCycle finder = new DirectedCycle(g);
		if( finder.hasCycle() ) throw new IllegalArgumentException("Digraph is not a DAG.");
		
		// A rooted DAG ?
		id = 0;
		for(i = 0; i < g.V(); i++) {
			boolean root = true;
			for(int w: g.adj(i) ) {
				root = false; 
				break;
			}
			if(root) id++;
			if(id > 1) throw new IllegalArgumentException("More than one root.");
		}
	}
	
	
	// the set of nouns (no duplicates), returned as an Iterable
	public Iterable<String> nouns() {
		return st.keys();
	}
	
	// is the word a WordNet noun?
	public boolean isNoun(String word) {
		return st.contains(word);
	}
	
	// distance between nounA and nounB 
	public int distance(String nounA, String nounB) {
		if( !isNoun(nounA) || !isNoun(nounB) ) throw new IllegalArgumentException();
		if( nounA.equals(nounB) ) return 0;
		
		return sap.length(st.get(nounA), st.get(nounB));
	}
	
	// a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
	// in a shortest ancestral path
	public String sap(String nounA, String nounB) {
		if( !isNoun(nounA) || !isNoun(nounB) ) throw new IllegalArgumentException();
		if( nounA.equals(nounB) ) return nounA;
		
		int w = sap.ancestor(st.get(nounA), st.get(nounB));
		if(w == -1) return null;
		return ls.get(w);
	}
	
	// for unit testing of this class
	public static void main(String[] args) {
		WordNet wordnet = new WordNet(args[0], args[1]);
		
		System.out.println(" *** Print all nouns (in WordNet) ****");
		for(String noun : wordnet.nouns() ) {
			System.out.println(noun);
		}
		
		System.out.println("**** Distance for pair of nouns ****");
		Integer d = wordnet.distance("Black_Plague", "black_marlin");
		System.out.println("Black_Plague - black_marlin = " + d.toString() );
		
		d = wordnet.distance("American_water_spaniel", "histology");
		System.out.println("American_water_spaniel - histology = " + d.toString() );
		
		d = wordnet.distance("Brown_Swiss", "barrel_roll");
		System.out.println("Brown_Swiss - barrel_roll = " + d.toString() );
		
		System.out.println("**** Common ancestor ****");
		String ancestor = wordnet.sap("individual", "edible_fruit");
		System.out.println("Ancestor of (individual,edible_fruit): " + ancestor);
		
		ancestor = wordnet.sap("skiing", "bay_wreath");
		System.out.println("Ancestor of (skiing,bay_wreath): " + ancestor);
		
		
	}
}
