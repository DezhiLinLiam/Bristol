package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


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
    private ArrayList<String> consumedArrayList = new ArrayList<>();

    private ArrayList<String> isUsingCharacter = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        File entitiesFile = Paths.get("config" + File.separator + "extended-entities.dot").toAbsolutePath().toFile();
        File actionsFile = Paths.get("config" + File.separator + "extended-actions.xml").toAbsolutePath().toFile();
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
        consumedArrayList = actionsLoad.getConsumeName();
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
        Players currentPlayer = new Players("");
        String[] words = {};

        command = command.toLowerCase();
        if(!validateString(command.substring(0, command.indexOf(":")))){
            return "The user name is invalid!";
        }
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
        ArrayList<ArrayList<GameEntity>> currentLocationHashmap = new ArrayList<>();
        ArrayList<String> paths = new ArrayList<>();
        
        paths = entitiesLoad.getPathCanGo(currentLocation);
        currentLocationHashmap = entitiesLoad.getEntitiesHashmap(currentLocation);
        switch(words[1]){
            case "inv":
                reString = currentPlayer.getInv();
                System.out.println(reString);
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
                    currentLocationHashmap = entitiesLoad.getEntitiesHashmap(currentLocation);
                    

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
                for (String path : entitiesLoad.paths.getPath(currentLocation)){
                    reString = reString + path + "\n";
                }
             
                System.out.println(reString);
                return reString;
            case "health":
                return "Your health is "+currentPlayer.health;

            default:
                Hash hash = actionsLoad.getActionHashMap();
                ArrayList<String> furnitures = new ArrayList<>();
                ArrayList<String> artefacts = new ArrayList<>();
                ArrayList<String> characters = new ArrayList<>();
                ArrayList<String> consumeds = new ArrayList<>();
                ArrayList<String> produceds = new ArrayList<>();
                int count = 0;
                boolean isEnter = false;
                for(String subject:hash.getSubjects(words[1])){
                    if(furnitureArrayList.contains(subject)){
                        furnitures.add(subject);
                    }
                    if(artefactArrayList.contains(subject)){
                        artefacts.add(subject);
                    }
                    if(characterArrayList.contains(subject))
                        characters.add(subject);
                }

                if(!containsOnlyStringsFrom(words,furnitures,artefacts,characters)){
                    return "Your input is invalid";
                }

                for(String subject:hash.getConsumed(words[1])){
                    if(consumedArrayList.contains(subject)){
                        consumeds.add(subject);
                    }
                }
                for(String subject:hash.getProduced(words[1])){
                    if(producedArrayList.contains(subject)){
                        produceds.add(subject);
                    }
                }


                for(String furniture:furnitures){
                    if(entitiesLoad.isFurnitureExist(currentLocation, furniture)){
                        count++;
                    }
                }
                if(count != furnitures.size()){
                    return "You  are not in the correct position!";
                }else{
                    isEnter = true;
                }
                for(String artefact:artefacts){
                    if(entitiesLoad.isArtefactExist(currentLocation,artefact) || currentPlayer.getInv().contains(artefact)){
                        count++;
                    }
                }

                if((count == furnitures.size()+artefacts.size()) && isEnter){
                    for(String consumed: consumeds){
                        if(consumed.equals("health")) {
                            currentPlayer.getHurt();

                        }else{
                            if (currentPlayer.inv.contains(consumed)) {
                                currentPlayer.inv.remove(consumed);
                            }
                            if (entitiesLoad.isArtefactExist(currentLocation, consumed)) {
                                entitiesLoad.removeArtefactName(currentLocation, consumed);
                            }
                            if (entitiesLoad.isFurnitureExist(currentLocation, consumed)) {
                                entitiesLoad.removeFurnitureName(currentLocation, consumed);
                            }
                            if (entitiesLoad.isCharacterExist(currentLocation, consumed)) {
                                entitiesLoad.removeCharacterName(consumed);
                            }
                            if (entitiesLoad.isPathExist(currentLocation, consumed)) {
                                entitiesLoad.removePath(currentLocation, consumed);
                            }
                        }

                    }
                    for(String produced : produceds){
                        if(produced.equals("health")){
                            currentPlayer.addHealth();
                        }else {
                            if (artefactArrayList.contains(produced))
                                entitiesLoad.addArtefactName(currentLocation, produced);
                            if (furnitureArrayList.contains(produced))
                                entitiesLoad.addFurnitureName(currentLocation, produced);
                            if (characterArrayList.contains(produced)) {
                                if (isUsingCharacter.contains(produced)) {
                                    entitiesLoad.removeCharacterName(produced);
                                }
                                entitiesLoad.addFurnitureName(currentLocation, produced);
                                isUsingCharacter.add(characterArrayList.get(characterArrayList.indexOf(produced)));
                            }
                            if (locationArrayList.contains(produced)) {
                                entitiesLoad.addPath(currentLocation, produced);
                            }
                        }
                    }
                    for(String consumed: consumeds) {
                        if (consumed.equals("health")) {
                            if (currentPlayer.health == 0) {
                                for(String entity: currentPlayer.inv){
                                    entitiesLoad.addArtefactName(currentLocation,entity);
                                }
                                currentPlayer.reset();
                                return "You died!";
                            }
                        }
                    }
                    return actionsLoad.hash.getNarration(words[1]).get(0);
                }else{
                    return "Your command is invalid!";
                }
        }

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
    public static boolean containsOnlyStringsFrom(String[] arr, ArrayList<String> allowedStrings1, ArrayList<String> allowedStrings2,ArrayList<String> allowedStrings3 ) {
        Set<String> combinedStrings = new HashSet<>();
        combinedStrings.addAll(allowedStrings1);
        combinedStrings.addAll(allowedStrings2);
        combinedStrings.addAll(allowedStrings3);



        if(arr.length>2) {
            for (int i = 2; i < arr.length; i++) {

                if (!combinedStrings.contains(arr[i])) {
                    return false;
                }
            }
        }
        return true;
    }


    private void addPlayer(String command){
        boolean isadd = true;
        String uname;
        if(command.contains(":")){
            uname = command.substring(0, command.indexOf(":")).toLowerCase();
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
        String[] standardCommands = {"inventory", "inv", "get", "drop", "goto", "look","health"};
        ArrayList<String> allEntities = new ArrayList<>();
        ArrayList<String> standard = new ArrayList<>();
        ArrayList<String> specific = new ArrayList<>();
        ArrayList<String> entities = new ArrayList<>();
        ArrayList<String> multipleKeyphrase = actionsLoad.getMultipleKeyphrase();

        allEntities.addAll(locationArrayList);
        allEntities.addAll(artefactArrayList);
        allEntities.addAll(furnitureArrayList);
        allEntities.addAll(characterArrayList);
        allEntities.addAll(producedArrayList);

         commands = commands.toLowerCase();
        System.out.println(commands);
         for(String a : multipleKeyphrase){
             a = " "+ a + " ";
             if(commands.contains(a)) {
                 String b = a.replace(" ","");
                 b = " "+ b + " ";
                 commands = commands.replace(a, b);
             }
         }

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
                int count = 0;
                for (int i = 0; i < command.length; i++) {
                    if (command[i].equals(standardCommand)) {
                        count++;
                        if (count > 1) {
                            return "Your input is invalid;";
                        }
                    }
                }

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
                        if(allEntities.contains(entity)){
                            return "Your command is invalid!";
                        }
                    }
                }
                if(standard.get(i).equals("look")){
                    for(String entity: entities){
                        if(allEntities.contains(entity)){
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
            
            if(standard.size() > 1 ){
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
    public static boolean hasDuplicateStrings(String[] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i].equals(array[j])) {
                    return true;
                }
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
        return -1;
    }

    public static boolean validateString(String input) {
        String regex = "^[A-Za-z\\s\\-\\']*$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(input).matches();
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
