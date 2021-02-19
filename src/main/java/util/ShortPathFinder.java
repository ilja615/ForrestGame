package main.java.util;

import java.util.ArrayList;
import java.util.Collections;

import main.java.World;
import main.java.tiles.BorderRockTile;

public class ShortPathFinder 
{
	private static ArrayList closed = new ArrayList();
	private static SortedList open = new SortedList();
	private static int maxSearchDistance = 32;
	
	private static Node[][] nodes;
	
	public ShortPathFinder()
	{
		nodes = new Node[World.worldWidth][World.worldHight];
		for (int x=0;x<World.worldWidth;x++) {
			for (int y=0;y<World.worldHight;y++) {
				nodes[x][y] = new Node(x,y);
			}
		}
	}
	
	public static ArrayList findPath(int sx, int sy, int tx, int ty) {
		// this is codde for to find a path who is short !!!
		if (!World.isValidLocation(tx,ty,tx,ty)) 
			return null;
		
		nodes[sx][sy].cost = 0;
		nodes[sx][sy].depth = 0;
		closed.clear();
		open.clear();
		open.add(nodes[sx][sy]);
		
		nodes[tx][ty].parent = null;
		
		int maxDepth = 0;
		while ((maxDepth < maxSearchDistance) && (open.size() != 0)) 
		{
			Node current = (Node) open.first();
			if (current == nodes[tx][ty]) 
				break;
			
			open.remove(current);
			closed.add(current);
			
			for (int x=-1;x<2;x++) {
				for (int y=-1;y<2;y++) {
					// not a neighbour, its the current tile
					if ((x == 0) && (y == 0)) 
						continue;
					// it can't go diagonal .
					if ((x != 0) && (y != 0)) 
						continue;
					// it can't go oob .
					if ((x < 0) || (y < 0)) 
						continue;
					// determine the location of the neighbor and evaluate it
					int xp = x + current.x;
					int yp = y + current.y;
					
					if (World.isValidLocation(sx,sy,xp,yp)) {
						//Movement cost :
						float movementCost = 1.0f;
						float nextStepCost = current.cost + movementCost;
//						System.out.println("x: "+x+" y: "+y+" xp: "+xp+" yp: "+yp);
						Node neighbour = nodes[xp][yp];
//						map.pathFinderVisited(xp, yp);
						
						if (nextStepCost < neighbour.cost) {
							if (open.contains(neighbour)) {
								open.remove(neighbour);
							}
							if (closed.contains(neighbour)) {
								closed.remove(neighbour);
							}
						}
						
						// if the node hasn't already been processed and discarded then
						// reset it's cost to our current cost and add it as a next possible
						// step (i.e. to the open list)
						if (!open.contains(neighbour) && !(closed.contains(neighbour))) {
							neighbour.cost = nextStepCost;
							neighbour.heuristic = (float) (Math.sqrt(((tx-xp)*(tx-xp))+((ty-yp)*(ty-yp))));
							maxDepth = Math.max(maxDepth, neighbour.setParent(current));
							open.add(neighbour);
						}
					}
				}
			}
		}

		// in this case is no path soo it has to return null :T
		if (nodes[tx][ty].parent == null) {
			return null;
		}
		
		// in this case is ther path whoo yey \o/ !!! :D :] happy momment'
		ArrayList path = new ArrayList();
		Node target = nodes[tx][ty];
		while (target != nodes[sx][sy]) {
			path.add(0, new Coord(target.x, target.y));
			target = target.parent;
		}
		path.add(0, new Coord(sx, sy));
		
		// thats it, we have our path 
		return path;
	}
	
	private class Node implements Comparable 
	{
		private int x; private int y; private float cost; private Node parent; private float heuristic; private int depth;
		
		public Node(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public int setParent(Node parent) {
			depth = parent.depth + 1;
			this.parent = parent;
			
			return depth;
		}
		
		public int compareTo(Object other) {
			Node o = (Node) other;
			
			float f = heuristic + cost;
			float of = o.heuristic + o.cost;
			
			if (f < of) {
				return -1;
			} else if (f > of) {
				return 1;
			} else {
				return 0;
			}
		}
	}
}
