
package edu.uob;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class ActionsLoad {
    Hash hash = new Hash();
    ArrayList<String> keyphraseName = new ArrayList<>();
    ArrayList<String> producedName = new ArrayList<>();
    ArrayList<String> subjectName = new ArrayList<>();
    ArrayList<String> consumeName = new ArrayList<>();
    ArrayList<String> multipleKeyphrase = new ArrayList<>();


    public ActionsLoad(File actionsFile) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(actionsFile);
            Element root = document.getDocumentElement();

            Actions(root);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    
    }
    private void Actions(Element root) {
        NodeList actionList = root.getElementsByTagName("action");

        for (int i = 0; i < actionList.getLength(); i++) {
            Node node = actionList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element actionElement = (Element) node;
                NodeList triggersList = actionElement.getElementsByTagName("triggers");

                if (triggersList.getLength() > 0) {
                    for(int j = 0; j < triggersList.getLength(); j++){
                        NodeList keyphraseList = actionElement.getElementsByTagName("keyphrase");
                        NodeList subjectsList = actionElement.getElementsByTagName("subjects");
                        NodeList consumedList = actionElement.getElementsByTagName("consumed");
                        NodeList producedList = actionElement.getElementsByTagName("produced");
                        NodeList narrationList = actionElement.getElementsByTagName("narration");

                        for(int m = 0; m < keyphraseList.getLength(); m++){
                            GameAction actions = new GameAction();
                            Element keyphraseElement = (Element) keyphraseList.item(m);
                            String keyphraseString = keyphraseElement.getTextContent();
                            if(keyphraseString.contains(" ")){
                                multipleKeyphrase.add(keyphraseString);
                                keyphraseString = keyphraseString.replace(" ", "");
                            }
                            keyphraseName.add(keyphraseString);

                            for(int n = 0; n < subjectsList.getLength(); n++){
                                Element subjectsElement = (Element) subjectsList.item(n);
                                NodeList entityList = subjectsElement.getElementsByTagName("entity");
                                for (int k = 0; k < entityList.getLength(); k++) {
                                    Element entityElement = (Element) entityList.item(k);
                                    String entityValue = entityElement.getTextContent().trim();
                                    actions.addSubjects(entityValue);
                                    if(!subjectName.contains(entityValue)){
                                        subjectName.add(entityValue);
                                    }
                                }
                            }
                            for(int n = 0; n < consumedList.getLength(); n++){
                                Element consumedElement = (Element) consumedList.item(n);
                                NodeList entityList = consumedElement.getElementsByTagName("entity");
                                for (int k = 0; k < entityList.getLength(); k++) {
                                    Element entityElement = (Element) entityList.item(k);
                                    String entityValue = entityElement.getTextContent().trim();
                                    actions.addConsumed(entityValue);
                                    if(!consumeName.contains(entityValue)){
                                        consumeName.add(entityValue);
                                    }
                                }
                            }
                            for(int n = 0; n < producedList.getLength(); n++){
                                Element producedElement = (Element) producedList.item(n);
                                NodeList entityList = producedElement.getElementsByTagName("entity");
                                for (int k = 0; k < entityList.getLength(); k++) {
                                    Element entityElement = (Element) entityList.item(k);
                                    String entityValue = entityElement.getTextContent().trim();
                                    actions.addProduced(entityValue);
                                    if(!producedName.contains(entityValue)){
                                        producedName.add(entityValue);
                                    }
                                }
                            }
                            for(int n = 0; n < narrationList.getLength(); n++){
                                Element narrationElement = (Element) narrationList.item(n);
                                String narrationValue = narrationElement.getTextContent().trim();
                                actions.addNarration(narrationValue);
                            }
                           
                            hash.setProperty(keyphraseString,actions);
                        }
                    }
                }
            }
        }
    }

    public ArrayList<String> getProducedName(){
        return producedName;
    }

    public ArrayList<String> getKeyphraseName(){
        return keyphraseName;
    }
    public ArrayList<String> getSubjectName(){
        return subjectName;
    }
    public  ArrayList<String> getConsumeName(){
        return consumeName;
    }

    public ArrayList<String> getMultipleKeyphrase(){return multipleKeyphrase; }

    public Hash getActionHashMap(){
        return hash;
    }
}


