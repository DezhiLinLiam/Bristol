package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.text.html.HTMLDocument.Iterator;

/** This class implements the STAG server. */
public final class GameServer {
    private static final char END_OF_TRANSMISSION = 4;
    private ActionsLoad actionsLoad;
    private EntitiesLoad entitiesLoad;
    private ArrayList<Players> players = new ArrayList<>();
    private ArrayList<String> locationArrayList = new ArrayList<>();
    private ArrayList<String> artefactArrayList = new ArrayList<>();
    private ArrayList<String> furnitureArrayList = new ArrayList<>();
    private ArrayList<String> characterArrayList = new ArrayList<>();
    private ArrayList<String> producedArrayList = new ArrayList<>();
    private ArrayList<String> subjectArrayList = new ArrayList<>();
    private ArrayList<String> keyphraseArrayList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "basic-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "basic-actions.xml").toAbsolutePath().toFile();
        GameServer server = new GameServer(entitiesFile, actionsFile);
        server.blockingListenOn(8888);
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.GameServer(File, File)}) otherwise we won't be able to mark
    * your submission correctly.
    *
    * <p>You MUST use the supplied {@code entitiesFile} and {@code actionsFile}
    *
    * @param entitiesFile The game configuration file containing all game entities to use in your game
    * @param actionsFile The game configuration file containing all game actions to use in your game
    *
    */
    public GameServer(File entitiesFile, File actionsFile) {

        actionsLoad = new ActionsLoad(actionsFile);
        entitiesLoad = new EntitiesLoad(entitiesFile);
        keyphraseArrayList = actionsLoad.getKeyphraseName();
        producedArrayList = actionsLoad.getProducedName();
        subjectArrayList = actionsLoad.getSubjectName();
        locationArrayList = entitiesLoad.getLocationName();
        artefactArrayList = entitiesLoad.getArtefactName();
        furnitureArrayList = entitiesLoad.getFurnitureName();
        characterArrayList = entitiesLoad.getCharacterName();
        
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.GameServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming game commands and carries out the corresponding actions.
    */
    public String handleCommand(String command) {
        Players currentPlayer = new Players("Liam");
        String[] words = {};

        command = command.toLowerCase();
        addPlayer(command);
        command = simplifyCommand(command);

        System.out.println(command);

        if(command.toLowerCase().contains("invalid")){
            return command;
        }

        for(Players player : players){
            words = command.split(" ");
            if(player.getName().equals(words[0])){
                currentPlayer = player;
            }
        }
 
        


        String currentLocation = currentPlayer.getPosition();
        String reString = "";
        ArrayList<ArrayList<GameEntity>> currentLocationHashmap = entitiesLoad.getEntitiesHashmap(currentLocation);
        ArrayList<String> paths = entitiesLoad.getPathCanGo(currentLocation);

        switch(words[1]){
            case "inv":
                reString = currentPlayer.getInv();
                return reString;

            case "get":
                if (words.length != 3){
                    return "Your command is invalid!";
                }
                for (int i = 0; i < currentLocationHashmap.size(); i++){
                    if(!currentLocationHashmap.get(i).isEmpty() && i > 0){
                        for(GameEntity entity : currentLocationHashmap.get(i)){
                            if(i == 1){
                                if(words[2].equals(entity.getName())){
                                    currentPlayer.addInv(words[2]);
                                    entitiesLoad.removeArtefactName(currentLocation, words[2]);
                                    return "You have picked it up";
                                }
                            }
                        }
                        return "You can't get it!";
                    }
                }

            case "drop":
                if (words.length != 3){
                    return "Your command is invalid!";
                }
                if(!currentPlayer.getInv().contains(words[2])){
                    return "You don't have it";
                }else{
                    currentPlayer.removeInv(words[2]);
                    entitiesLoad.addArtefactName(currentLocation, words[2]);
                    return "You have already drop it.!";
                }

            case "goto":
                if (words.length != 3){
                    return "Your command is invalid!";
                }
                if(entitiesLoad.getPathCanGo(currentLocation).contains(words[2])){
                    currentPlayer.setPosition(words[2]);
                    currentLocation = currentPlayer.getPosition();
                    
                    return "You have arrived " + words[2];
                }else{
                    return "Your command is invalid!";
                }

            case "look":
                for (int i = 0; i < currentLocationHashmap.size(); i++){
                    if(i == 0 && !currentLocationHashmap.get(0).isEmpty()){
                        for(GameEntity entity : currentLocationHashmap.get(0)){
                            reString = "You are in " + entity.getDescription()+". You can see:\n";
                        }
                    }else{
                        if(!currentLocationHashmap.get(i).isEmpty()){
                            for(GameEntity entity : currentLocationHashmap.get(i)){
                                reString = reString + entity.getDescription() + "\n";
                            }
                        }
                    }
                }
                for(Players player:players){
                    if(!player.getName().equals(currentPlayer.getName()) && player.getPosition().equals(currentLocation)){
                       reString = reString + "player: " + player.getName()+ "\n";
                    }
                }
                reString = reString + "You can access from here: \n";
                for (String path : paths){
                    reString = reString + path + "\n";
                }
             
                System.out.println(reString);
                return reString;
            default: break;
        }

        switch(words[1]){
            case "open":
                int otherOpenCount = 0;
                for (String word : words) {
                    if (word.equals("trapdoor")) {

                    } else if (word.equals("key")) {
   
                    }else{
                        if(!(word.equals(currentPlayer.getName()) || word.equals("open")|| word.equals(currentLocation))) {
                            otherOpenCount++;
                        }
                    }
                }

                if ( otherOpenCount == 0){
                    if((currentPlayer.getInv().contains("key") || entitiesLoad.isArtefactExist(currentLocation, "key")) && currentLocation.equals("cabin")){
                       if(currentPlayer.getInv().contains("key")) currentPlayer.removeInv("key");
                       if(entitiesLoad.isArtefactExist(currentLocation, "key")) entitiesLoad.removeArtefactName(currentLocation, "key");
                        currentPlayer.addStoreRoom("key");
                        entitiesLoad.addPath("cabin", "cellar");
                        return actionsLoad.getActionHashMap().getNarration("open").get(0);
                    }else{
                        if(!currentPlayer.getInv().contains("key")) return "You don't have the key";
                        else return "The location where you are can't find a trapdoor";
                    }
                }else{
                    return "Your command is invalid!";
                }

            case "chop":
            
                int otherChopCount = 0;

                for (String word : words) {
                    if (word.equals("tree")) {
                      
                    } else if (word.equals("axe")) {
                      
                    } else{
                        if(!(word.equals(currentPlayer.getName()) || word.equals("chop")|| word.equals(currentLocation))) otherChopCount++;
                    }
                }

             
                if (otherChopCount == 0){
                    if((currentPlayer.getInv().contains("axe") || entitiesLoad.isArtefactExist(currentLocation, "axe")) && currentPlayer.getPosition().equals("forest") && entitiesLoad.isFurnitureExist(currentLocation, "tree")){
                        entitiesLoad.removeArtefactName(currentLocation, "tree");
                        entitiesLoad.addArtefactName(currentLocation, "log");
                        return actionsLoad.getActionHashMap().getNarration("chop").get(0);
                    }else{
                        if(currentPlayer.getInv().contains("axe")) return "You are not in forest!";
                        else return "You don't have an axe!";
                    }
                }else{
                    return "Your command is invalid!";
                }

            case "drink":
               
                int otherDrinkCount = 0;

                for (String word : words) {
                    if (word.equals("potion")) {
                       
                    } else{
                        if(!(word.equals(currentPlayer.getName()) || word.equals("drink" )|| word.equals(currentLocation))) otherDrinkCount++;
                    }
                }

                if (otherDrinkCount == 0){
                    if(currentPlayer.getInv().contains("potion") || entitiesLoad.isArtefactExist(currentLocation, "potion") ){
                        if(currentPlayer.getInv().contains("potion")) currentPlayer.removeInv("potion");
                        if(entitiesLoad.isArtefactExist(currentLocation, "potion")) entitiesLoad.removeArtefactName(currentLocation, "potion");
                        if(currentPlayer.health < 3)currentPlayer.addHealth();
                        else{return "Your health is already full, it cannot be restored.";}
                        return actionsLoad.getActionHashMap().getNarration("drink").get(0);
                    }else{
                        return "You do not have potion!";
                    }
                }else{
                    return "Your command is invalid!";
                }
            
            case "fight":
               
                int otherFightCount = 0;

                for (String word : words) {
                    if (word.equals("elf")) {
                        
                    } else{
                        if(!(word.equals(currentPlayer.getName()) || word.equals("fight" )|| word.equals(currentLocation))) otherFightCount++;
                    }
                }

               
                if (otherFightCount == 0){
                    if(currentLocation.equals("cellar")){
                        currentPlayer.getHurt();
                        if(currentPlayer.health == 0){
                            for(String entity : currentPlayer.inv){
                                entitiesLoad.addArtefactName(currentLocation, entity);
                            }
                            currentPlayer = new Players(currentPlayer.getName());
                            return "You are dead, and you have respawned in the cabin. ";
                        }
                        return actionsLoad.getActionHashMap().getNarration("fight").get(0);
                    }else{
                      
                        return "The elf is not here!";
                    }
                }else{
                    return "Your command is invalid!";
                }
            case "pay":
                int otherPayCount = 0;

                for (String word : words) {
                    if (word.equals("elf")) {
                       
                    } else if (word.equals("coin")) {
                       
                    } else{
                        if(!(word.equals(currentPlayer.getName()) || word.equals("pay")|| word.equals(currentLocation))) otherPayCount++;
                    }
                }

              
                if (otherPayCount == 0){
                    if((currentPlayer.getInv().contains("coin") || entitiesLoad.isArtefactExist(currentLocation, "coin"))&& currentPlayer.getPosition().equals("cellar")){
                        if (currentPlayer.getInv().contains("coin")) currentPlayer.removeInv("coin");
                        if(entitiesLoad.isArtefactExist(currentLocation, "coin")) entitiesLoad.removeArtefactName(currentLocation, "coin");
                        entitiesLoad.addArtefactName(currentLocation, "shovel");
                        return actionsLoad.getActionHashMap().getNarration("pay").get(0);
                    }else{
                        if(currentPlayer.getInv().contains("coin")) return "You are not in cellar!";
                        else return "You don't have a coin!";
                    }
                }else{
                    return "Your command is invalid!";
                }
            case "bridge":
               
                int otherBridgeCount = 0;

                for (String word : words) {
                    if (word.equals("log")) {

                    } else if (word.equals("river")) {
     
                    } else{
                        if(!(word.equals(currentPlayer.getName()) || word.equals("bridge")|| word.equals(currentLocation))) otherBridgeCount++;
                    }
                }

               
                if (otherBridgeCount == 0){
                    if((currentPlayer.getInv().contains("log") || entitiesLoad.isArtefactExist(currentLocation, "log"))&& currentPlayer.getPosition().equals("riverbank")){
                        if(currentPlayer.getInv().contains("log"))currentPlayer.removeInv("log");
                        if(entitiesLoad.isArtefactExist(currentLocation, "log")) entitiesLoad.removeArtefactName(currentLocation, "log");
                        entitiesLoad.addPath("riverbank", "clearing");
                        return actionsLoad.getActionHashMap().getNarration("bridge").get(0);
                    }else{
                        if(currentPlayer.getInv().contains("coin")) return "You are not in cellar!";
                        else return "You don't have a coin!";
                    }
                }else{
                    return "Your command is invalid!";
                }

            case "dig":
              
                int otherDigCount = 0;

                for (String word : words) {
                    if (word.equals("ground")) {
                     
                    } else if (word.equals("shovel")) {
                       
                    } else{
                        if(!(word.equals(currentPlayer.getName()) || word.equals("dig")|| word.equals(currentLocation))) otherDigCount++;
                    }
                }

               
                if (otherDigCount == 0){
                    if((currentPlayer.getInv().contains("shovel") || entitiesLoad.isArtefactExist(currentLocation, "shovel")) && currentPlayer.getPosition().equals("clearing") && entitiesLoad.isFurnitureExist(currentLocation, "ground")){
                        entitiesLoad.removeFurnitureName(currentLocation, "ground");
                        entitiesLoad.addFurnitureName(currentLocation, "hole");
                        entitiesLoad.addArtefactName(currentLocation, "gold");
                        return actionsLoad.getActionHashMap().getNarration("dig").get(0);
                    }else{
                        if(currentPlayer.getInv().contains("coin")) return "You are not at clearing!";
                        else return "You don't have a shovel!";
                    }
                }else{
                    return "Your command is invalid!";
                }
            case "blow":
             
                int otherHornCount = 0;

                for (String word : words) {
                    if (word.equals("horn")) {

                    }else{
                        if(!(word.equals(currentPlayer.getName()) || word.equals("blow")|| word.equals(currentLocation))) otherHornCount++;
                    }
                }
           
              
                if (otherHornCount == 0){
                    if(currentPlayer.getInv().contains("horn") || entitiesLoad.isArtefactExist(currentLocation, "horn")){
                        entitiesLoad.entities.forEach((key, value) -> {
                            for (ArrayList<GameEntity> innerList : value) {
                                innerList.removeIf(gameEntity -> gameEntity.getName().equals("lumberjack"));
                            }
                        });                                
                        entitiesLoad.addCharacterName(currentLocation, "lumberjack");
                        return actionsLoad.getActionHashMap().getNarration("blow").get(0);
                        
                    }else{
                       return "You don't have a horn!";
                    }
                }else{
                    return "Your command is invalid!";
                }
                
        }
        
        return "Your command is invalid!";
    }

    //  === Methods below are there to facilitate server related operations. ===

    /**
    * Starts a *blocking* socket server listening for new connections. This method blocks until the
    * current thread is interrupted.
    *
    * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
    * you want to.
    *
    * @param portNumber The port to listen on.
    * @throws IOException If any IO related operation fails.
    */
    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.out.println("Connection closed");
                }
            }
        }
    }

    private void addPlayer(String command){
        boolean isadd = true;
        String uname;
        if(command.contains(":")){
            uname = command.substring(0, command.indexOf(":"));
            for(Players player:players){
                if(player.getName().equals(uname)){
                    isadd = false;
                }
            }
            if(isadd){
                Players newPlayer = new Players(uname);
                newPlayer.iniStoreRoom(producedArrayList);
                players.add(newPlayer);
            }
        }
    }

    private String simplifyCommand(String commands){
        String uname = "";
        String entitiesString = "";
        String[] standardCommands = {"inventory", "inv", "get", "drop", "goto", "look"};
        ArrayList<String> allEntities = new ArrayList<>();
        ArrayList<String> standard = new ArrayList<>();
        ArrayList<String> specific = new ArrayList<>();
        ArrayList<String> entities = new ArrayList<>();

        allEntities.addAll(locationArrayList);
        allEntities.addAll(artefactArrayList);
        allEntities.addAll(furnitureArrayList);
        allEntities.addAll(characterArrayList);
        allEntities.addAll(producedArrayList);

        if(!commands.contains(":")){
            return "Your command is invalid!";
        }
        String[] command = commands.split("[: ]");
        command = Arrays.stream(command)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
        uname = command[0];
        

        for (String entity : allEntities) {
            if(contains(command, entity)){
                if(!entities.contains(entity)){
                    entities.add(entity);
                }
            }
        }
        
        for(String entity : entities){
            entitiesString = entitiesString + entity +  " ";
        }
       
        for (String standardCommand : standardCommands) {   
            if(contains(command, standardCommand)){
                standard.add(standardCommand);
            }    
        }

        for (String specificCommand : keyphraseArrayList){
            if(contains(command, specificCommand)){
                specific.add(specificCommand);
            }
        }

       
       
        
        if(!standard.isEmpty() && !specific.isEmpty() ){
            return "Your command is invalid!";
        }

        if( standard.isEmpty() && specific.isEmpty()){
            return "Your command is invalid!";
        }
        if(!standard.isEmpty()){
            for (int i = 0; i < standard.size(); i++) {
                if (standard.get(i).equals("inventory") || standard.get(i).equals("inv")) {
                    standard.set(i, "inv");
                    for(String entity: entities){
                        if(subjectArrayList.contains(entity)){
                            return "Your command is invalid!";
                        }
                    }
                }
                if(standard.get(i).equals("look")){
                    for(String entity: entities){
                        if(subjectArrayList.contains(entity)){
                            return "Your command is invalid!";
                        }
                    }
                }

                if(standard.get(i).equals("get")){
                    for(String subjetct :subjectArrayList){
                        for(String entity:entities){
                            if(subjetct.equals(entity)){
                                if(findStringIndex(command,"get")>findStringIndex(command, entity)){
                                    return "Your command is invalid!";
                                }
                            }
                        }
                    }
                }

                if(standard.get(i).equals("drop")){
                    for(String subjetct :subjectArrayList){
                        for(String entity:entities){
                            if(subjetct.equals(entity)){
                                if(findStringIndex(command,"drop")>findStringIndex(command, entity)){
                                    return "Your command is invalid!";
                                }
                            }
                        }
                    }
                }

                if(standard.get(i).equals("goto")){
                    for(String subjetct :subjectArrayList){
                        for(String entity:entities){
                            if(subjetct.equals(entity)){
                                if(findStringIndex(command,"goto")>findStringIndex(command, entity)){
                                    return "Your command is invalid!";
                                }
                            }
                        }
                    }
                }

            }
            
            if(standard.size() > 1){
                return  "Invalid! Your command contains two standard commands!";
            }

            return uname + " " + standard.get(0) + " " + entitiesString;
        }
        if(!specific.isEmpty()){
            for (int i = 0; i < specific.size(); i++) {
                if (specific.get(i).equals("open") || specific.get(i).equals("unlock")) {
                    specific.set(i, "open");
                }
                if (specific.get(i).equals("chop") || specific.get(i).equals("cut") || specific.get(i).equals("cut down")) {
                    specific.set(i, "chop");
                }
                if (specific.get(i).equals("fight") || specific.get(i).equals("hit") || specific.get(i).equals("attack")) {
                    specific.set(i, "fight");
                }
            }
            
            specific = (ArrayList<String>) specific.stream()
                .distinct()
                .collect(Collectors.toList());


            if(specific.size() > 1){
                return  "Invalid! Your command contains two specific commands!";
            }

            return uname + " " + specific.get(0) + " " + entitiesString;
        }
        return "Your command is invalid!";
    }

    public static boolean contains(String[] array, String target) {
        for (String element : array) {
            if (element.equals(target)) {
                return true;
            }
        }
        return false;
    }
    
    public int findStringIndex(String[] array, String target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target)) {
                return i;
            }
        }
        return -1; // 如果没有找到目标字符串，返回 -1
    }
    /**
    * Handles an incoming connection from the socket server.
    *
    * <p>This method isn't used for marking. You shouldn't have to modify this method, but you can if
    * * you want to.
    *
    * @param serverSocket The client socket to read/write from.
    * @throws IOException If any IO related operation fails.
    */
    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {
            System.out.println("Connection established");
            String incomingCommand = reader.readLine();
            if(incomingCommand != null) {
                System.out.println("Received message from " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }
}
