public class PrefixST {
	private int N;
	private Node root;
	private Node prev;
	String oldkey;
	private static final int R = 26;        // extended ASCII
	private static final int A = (int)'A';
	
	private class Node {
		private Node[] next = new Node[R];
		private Integer val;
	}
	
	public int size() {
		return N;
	}
	
	public boolean contains(String key) {
		return get(key) != null;
	}
	
	 private Node put(Node x, String key, Integer val, int d) {
		 if (x == null) x = new Node();
	        if (d == key.length()) {
	            if (x.val == null) N++;
	            x.val = val;
	            return x;
	        }
	        int c = (int)key.charAt(d);
	        x.next[c-A] = put(x.next[c-A], key, val, d+1);
	        return x;
	}
	 
	public void put(String key, Integer val) {
		root = put(root, key, val, 0);
	}
	
	public Integer get(String key) {
		if(key==null) throw new NullPointerException();
		if(key.length() == 0) throw new IllegalArgumentException("key must have length >= 1");
		if(root == null) return null;
		int d = (prev == null? 0 : (oldkey.length() <= key.length() ? oldkey.length(): 0) );
		Node x = (d == 0 ? root : prev);
		
		while(x != null) {
			if(d == key.length()) break;
			int c = (int)key.charAt(d);
			x = x.next[c-A]; d++;	
		}
		prev = x;
		oldkey = key;
		return (x == null ? null : x.val);
	}
}
