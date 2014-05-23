import java.util.Arrays;

public class CircularSuffixArray {
	private final String s;
	private final int[] index;
	
	// circular suffix array of s
	public CircularSuffixArray(String s) {
		this.s = s;
		String[] sorted = new String[s.length()];
		String[] suffix = new String[s.length()];
		index = new int[s.length()];
		
		char[] charArray = s.toCharArray();
		
		// Build circular suffixes
		for(int i=0; i < s.length(); i++) {
			char c = charArray[0];
			sorted[i] = new String(charArray);
			suffix[i] = sorted[i];
			for(int j=0; j < s.length()-1; j++) {
				charArray[j] = charArray[j+1];
			}
			charArray[s.length()-1] = c;
		}
		
		// Sort string in ascending order (fixed length)
		LSD.sort(sorted, sorted[0].length());
		
		// Binary search through sorted string O(logN)
		for(int i=0; i < s.length(); i++) { 
			index[Arrays.binarySearch(sorted, suffix[i])] = i;
		}
	}
	
	// length of s
	public int length() {
		return s.length();
	}
	
	// returns index of ith sorted suffix
	public int index(int i) {
		return index[i];
	}	
}
