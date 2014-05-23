
public class BurrowsWheeler {
	// apply Burrows-Wheeler encoding, reading from standard input and writing to standard output
	public static void encode() {
		StringBuilder sb = new StringBuilder();
		
		while( !BinaryStdIn.isEmpty() ) {
			char c = BinaryStdIn.readChar();
			sb.append(c);
		}
		String s = sb.toString();
		String[] suffix = new String[s.length()];
		
		// Build circular suffix array
		char[] charArray = s.toCharArray();
		for(int i=0; i < s.length(); i++) {
			char c = charArray[0];
			suffix[i] = new String(charArray);
			for(int j=0; j < s.length()-1; j++) {
				charArray[j] = charArray[j+1];
			}
			charArray[s.length()-1] = c;
		}
		
		// Construct sorted suffixes array t[]
		int first = -1;
		int last = s.length()-1;
		CircularSuffixArray csa = new CircularSuffixArray(s);
	
		for(int i=0; i < s.length(); i++) {
			charArray[i] = suffix[csa.index(i)].charAt(last);
			if(csa.index(i) == 0) {
				first = i;
			}
		}
		
		// Print to output stream
		BinaryStdOut.write(first);
		for(int i=0; i < s.length(); i++) {
			BinaryStdOut.write(charArray[i]);
		}
		BinaryStdOut.flush();
	}
	
	// apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
	public static void decode() {
		
	}
	
	// if args[0] is '-', apply Burrows-Wheeler encoding
	// if args[0] is '-', apply Burrows-Wheeler decoding
	public static void main(String[] args) {
		if(args.length < 1) {
			throw new IllegalArgumentException("Usage: BurrowsWheeler -|+");
		}else if(args[0].equals("-")) { 
			BurrowsWheeler.encode();
		}else if(args[0].equals("+")) {
			BurrowsWheeler.decode();
		}else {
			throw new IllegalArgumentException("Usage: BurrowsWheeler -|+");
		}
	}

}
