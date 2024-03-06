package edu.uob;
import java.util.ArrayList;
import java.util.HashMap;

public class Hash {
    private HashMap<String, GameAction> properties = new HashMap<>();

    public Hash() {
        properties = new HashMap<>();
    }

    public void setProperty(String key, GameAction value) {
        properties.put(key, value);
    }

    public GameAction getProperty(String key) {
        return properties.get(key);
    }
    public ArrayList<String> getSubjects(String key){
        return properties.get(key).getSubjects();
    }
    public ArrayList<String> getConsumed(String key){
        return properties.get(key).getConsumed();
    }
    public ArrayList<String> getProduced(String key){
        return properties.get(key).getProduced();
    }
    public ArrayList<String> getNarration(String key){
        return properties.get(key).getNarration();
    }
}
