package edu.uob;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;


public class DealWithCommand {
    String storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
    public static ArrayList<String> conditionString(String command){
        char[] chars = command.toCharArray();
        ArrayList<String> token = new ArrayList<>();
        ArrayList<Character> words = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < chars.length; i++){
            char word = chars[i];
            if(word == '\'' || count%2 != 0 ){
                if(word =='\'')count++;
                if(word !='\'')words.add(word);
                if(count%2 == 0){
                    StringBuilder sb = new StringBuilder();
                    for (char c : words) sb.append(c);
                    String str = sb.toString().trim();
                    token.add(str);
                    words.clear();
                }
            }else {
                if(word =='(' || word == ')'){
                    if(!words.isEmpty()){
                        StringBuilder sb = new StringBuilder();
                        for (char c : words) sb.append(c);
                        String str = sb.toString().trim();
                        token.add(str);
                        words.clear();
                    }
                    words.add(word);
                    StringBuilder sb = new StringBuilder();
                    for (char c : words) sb.append(c);
                    String str = sb.toString().trim();
                    token.add(str);
                    words.clear();
                }
                if( word == '>' || word == '<' || word == '=' || word == '!'){
                    if ( !words.isEmpty() && !words.get(0).equals('>')&& !words.get(0).equals('<')
                            && !words.get(0).equals('=') && !words.get(0).equals('!')) {
                        StringBuilder sb = new StringBuilder();
                        for (char c : words) sb.append(c);
                        String str = sb.toString().trim();
                        token.add(str);
                        words.clear();
                    }
                    words.add(word);
                    if(chars[i+1] != '>' && chars[i+1]  != '<' && chars[i+1]  != '='){
                        StringBuilder sb = new StringBuilder();
                        for (char c : words) sb.append(c);
                        String str = sb.toString().trim();
                        token.add(str);
                        words.clear();
                    }
                }else {
                    if ((word >= 'A' && word <= 'Z') || word >= 'a' && word <= 'z' || word == '_'
                            || word =='@' ||word == '.'  ) {
                        words.add(word);
                    }
                    if (word == '*') words.add(word);
                    if (word >= '0' && word <= '9') words.add(word);
                    if ((word == ' ' && !words.isEmpty()) || (i == (chars.length - 1) && !words.isEmpty())) {
                        StringBuilder sb = new StringBuilder();
                        for (char c : words) sb.append(c);
                        String str = sb.toString().trim();
                        token.add(str);
                        words.clear();
                    }
                }
            }
        }
        int index = -1;
        for(int i = 0; i < token.size() ; i++){
            if(token.get(i).equalsIgnoreCase("where")){
                index = i;
            }
        }
        for(int i = 0; i <= index ; i++ ){
            token.remove(0);
        }
        return token;
    }
    public static ArrayList<String> Command(String command){
        char[] chars = command.toCharArray();
        ArrayList<String> token = new ArrayList<>();
        ArrayList<Character> words = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < chars.length; i++){
            char word = chars[i];
            if(word == '\'' || count%2 != 0 ){
                if(word =='\'')count++;
                if(word !='\'')words.add(word);
                if(count%2 == 0){
                    StringBuilder sb = new StringBuilder();
                    for (char c : words) sb.append(c);
                    String str = sb.toString().trim();
                    token.add(str);
                    words.clear();
                }
            }else {
                if( word == '>' || word == '<' || word == '=' || word == '!'){
                    if ( !words.isEmpty() && !words.get(0).equals('>')&& !words.get(0).equals('<')
                            && !words.get(0).equals('=') && !words.get(0).equals('!')) {
                        StringBuilder sb = new StringBuilder();
                        for (char c : words) sb.append(c);
                        String str = sb.toString().trim();
                        token.add(str);
                        words.clear();
                    }
                    words.add(word);
                    if(chars[i+1] != '>' && chars[i+1]  != '<' && chars[i+1]  != '='){
                        StringBuilder sb = new StringBuilder();
                        for (char c : words) sb.append(c);
                        String str = sb.toString().trim();
                        token.add(str);
                        words.clear();
                    }
                }else {
                    if ((word >= 'A' && word <= 'Z') || (word >= 'a' && word <= 'z' || word == '_' || word =='@' ||word == '.')
                            ||word == '#' ||word == '$' ||word == '%' ||word == '&' || word == '?'  ||word == '~') {
                        words.add(word );
                    }
                    if (word == '*') words.add(word);
                    if (word >= '0' && word <= '9') words.add(word);
                    if ((word == ' ' && !words.isEmpty()) || (i == (chars.length - 1) && !words.isEmpty())) {
                        StringBuilder sb = new StringBuilder();
                        for (char c : words) sb.append(c);
                        String str = sb.toString().trim();
                        token.add(str);
                        words.clear();
                    }
                }
            }
        }
        return token;
    }
    public String use(ArrayList<String> token, StringBuilder databaseNameBuilder, ArrayList<String> originalToken){
        if(token.size() == 2){
            token.set(1, token.get(1).toLowerCase());
            databaseNameBuilder.delete(0,databaseNameBuilder.length());
            databaseNameBuilder.append(originalToken.get(1));
            return "[OK]";
        }
        return "[ERROR]:: Your input about USE command is  invalid!";
    }
    public String createDatabase(ArrayList<String> token, ArrayList<String> originalToken){
        if(token.size() == 3){
            token.set(2, token.get(2).toLowerCase());
            String str = storageFolderPath + File.separator + originalToken.get(2);
            File directory = new File(str);
            if(directory.exists()){
                return "[ERROR]:: The database called"+ originalToken.get(2) + " already exists!";
            }
            if(directory.mkdir()){
                return "[OK]: The new database " + originalToken.get(2) +" is created!";
            }
        }
        return "[ERROR]:: Your input about CREATDATABASE is invalid";
    }

    public String createTable(ArrayList<String> token, String currentDatabase){
        if (token.size()>=3){
            try {
                File file = new File(currentDatabase+ File.separator + token.get(2)+".tab");
                if(file.exists()){
                    return "[ERROR]:: The table called " +token.get(2)+ " already exists!";
                }else {
                    if(file.createNewFile()){
                        if (token.size() > 3) {
                            ArrayList<ArrayList<String>> attributes = new ArrayList<>();
                            ArrayList<String> attribute = new ArrayList<>();
                            for (int i = 3; i < token.size(); i++) {
                                attribute.add(token.get(i));
                            }
                            attribute.add(0, "id");
                            attributes.add(attribute);
                            writeIntoFIle(attributes, currentDatabase + File.separator + token.get(2) + ".tab");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                File file = new File(currentDatabase+ File.separator + token.get(2)+"id.txt");
                if(file.createNewFile()){
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write("1");
                    fileWriter.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return "[OK]";
        }
        return "[ERROR]:: Your input about CREATTABLE is invalid";
    }
    public String dropDatabase(ArrayList<String> token, ArrayList<String> originalToken){
        String returnStr = "Your input about DROP is invalid!";
        if(token.size() == 3){
            String str = storageFolderPath + File.separator + originalToken.get(2);
            File directory = new File(str);
            returnStr=deleteDirectory(directory, originalToken);
            if(str.equals(readCurrentDatabaseTxt())){
                File file = new File(storageFolderPath + File.separator + "currentDatabase.txt");
                try {
                    PrintWriter writer = new PrintWriter(file);
                    writer.print("");
                    writer.close();
                } catch (FileNotFoundException e) {
                    System.err.println("File not found: " + "currentDatabase.txt");
                }
            }
        }
        return returnStr;
    }
    public static String deleteDirectory(File directory, ArrayList<String> token) {
        if (!directory.exists() || !directory.isDirectory()) {
            return "[ERROR]:: The database called " + token.get(2) + " do not exist!";
        }
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(file, token);
            } else {
                file.delete();
            }
        }
        directory.delete();
        return "[OK]: The database called " + token.get(2) + " is dropped!";
    }

    public String dropTable(ArrayList<String> token, String currentDatabase, ArrayList<String> originalToken){
        if (token.size()>=3){
            File folder = new File(currentDatabase);
            File[] files = folder.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isFile() && file.getName().equals(originalToken.get(2) + ".tab")) {
                    file.delete();
                    for (File file1 : files) {
                        if (file1.isFile() && file1.getName().equals(originalToken.get(2) + "id.txt")) {
                            file1.delete();
                        }
                    }
                    return "[OK]: The table called " + originalToken.get(2) + " is dropped!";
                }
            }

            return "[ERROR]:: The table called " + originalToken.get(2) + ".tab do not exist!";
        }
        return "[ERROR]:: Your input about DROPTABLE is invalid";
    }
    public String alter(ArrayList<String> token, String currentDatabase, ArrayList<String> originalToken){
        if((token.size() >4 && token.get(3).equals("add") && Objects.equals(token.get(1), "table")) || (token.size() >4 && token.get(3).equals("drop") && Objects.equals(token.get(1), "table"))){
            File folder = new File(currentDatabase);
            File[] files = folder.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isFile() && file.getName().equals(originalToken.get(2) + ".tab")) {
                    if (token.get(3).equals("drop")) {
                        ArrayList<ArrayList<String>> table ;
                        String dropAttribute = originalToken.get(4);
                        table = tableToArrayList(currentDatabase + File.separator + originalToken.get(2) + ".tab");
                        if(dropAttribute.equalsIgnoreCase("id")) return "[ERROR]:: You can't drop \"id\" !";
                        int index = -1;
                        for(int j = 0; j<table.get(0).size(); j++) {
                            if (table.get(0).get(j).contains(dropAttribute)) {
                                index = j;
                                break;
                            }
                        }
                        if(index != -1){
                            for(int j =0; j < table.size(); j++){
                                table.get(j).remove(index);
                                writeIntoFIle(table,currentDatabase + File.separator + originalToken.get(2) + ".tab");
                            }
                            return "[OK]: The attribute called " + originalToken.get(4) + " is dropped!";
                        }
                        return "[ERROR]:: There isn't a attribute called " + originalToken.get(4);
                    }
                    if(token.get(3).equals("add")){
                        ArrayList<ArrayList<String>> table ;
                        String addAttribute = originalToken.get(4);
                        table = tableToArrayList(currentDatabase + File.separator + originalToken.get(2) + ".tab");
                        table.get(0).add(addAttribute);
                        for(int i = 1; i < table.size(); i++){
                            table.get(i).add("");
                        }
                        writeIntoFIle(table,currentDatabase + File.separator + originalToken.get(2) + ".tab");
                        return "[OK]: The attribute called " + originalToken.get(4) + " is added!";
                    }
                }
            }
            return "[ERROR]:: The table called " + originalToken.get(2) + ".tab do not exist!";
        }
        return "[ERROR]:: Your input about ALTER is invalid!";
    }
    public String insert(ArrayList<String> token,  String currentDatabase, ArrayList<String> originalToken){
        if(token.size() >= 5 && (token.get(1).equals("into")) && (token.get(3).equals("values") )){
            File folder = new File(currentDatabase);
            File[] files = folder.listFiles();
            assert files != null;
            for (File file : files) {
                String line = "";
                if (file.isFile() && file.getName().equals(originalToken.get(2) + ".tab")) {
                    ArrayList<ArrayList<String>> table ;
                    ArrayList<String> add = new ArrayList<>();
                    table = tableToArrayList(currentDatabase + File.separator + originalToken.get(2) + ".tab");
                    for(int i = 4; i < token.size(); i++){
                        add.add(token.get(i));
                    }
                    try {
                        file = new File(currentDatabase + File.separator + originalToken.get(2) + "id.txt");
                        FileReader fileReader = new FileReader(file);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                        line = bufferedReader.readLine();
                        bufferedReader.close();


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    add.add(0,line);
                    table.add(add);

                    writeIntoFIle(table,currentDatabase + File.separator + originalToken.get(2) + ".tab");
                    try {
                        file = new File(currentDatabase + File.separator + originalToken.get(2) + "id.txt");
                        FileReader fileReader = new FileReader(file);
                        BufferedReader bufferedReader = new BufferedReader(fileReader);
                         line = bufferedReader.readLine();
                        bufferedReader.close();

                        int number = Integer.parseInt(line);
                        number += 1;

                        FileWriter fileWriter = new FileWriter(file);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        bufferedWriter.write(String.valueOf(number));
                        bufferedWriter.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return "[OK]";
                }
            }
            return "[ERROR]: The table called " + originalToken.get(2) + ".tab do not exist!";
        }
        return "[ERROR]:: Your input about INSERT is invalid!";
    }

    public String readCurrentDatabaseTxt(){
        String filePath = storageFolderPath + File.separator +"currentDatabase.txt";

        File fileToOpen = new File(filePath);
        FileReader reader;
        if (!fileToOpen.exists()) {
            try {
                fileToOpen.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Error creating new file: " + e.getMessage());
            }
        }
        try {
            reader = new FileReader(fileToOpen);
        } catch (FileNotFoundException e) {
            try {
                throw new FileNotFoundException();
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        BufferedReader bufferReader = new BufferedReader(reader);
        String line ;
        try {
            line = bufferReader.readLine();
            if(line == null) {
                return  "[ERROR]:: You should use a database first!";
            }
            bufferReader.close();
            return line;
        } catch (IOException e) {
            return "[ERROR]:: You should use a database first!";
        }

    }

    public  static ArrayList<ArrayList<String>> tableToArrayList(String name){

        File fileToOpen = new File(name);
        FileReader reader;
        try {
            reader = new FileReader(fileToOpen);
        } catch (FileNotFoundException e) {
            try {
                throw new FileNotFoundException("file not found!");
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        BufferedReader bufferReader = new BufferedReader(reader);
        String line;
        ArrayList<ArrayList<String>> table = new ArrayList<>();
        ArrayList<Character> word = new ArrayList<>();
        char a;
        try {
            line = bufferReader.readLine();
            if(line == null) throw  new IOException("the file is empty!");
            while (line != null ){
                ArrayList<String> element = new ArrayList<>();
                for(int j = 0; j < line.length();j++){
                    a = line.charAt(j);
                    if (a != '\t') word.add(a);
                    if(a == '\t' && !word.isEmpty()){
                        StringBuilder sb = new StringBuilder();
                        for (char c : word) sb.append(c);
                        String str = sb.toString();
                        element.add(str);
                        word.clear();
                    }
                    if(j == (line.length()-1) && !word.isEmpty()){
                        StringBuilder sb = new StringBuilder();
                        for (char c : word) sb.append(c);
                        String str = sb.toString();
                        element.add(str);
                        word.clear();
                        table.add(element);
                    }

                }
                line = bufferReader.readLine();
            }
            bufferReader.close();
        } catch (IOException e) {
            try {
                throw new IOException("the file is empty!");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return table;
    }
    public  static void writeIntoFIle(ArrayList<ArrayList<String>> table, String name) {
        File file = new File(name);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (ArrayList<String> row : table) {
                StringBuilder sb = new StringBuilder();
                int lastIndex = row.size() - 1;
                for (int i = 0; i < row.size(); i++) {
                    String element = row.get(i);
                    sb.append(element);
                    if (i != lastIndex) {
                        sb.append("\t");
                    }
                }
                writer.write(sb.toString());
                writer.newLine();
            }
            } catch (IOException e) {
                throw new RuntimeException("The table can't write into a file", e);
        }
    }
    public String select(ArrayList<String> token,String currentDatabase,ArrayList<String> conditionTokens) {
        if (token.size() >= 4 && token.contains("from") && !token.contains("where")) {
            int indexOfFrom = token.indexOf("from");
            token.set(indexOfFrom + 1, token.get(indexOfFrom + 1).toLowerCase());
            String table;
            table = token.get(token.indexOf("from") + 1) + ".tab";
            File folder = new File(currentDatabase);
            File[] files = folder.listFiles();
            assert files != null;
            for (File file : files) {
                if (file.isFile() && file.getName().equals(table)) {
                    StringBuilder returnString = new StringBuilder();
                    ArrayList<ArrayList<String>>  tableArrayList;
                    tableArrayList = DealWithCommand.tableToArrayList(currentDatabase+File.separator+table);
                    for(int i = 1; i < indexOfFrom; i++){
                        for (int j = 1; j <tableArrayList.get(0).size(); j++){
                            if(token.get(i).equalsIgnoreCase(tableArrayList.get(0).get(j))){
                                token.set(i,tableArrayList.get(0).get(j));
                            }
                        }
                    }
                    int numOfRow = tableArrayList.size();
                    if(token.get(1).equals("*") && token.get(2).equals("from")){
                        for(List<String> row: tableArrayList){
                            String line = String.join("\t",row);
                            returnString.append(line);
                            if(numOfRow != 0){
                                returnString.append("\n");
                                numOfRow--;
                            }
                        }
                    }else{
                        ArrayList<ArrayList<String>> output = new ArrayList<>();
                        ArrayList<String> attributeList = new ArrayList<>();
                        for(int i = token.indexOf("select")+1; i < token.indexOf("from"); i++){
                            attributeList.add(token.get(i));
                        }
                        for (String s : attributeList) {
                            if (tableArrayList.get(0).contains(s)) {
                                int index = tableArrayList.get(0).indexOf(s);
                                for (int j = 0; j < tableArrayList.size(); j++) {
                                    ArrayList<String> add = new ArrayList<>();
                                    output.add(add);
                                    output.get(j).add(tableArrayList.get(j).get(index));
                                }
                            }
                        }
                        for(List<String> row: output){
                            String line = String.join("\t",row);
                            returnString.append(line);
                            if(numOfRow != 0){
                                returnString.append("\n");
                                numOfRow--;
                            }
                        }
                    }

                    return "[OK]" +"\n" +returnString ;
                }
            }
            return "[ERROR]:: The table called " + table + " do not exist!";
        }
        if (token.size() >= 6 && token.contains("from") && token.contains("where")) {

            File folder = new File(currentDatabase);
            File[] files = folder.listFiles();
            String table;
            table = token.get(token.indexOf("from") + 1) + ".tab";

            assert files != null;
            for(File file : files){
                if (file.isFile() && file.getName().equals(table)) {
                    ArrayList<ArrayList<String>>  tableArrayList;
                    StringBuilder returnString = new StringBuilder();
                    ArrayList<ArrayList<String>> output1;
                    ArrayList<ArrayList<String>> output2 = new ArrayList<>();
                    ArrayList<ArrayList<String>> output3;
                    ArrayList<String> condition = new ArrayList<>();
                    ArrayList<String> booleanOperator = new ArrayList<>();
                    tableArrayList = DealWithCommand.tableToArrayList(currentDatabase+File.separator+table);
                    for (int i = token.indexOf("where") + 1; i < token.size(); i++ ){
                        if((i - token.indexOf("where"))%4 == 1) {
                            for (int j = 0; j <tableArrayList.get(0).size(); j++){
                                if(token.get(i).equalsIgnoreCase(tableArrayList.get(0).get(j))){
                                    token.set(i,tableArrayList.get(0).get(j));
                                }
                            }
                        }
                    }
                    for (int i = token.indexOf("where") + 1; i < token.size(); i++ ){
                        if((i - token.indexOf("where"))%4 == 0) booleanOperator.add(token.get(i));
                        else condition.add(token.get(i));
                    }

                    for(int i = 1; i < token.indexOf("from"); i++){
                        for (int j = 1; j <tableArrayList.get(0).size(); j++){
                            if(token.get(i).equalsIgnoreCase(tableArrayList.get(0).get(j))){
                                token.set(i,tableArrayList.get(0).get(j));
                            }
                        }
                    }
                    if(conditionTokens.contains("(")) {
                        output2 = processComplexCondition(conditionTokens, tableArrayList);
                    }else {
                        int count1 = 0;
                        for (int i = 0; i < condition.size() / 3; i++) {

                            String first = condition.get(3 * i);
                            String second = condition.get(3 * i + 1);
                            String third = condition.get(3 * i + 2);
                            if (!second.equals("==") && !second.equals("!=") && !second.equals(">") && !second.equals("<")
                                    && !second.equals(">=") && !second.equals("<=") && !second.equals("like")) {
                                return "[ERROR]:: Your input about SELECT command is  invalid!";
                            }
                            if (count1 == 0) output2 = selectWhere(tableArrayList, first, second, third);
                            if (count1 != 0) {
                                if (booleanOperator.get(count1 - 1).equals("and")) {
                                    output3 = selectWhere(output2, first, second, third);
                                    output2.clear();
                                    for(int x =0; x < output3.size();x++){
                                        ArrayList<String> add = new ArrayList<>();
                                        output2.add(add);
                                        output2.set(x,output3.get(x));
                                    }
                                }
                                if (booleanOperator.get(count1 - 1).equals("or")) {
                                    ArrayList<ArrayList<String>> result = new ArrayList<>();
                                    output3 = selectWhere(tableArrayList, first, second, third);
                                    for (ArrayList<String> list : output3) {
                                        if (!result.contains(list)) {
                                            result.add(list);
                                        }
                                    }
                                    for (ArrayList<String> list : output2) {
                                        if (!result.contains(list)) {
                                            result.add(list);
                                        }
                                    }
                                    output2.clear();
                                    for(int x =0; x <result.size();x++){
                                        ArrayList<String> add = new ArrayList<>();
                                        output2.add(add);
                                        output2.set(x,result.get(x));
                                    }
                                }
                            }
                            count1++;
                        }
                    }
                    output1 =output2;
                    int numOfRow = tableArrayList.size();
                    if(token.get(1).equals("*") && token.get(2).equals("from")){

                        for(List<String> row: output1){
                            String line = String.join("\t",row);
                            returnString.append(line);
                            if(numOfRow != 0){
                                returnString.append("\n");
                                numOfRow--;
                            }
                        }
                    }else{
                        ArrayList<ArrayList<String>> output = new ArrayList<>();
                        ArrayList<String> attributeList = new ArrayList<>();
                        for(int i = token.indexOf("select")+1; i < token.indexOf("from"); i++){
                            attributeList.add(token.get(i));
                        }
                        for (String s : attributeList) {
                            if (output1.get(0).contains(s)) {
                                int index = output1.get(0).indexOf(s);
                                for (int j = 0; j < output1.size(); j++) {
                                    ArrayList<String> add = new ArrayList<>();
                                    output.add(add);
                                    output.get(j).add(output1.get(j).get(index));
                                }
                            }
                        }
                        for(List<String> row: output){
                            String line = String.join("\t",row);
                            returnString.append(line);
                            if(numOfRow != 0){
                                returnString.append("\n");
                                numOfRow--;
                            }
                        }
                    }
                    return "[OK]" +"\n" +returnString ;

                }
            }
            return  "[ERROR]:: The table called " + table + " do not exist!";
        }
        return "[ERROR]:: Your input about SELECT command is  invalid!";
    }
    public static ArrayList<ArrayList<String>> selectWhere(ArrayList<ArrayList<String>> tableArrayList, String first, String second, String third){
        ArrayList<ArrayList<String>> output1 = new ArrayList<>();
        ArrayList<String> NUll = new ArrayList<>();
        if(tableArrayList.get(0).contains(first)){
            output1 .add(tableArrayList.get(0));
            if(second.equals("==")){
                int index;
                index = tableArrayList.get(0).indexOf(first);
                for (ArrayList<String> strings : tableArrayList) {
                    if (strings.get(index).equals(third)) {
                        output1.add(strings);
                    }
                }
            }
            if(second.equals("!=")){
                int index;
                index = tableArrayList.get(0).indexOf(first);
                for (int j = 1; j < tableArrayList.size(); j++) {
                    if (!tableArrayList.get(j).get(index).equals(third)) {
                        output1.add(tableArrayList.get(j));
                    }
                }
            }

            if(second.equals(">")){
                if (third.matches("\\d+(\\.\\d+)?")) {
                    for (ArrayList<String> strings : tableArrayList) {
                        int index;
                        index = tableArrayList.get(0).indexOf(first);
                        if (strings.get(index).matches("\\d+(\\.\\d+)?")) {
                            if (Double.parseDouble(strings.get(index)) > Double.parseDouble(third)) {
                                output1.add(strings);
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < tableArrayList.size(); j++) {
                            output1.add(NUll);
                    }
                }
            }
            if(second.equals(">=")) {
                if (third.matches("\\d+(\\.\\d+)?")) {
                    for (ArrayList<String> strings : tableArrayList) {
                        int index;
                        index = tableArrayList.get(0).indexOf(first);
                        if (strings.get(index).matches("\\d+(\\.\\d+)?")) {
                            if (Double.parseDouble(strings.get(index)) >= Double.parseDouble(third)) {
                                output1.add(strings);
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < tableArrayList.size(); j++) {
                        output1.add(NUll);
                    }
                }
            }
            if(second.equals("<")){
                if (third.matches("\\d+(\\.\\d+)?")) {
                    for (ArrayList<String> strings : tableArrayList) {
                        int index;
                        index = tableArrayList.get(0).indexOf(first);
                        if (strings.get(index).matches("\\d+(\\.\\d+)?")) {
                            if (Double.parseDouble(strings.get(index)) < Double.parseDouble(third)) {
                                output1.add(strings);
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < tableArrayList.size(); j++) {
                        output1.add(NUll);
                    }
                }
             }
            if(second.equals("<=")){
                if (third.matches("\\d+(\\.\\d+)?")) {
                    for (ArrayList<String> strings : tableArrayList) {
                        int index;
                        index = tableArrayList.get(0).indexOf(first);
                        if (strings.get(index).matches("\\d+(\\.\\d+)?")) {
                            if (Double.parseDouble(strings.get(index)) <= Double.parseDouble(third)) {
                                output1.add(strings);
                            }
                        }
                    }
                } else {
                    for (int j = 0; j < tableArrayList.size(); j++) {
                        output1.add(NUll);
                    }
                }
            }
            if(second.equals("like")){
                int index;
                index = tableArrayList.get(0).indexOf(first);
                for (ArrayList<String> strings : tableArrayList) {
                    if (strings.get(index).contains(third)) {
                        output1.add(strings);
                    }
                }
            }
        }
        return output1;
    }
    public String update(ArrayList<String> token, String currentDatabase, ArrayList<String> conditionTokens){
        if(token.size()>=10 && token.contains("set") && token.contains("where")) {
            token.set(1, token.get(1).toLowerCase());
            File folder = new File(currentDatabase);
            File[] files = folder.listFiles();
            String table;
            table = token.get(1) + ".tab";

            assert files != null;
            for (File file : files) {
                if (file.isFile() && file.getName().equals(table)) {
                    ArrayList<ArrayList<String>> tableArrayList;
                    ArrayList<ArrayList<String>> output2 = new ArrayList<>();
                    ArrayList<ArrayList<String>> output3;
                    ArrayList<String> condition = new ArrayList<>();
                    ArrayList<String> booleanOperator = new ArrayList<>();
                    tableArrayList = DealWithCommand.tableToArrayList(currentDatabase + File.separator + table);
                    for (int i = token.indexOf("where") + 1; i < token.size(); i++ ){
                        if((i - token.indexOf("where"))%4 == 1) {
                            for (int j = 0; j <tableArrayList.get(0).size(); j++){
                                if(token.get(i).equalsIgnoreCase(tableArrayList.get(0).get(j))){
                                    token.set(i,tableArrayList.get(0).get(j));
                                }
                            }
                        }
                    }
                    for (int i = token.indexOf("where") + 1; i < token.size(); i++) {
                        if ((i - token.indexOf("where")) % 4 == 0) booleanOperator.add(token.get(i));
                        else condition.add(token.get(i));
                    }
                    if(conditionTokens.contains("(")) {
                        output2 = processComplexCondition(conditionTokens, tableArrayList);
                    }else {
                        int count1 = 0;
                        for (int i = 0; i < condition.size() / 3; i++) {
                            String first = condition.get(3 * i);
                            String second = condition.get(3 * i + 1);
                            String third = condition.get(3 * i + 2);
                            if (!second.equals("==") && !second.equals("!=") && !second.equals(">") && !second.equals("<")
                                    && !second.equals(">=") && !second.equals("<=") && !second.equals("like")) {
                                return "[ERROR]:: Your input about UPDATE command is  invalid!";
                            }
                            if (count1 == 0)
                                output2 = DealWithCommand.selectWhere(tableArrayList, first, second, third);
                            if (count1 != 0) {
                                if (booleanOperator.get(count1 - 1).equals("and")) {
                                    output3 = DealWithCommand.selectWhere(output2, first, second, third);
                                    output2 = output3;
                                }
                                if (booleanOperator.get(count1 - 1).equals("or")) {
                                    ArrayList<ArrayList<String>> result = new ArrayList<>();
                                    output3 = DealWithCommand.selectWhere(tableArrayList, first, second, third);
                                    for (ArrayList<String> list : output3) {
                                        if (!result.contains(list)) {
                                            result.add(list);
                                        }
                                    }
                                    for (ArrayList<String> list : output2) {
                                        if (!result.contains(list)) {
                                            result.add(list);
                                        }
                                    }

                                    output2 = result;
                                }
                            }
                            count1++;
                        }
                    }
                    ArrayList<String> attributeName = new ArrayList<>();
                    ArrayList<String> value = new ArrayList<>();
                    if((token.indexOf("where")-token.indexOf("set")-1) % 3 != 0) {
                        return "[ERROR]:: Your input about UPDATE is invalid!";
                    }
                    for (int i = token.indexOf("set") + 1; i < token.size(); i++ ){
                        if((i - token.indexOf("set"))%3 == 1) {
                            for (int j = 0; j <tableArrayList.get(0).size(); j++){
                                if(token.get(i).equalsIgnoreCase(tableArrayList.get(0).get(j))){
                                    token.set(i,tableArrayList.get(0).get(j));
                                }
                            }
                        }
                    }
                    for(int j = token.indexOf("set") + 1; j < token.indexOf("where"); j++ ){
                        if((j-token.indexOf("set"))%3 == 1) {
                            if(token.get(j).equalsIgnoreCase("id")) return "[ERROR]:: The \"id\" can't be changed!";
                            attributeName.add(token.get(j));
                        }
                        if((j-token.indexOf("set"))%3 == 2) {
                            if(!token.get(j).equals("="))  return "[ERROR]:: Your input about UPDATE is invalid!";
                        }
                        if((j-token.indexOf("set"))%3 == 0) value.add(token.get(j));
                    }
                    for (ArrayList<String> strings : output2) {
                        for (int j = 1; j < tableArrayList.size(); j++) {
                            if (strings.equals(tableArrayList.get(j))) {
                                for (int p = 0; p < attributeName.size(); p++) {
                                    if (tableArrayList.get(0).contains(attributeName.get(p))) {
                                        tableArrayList.get(tableArrayList.indexOf(tableArrayList.get(j))).set(tableArrayList.get(0).indexOf(attributeName.get(p)), value.get(p));
                                    }
                                }
                            }
                        }
                    }
                    DealWithCommand.writeIntoFIle(tableArrayList,currentDatabase + File.separator + table);
                    return "[OK]";
                }
            }
            return "[ERROR]:: The table called " + table +" do not exist!";
        }
        return "[ERROR]:: Your input about UPDATE is invalid!";
    }
    public String join(ArrayList<String> token, String currentDatabase){
        if(token.size() == 8 && token.get(2).equals("and") && token.get(4).equals("on") && token.get(6).equals("and")){
            token.set(1, token.get(1).toLowerCase());
            token.set(3, token.get(3).toLowerCase());
            File folder1 = new File(currentDatabase);
            ArrayList<ArrayList<String>> output = new ArrayList<>();
            File[] files1 = folder1.listFiles();
            String table1;
            table1 = token.get(1) + ".tab";
            assert files1 != null;
            for (File file1 : files1) {

                if (file1.isFile() && file1.getName().equals(table1)) {
                    File folder2 = new File(currentDatabase);
                    File[] files2 = folder2.listFiles();
                    String table2;
                    table2 = token.get(3) + ".tab";
                    assert files2 != null;
                    for (File file2 : files2) {
                        if (file2.isFile() && file2.getName().equals(table2)) {
                            ArrayList<ArrayList<String>> tableArrayList1;
                            ArrayList<ArrayList<String>> tableArrayList2;
                            tableArrayList1 = DealWithCommand.tableToArrayList(currentDatabase + File.separator +table1);
                            tableArrayList2 = DealWithCommand.tableToArrayList(currentDatabase + File.separator +table2);
                            for (int i = token.indexOf("on") + 1; i < token.size(); i++ ){
                                if((i - token.indexOf("on") )== 1) {
                                    for (int j = 0; j <tableArrayList1.get(0).size(); j++){
                                        if(token.get(i).equalsIgnoreCase(tableArrayList1.get(0).get(j))){
                                            token.set(i,tableArrayList1.get(0).get(j));
                                        }
                                    }
                                }
                            } for (int i = token.indexOf("on") + 1; i < token.size(); i++ ){
                                if((i - token.indexOf("where")) == 3) {
                                    for (int j = 0; j <tableArrayList1.get(0).size(); j++){
                                        if(token.get(i).equalsIgnoreCase(tableArrayList1.get(0).get(j))){
                                            token.set(i,tableArrayList1.get(0).get(j));
                                        }
                                    }
                                }
                            }
                            if(tableArrayList1.get(0).contains(token.get(7)) && tableArrayList2.get(0).contains(token.get(5))){
                                output = order(tableArrayList1,tableArrayList2,token.get(7),token.get(5),token.get(1),token.get(3));
                            }
                            if(tableArrayList1.get(0).contains(token.get(5))&& tableArrayList2.get(0).contains(token.get(7)) ){
                                output = order(tableArrayList1,tableArrayList2,token.get(5),token.get(7),token.get(1),token.get(3));
                            }
                            StringBuilder returnString = new StringBuilder();

                            int numOfRow = output.size();
                            for(List<String> row: output) {
                                String line = String.join("\t", row);
                                returnString.append(line);
                                if (numOfRow != 0) {
                                    returnString.append("\n");
                                    numOfRow--;
                                }

                            }
                            return "[OK]" + "\n" + returnString;
                        }

                    }
                    return "[ERROR]:: The table called " + token.get(3) +" do not exist!";
                }
            }
            return "[ERROR]:: The table called " + token.get(1) +" do not exist!";
        }
        return "[ERROR]:: Your input about UPDATE is invalid!";
    }


    public ArrayList<ArrayList<String>> order(ArrayList<ArrayList<String>> tableArraylist1, ArrayList<ArrayList<String>> tableArrayList2 , String attribute1, String attribute2, String table1, String table2){
        ArrayList<ArrayList<String>> output = new ArrayList<>();
        tableArraylist1.get(0).indexOf(attribute1);
        for(int i = 0; i < tableArraylist1.size();i++){
            ArrayList<String> add = new ArrayList<>();
            output.add(add);
            for (int j = 0; j < tableArraylist1.get(0).size(); j++) {

                if (i == 0) {
                    if(j !=0) output.get(i).add(table1 + "." + tableArraylist1.get(0).get(j));
                    else output.get(i).add(tableArraylist1.get(i).get(j));
                } else {
                    output.get(i).add(tableArraylist1.get(i).get(j));
                }
            }
        }
        if(!attribute1.equals("id")) {
            for (int i = 0; i < tableArraylist1.size(); i++) {
                output.get(i).remove(tableArraylist1.get(0).indexOf(attribute1));
            }
        }
            ArrayList<String> order = new ArrayList<>();
            for (int i = 1; i < tableArraylist1.size(); i++) {
                order.add(tableArraylist1.get(i).get(tableArraylist1.get(0).indexOf(attribute1)));
            }
            tableArrayList2.sort((o1, o2) -> {
                String name1 = o1.get(0);
                String name2 = o2.get(0);
                int index1 = order.indexOf(name1);
                int index2 = order.indexOf(name2);
                return Integer.compare(index1, index2);
            });
        int index = tableArrayList2.get(0).indexOf(attribute2);
        for (ArrayList<String> strings : tableArrayList2) {
            strings.remove(index);
        }
        for(int i = 0; i < tableArrayList2.size();i++ ){
            for (int j = 0; j < tableArrayList2.get(0).size(); j++) {
                if (i == 0) {
                    output.get(i).add(table2 + "." + tableArrayList2.get(i).get(j));
                } else {
                        output.get(i).add(tableArrayList2.get(i).get(j));
                }
            }
        }
        return output;
    }
    public String delete (ArrayList<String> token, String currentDatabase, ArrayList<String> conditionTokens){
        if(token.size()>=7 && token.get(1).equals("from") && token.get(3).equals("where")){
            File folder = new File(currentDatabase);
            token.set(2, token.get(2).toLowerCase());
            File[] files = folder.listFiles();
            String table;
            table = token.get(token.indexOf("from") + 1) + ".tab";
            assert files != null;
            for(File file : files) {
                if (file.isFile() && file.getName().equals(table)) {
                    ArrayList<ArrayList<String>> tableArrayList;
                    ArrayList<ArrayList<String>> output2 =new ArrayList<>();
                    tableArrayList = DealWithCommand.tableToArrayList(currentDatabase + File.separator + table);
                    // Extract and process the complex condition after the WHERE keyword
                    if(conditionTokens.contains("(")) {
                        output2 = processComplexCondition(conditionTokens, tableArrayList);
                    }
                    else{
                        ArrayList<ArrayList<String>> output3;
                        ArrayList<String> condition = new ArrayList<>();
                        ArrayList<String> booleanOperator = new ArrayList<>();
                        tableArrayList = DealWithCommand.tableToArrayList(currentDatabase+File.separator+table);
                        for (int i = token.indexOf("where") + 1; i < token.size(); i++ ){
                            if((i - token.indexOf("where"))%4 == 1) {
                                for (int j = 0; j <tableArrayList.get(0).size(); j++){
                                    if(token.get(i).equalsIgnoreCase(tableArrayList.get(0).get(j))){
                                        token.set(i,tableArrayList.get(0).get(j));
                                    }
                                }
                            }
                        }
                        for (int i = token.indexOf("where") + 1; i < token.size(); i++ ){
                            if((i - token.indexOf("where"))%4 == 0) booleanOperator.add(token.get(i));
                            else condition.add(token.get(i));
                        }

                        for(int i = 1; i < token.indexOf("from"); i++){
                            for (int j = 1; j <tableArrayList.get(0).size(); j++){
                                if(token.get(i).equalsIgnoreCase(tableArrayList.get(0).get(j))){
                                    token.set(i,tableArrayList.get(0).get(j));
                                }
                            }
                        }
                        int count1 = 0;
                        for(int i = 0; i < condition.size()/3;i++) {

                            String first = condition.get(3 * i);
                            String second = condition.get(3 * i + 1);
                            String third = condition.get(3 * i + 2);
                            if (!second.equals("==") && !second.equals("!=") && !second.equals(">") && !second.equals("<")
                                    && !second.equals(">=") && !second.equals("<=") && !second.equals("like")) {
                                return "[ERROR]:: Your input about SELECT command is  invalid!";
                            }
                            if (count1 == 0) output2 = selectWhere(tableArrayList, first, second, third);
                            if (count1 != 0) {
                                if (booleanOperator.get(count1 - 1).equals("and")) {
                                    output3 = selectWhere(output2, first, second, third);
                                    output2 =output3;
                                }
                                if (booleanOperator.get(count1 - 1).equals("or")) {
                                    ArrayList<ArrayList<String>> result = new ArrayList<>();
                                    output3 = selectWhere(tableArrayList, first, second, third);
                                    for (ArrayList<String> list : output3) {
                                        if (!result.contains(list)) {
                                            result.add(list);
                                        }
                                    }
                                    for (ArrayList<String> list : output2) {
                                        if (!result.contains(list)) {
                                            result.add(list);
                                        }
                                    }

                                    output2 = result;
                                }
                            }
                            count1++;
                        }
                    }
                    // Process the modified condition tokens using DealWithCommand.selectWhere
                   // output2 = selectWhere(processedConditionTokens, tableArrayList);

                    for (int i = 1; i < output2.size(); i++) {
                        for (int j = 0; j < tableArrayList.size(); j++) {
                            if (output2.get(i).equals(tableArrayList.get(j))) {
                                tableArrayList.remove(j);
                                j--;
                            }
                        }
                    }
                    DealWithCommand.writeIntoFIle(tableArrayList,currentDatabase+File.separator+table);
                    return "[OK]";
                }
            }
            return "[ERROR]:: The table called "+ token.get(2) + " do not exist!";
        }
        return "[ERROR]:: The input about DELETE is invalid!";
    }
    private ArrayList<ArrayList<String>> processComplexCondition(ArrayList<String> token, ArrayList<ArrayList<String>> tableArrayList) {
        ArrayList<ArrayList<String>> returnTable= new ArrayList<>();
        ArrayList<ArrayList<ArrayList<String>>> tables=new ArrayList<>();
        Stack<Integer> leftBracketStack = new Stack<>();
        Stack<Integer> rightBracketStack = new Stack<>();

        token.add(0, "(");
        token.add(token.size()-2,")");
        for(int i = 0; i < token.size(); i++){
            if(token.get(i).equals("(")) {
                leftBracketStack.push(i);
            }
        }
        for(int i = token.size()-1; i >= 0; i--){
            if(token.get(i).equals(")")) {
                rightBracketStack.push(i);
            }
        }
        if (rightBracketStack.size() != leftBracketStack.size()) return returnTable;
        for(int i = leftBracketStack.size()-1; i >= 0 ; i--){
            if((rightBracketStack.get(i)-leftBracketStack.get(i)) == 4){
                rightBracketStack.remove(i);
                leftBracketStack.remove(i);
            }
        }

        leftBracketStack.clear();
        rightBracketStack.clear();
        for(int i = 0; i < token.size(); i++){
            if(token.get(i).equals("(")) leftBracketStack.add(i);
            if(token.get(i).equals(")")) {
                boolean enter =true;
                int leftIndex = leftBracketStack.pop();
                int rightIndex = i;

                ArrayList<ArrayList<String>> output2 = new ArrayList<>();
                ArrayList<ArrayList<String>> output3;
                for (int m = leftIndex + 1; m < rightIndex ; m++) {
                    if (token.get(m).contains("Table")) {
                        enter =false;
                        ArrayList<ArrayList<ArrayList<String>>> tableName = new ArrayList<>();
                        ArrayList<String> booleanOperator = new ArrayList<>();
                        ArrayList<String> condition = new ArrayList<>();
                        for (int n = leftIndex + 1; n < rightIndex; n++) {
                            if (token.get(n).contains("Table")) {
                                tableName.add(tables.get(Integer.parseInt(token.get(n).replace("Table", ""))-1));
                            } else if (token.get(n).equalsIgnoreCase("and") || token.get(n).equalsIgnoreCase("or")) {
                                booleanOperator.add(token.get(n));
                            } else {
                                condition.add(token.get(n));
                                if (condition.size() == 3) {
                                    ArrayList<ArrayList<String>> conditionTable;
                                    conditionTable = selectWhere(tableArrayList, condition.get(0), condition.get(1), condition.get(2));
                                    tableName.add(conditionTable);
                                    condition.clear();
                                }
                            }
                        }
                        ArrayList<ArrayList<String>> add = new ArrayList<>();
                        while (!tableName.isEmpty()) {
                            ArrayList<ArrayList<String>> first = tableName.get(0);
                            ArrayList<ArrayList<String>> third = tableName.get(1);
                            add = tableDealWith(first, booleanOperator.get(0), third);
                            tableName.remove(0);
                            tableName.remove(0);
                            booleanOperator.remove(0);
                            if (tableName.isEmpty()) break;
                            tableName.add(0, add);
                        }
                        ArrayList<ArrayList<ArrayList<String>>> toRemove =new ArrayList<>();
                        for (int n = leftIndex + 1; n < rightIndex; n++) {
                            if (token.get(n).contains("Table")) {
                                toRemove.add(tables.get(Integer.parseInt(token.get(n).replace("Table", "")) - 1));
                            }
                        }
                        tables.removeAll(toRemove);
                        tables.add(add);
                        if (rightIndex > leftIndex) {
                            token.subList(leftIndex, rightIndex).clear();
                        }

                        token.set(leftIndex, "Table" + tables.size());
                        break;
                    }
                }

                if(enter){
                    ArrayList<String> condition = new ArrayList<>();
                    ArrayList<String> booleanOperator = new ArrayList<>();
                    for (int j = leftIndex + 1; j < rightIndex; j++) {
                        if ((j - leftIndex) % 4 == 0) booleanOperator.add(token.get(j));
                        else condition.add(token.get(j));
                    }
                    int count1 = 0;
                    for (int j = 0; j < condition.size() / 3; j++) {
                        String first = condition.get(3 * j);
                        String second = condition.get(3 * j + 1);
                        String third = condition.get(3 * j + 2);
                        if (!second.equals("==") && !second.equals("!=") && !second.equals(">") && !second.equals("<")
                                && !second.equals(">=") && !second.equals("<=") && !second.equals("like")) {
                            return returnTable;
                        }
                        if (count1 == 0) output2 = selectWhere(tableArrayList, first, second, third);
                        if (count1 != 0) {
                            if (booleanOperator.get(count1 - 1).equals("and")) {
                                output3 = selectWhere(output2, first, second, third);
                                output2.clear();
                                for(int x =0; x < output3.size();x++){
                                    ArrayList<String> add = new ArrayList<>();
                                    output2.add(add);
                                    output2.set(x,output3.get(x));
                                }
                            }
                            if (booleanOperator.get(count1 - 1).equals("or")) {
                                ArrayList<ArrayList<String>> result = new ArrayList<>();
                                output3 = selectWhere(tableArrayList, first, second, third);
                                for (ArrayList<String> list : output3) {
                                    if (!result.contains(list)) {
                                        result.add(list);
                                    }
                                }
                                for (ArrayList<String> list : output2) {
                                    if (!result.contains(list)) {
                                        result.add(list);
                                    }
                                }
                                output2.clear();
                                for(int x =0; x <result.size();x++){
                                    ArrayList<String> add = new ArrayList<>();
                                    output2.add(add);
                                    output2.set(x,result.get(x));
                                }
                            }
                        }
                        count1++;
                    }
                    ArrayList<ArrayList<String>> add = new ArrayList<>();
                    for (int j = 0; j < output2.size(); j++) {
                        ArrayList<String> add1 = new ArrayList<>();
                        add.add(add1);
                        for (int p = 0; p < output2.get(0).size(); p++) {
                            add.get(j).add(output2.get(j).get(p));
                        }
                    }
                    if (rightIndex > leftIndex) {
                        token.subList(leftIndex, rightIndex).clear();
                    }
                    tables.add(add);
                    token.set(leftIndex, "Table" + tables.size());

                }
                if(leftBracketStack.size()>0) i = leftBracketStack.get(leftBracketStack.size()-1);
                else i = 0;
            }
        }

        if(!tables.isEmpty()){
            returnTable=tables.get(0);
        }
        Collections.sort(returnTable, new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> o1, ArrayList<String> o2) {
                String id1 = o1.get(0);
                String id2 = o2.get(0);

                if (id1.matches("\\d+") && id2.matches("\\d+")) {
                    int intId1 = Integer.parseInt(id1);
                    int intId2 = Integer.parseInt(id2);
                    return Integer.compare(intId1, intId2);
                } else {
                    return id2.compareTo(id1);
                }
            }
        });

        System.out.println(returnTable);
        return returnTable;
    }
    private ArrayList<ArrayList<String>> tableDealWith (ArrayList<ArrayList<String>> first, String booleanOperator,ArrayList<ArrayList<String>> third ){
        ArrayList<ArrayList<String>> returnTable =new ArrayList<>();
        if(booleanOperator.equalsIgnoreCase("and")){
            for (ArrayList<String> strings : first) {
                for (ArrayList<String> stringArrayList : third) {
                    if (strings.equals(stringArrayList)) {
                        returnTable.add(strings);
                    }
                }
            }
        }
        if(booleanOperator.equalsIgnoreCase("or")){
            returnTable.addAll(first);
            for (ArrayList<String> strings : third) {
                if (!returnTable.contains(strings)) {
                    returnTable.add(strings);
                }
            }
        }

        return returnTable;
    }
    public static String readFromFile(String filePath) {
        // 读取文件
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder();

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                fileContent.append(line).append('\n');
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            return "[ERROR]";
        }

        // 检查文件内容是否为空
        if (fileContent.toString().trim().isEmpty()) {
            return "[ERROR]";
        }

        return "OK";
    }
}

