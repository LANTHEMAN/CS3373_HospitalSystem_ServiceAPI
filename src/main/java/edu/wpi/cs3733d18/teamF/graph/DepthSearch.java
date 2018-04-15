package edu.wpi.cs3733d18.teamF.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class DepthSearch implements PathFindingAlgorithm{
    /**
     * Static function to determine the best route from one node to another
     * given a graph
     *
     * @param graph       The graph to find path on
     * @param source      The source node in the graph
     * @param destination The destination node in the graph
     * @return An array of Nodes that represent a path through the graph
     */
    public Path getPath(Graph graph, Node source, Node destination) {
        // see if the destination exists or if src equals dst
        if (destination == null || source == null) {
            throw new AssertionError();
        }
        else if (source == destination) {
            ArrayList<Node> path = new ArrayList<>();
            path.add(source);
            return new Path(path, graph);
        }

        //create visited nodes dictionary and store the path to that node
        HashMap<Node, ArrayList<Node>> VisitedNode= new HashMap<>();

        //create the open set
        Stack<Node> OpenSet = new Stack<>();

        //initialize the open set and visited nodes
        OpenSet.add(source);
        VisitedNode.put(source,new ArrayList<Node>());
        VisitedNode.get(source).add(source);


        while (!OpenSet.isEmpty()){
            Node CurrentNode = OpenSet.pop();


            if (CurrentNode == destination){
                System.out.println("sldmdmdfgfuisaio;efwnufew");
                System.out.println(VisitedNode.get(CurrentNode));
                return new Path(VisitedNode.get(CurrentNode), graph);
            }

            else {
                for (Node neighbor : graph.getNeighbors(CurrentNode)) {
                    if (!VisitedNode.containsKey(neighbor)) {
                        OpenSet.add(neighbor);
                        VisitedNode.put(neighbor,new ArrayList<Node>());
                        VisitedNode.get(neighbor).addAll(VisitedNode.get(CurrentNode));
                        VisitedNode.get(neighbor).add(neighbor);
                    }
                }
            }



        }


    return new Path(new ArrayList<>(), graph);
    }


}