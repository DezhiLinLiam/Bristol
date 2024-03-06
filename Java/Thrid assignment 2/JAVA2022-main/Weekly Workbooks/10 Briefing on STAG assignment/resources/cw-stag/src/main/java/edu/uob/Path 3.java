package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;

public class Path {
    private HashMap<String, ArrayList<String>> paths = new HashMap<>();

    public void addPath(String start, String destination) {
        if(paths.containsKey(start)){
            ArrayList<String> newDestination = paths.get(start);
            newDestination.add(destination);
            paths.put(start, newDestination);
        }else{
            ArrayList<String> newDestination = new ArrayList<>();
            newDestination.add(destination);
            paths.put(start, newDestination);
        }
    }

    public ArrayList<String> getPath(String start) {
        return paths.get(start);
    }
}
