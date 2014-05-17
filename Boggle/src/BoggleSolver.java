import java.util.Set;
import java.util.TreeSet;

public class BoggleSolver {
	private TST<Integer> tstPrefix;
	private TST<Integer> tstDict;
	private static final int MIN_WORDS = 3;
	
	// Initializes the data structure using the given array of strings as the dictionary.
	// (Assume each word in the dictionary contains only the uppercase letters A through Z.)
	public BoggleSolver(String[] dictionary) {
		tstPrefix = new TST<Integer>();
		tstDict = new TST<Integer>();
		for(String s : dictionary) {
			for(int i=1; i <= s.length(); i++) {
				tstPrefix.put(s.substring(0,i), i);
			}
			tstDict.put(s, s.length());
		}
	}
	
	// Recursive depth-first search (Works correctly, but failed timing test)
	private void dfs(BoggleBoard board, int row, int col, boolean[][]visited, String s,
			Set<String> validWords) {
		
		if( row < 0 || col < 0 || row >= board.rows() || col >= board.cols() ) return;
		
		if(visited[row][col]) return;
		
		String prefix;
		char c = board.getLetter(row, col);
		if(c == 'Q') prefix = s + "QU";
		else prefix = s + Character.toString(c);
		
		Integer ret = tstPrefix.get(prefix);
		if(ret == null) return;
		else if(ret >= MIN_WORDS){
			if(  tstDict.contains(prefix) ) {
				validWords.add(prefix);
			}
		}
		
		visited[row][col] = true;
		for(int drow = -1; drow <= 1; drow++) {
			int newrow = row + drow;
			for(int dcol = -1; dcol <= 1; dcol++) {
				int newcol = col + dcol;
				dfs(board, newrow, newcol, visited, prefix, validWords);
			}
		}
		visited[row][col] = false;
	}
	
	// Non-recursive depth-first search (BUGGY)
	private void dfs_nonRec(BoggleBoard board, int ii, int jj, Set<String> validWords) {
		final int ROWS = board.rows();
		final int COLS = board.cols();
		
		boolean[][] visited = new boolean[ROWS][COLS];
		Stack<int[]> stCoords = new Stack<int[]>();
		Stack<int[]> stParent = new Stack<int[]>();
		Stack<int[]> stEdgeTo = new Stack<int[]>();
		Stack<String> stPrefix = new Stack<String>();
		
		int[] pt = {ii,jj};
		stCoords.push(pt);
		stParent.push(pt);
		stPrefix.push("");
		
		while(!stCoords.isEmpty()) {
			pt = stCoords.pop();
			int row = pt[0];
			int col = pt[1];
			String olds = stPrefix.pop();
			stEdgeTo.push(stParent.pop());
			
			if( row < 0 || col < 0 || row >= ROWS || col >= COLS ){
				stEdgeTo.pop();
				continue;
			}
			if(visited[row][col]) {
				stEdgeTo.pop();
				continue;
			}
			
			String prefix;
			char c = board.getLetter(row, col);
			
			if(c=='Q') prefix = olds + "QU";
			else prefix = olds + Character.toString(c);
			
			Integer ret = tstPrefix.get(prefix);
			if(ret == null) {
				if(!stParent.isEmpty()) {
					pt = stParent.peek();
					ii = pt[0];
					jj = pt[1];
					do
					{
						int[] p = stEdgeTo.pop();
						row = p[0]; col = p[1];
						visited[row][col] = false;
					}while(row != ii || col != jj);
					visited[ii][jj] = true;
				}
				continue;
			}else if(ret >= MIN_WORDS) {
				if( tstDict.contains(prefix) ) validWords.add(prefix);
			}
			
			visited[row][col] = true;
			for(int drow = -1; drow <= 1; drow++) {
				int newrow = row + drow;
				if(newrow < 0 || newrow >= ROWS) continue;
				for(int dcol = -1; dcol <= 1; dcol++) {
					int newcol = col + dcol;
					if(newcol < 0 || newcol >= COLS) continue;
					int[] newpt = {newrow,newcol};
					int[] parent = {row,col};
					stCoords.push(newpt);
					stParent.push(parent);
					stPrefix.push(prefix);
				}
			}
		}
	}
	
	// Returns the set of all valid words in the given Boggle board, as an Iterable.
	public Iterable<String> getAllValidWords(BoggleBoard board) {
		TreeSet<String> validWords = new TreeSet<String>();	
		for(int i=0; i < board.rows(); i++ ) {
				for(int j=0; j < board.cols(); j++) {
					boolean[][] visited = new boolean[board.rows()][board.cols()];
					dfs(board, i, j, visited, "", validWords); // Works correctly, but failed timing test
					// dfs_nonRec(board, i, j, validWords); BUGGY
				}
			}
			return validWords;
	}
	
	// Returns the score of the given word if it is in the dictionary, zero otherwise.
	// (Assume the word contains only the uppercase letters A through Z.)
	public int scoreOf(String word) {
		Integer ret = tstDict.get(word);
		if(ret == null) return 0;
		else if(ret >= 8) return 11;
		else {
			switch (ret) {
			case 0:
			case 1:
			case 2:
				ret = 0;
				break;
			case 3:
			case 4:
				ret = 1;
				break;
			case 5:
				ret = 2;
				break;
			case 6:
				ret = 3;
				break;
			case 7:
				ret = 5;
				break;
			default:
				ret = 0;
				break;
			}
			return ret;
		}
	}
	
	public static void main(String[] args) {
		In in = new In(args[0]);
	    String[] dictionary = in.readAllStrings();
	    BoggleSolver solver = new BoggleSolver(dictionary);
	    BoggleBoard board = new BoggleBoard(args[1]);
	    int score = 0;
	    for (String word : solver.getAllValidWords(board))
	    {
	    	StdOut.println(word);
	        score += solver.scoreOf(word);
	    }
	    StdOut.println("Score = " + score);
	}
		
}
