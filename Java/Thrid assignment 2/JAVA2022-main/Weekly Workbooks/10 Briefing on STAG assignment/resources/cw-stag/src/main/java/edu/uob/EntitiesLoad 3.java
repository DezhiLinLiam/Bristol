package edu.uob;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileReader;
import com.alexmerz.graphviz.Parser;
import com.alexmerz.graphviz.ParseException;
import com.alexmerz.graphviz.objects.Graph;
import com.alexmerz.graphviz.objects.Node;
import com.alexmerz.graphviz.objects.Edge;

public class EntitiesLoad {
    HashMap<String, ArrayList<ArrayList<GameEntity>>> entities = new HashMap<>();
    ArrayList<GameEntity> allEntities = new ArrayList<>();
    ArrayList<String> locationArrayList = new ArrayList<>();
    ArrayList<String> artefactArrayList = new ArrayList<>();
    ArrayList<String> furnitureArrayList = new ArrayList<>();
    ArrayList<String> characterArrayList = new ArrayList<>();
    Path paths = new Path();

    public EntitiesLoad(File entitiesFile) {
        try {
            Parser parser = new Parser();
            FileReader reader = new FileReader(entitiesFile);
            parser.parse(reader);
            Graph wholeDocument = parser.getGraphs().get(0);
            ArrayList<Graph> sections = wholeDocument.getSubgraphs();

           ArrayList<Graph> locations = sections.get(0).getSubgraphs();
           for (Graph location : locations) {
                Node locationDetails = location.getNodes(false).get(0);
                ArrayList<Graph> subgraphs = location.getSubgraphs();
                String locationName = locationDetails.getId().getId();
                String locationDescription = locationDetails.getAttribute("description");
                ArrayList<GameEntity> descriptions = new ArrayList<>();
                ArrayList<GameEntity> artefacts = new ArrayList<>();
                ArrayList<GameEntity> furnitures = new ArrayList<>();
                ArrayList<GameEntity> characters = new ArrayList<>();
                ArrayList<ArrayList<GameEntity>> marginArrayList = new ArrayList<>();


                Location location1 = new Location(locationName,locationDescription);
                locationArrayList.add(locationName);
                descriptions.add(location1);
                allEntities.add(location1);
               for (Graph subgraph : subgraphs){
                    String subgraphType = subgraph.getId().getId();
                    ArrayList<Node> subgraphNodes = subgraph.getNodes(false);

                    if (subgraphType.equals("artefacts")) {
                        for (Node artefactNode : subgraphNodes) {
                            String artefactName = artefactNode.getId().getId();
                            String artefactDescription = artefactNode.getAttribute("description");
                            Artefact artefact = new Artefact(artefactName, artefactDescription); 
                            artefactArrayList.add(artefactName);
                            artefacts.add(artefact);
                            allEntities.add(artefact);
                        }
                    }else if(subgraphType.equals("furniture")){
                        for (Node furnitureNode : subgraphNodes) {
                            String furnitureName = furnitureNode.getId().getId();
                            String furnitureDescription = furnitureNode.getAttribute("description");
                            Furniture furniture = new Furniture(furnitureName, furnitureDescription);
                            furnitureArrayList.add(furnitureName);
                            furnitures.add(furniture);
                            allEntities.add(furniture);
                        }
                    }else if(subgraphType.equals("characters")){
                        for (Node characterNode : subgraphNodes) {
                            String characterName = characterNode.getId().getId();
                            String characterDescription = characterNode.getAttribute("description");
                            Character character = new Character(characterName, characterDescription); 
                            characterArrayList.add(characterName);
                            characters.add(character);
                            allEntities.add(character);
                        }
                    }
               }
               marginArrayList.add(descriptions);
               marginArrayList.add(artefacts);
               marginArrayList.add(furnitures);
               marginArrayList.add(characters);
               entities.put(locationName, marginArrayList);
           }

            ArrayList<Edge> pathsMap = sections.get(1).getEdges();
            for (Edge path : pathsMap) {
                Node fromLocationNode = path.getSource().getNode();
                String fromName = fromLocationNode.getId().getId();
                Node toLocationNode = path.getTarget().getNode();
                String toName = toLocationNode.getId().getId();

               paths.addPath(fromName,toName);
            }
        } catch(IOException e){
            e.printStackTrace();
        }catch (ParseException pe) {
            System.err.println("ParseException was thrown when attempting to read entities file");
            pe.printStackTrace();
        }
    }

    public ArrayList<String> getLocationName(){
        return locationArrayList;
    }

    public ArrayList<String> getArtefactName(){
        return artefactArrayList;
    }

    public void removeArtefactName(String key, String entity){
        ArrayList<GameEntity> artefactRomve = entities.get(key).get(1);
        for(int i = 0; i < artefactRomve.size(); i++){
            if(artefactRomve.get(i).getName().equals(entity)){
                entities.get(key).get(1).remove(i);
            }
        }
    }

    public void addArtefactName(String key, String entity){
        for(GameEntity add: allEntities) {
            if(add.getName().equals(entity)){
                entities.get(key).get(1).add(add);
                break;
            }
        };
    }

    public void removeFurnitureName(String key, String entity){
        ArrayList<GameEntity> artefactRomve = entities.get(key).get(1);
        for(int i = 0; i < artefactRomve.size(); i++){
            if(artefactRomve.get(i).getName().equals(entity)){
                entities.get(key).get(2).remove(i);
            }
        }
    }

    public void addFurnitureName(String key, String entity){
        for(GameEntity add: allEntities) {
            if(add.getName().equals(entity)){
                entities.get(key).get(2).add(add);
                break;
            }
        };
    }

    public boolean isArtefactExist(String key, String name){
        for(GameEntity entity1 : entities.get(key).get(1)){
            if(entity1.getName().equals(name)){
                return true;
            }
        }
        return false;
    }

    public boolean isFurnitureExist(String key, String name){
        for(GameEntity entity1 : entities.get(key).get(2)){
            if(entity1.getName().equals(name)){
                return true;
            }
        }
        return false;
    }
    public void removeCharacterName(String key, String entity){
        ArrayList<GameEntity> artefactRomve = entities.get(key).get(1);
        for(int i = 0; i < artefactRomve.size(); i++){
            if(artefactRomve.get(i).getName().equals(entity)){
                entities.get(key).get(3).remove(i);
            }
        }
    }

    public void addCharacterName(String key, String entity){
        for(GameEntity add: allEntities) {
            if(add.getName().equals(entity)){
                entities.get(key).get(3).add(add);
                break;
            }
        };
    }

    public ArrayList<String> getFurnitureName(){
        return furnitureArrayList;
    }

    public ArrayList<String> getCharacterName(){
        return characterArrayList;
    }

    public ArrayList<ArrayList<GameEntity>> getEntitiesHashmap(String key){
        return entities.get(key);
    }

    public ArrayList<GameEntity> getLocationDescriptionsByKey(String key){
        return entities.get(key).get(0);
    }

    public ArrayList<GameEntity> getArtefactsByKey(String key){
        return entities.get(key).get(1);
    }

    public ArrayList<GameEntity> getFurnituresByKey(String key){
        return entities.get(key).get(2);
    }

    public ArrayList<GameEntity> getCharactersByKey(String key){
        return entities.get(key).get(3);
    }

    public boolean isPathValid(String start, String destination){
        return paths.getPath(start).contains(destination);
    }

    public ArrayList<String> getPathCanGo(String key){
        return paths.getPath(key);
    }

    public void addPath(String start, String destination){
        paths.addPath(start, destination);
    }

}
