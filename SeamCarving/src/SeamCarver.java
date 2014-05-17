import java.awt.Color;

public class SeamCarver {
	private Picture img;
	private double[][] e;
	private int[][]clrmat;
	private int w;
	private int h;
	private static final double MAX_ENERGY = 195075.0;
	
	public SeamCarver(Picture picture) {
		img = picture;
		e = new double[img.height()][img.width()];
		w = img.width(); h = img.height();
		clrmat = new int[h][w];
		
		// Copy color information
		for(int i=0; i < h; i++) {
			for(int j=0; j < w; j++) {
				clrmat[i][j] = picture.get(j,i).getRGB();
			}
		}
		
		// Compute energy of first and last row
		for(int j=0; j < w; j++) {
			e[0][j] = MAX_ENERGY;
			e[h-1][j] = MAX_ENERGY; 
		}
		
		// Compute energy of pixels at other bother
		for(int i=1; i < h - 1 ; i++) {
			e[i][0] = MAX_ENERGY;
			e[i][w-1] = MAX_ENERGY;
		}
		
		// Compute energy for the rest of the pixels
		for(int i=1; i < h-1; i++) {
			for(int j=1; j < w-1; j++) {
				e[i][j] = horizontalEnergy(j-1, j+1, i) + verticalEnergy(i-1, i+1, j); 	
			}
		}
	}
	
	// current picture
	public Picture picture() {
		if( img.width() != width() || img.height() != height() ) {
			img = new Picture(width(), height());
			for(int i=0; i < height(); i++) {
				for(int j=0; j < width(); j++) {
					int rgb = clrmat[i][j];
					img.set(j, i, 
							new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF));
				}
			}
		}
		return img;
	}
	
	// width of current picture
	public int width() {
		return w;
	}
	
	// height of current picture
	public int height() {
		return h;
	}
	
	//energy of pixel at column x and row y in current picture
	public double energy(int x, int y) {
		if( x < 0 || x >= width()  || y < 0 || y >= height() ) throw new IndexOutOfBoundsException();
		return e[y][x];
	}
	
	//sequence of indices for horizontal seam in current picture
	public int[] findHorizontalSeam() {
		transpose();
		int[] horizontalSeam = findVerticalSeam();
		transpose();
		return horizontalSeam;
	}
	
	// sequence of indices for vertical seam in current picture
	public int[] findVerticalSeam() {
		
		double[][] energyTo = new double[height()][width()];
		int[][] edgeTo = new int[height()-1][width()];
		int[] V = new int[height()];
		double v;
		
		for(int j=0; j < width(); j++ ) energyTo[0][j] = MAX_ENERGY;
		
		for(int i=1; i < height(); i++ ) {
			for(int j=0; j < width(); j++) {
				v = energyTo[i-1][j];
				edgeTo[i-1][j] = j;
				if( j >= 1) {
					if(Double.compare(v, energyTo[i-1][j-1]) > 0 ){
						v = energyTo[i-1][j-1];
						edgeTo[i-1][j] = j-1;
					}
				}
				if( j < width() - 1 ) {
					if(Double.compare(v, energyTo[i-1][j+1]) > 0 ) {
						v = energyTo[i-1][j+1];
						edgeTo[i-1][j] = j+1;
					}
				}
				energyTo[i][j] = energy(j, i) + v;
			}
		}
		v = Double.MAX_VALUE;
		int i = height() - 1;
		for(int j=0; j < width(); j++ ) {
			if(Double.compare(v, energyTo[i][j]) > 0 ) {
				v = energyTo[i][j];
				V[i] = j;
			}
		}
		for( i = height() - 2; i >=0; i--) V[i] = edgeTo[i][V[i+1]];
		if(V[1] >= 1) V[0] = V[1] - 1;
		else V[0] = V[1];
		return V;
	}
	
	
	// transpose image
	private void transpose() {
		double[][] arr = new double[width()][height()];
		int[][] mat = new int[width()][height()];
		for(int i=0; i < arr.length; i++) {
			for(int j=0; j < arr[0].length; j++) {
				arr[i][j] = e[j][i];
				mat[i][j] = clrmat[j][i];
			}
		}
		e = arr; clrmat = mat;
		w = arr[0].length; h = arr.length;
	}
	
	// compute energy in horizontal direction
	private double horizontalEnergy(int prevx, int nextx, int row) {
		double Rx = ((clrmat[row][nextx] >> 16) & 0xFF) - ((clrmat[row][prevx] >> 16) & 0xFF);
		double Gx = ((clrmat[row][nextx] >> 8) & 0xFF) - ((clrmat[row][prevx] >> 8) & 0xFF);
		double Bx = ((clrmat[row][nextx] & 0xFF) - (clrmat[row][prevx] & 0xFF));
		return Rx*Rx + Gx*Gx + Bx*Bx;
	}
	
	// compute energy in vertical direction
	private double verticalEnergy(int prevy, int nexty, int col) {
		double Ry = ((clrmat[nexty][col] >> 16) & 0xFF) - ((clrmat[prevy][col] >> 16) & 0xFF);
		double Gy = ((clrmat[nexty][col] >> 8) & 0xFF) - ((clrmat[prevy][col] >> 8) & 0xFF);
		double By = ((clrmat[nexty][col] & 0xFF) - (clrmat[prevy][col] & 0xFF));
		return Ry*Ry + Gy*Gy + By*By;
	}
	
	// remove horizontal seam from current picture
	public void removeHorizontalSeam(int[] a) {
		transpose();
		removeVerticalSeam(a);
		transpose();	
	}
	
	// remove horizontal seam from current picture
	public void removeVerticalSeam(int[] a) {
		if( a.length != height() ) throw new IllegalArgumentException();
		
		// shift color matrix to the left
		for(int i=0; i < height(); i++ ){
			if( a[i] < 0 || a[i] >= width() ) throw new IllegalArgumentException();
			System.arraycopy(clrmat[i], a[i]+1, clrmat[i], a[i], width()-a[i]-1);
			clrmat[i][width()-1] = 0;
		}
		
		// incremental modification of energy matrix
		for(int i=1; i < height()-1; i++) {
			int d = a[i] - a[i-1];
			if( d < 0) d *= -1;
			if(  d > 1) throw new IllegalArgumentException();
			int c = a[i]-1;
			if( c > 0 ) 
				e[i][c] = horizontalEnergy(c-1, c+1, i) + verticalEnergy(i-1, i+1, c);
			for(c = a[i]; c < width()-1; c++) {
				if( c == 0 ) e[i][c+1] = MAX_ENERGY;
				else e[i][c+1] = horizontalEnergy(c-1, c+1, i) + verticalEnergy(i-1, i+1, c);
			}
			e[i][width()-1] = MAX_ENERGY;
		}
		
		// shift energy matrix to the left
		for(int i=0; i < height(); i++ ){
			System.arraycopy(e[i], a[i]+1, e[i], a[i], width()-a[i]-1);
		}
		w -= 1;
	}
}
