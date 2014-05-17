public class BaseballElimination {
	private final int N;
	private final int[] w;
	private final int[] l;
	private final int[] r;
	private final int[][] g;
	private final boolean[] eliminated;
	private final LinearProbingHashST<String, Integer> lookup;
	private final Bag<String>[] certs;
	
	// create a baseball division from given filename in format specified below
	public BaseballElimination(String filename) {
		In in = new In(filename); 
		N = in.readInt(); 
		String[] teams = new String[N];
		w = new int[N];
		l = new int[N];
		r = new int[N];
		g = new int[N][N];
		eliminated = new boolean[N];
		certs = (Bag<String>[])new Bag[N];
		
		lookup = new LinearProbingHashST<String, Integer>();
		
		// Parse file stream
		for(int i=0; i < N; i++) {
			teams[i] = in.readString();
			lookup.put(teams[i], i);
			w[i] = in.readInt();
			l[i] = in.readInt();
			r[i] = in.readInt();
			
			for(int j=0; j<N; j++){
				g[i][j] = in.readInt();
			}
		}	
		if(N > 1) {
			int V = (N-1)*(N-2)/2 +  N + 1;
			int[] id = new int[N];
			int[] revid = new int[N-1];
			for(int i=0; i<N; i++) {
				double gstar = 0;
				int s = N - 1;
				int t = s + 1;
				int v = t + 1;
				int c = 0;
				
				// Trivial elimination
				for(int j=0; j<N; j++) {
					if(j==i) continue;
					if( w[i] + r[i] < w[j] ) {
						eliminated[i] = true;
						if(certs[i] == null) certs[i] = new Bag<String>();
						certs[i].add(teams[j]);
					}
				}
				if(eliminated[i]) continue;
				
				for(int j=0; j<N; j++) id[j] = (i==j ? -1 : c++); 
				c = 0;
				for(int j=0; j<N; j++) {
					if(id[j] == -1) continue;
					revid[c++] = j;
				}
				
				// Non-trivial elimination with max-flow
				FlowNetwork G = new FlowNetwork(V);
				for(int j=0; j<N; j++) {
					if(i==j) continue;
					for(int k=0; k<N; k++) {
						if(j==k || i==k || j > k) continue;
						gstar += g[j][k];
						G.addEdge(new FlowEdge(s,v,g[j][k]));
						G.addEdge(new FlowEdge(v,id[j], Double.POSITIVE_INFINITY));
						G.addEdge(new FlowEdge(v,id[k], Double.POSITIVE_INFINITY));
						v++;
					}
				}
				for(int j=0; j<N; j++) {
					if(j==i) continue;
					G.addEdge(new FlowEdge(id[j], t, w[i] + r[i] - w[j]));
				}
				
				FordFulkerson maxflow = new FordFulkerson(G, s, t);
				if( Double.compare(maxflow.value(),gstar) < 0 ) {
					eliminated[i] = true;
					for(int j=0; j<N-1; j++) {
						if( maxflow.inCut(j) ) {
							int k = revid[j];
							if( certs[i] == null ) certs[i] = new Bag<String>();
							certs[i].add(teams[k]);
						}
					}
				}
			}
		}
	}
	
	// number of teams
	public int numberOfTeams() {
		return N;
	}
	
	// all teams
	public Iterable<String> teams() {
		return lookup.keys();
	}
	
	// number of wins for given team
	public int wins(String team) {
		if(!lookup.contains(team) ) throw new IllegalArgumentException();
		return w[lookup.get(team)];
	}
	
	// number of losses for given team
	public int losses(String team) {
		if(!lookup.contains(team) ) throw new IllegalArgumentException();
		return l[lookup.get(team)];
	}
	
	// number of remaining games for given team
	public int remaining(String team) {
		if( !lookup.contains(team) ) throw new IllegalArgumentException();
		return r[lookup.get(team)];
	}
	
	// number of remaining games between team1 and team2
	public int against(String team1, String team2) {
		if(!lookup.contains(team1) || !lookup.contains(team2) ) throw new IllegalArgumentException();
		return g[lookup.get(team1)][lookup.get(team2)];
	}
	
	// is given team eliminated ?
	public boolean isEliminated(String team) {
		if( !lookup.contains(team) ) throw new IllegalArgumentException();
		return eliminated[lookup.get(team)];
	}
	
	// subset R of teams that eliminates given team, null if not eliminated
	public Iterable<String> certificateOfElimination(String team) {
		if( !lookup.contains(team) ) throw new IllegalArgumentException();
		return certs[lookup.get(team)];
	}
	
	public static void main(String[] args) {
		BaseballElimination division = new BaseballElimination(args[0]);
		for (String team : division.teams()) {
			if (division.isEliminated(team)) {
				StdOut.print(team + " is eliminated by the subset R = { ");
				for (String t : division.certificateOfElimination(team))
					StdOut.print(t + " ");
		            StdOut.println("}");
		        }
		        else {
		            StdOut.println(team + " is not eliminated");
		        }
		    }
	}
}
