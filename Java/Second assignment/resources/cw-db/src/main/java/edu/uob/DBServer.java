package edu.uob;
import java.lang.String;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.*;

/** This class implements the DB server. */
public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private static String storageFolderPath;

    public static void main(String args[]) throws IOException {
        DBServer server = new DBServer();
        File fileToDelete = new File(storageFolderPath + File.separator +"currentDatabase.txt");

        if (fileToDelete.exists()) {
            fileToDelete.delete();
        }

        server.blockingListenOn(8888);
        if (fileToDelete.exists()) {
            fileToDelete.delete();
        }
    }

    /**
    * KEEP this signature otherwise we won't be able to mark your submission correctly.
    */
    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist !
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    /**
    * KEEP this signature (i.e. {@code edu.uob.DBServer.handleCommand(String)}) otherwise we won't be
    * able to mark your submission correctly.
    *
    * <p>This method handles all incoming DB commands and carries out the required actions.
    */
    public String handleCommand(String command) {
        // TODO implement your server logic here
        DealWithCommand DealWithCommand = new DealWithCommand();
        ArrayList<String> token = new ArrayList<>();
        ArrayList<String> originalToken= new ArrayList<>();
        ArrayList<String> conditionsToken= new ArrayList<>();
        String databaseName="",returnString;
        String currentDatabase;
        StringBuilder databaseNameBuilder = new StringBuilder(databaseName);
        String[] reservedWords = {"use", "create", "alter", "insert", "select", "update", "delete", "join", "add",
                                    "drop", "and", "or", "on", "where", "into", "set", "like","values","from",
                                    "table","database","boolean"};
        originalToken = DealWithCommand.Command(command);
        token = DealWithCommand.Command(command);
        conditionsToken = DealWithCommand.conditionString(command);
        System.out.println(conditionsToken);
        for (int i = 0; i < token.size(); i++) {
            String upperCaseWord = token.get(i);
            for (int j = 0; j < reservedWords.length; j++) {
                String lowerCaseWord = reservedWords[j];
                if (upperCaseWord.equalsIgnoreCase(lowerCaseWord)) {
                    token.set(i, lowerCaseWord);
                    break;
                }
            }
        }
        for (int i = 0; i < conditionsToken.size(); i++) {
            String upperCaseWord = conditionsToken.get(i);
            for (int j = 0; j < reservedWords.length; j++) {
                String lowerCaseWord = reservedWords[j];
                if (upperCaseWord.equalsIgnoreCase(lowerCaseWord)) {
                    conditionsToken.set(i, lowerCaseWord);
                    break;
                }
            }
        }
        for (int i = 0; i < originalToken.size(); i++) {
            String upperCaseWord = originalToken.get(i);
            for (int j = 0; j < reservedWords.length; j++) {
                String lowerCaseWord = reservedWords[j];
                if (upperCaseWord.equalsIgnoreCase(lowerCaseWord)) {
                    originalToken.set(i, lowerCaseWord);
                    break;
                }
            }
        }
        System.out.println(token);
        if(token.size()==0){
            return "[ERROR]::Your input is invalid, I can't find what command you'd like to input!";
        }


        switch (token.get(0)){
            case "use":
                if( token.size() < 2 ) {
                    returnString ="[ERROR]:: Your input about USE is invalid";
                    break;
                }
                returnString = DealWithCommand.use(token, databaseNameBuilder, originalToken);
                if(token.size()>2){
                    break;
                }
                databaseName = databaseNameBuilder.toString();
                databaseName = storageFolderPath + File.separator + databaseName;
                File folder = new File(databaseName);
                if (folder.exists() && folder.isDirectory()){
                    try {
                        File file = new File(storageFolderPath + File.separator +"currentDatabase.txt");
                        file.createNewFile();
                        FileWriter writer = new FileWriter(file);
                        writer.write(databaseName);
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    returnString = "[OK]: Using " + token.get(1) + " database now!";
                }
                else {
                    returnString = "[ERROR]:: There is not a database called " + databaseNameBuilder;
                }
                break;
            case "create":
                currentDatabase = DealWithCommand.readCurrentDatabaseTxt();
                if( token.size() < 3 ) {
                    returnString ="[ERROR]:: Your input about CREATE is invalid";
                    break;
                }
                if(Arrays.asList(reservedWords).contains(token.get(2))){
                    if(token.get(1).equals("database")){
                        returnString = "[ERROR]:: The database name you want to create is a reserve word!";
                        break;
                    }
                    if(token.get(1).equals("table")){
                        returnString = "[ERROR]:: The table name you want to create is a reserve word!";
                        break;
                    }
                }
                for(int i =0; i < token.size();i++){
                    if(token.get(i).equalsIgnoreCase("true") ||token.get(i).equalsIgnoreCase("false") ) {
                        return "[ERROR]:: The name you want to create is a reserve word!";
                    }
                }
                if(token.get(1).equals("database")){
                    returnString = DealWithCommand.createDatabase(token, originalToken);
                    break;
                } else if (token.get(1).equals("table")) {
                    if(edu.uob.DealWithCommand.readFromFile(storageFolderPath + File.separator +"currentDatabase.txt").contains("ERROR")){
                        return "[ERROR]:: You should use a database first!";
                    }
                    token.set(2, token.get(2).toLowerCase());
                    returnString = DealWithCommand.createTable(token, currentDatabase);
                }else {
                    returnString = "[ERROR]:: Your input about create command is  invalid!";
                }
                break;
            case "drop":
                if(edu.uob.DealWithCommand.readFromFile(storageFolderPath + File.separator +"currentDatabase.txt").contains("ERROR")){
                    return "[ERROR]:: You should use a database first!";
                }
                if( token.size() <= 2 ) {
                    returnString ="[ERROR]:: Your input about DROP is invalid";
                    break;
                }
                currentDatabase = DealWithCommand.readCurrentDatabaseTxt();

                if(token.get(1).equals("database")){
                    returnString = DealWithCommand.dropDatabase(token, originalToken);
                    break;
                } else if (token.get(1).equals("table")) {
                    token.set(2, token.get(2).toLowerCase());
                    if(currentDatabase.equals("[ERROR]:: You should use a database first!") ){
                        returnString = "[ERROR]:: You should use a database first!";
                        break;
                    }
                    returnString = DealWithCommand.dropTable(token, currentDatabase, originalToken);
                }else {
                    returnString = "[ERROR]:: Your input about create command is  invalid!";
                }
                break;
            case "alter":
                if(edu.uob.DealWithCommand.readFromFile(storageFolderPath + File.separator +"currentDatabase.txt").contains("ERROR")){
                    return "[ERROR]:: You should use a database first!";
                }
                if( token.size() == 1 ) {
                returnString ="[ERROR]:: Your input about ALTER is invalid";
                break;
                }
                if(Arrays.asList(reservedWords).contains(token.get(4))){
                    returnString = "[ERROR]:: The attribute name you want to alter is a reserve word!";
                    break;
                }
                for(int i =0; i < token.size();i++){
                    if(token.get(i).equalsIgnoreCase("true") ||token.get(i).equalsIgnoreCase("false") ) {
                        return "[ERROR]:: The attribute name you want to alter is a reserve word!";
                    }
                }
                token.set(2, token.get(2).toLowerCase());
                currentDatabase = DealWithCommand.readCurrentDatabaseTxt();
                returnString = DealWithCommand.alter(token, currentDatabase, originalToken);
                break;
            case "insert":
                if(edu.uob.DealWithCommand.readFromFile(storageFolderPath + File.separator +"currentDatabase.txt").contains("ERROR")){
                    return "[ERROR]:: You should use a database first!";
                }
                if(token.size()>=3) token.set(2, token.get(2).toLowerCase());
                currentDatabase = DealWithCommand.readCurrentDatabaseTxt();
                returnString = DealWithCommand.insert(token,currentDatabase,originalToken);
                break;
            case "select":
                if(edu.uob.DealWithCommand.readFromFile(storageFolderPath + File.separator +"currentDatabase.txt").contains("ERROR")){
                    return "[ERROR]:: You should use a database first!";
                }
                if(token.size() < 4){
                    return "\"[ERROR]:: Your input about SELECT command is  invalid!";
                }
                currentDatabase = DealWithCommand.readCurrentDatabaseTxt();
                returnString =DealWithCommand.select(token, currentDatabase, conditionsToken);
                break;
            case "update":
                if(edu.uob.DealWithCommand.readFromFile(storageFolderPath + File.separator +"currentDatabase.txt").contains("ERROR")){
                    return "[ERROR]:: You should use a database first!";
                }
                currentDatabase = DealWithCommand.readCurrentDatabaseTxt();
                returnString = DealWithCommand.update(token,currentDatabase,conditionsToken);
                break;
            case "delete":
                if(edu.uob.DealWithCommand.readFromFile(storageFolderPath + File.separator +"currentDatabase.txt").contains("ERROR")){
                    return "[ERROR]:: You should use a database first!";
                }
                currentDatabase =DealWithCommand.readCurrentDatabaseTxt();
                returnString = DealWithCommand.delete(token,currentDatabase,conditionsToken);
                break;
            case "join":
                if(edu.uob.DealWithCommand.readFromFile(storageFolderPath + File.separator +"currentDatabase.txt").contains("ERROR")){
                    return "[ERROR]:: You should use a database first!";
                }
                currentDatabase =DealWithCommand.readCurrentDatabaseTxt();
                returnString = DealWithCommand.join(token,currentDatabase);
                break;
            default:
                returnString ="[ERROR]::Your input is invalid, I can't find what command you'd like to input!";
                break;
        }
        return returnString;
    }




    //  === Methods below handle networking aspects of the project - you will not need to change these ! ===

    public void blockingListenOn(int portNumber) throws IOException {
        try (ServerSocket s = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);
            while (!Thread.interrupted()) {
                try {
                    blockingHandleConnection(s);
                } catch (IOException e) {
                    System.err.println("Server encountered a non-fatal IO error:");
                    e.printStackTrace();
                    System.err.println("Continuing...");
                }
            }
        }
    }

    private void blockingHandleConnection(ServerSocket serverSocket) throws IOException {
        try (Socket s = serverSocket.accept();
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + serverSocket.getInetAddress());
            while (!Thread.interrupted()) {
                String incomingCommand = reader.readLine();
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
    }


}
