
public class SAP {
	private final Digraph G;
	private SET<Integer> stA;
	private SET<Integer> stB;
	private int a, b;
	private BreadthFirstDirectedPaths bfsA, bfsB;
	
	// constructor takes a digraph (not necessarily a DAG)
	public SAP(Digraph G) {
		this.G = new Digraph(G);
		a = b = -1;
		stA = stB = null;
		bfsA = bfsB = null;
	}
	
	// length of shortest ancestral path between v and w; -1 if no such path
	public int length(int v, int w) {
		if(v < 0 || v >= G.V() || w < 0 || w >= G.V()) throw new IndexOutOfBoundsException();
		if( v == w) return 0;
		
		stA = stB = null;
		if( a != v ) {
			bfsA = new BreadthFirstDirectedPaths(G, v);
			a = v;
		} 
		assert bfsA != null;
		
		if( b != w ) {
			bfsB = new BreadthFirstDirectedPaths(G, w);
			b = w;
		}
		assert bfsB != null;
		
		int dist, minDist = Integer.MAX_VALUE;
		for(int i = 0; i < G.V(); i++ ){
			if( !bfsA.hasPathTo(i) || !bfsB.hasPathTo(i) ) continue;
			dist = bfsA.distTo(i) + bfsB.distTo(i);
			if( dist < minDist ) minDist = dist;
		}
		return (minDist != Integer.MAX_VALUE ? minDist : -1);
	}
	
	// a common ancestor of v and w that participates in a shortest ancestral path; 
	// -1 if no such path
	public int ancestor(int v, int w) {
		if(v < 0 || v >= G.V() || w < 0 || w >= G.V()) throw new IndexOutOfBoundsException();
		if( v == w) return w;
		
		stA = stB = null;
		if( a != v ) {
			bfsA = new BreadthFirstDirectedPaths(G, v);
			a = v;
		} 
		assert bfsA != null;
		
		if( b != w ) {
			bfsB = new BreadthFirstDirectedPaths(G, w);
			b = w;
		}
		assert bfsB != null;
		
		int dist, minDist = Integer.MAX_VALUE;
		v = 0;
		for(int i = 0; i < G.V(); i++ ){
			if( !bfsA.hasPathTo(i) || !bfsB.hasPathTo(i) ) continue;
			dist = bfsA.distTo(i) + bfsB.distTo(i);
			if( dist < minDist ) {
				minDist = dist;
				v = i;
			}
		}
		return (minDist != Integer.MAX_VALUE ? v : -1);
	}
	
	// length of shortest ancestral path between any vertex in v and any vertex in w;
	// -1 if no such path
	public int length(Iterable<Integer> v, Iterable<Integer> w) {
		boolean notfound = false;
		for(Integer i: v) {
			if( i < 0 || i >= G.V() ) throw new IndexOutOfBoundsException();
			if( stA != null && !stA.contains(i) ) {
				notfound = true;
			}
		}
		a = b = -1;
		if(notfound || stA == null) {
			bfsA = new BreadthFirstDirectedPaths(G, v);
			stA = new SET<Integer>();
			for(Integer i: v) stA.add(i);
		}
		
		notfound = false;
		for(Integer i: w) {
			if( i < 0 || i >= G.V() ) throw new IndexOutOfBoundsException();
			if( stB != null && !stB.contains(i) ) {
				notfound = true;
			}
		}
		if(notfound || stB == null) {
			bfsB = new BreadthFirstDirectedPaths(G, w);
			stB = new SET<Integer>();
			for(Integer i: w) stB.add(i);
	
		}
	
		int dist, minDist = Integer.MAX_VALUE;
		for(int i = 0; i < G.V(); i++ ){
			if( !bfsA.hasPathTo(i) || !bfsB.hasPathTo(i) ) continue;
			dist = bfsA.distTo(i) + bfsB.distTo(i);
			if( dist < minDist ) minDist = dist;
		}
		return (minDist != Integer.MAX_VALUE ? minDist : -1);
	}
	
	// a common ancestor that participates in shortest ancestral path;
	// -f if no such path
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
		boolean notfound = false;
		for(Integer i: v) {
			if( i < 0 || i >= G.V() ) throw new IndexOutOfBoundsException();
			if( stA != null && !stA.contains(i) ) {
				notfound = true;
			}
		}
		
		a = b = -1;
		if(notfound || stA == null) {
			bfsA = new BreadthFirstDirectedPaths(G, v);
			stA = new SET<Integer>();
			for(Integer i: v) stA.add(i);
		}
		
		notfound = false;
		for(Integer i: w) {
			if( i < 0 || i >= G.V() ) throw new IndexOutOfBoundsException();
			if( stB != null && !stB.contains(i) ) {
				notfound = true;
			}
		}
		if(notfound || stB == null) {
			bfsB = new BreadthFirstDirectedPaths(G, w);
			stB = new SET<Integer>();
			for(Integer i: w) stB.add(i);
		}
	
		int dist, minDist = Integer.MAX_VALUE;
		int vtx = 0;
		for(int i = 0; i < G.V(); i++ ){
			if( !bfsA.hasPathTo(i) || !bfsB.hasPathTo(i) ) continue;
			dist = bfsA.distTo(i) + bfsB.distTo(i);
			if( dist < minDist ) {
				vtx = i;
				minDist = dist;
			}
		}
		return (minDist != Integer.MAX_VALUE ? vtx : -1);
	}
	
	public static void main(String[] args) {
		In in = new In(args[0]);
		Digraph G = new Digraph(in);
		SAP sap = new SAP(G);
		while( !StdIn.isEmpty() ) {
			int v = StdIn.readInt();
			int w = StdIn.readInt();
			int length = sap.length(v, w);
			int ancestor = sap.ancestor(v, w);
			StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
		}

	}

}
