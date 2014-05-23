
public class MoveToFront {
	private static final int R = 256;
	// apply move-to-front encoding, reading from standard input and writing to standard output
	public static void encode() {
		int i;
		char[] charseq = new char[R];
		
		for(i=0; i<R; i++) charseq[i] = (char)i;
		
		while(!BinaryStdIn.isEmpty()) {
			char c = BinaryStdIn.readChar();
			// Find the position in which the character appears
			for(i=0; charseq[i] != c; i++);
			// Print the position in byte
			BinaryStdOut.write((byte)i);
			// Right shift the characters
			for(;i > 0; i--) {
				charseq[i] = charseq[i-1];
			}
			charseq[0] = c;
		}
		BinaryStdOut.flush();
	}
	// apply move-to-front decoding, reading from standard input and writing to standard output
	public static void decode() {
		int i;
		char[] charSeq = new char[R];
		for(i=0; i<R; i++) charSeq[i] = (char)i;
		while( !BinaryStdIn.isEmpty() ) {
			byte b = BinaryStdIn.readByte();
			char c = charSeq[b];
			// Print character at position b
			BinaryStdOut.write(c);
			// Right shift characters for position < b
			for(; b > 0; b--) {
				charSeq[b] = charSeq[b-1];
			}
			charSeq[0] = c;
		}
		BinaryStdOut.flush();
	}
	
	// if args[0] is '-' apply move-to-front encoding
	// if args[0] is '+' apply move-to-front decoding
	public static void main(String[] args) {
		if(args.length < 1) {
			throw new IllegalArgumentException("Usage: MoveToFront -|+");
		}else if(args[0].equals("-")) { 
			MoveToFront.encode();
		}else if(args[0].equals("+")) {
			MoveToFront.decode();
		}else {
			throw new IllegalArgumentException("Usage: MoveToFront -|+");
		}
	}
}
