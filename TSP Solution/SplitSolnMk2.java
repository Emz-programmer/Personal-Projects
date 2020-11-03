import java.util.*;

public class SplitSolnMk2 {
	static double radOfEarth = 6371;
	static double radianConversion = Math.PI / 180.0;

	public static void main(String args[]) {
		String[] gps;
		// String [] gpsOld;
		FileIO file = new FileIO();
		gps = file.load("D:\\Downloads\\NewGPS.txt");
		// gpsOld = file.load("D:\\Downloads\\NewGPS.txt");
		for (int i = 0; i < gps.length; i++) {
			if (i == 0) {
				gps[i] = gps[i].substring(0, gps[i].length() - 1);// remove buggy header data
			} else
				gps[i] = gps[i].substring(0, gps[i].length() - 1);
		}
		
		long startTime = System.currentTimeMillis();
		// convert to double array for lat and longatude
		double[][] coords = new double[1001][2];
		for (int i = 0; i < 1001; i++) {
			String[] hold = gps[i].split(",");
			coords[i][0] = Double.valueOf(hold[0]);
			coords[i][1] = Double.valueOf(hold[1]);
		}

		// Divide america
		double americaEast = -32.553655;// long
		double americaWest = -167.921403;// long
		double americaMiddle = -96.975327;// long
		double americaSouth = 19.049879;// lat
		// Divide europe
		double europeMiddle = 6.5918;// long
		double europeEast = 52.780217;// long
		double europeSouth = 45.001891;// lat
		// Divid asia
		double asiaSouth = 32.856117;// lat

		double[][][] splitCoords = new double[8][coords.length][2];
		int a = 0, b = 0, c = 0, d = 0, e = 0, f = 0, g = 0, h = 0;

		for (int i = 0; i < coords.length; i++) {
			// 0 = west Europe
			if (coords[i][1] <= europeMiddle && coords[i][1] > americaEast && coords[i][0] >= europeSouth) {
				splitCoords[0][a][0] = coords[i][0];
				splitCoords[0][a][1] = coords[i][1];
				a++;
			}
			// 1= east north america
			else if (coords[i][1] <= americaEast && coords[i][1] > americaMiddle && coords[i][0] >= americaSouth) {
				splitCoords[1][b][0] = coords[i][0];
				splitCoords[1][b][1] = coords[i][1];
				b++;
			}
			// 2 = west north america
			else if (coords[i][1] <= americaMiddle && coords[i][1] > americaWest && coords[i][0] >= americaSouth) {
				splitCoords[2][c][0] = coords[i][0];
				splitCoords[2][c][1] = coords[i][1];
				c++;
			}
			// 3 = south america
			else if (coords[i][1] <= americaEast && coords[i][0] > americaWest && coords[i][0] < americaSouth) {
				splitCoords[3][d][0] = coords[i][0];
				splitCoords[3][d][1] = coords[i][1];
				d++;
			}
			// 4= south europe/ africa
			else if (coords[i][1] > americaEast && coords[i][1] <= europeEast && coords[i][0] < europeSouth) {
				splitCoords[4][e][0] = coords[i][0];
				splitCoords[4][e][1] = coords[i][1];
				e++;
			}
			// 7= east europe
			else if (coords[i][1] > europeMiddle && coords[i][1] <= europeEast && coords[i][0] >= europeSouth) {
				splitCoords[7][h][0] = coords[i][0];
				splitCoords[7][h][1] = coords[i][1];
				h++;
			}

			// 5 = south asia/australia 6= north asia
			else {
				if (coords[i][0] < asiaSouth) {
					splitCoords[5][f][0] = coords[i][0];
					splitCoords[5][f][1] = coords[i][1];
					f++;
				} else {
					splitCoords[6][g][0] = coords[i][0];
					splitCoords[6][g][1] = coords[i][1];
					g++;
				}
			}
		}

		for (int i = 0; i < a && splitCoords[0][i][0] != 0; i++) {
			// System.out.println(splitCoords[0][i][0] + "," + splitCoords[0][i][1]);
		}
		double [][] fullRoute= new double [1002][2];
		int x=0;
		// Now determine entry and exit points
		double[] enter = new double[2];
		double[] exit = new double[2];
		int start = 0;
		int end = 0;
		exit[1] = 1000;
		enter[1] = -1000;
		for (int i = 0; i < a; i++) {
			if (splitCoords[0][i][1] > enter[1]) {
				enter[0] = splitCoords[0][i][0];
				enter[1] = splitCoords[0][i][1];
				start = i;
			}
			if (splitCoords[0][i][1] < exit[1]) {
				exit[0] = splitCoords[0][i][0];
				exit[1] = splitCoords[0][i][1];
				end = i;
			}
		}

		double[][] westEUdist = new double[a][a];
		for (int i = 0; i < a; i++) {
			for (int j = 0; j < a; j++) {
				westEUdist[i][j] = getDistance(splitCoords[0][i][0], splitCoords[0][i][1], splitCoords[0][j][0],
						splitCoords[0][j][1]);
			}
		}

		int[] westEUpath = getFlightPath(splitCoords[0], westEUdist, start, end);
		
		for(int j=0;j<500;j++) {
		for(int i=0;true;i++) {
			int [] lastPath=westEUpath;
			westEUpath=nonRandOpt2(westEUpath, westEUdist);
			if(lastPath==westEUpath)break;
		}
		
		for(int i=0;i<9000;i++) {
			westEUpath=opt2(westEUpath, westEUdist);
		}
		}
		
		//westEUpath= optimiseRoute(westEUpath,westEUdist);
		
		for(int i=0;i<westEUpath.length;i++) {
			//System.out.println(splitCoords[0][westEUpath[i]][0]+","+splitCoords[0][westEUpath[i]][1]);
			fullRoute[x][0]= splitCoords[0][westEUpath[i]][0];
			fullRoute[x][1]= splitCoords[0][westEUpath[i]][1];
			x++;
		}
		
		
		exit[1] = 1000;
		enter[1] = -1000;
		for (int i = 0; i < b; i++) {
			if (splitCoords[1][i][1] > enter[1]) {
				enter[0] = splitCoords[1][i][0];
				enter[1] = splitCoords[1][i][1];
				start = i;
			}
			if (splitCoords[1][i][1] < exit[1]) {
				exit[0] = splitCoords[1][i][0];
				exit[1] = splitCoords[1][i][1];
				end = i;
			}
		}

		double[][] northWestAMdist = new double[b][b];
		for (int i = 0; i < b; i++) {
			for (int j = 0; j < b; j++) {
				northWestAMdist[i][j] = getDistance(splitCoords[1][i][0], splitCoords[1][i][1], splitCoords[1][j][0],splitCoords[1][j][1]);
			}
		}

		int[] northWestAMpath = getFlightPath(splitCoords[1], northWestAMdist, start, end);
		
		
		for(int j=0;j<500;j++) {
		for(int i=0;true;i++) {
			int [] lastPath= northWestAMpath;
			northWestAMpath=nonRandOpt2(northWestAMpath, northWestAMdist);
			if(lastPath==northWestAMpath)break;
		}
		
		for(int i=0;i<9000;i++) {
			northWestAMpath=opt2(northWestAMpath, northWestAMdist);
		}
		}
		
	
		//northWestAMpath= optimiseRoute(northWestAMpath, northWestAMdist);
		
		for(int i=0;i<northWestAMpath.length;i++) {
			//System.out.println(splitCoords[1][northWestAMpath[i]][0]+","+splitCoords[1][northWestAMpath[i]][1]);
			fullRoute[x][0]= splitCoords[1][northWestAMpath[i]][0];
			fullRoute[x][1]= splitCoords[1][northWestAMpath[i]][1];
			x++;
		}
		
		exit[0] = 1000;
		enter[1] = -1000;
		for (int i = 0; i < c; i++) {
			if (splitCoords[2][i][1] > enter[1]) {
				enter[0] = splitCoords[2][i][0];
				enter[1] = splitCoords[2][i][1];
				start = i;
			}
			if (splitCoords[2][i][0] < exit[0]) {
				exit[0] = splitCoords[2][i][0];
				exit[1] = splitCoords[2][i][1];
				end = i;
			}
		}

		double[][] northEastAMdist = new double[c][c];
		for (int i = 0; i < c; i++) {
			for (int j = 0; j < c; j++) {
				northEastAMdist[i][j] = getDistance(splitCoords[2][i][0], splitCoords[2][i][1], splitCoords[2][j][0],splitCoords[2][j][1]);
			}
		}

		int[] northEastAMpath = getFlightPath(splitCoords[2], northEastAMdist, start, end);
		
		
		for(int j=0;j<500;j++) {
		for(int i=0;true;i++) {
			int [] lastPath= northEastAMpath;
			northEastAMpath=nonRandOpt2(northEastAMpath, northEastAMdist);
			if(lastPath==northEastAMpath)break;
		}
		
		for(int i=0;i<9000;i++) {
			northEastAMpath=opt2(northEastAMpath, northEastAMdist);
		}
		}
		
		
		
		//northEastAMpath=optimiseRoute(northEastAMpath, northEastAMdist);
		
		for(int i=0;i<northEastAMpath.length;i++) {
			//System.out.println(splitCoords[2][northEastAMpath[i]][0]+","+splitCoords[2][northEastAMpath[i]][1]);
			fullRoute[x][0]= splitCoords[2][northEastAMpath[i]][0];
			fullRoute[x][1]= splitCoords[2][northEastAMpath[i]][1];
			x++;
		}

		
		
		exit[0] = -1000;
		enter[0] = -1000;
		for(int i=0;i<d;i++) {
			if (splitCoords[3][i][0] > exit[0]) {
				exit[0] = splitCoords[3][i][0];
				exit[1] = splitCoords[3][i][1];
				end = i;
			}
		}
		for (int i = 0; i < d; i++) {
			if (splitCoords[3][i][0] > enter[0] && i!=end) {
				enter[0] = splitCoords[3][i][0];
				enter[1] = splitCoords[3][i][1];
				start = i;
			}
			
		}
		
		//System.out.println("End Pos: "+splitCoords[3][end][0]+","+splitCoords[3][end][1]+" D: ");
		double[][] southAMdist = new double[d][d];
		for (int i = 0; i < d; i++) {
			for (int j = 0; j < d; j++) {
				southAMdist[i][j] = getDistance(splitCoords[3][i][0], splitCoords[3][i][1], splitCoords[3][j][0],splitCoords[3][j][1]);
			}
		}

		int[] southAMpath = getFlightPath(splitCoords[3], southAMdist, start, end);
		southAMpath[d-1]= end;
		
		for(int j=0;j<500;j++) {
		for(int i=0;true;i++) {
			int [] lastPath= southAMpath;
			southAMpath=nonRandOpt2(southAMpath, southAMdist);
			if(lastPath==southAMpath) break;
		}
		
		for(int i=0;i<9000;i++) {
			southAMpath=opt2(southAMpath, southAMdist);
		}
		}
		
				
		//southAMpath=optimiseRoute(southAMpath, southAMdist);
		
		//System.out.println("=======================================");
		for(int i=0;i<southAMpath.length;i++) {
			//System.out.println(splitCoords[3][southAMpath[i]][0]+","+splitCoords[3][southAMpath[i]][1]);
			fullRoute[x][0]= splitCoords[3][southAMpath[i]][0];
			fullRoute[x][1]= splitCoords[3][southAMpath[i]][1];
			x++;
		}
		
		//System.out.println("A: "+a+" B: "+b+" C: "+c+" D: "+d);
		//System.out.println("City count: "+(a+b+c+d));
		
		//Go south to europe
		
		exit[1] = 1000;
		enter[1] = -1000;
		for (int i = 0; i < e; i++) {
			if (splitCoords[4][i][1] > enter[1] && splitCoords[4][i][1]<-5 && splitCoords[4][i][1]>-10) {
				enter[0] = splitCoords[4][i][0];
				enter[1] = splitCoords[4][i][1];
				start = i;
			}
			if (splitCoords[4][i][1] < exit[1]) {
				exit[0] = splitCoords[4][i][0];
				exit[1] = splitCoords[4][i][1];
				end = i;
			}
		}

		double[][] southEUdist = new double[e][e];
		for (int i = 0; i < e; i++) {
			for (int j = 0; j < e; j++) {
				southEUdist[i][j] = getDistance(splitCoords[4][i][0], splitCoords[4][i][1], splitCoords[4][j][0],splitCoords[4][j][1]);
			}
		}

		int[] southEUpath = getFlightPath(splitCoords[4], southEUdist, start, end);
		
		
		for(int j=0;j<500;j++) {
		for(int i=0;true;i++) {
			int []lastPath= southEUpath;
			southEUpath=nonRandOpt2(southEUpath, southEUdist);
			if(lastPath==southEUpath) break;
		}
		
		for(int i=0;i<9000;i++) {
			southEUpath=opt2(southEUpath, southEUdist);
		}
		}
		
		
		//southEUpath=optimiseRoute(southEUpath, southEUdist);
		
		for(int i=0;i<southEUpath.length;i++) {
			//System.out.println(splitCoords[4][southEUpath[i]][0]+","+splitCoords[4][southEUpath[i]][1]);
			fullRoute[x][0]= splitCoords[4][southEUpath[i]][0];
			fullRoute[x][1]= splitCoords[4][southEUpath[i]][1];
			x++;
		}
		
		
		//south asia
		exit[0] = -1000;
		enter[1] = 1000;
		for (int i = 0; i < f; i++) {
			if (splitCoords[5][i][1] < enter[1]) {
				enter[0] = splitCoords[5][i][0];
				enter[1] = splitCoords[5][i][1];
				start = i;
			}
			if (splitCoords[5][i][0] > exit[0]) {
				exit[0] = splitCoords[5][i][0];
				exit[1] = splitCoords[5][i][1];
				end = i;
			}
		}

		double[][] southASdist = new double[f][f];
		for (int i = 0; i < f; i++) {
			for (int j = 0; j < f; j++) {
				southASdist[i][j] = getDistance(splitCoords[5][i][0], splitCoords[5][i][1], splitCoords[5][j][0],splitCoords[5][j][1]);
			}
		}

		int[] southASpath = getFlightPath(splitCoords[5], southASdist, start, end);
		
		for(int j=0;j<500;j++) {
		for(int i=0;true;i++) {
			int [] lastPath= southASpath; 
			southASpath=nonRandOpt2(southASpath, southASdist);
			if(lastPath==southASpath) break;
		}
		
		for(int i=0;i<9000;i++) {
			southASpath=opt2(southASpath, southASdist);
		}
		}
		
		
		
		//southASpath=optimiseRoute(southASpath, southASdist);
		
		for(int i=0;i<southASpath.length;i++) {
			//System.out.println(splitCoords[5][southASpath[i]][0]+","+splitCoords[5][southASpath[i]][1]);
			fullRoute[x][0]= splitCoords[5][southASpath[i]][0];
			fullRoute[x][1]= splitCoords[5][southASpath[i]][1];
			x++;
		}
		
		
		//north Asia
		
		exit[1] = 1000;
		enter[0] = 1000;
		for (int i = 0; i < g; i++) {
			if (splitCoords[6][i][0] < enter[0]) {
				enter[0] = splitCoords[6][i][0];
				enter[1] = splitCoords[6][i][1];
				start = i;
			}
			if (splitCoords[6][i][1] < exit[1]) {
				exit[0] = splitCoords[6][i][0];
				exit[1] = splitCoords[6][i][1];
				end = i;
			}
		}

		double[][] northASdist = new double[g][g];
		for (int i = 0; i < g; i++) {
			for (int j = 0; j < g; j++) {
				northASdist[i][j] = getDistance(splitCoords[6][i][0], splitCoords[6][i][1], splitCoords[6][j][0],splitCoords[6][j][1]);
			}
		}

		int[] northASpath = getFlightPath(splitCoords[6], northASdist, start, end);
		
		
		for(int j=0;j<500;j++) {
		for(int i=0;true;i++) {
			int [] lastPath= northASpath;
			northASpath=nonRandOpt2(northASpath, northASdist);
			if(lastPath==northASpath)break;
		}
		
		for(int i=0;i<9000;i++) {
			northASpath=opt2(northASpath, northASdist);
		}
		}
		
		
		//northASpath=optimiseRoute(northASpath, northASdist);
		
		for(int i=0;i<northASpath.length;i++) {
			//System.out.println(splitCoords[6][northASpath[i]][0]+","+splitCoords[6][northASpath[i]][1]);
			fullRoute[x][0]= splitCoords[6][northASpath[i]][0];
			fullRoute[x][1]= splitCoords[6][northASpath[i]][1];
			x++;
		}
		
		
		//East Europe
		exit[1] = 1000;
		enter[1] = -1000;
		for (int i = 0; i < h; i++) {
			if (splitCoords[7][i][1] > enter[1]) {
				enter[0] = splitCoords[7][i][0];
				enter[1] = splitCoords[7][i][1];
				start = i;
			}
			if (splitCoords[7][i][1] < exit[1]) {
				exit[0] = splitCoords[7][i][0];
				exit[1] = splitCoords[7][i][1];
				end = i;
			}
		}

		double[][] eastEUdist = new double[h][h];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < h; j++) {
				eastEUdist[i][j] = getDistance(splitCoords[7][i][0], splitCoords[7][i][1], splitCoords[7][j][0],splitCoords[7][j][1]);
			}
		}

		int[] eastEUpath = getFlightPath(splitCoords[7], eastEUdist, start, end);
		
		
		for(int j=0;j<500;j++) {
		for(int i=0;true;i++) {
			int [] lastPath= eastEUpath;
			eastEUpath=nonRandOpt2(eastEUpath, eastEUdist);
			if(lastPath==eastEUpath)break;
		}
		
		for(int i=0;i<9000;i++) {
			eastEUpath=opt2(eastEUpath, eastEUdist);
		}
		}
		
		
		//eastEUpath=optimiseRoute(eastEUpath, eastEUdist);
		
		for(int i=0;i<eastEUpath.length;i++) {
			//System.out.println(splitCoords[7][eastEUpath[i]][0]+","+splitCoords[7][eastEUpath[i]][1]);
			fullRoute[x][0]= splitCoords[7][eastEUpath[i]][0];
			fullRoute[x][1]= splitCoords[7][eastEUpath[i]][1];
			x++;
		}
		
		//check for missing cities
		boolean []visited = new boolean[1000];
		for(int i=0;i<1000;i++) {
			for(int j=0;j<1000;j++) {
				if(coords[i][0]==fullRoute[j][0] && coords[i][1]==fullRoute[j][1]) {
					visited[i]=true;
					break;
				}
			}
		}
		for(int i=0;i<1000 && x<fullRoute.length;i++) {
			if(!visited[i]) {
				fullRoute[x][0]=coords[i][0];
				fullRoute[x][1]=coords[i][1];
				x++;
			}
		}
		
		
		
		//fullRoute[x][0]=fullRoute[0][0];
		//fullRoute[x][1]=fullRoute[0][1];
		double [][] fullDist= new double[coords.length][coords.length];
		for(int i=0;i<coords.length;i++) {
			for(int j=0;j<coords.length;j++) {
				fullDist[i][j]= getDistance(fullRoute[i][0],fullRoute[i][1], fullRoute[j][0],fullRoute[j][1]);
			}
		}
		int [] fullPath = new int [fullRoute.length];
		for(int i=0;i<fullPath.length-1;i++) {
			fullPath[i]=i;
		}
		
		
		
	
		
		for(int i=0;true;i++) {
			int [] lastPath = fullPath;
			fullPath= nonRandOpt2part(fullPath, fullDist);
			if(fullPath==lastPath)break;
		}
		
		for(int i=0;i<20000;i++) {
			fullPath= opt2(fullPath, fullDist);
		}
		
		for(int j=0;j<70000;j++) {
		for(int i=0;true;i++) {
			int [] lastPath = fullPath;
			fullPath= nonRandOpt2(fullPath, fullDist);
			if(fullPath==lastPath)break;
		}
		for(int i=0;i<1000;i++) {
			fullPath= opt2(fullPath, fullDist);
		}
		}
		
		for(int i=0;true;i++) {
			int [] lastPath = fullPath;
			fullPath= nonRandOpt2(fullPath, fullDist);
			if(fullPath==lastPath)break;
		}
		//fullPath= optimiseRoute(fullPath, fullDist);
		
		double finalDist=0;
		toHackerRank(fullRoute, fullPath, coords);
		for(int i=0;i<fullRoute.length;i++) {
			//System.out.println(fullRoute[fullPath[i]][0]+","+fullRoute[fullPath[i]][1]);
			if(i<fullRoute.length-1 && fullDist[fullPath[i]][fullPath[i+1]]<100) System.out.println("Invalid solution");
			if(i<fullRoute.length-1) finalDist+= fullDist[fullPath[i]][fullPath[i+1]];
		}
		System.out.println();
		long endTime   = System.currentTimeMillis();
		System.out.println("runtime: "+(endTime-startTime)/1000);
		System.out.println("Distance: "+finalDist);
		System.out.println("Time: "+((finalDist/800.0)+(fullRoute.length/2)));
	}

	public static double getDistance(double lat1, double long1, double lat2, double long2) {
		double latitudeDistance = (lat2 - lat1) * radianConversion;
		double longatudeDistance = (long2 - long1) * radianConversion;

		double centralAnglePart1 = Math.pow(Math.sin(latitudeDistance / 2.0), 2.0) + Math.cos(lat1 * radianConversion)
				* Math.cos(lat2 * radianConversion) * Math.pow(Math.sin(longatudeDistance / 2.0), 2.0);
		double centralAnglePart2 = 2.0 * Math.atan2(Math.sqrt(centralAnglePart1), Math.sqrt(1.0 - centralAnglePart1));
		return radOfEarth * centralAnglePart2;
	}
	public static boolean deadEnd;
	public static int[] getFlightPath(double[][] coords, double[][] dist, int start, int end) {
		int length = dist.length;
		//System.out.println("LENGTH: "+length+"================================");
		
		//System.out.println("Length: "+length/2);
		int[] route = new int[length];
		int[] startRoute = new int[length/2];
		int[] endRoute = new int[length/2];
		//System.out.println("Half length: "+endRoute.length);
		boolean[] wasVisited = new boolean[length];
		int startIndex = start;
		int endIndex = end;
		int nextStartIndex=0;
		int nextEndIndex=0;
		for (int i = 1; i < length; i++) {
			wasVisited[i] = false;
		}
		deadEnd = false;
		wasVisited[startIndex]=true;
		wasVisited[endIndex]=true;
		for (int i = 0; i < (length/2)-1; i++) {
			int current=i;
			
			nextStartIndex= getNextIndex(dist, startIndex, wasVisited);
			
			while(nextStartIndex==-1) {// | (nextStartIndex>0 && i<endRoute.length-1 && dist[startIndex][nextStartIndex]>200 && deadEnd==false)) {
				
				startRoute= deadEndFix(startRoute, dist, startIndex, i);
				startIndex=startRoute[i];
				nextStartIndex=getNextIndex(dist, startIndex, wasVisited);
				deadEnd=true;
			}
			
			
				wasVisited[nextStartIndex]=true;
				startRoute[i]=startIndex;
				startIndex=nextStartIndex;
			
			
			//==========================================================
			nextEndIndex= getNextIndex(dist, endIndex, wasVisited);
			deadEnd=false;
			while(nextEndIndex==-1) {// | (nextEndIndex>0 && i<endRoute.length-1 && dist[endIndex][nextEndIndex]>200 && deadEnd==false)) {
				deadEnd=true;
				endRoute= deadEndFix(endRoute, dist, endIndex, i);
				endIndex=endRoute[i];
				nextEndIndex=getNextIndex(dist, endIndex, wasVisited);
			}
			
				wasVisited[nextEndIndex]=true;
				endRoute[i]= endIndex;
				endIndex= nextEndIndex;
			
			
			
		}
		
		
		if(nextStartIndex>=0)startRoute[startRoute.length-1]= nextStartIndex;
		if(nextEndIndex>=0)endRoute[endRoute.length-1]=nextEndIndex;
		
		//System.out.println("DEBUG: "+nextStartIndex+","+nextEndIndex);
		int x=0;
		
		
		
		//System.out.println("Debug: "+startRoute.length);
		for(int i=0;i<startRoute.length;i++) {
			route[x]= startRoute[i];
			x++;
		}
		endRoute= reverseArray(endRoute);		
		int unused=0;
		if(length%2==1) {
			for(int i=0;i<length;i++) {
				if(!wasVisited[i]) {
					unused=i;
					break;
				}
			}
			route[x]= unused;
			x++;
		}
		//System.out.println("Before loop: "+x);		
		for(int i=0;i<endRoute.length;i++) {
			//if(x==38) System.out.println("It was i");
			//try {
			route[x]= endRoute[i];
			//}catch(ArrayIndexOutOfBoundsException e) {System.out.println("Oopsie: "+x+" i="+i);};
			x++;
		}
		
		
		
		
		return route;
	}

	public static int getNextIndex(double[][] dist, int index, boolean[] wasVisited) {
		double minDist = 10000000;// very large distance that is greater than all others
		int minIndex = -1;
		for (int i = 0; i < dist.length; i++) {
			// System.out.println("i: "+i+" index: "+index+" dist: "+dist[index][i]);
			if (dist[index][i] >= 100 && !wasVisited[i] && dist[index][i] < minDist) {
				minDist = dist[index][i];
				minIndex = i;
			}
		}
		// This will return -1 when no route exists
		return minIndex;
	}
	
	public static int getNextBadIndex(double[][] dist, int index, boolean[] wasVisited) {
		double minDist = 10000000;// very large distance that is greater than all others
		int minIndex = -1;
		for (int i = 0; i < dist.length; i++) {
			// System.out.println("i: "+i+" index: "+index+" dist: "+dist[index][i]);
			if (dist[index][i] > 0 && !wasVisited[i] && dist[index][i] < minDist) {
				minDist = dist[index][i];
				minIndex = i;
			}
		}
		// This will return -1 when no route exists
		return minIndex;
	}

	
	public static int[] deadEndFix(int [] route, double[][] dist, int index, int runs) {
		//take index that can't be added onto the end of the array and slot it into the array at the best pos.
		double minDist=100000000;
		int A=0;
		int B=0;
		//route[runs]=0;
		
		for(int i=0;i<runs-1;i++) {
			double cityA = dist[index][route[i]];
			double cityB = dist[index][route[i + 1]];
			double distA2C2B = cityA + cityB;
			if (cityA >= 100 && cityB >= 100 && distA2C2B < minDist) {
				minDist = distA2C2B;
				A = i;
				B = i + 1;
			}
		}
		
		for (int j = runs; j > A; j--) {
			route[j] = route[j - 1];
		}
		route[B]=index;
		
		
		return route;
	}
	
	public static int[] reverseArray(int [] route) {
		int [] revRoute= new int [route.length];
		int x=route.length-1;
		for(int i=0;i<route.length;i++) {
			revRoute[x]=route[i];
			x--;
		}
		return revRoute;
	}
	
	public static int[] opt2(int[] route, double[][]dist) {
		Random rd= new Random();
		int check1 = rd.nextInt(route.length-2);
		while(check1==0 | check1==route.length-2 | check1==route.length) {
			check1=rd.nextInt(route.length-2);
		}
		int check2 = rd.nextInt(route.length-2);
		while(check2==0| check2==route.length-2| check2==check1) {
			check2=rd.nextInt(route.length-2);
		}
		
		//get distance between check1 and next pos
		double distA2B = dist[route[check1]][route[check1+1]];
		double distC2D = dist[route[check2]][route[check2+1]];
		double distB2E = dist[route[check1+1]][route[check1+2]];
		double distF2C = dist[route[check2-1]][route[check2]];
		
		double distA2C = dist[route[check1]][route[check2]];
		double distB2D = dist[route[check1+1]][route[check2+1]];
		
		
		double distC2E = dist[route[check2]][route[check1+2]];
		double distF2B = dist[route[check2-1]][route[check1+1]];
		
		double oldPath = distA2B+distC2D+distB2E+distF2C;
		double newPath = distA2C+distB2D+distC2E+distF2B;
		//Check if swap is needed
		if((oldPath>newPath| distA2B<100) && distA2C>=100 && distB2D>=100 && distC2E>=100 && distF2B>=100) {
			//swap b and c
			//System.out.println("Swapping: "+route[check1+1]+" & "+route[check2]);
			int oldB = route[check1+1]; //hold B
			//move C to B
			route[check1+1]= route[check2];
			//put B in C
			route[check2]=oldB;
		}
		
		return route;
		
	}
	
	public static int[] nonRandOpt2(int[] route, double[][] dist) {
		for (int i = 0; i < route.length - 2; i++) {
			for (int j = route.length - 2; j > i; j--) {
				int check1 = i;
				int check2 = j;

				double distA2B = dist[route[check1]][route[check1 + 1]];
				double distC2D = dist[route[check2]][route[check2 + 1]];
				double distB2E = dist[route[check1 + 1]][route[check1 + 2]];
				double distF2C = dist[route[check2 - 1]][route[check2]];

				double distA2C = dist[route[check1]][route[check2]];
				double distB2D = dist[route[check1 + 1]][route[check2 + 1]];

				double distC2E = dist[route[check2]][route[check1 + 2]];
				double distF2B = dist[route[check2 - 1]][route[check1 + 1]];

				double oldPath = distA2B + distC2D + distB2E + distF2C;
				double newPath = distA2C + distB2D + distC2E + distF2B;

				if ((oldPath > newPath) && distA2C >= 100 && distB2D >= 100
						&& distC2E >= 100 && distF2B >= 100) {
					// swap b and c
					// System.out.println("Swapping: "+route[check1+1]+" & "+route[check2]);
					int oldB = route[check1 + 1]; // hold B
					// move C to B
					route[check1 + 1] = route[check2];
					// put B in C
					route[check2] = oldB;
				}
			}
		}
		return route;
	}
	
	
	public static int[] nonRandOpt2part(int[] route, double[][] dist) {
		for (int i = 0; i < route.length/2 - 2; i++) {
			for (int j = route.length/2 - 2; j > i; j--) {
				int check1 = i;
				int check2 = j;

				double distA2B = dist[route[check1]][route[check1 + 1]];
				double distC2D = dist[route[check2]][route[check2 + 1]];
				double distB2E = dist[route[check1 + 1]][route[check1 + 2]];
				double distF2C = dist[route[check2 - 1]][route[check2]];

				double distA2C = dist[route[check1]][route[check2]];
				double distB2D = dist[route[check1 + 1]][route[check2 + 1]];

				double distC2E = dist[route[check2]][route[check1 + 2]];
				double distF2B = dist[route[check2 - 1]][route[check1 + 1]];

				double oldPath = distA2B + distC2D + distB2E + distF2C;
				double newPath = distA2C + distB2D + distC2E + distF2B;

				if ((oldPath > newPath) && distA2C >= 100 && distB2D >= 100
						&& distC2E >= 100 && distF2B >= 100) {
					// swap b and c
					// System.out.println("Swapping: "+route[check1+1]+" & "+route[check2]);
					int oldB = route[check1 + 1]; // hold B
					// move C to B
					route[check1 + 1] = route[check2];
					// put B in C
					route[check2] = oldB;
				}
			}
		}
		
		for (int i = route.length/2; i < route.length - 2; i++) {
			for (int j = route.length - 2; j > i; j--) {
				int check1 = i;
				int check2 = j;

				double distA2B = dist[route[check1]][route[check1 + 1]];
				double distC2D = dist[route[check2]][route[check2 + 1]];
				double distB2E = dist[route[check1 + 1]][route[check1 + 2]];
				double distF2C = dist[route[check2 - 1]][route[check2]];

				double distA2C = dist[route[check1]][route[check2]];
				double distB2D = dist[route[check1 + 1]][route[check2 + 1]];

				double distC2E = dist[route[check2]][route[check1 + 2]];
				double distF2B = dist[route[check2 - 1]][route[check1 + 1]];

				double oldPath = distA2B + distC2D + distB2E + distF2C;
				double newPath = distA2C + distB2D + distC2E + distF2B;

				if ((oldPath > newPath) && distA2C >= 100 && distB2D >= 100
						&& distC2E >= 100 && distF2B >= 100) {
					// swap b and c
					// System.out.println("Swapping: "+route[check1+1]+" & "+route[check2]);
					int oldB = route[check1 + 1]; // hold B
					// move C to B
					route[check1 + 1] = route[check2];
					// put B in C
					route[check2] = oldB;
				}
			}
		}

		return route;
	}
	
	public static void toHackerRank(double[][]route, int[] path, double [][]coords) {
		for(int i=0;i<path.length;i++) {
			for(int j=0;j<coords.length;j++) {
				if(coords[j][0]==route[path[i]][0] && coords[j][1]==route[path[i]][1]) {
					System.out.print(j+",");
				}
			}
		}
	}
	
	public static int[] optimiseRoute(int [] route, double [][] dist) {
		int minIndex=-1;
		
		double minDist=0;
		int A=0;
		int B=0;
		for(int i=1;i<route.length-2;i++) {
			minDist= dist[i][i+1] + dist[i-1][i];
			minIndex = route[i];
			if(dist[i][i+1]<100 | dist[i-1][i]<100) minDist=100000;
			for(int j=1;j<route.length-2;j++) {
				if(dist[i][j]>=100 && dist[i][j+1]>=100 && dist[i][j]+dist[i][j+1]<minDist && dist[i-1][i+1]>=100) {
					minDist= dist[i][j];
					A=j;
					B=j+1;
					
				}
			}
			if(minIndex== route[i]) {
			for (int j = i; j > A; j--) {
				route[j] = route[j - 1];
			}
			route[B]=minIndex;		
		}
		}
		
		
		return route;
	}
}
