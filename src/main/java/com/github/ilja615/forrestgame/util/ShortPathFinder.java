/*
 * Copyright (c) 2021-2022 the ForrestGame contributors.
 *
 * This file is part of ForrestGame.
 *
 * ForrestGame is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ForrestGame is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ForrestGame.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.github.ilja615.forrestgame.util;

import com.github.ilja615.forrestgame.entity.Entity;
import com.github.ilja615.forrestgame.entity.Player;
import com.github.ilja615.forrestgame.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * A path finder. Finds the shortest path to get from A to B.
 */
public class ShortPathFinder
{
    private static final int MAX_SEARCH_DISTANCE = 32;
    private Node[][] nodes;

    public ShortPathFinder()
    {
    }

    /**
     * Finds the shortest path from {@code from} to {@code to}.
     *
     * @param world the world to find the path in
     * @param from  the starting coordinate
     * @param to    the ending coordinate
     * @return the shortest path from {@code from} to {@code to}. If no path is found, this returns an empty list.
     */
    public List<Coordinate> findPath(final World world, final Coordinate from, final Coordinate to, final Entity entity)
    {
        nodes = new Node[world.WORLD_WIDTH][world.WORLD_HEIGHT];

        for (int x = 0; x < world.WORLD_WIDTH; x++)
        {
            for (int y = 0; y < world.WORLD_HEIGHT; y++)
            {
                nodes[x][y] = new Node(x, y);
            }
        }

        // this is codde for to find a path who is short !!!
        if (!world.isWithinWorld(to))
            return new ArrayList<>();

        final List<Node> closed = new ArrayList<>();
        final List<Node> open = new SortedList<>();

        open.add(nodes[from.x()][from.y()]);

        // idk if this is intentional, @ilja615
        // nodes[tx][ty].setParent(null);

        int maxDepth = 0;

        while (maxDepth < MAX_SEARCH_DISTANCE && !open.isEmpty())
        {
            final Node current = open.get(0);
            if (current == nodes[to.x()][to.y()]) break;

            open.remove(current);
            //closed.add(current);

            for (int x = -1; x < 2; x++)
            {
                for (int y = -1; y < 2; y++)
                {
                    // not a neighbour, its the current tile
                    if (x == 0 && y == 0) continue;
                    // it can't go diagonal.
                    if (x != 0 && y != 0) continue;
                    // idk why it has to continue when going left or down idk.
                    //if (x < 0 || y < 0) continue;
                    // determine the location of the neighbor and evaluate it
                    final int xp = x + current.getX();
                    final int yp = y + current.getY();

                    if ((entity instanceof Player && world.isWithinWorld(new Coordinate(xp, yp))) || (!(entity instanceof Player) && world.isWithinView(new Coordinate(xp, yp))))
                    {
                        if (!world.getTileAt(xp, yp).isObstacle(entity))
                        {
                            // Movement cost :
                            final float movementCost = 1.0f;
                            final float nextStepCost = current.getCost() + movementCost;
                            final Node neighbor = nodes[xp][yp];
                            //  map.pathFinderVisited(xp, yp);

                            // if the node hasn't already been processed and discarded then
                            // reset it's cost to our current cost and add it as a next possible
                            // step (i.e. to the open list)
                            if (!closed.contains(neighbor))
                            {
                                neighbor.setParent(current);
                                neighbor.setCost(nextStepCost);
                                neighbor.setHeuristic((float) Math.sqrt(Math.sqrt(to.x() - xp) + Math.sqrt(to.y() - yp)));
                                maxDepth = Math.max(maxDepth, neighbor.setParent(current));
                                closed.add(neighbor);
                                open.add(neighbor);
                            }

                            if (nextStepCost < neighbor.getCost())
                            {
                                neighbor.setParent(current);
                                neighbor.setCost(nextStepCost);
                                neighbor.setHeuristic((float) Math.sqrt(Math.sqrt(to.x() - xp) + Math.sqrt(to.y() - yp)));
                                open.add(neighbor);
                                //closed.add(neighbor);
                            }
                        }
                    }
                }
            }
        }

        // in this case is no path soo it has to return empty :T
        if (nodes[to.x()][to.y()].getParent() == null)
            return new ArrayList<>();

        // in this case is ther path whoo yey \o/ !!! :D :] happy momment'
        final List<Coordinate> path = new ArrayList<>();
        Node target = nodes[to.x()][to.y()];

        while (target != nodes[from.x()][from.y()])
        {
            path.add(0, new Coordinate(target.getX(), target.getY()));
            target = target.getParent();
        }

        path.add(0, new Coordinate(from.x(), from.y()));

        // thats it, we have our path
        return path;
    }

    private static class Node implements Comparable<Node>
    {
        private final int x;
        private final int y;
        private float cost = 0F;
        private Node parent = null;
        private float heuristic = 0F;
        private int depth = 0;

        public Node(final int x, final int y)
        {
            this.x = x;
            this.y = y;
        }

        public int getX()
        {
            return x;
        }

        public int getY()
        {
            return y;
        }

        public float getCost()
        {
            return cost;
        }

        public void setCost(final float cost)
        {
            this.cost = cost;
        }

        public Node getParent()
        {
            return parent;
        }

        public int setParent(final Node parent)
        {
            this.depth = parent.getDepth() + 1;
            this.parent = parent;

            return getDepth();
        }

        public float getHeuristic()
        {
            return heuristic;
        }

        public void setHeuristic(final float heuristic)
        {
            this.heuristic = heuristic;
        }

        public int getDepth()
        {
            return depth;
        }

        @Override
        public int compareTo(final Node other)
        {
            final float f = getHeuristic() + getCost();
            final float of = other.getHeuristic() + other.getCost();

            return Float.compare(f, of);
        }

        @Override
        public String toString()
        {
            return "(" + x + ", " + y + ")";
        }
    }
}
