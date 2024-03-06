// 这是一个相对复杂的项目，但不用担心，我们可以逐步完成。以下是一个简化的步骤来开始实现你的游戏引擎：

// 分析需求：仔细阅读作业要求，确保你理解每个任务的目标和要求。
// 设计你的游戏引擎架构：根据作业要求，设计类和接口，考虑游戏引擎的整体架构。
// 初始化游戏状态：根据任务7的要求，使用Java API for XML Processing (JAXP)从XML文件中加载游戏实体、位置和动作。创建相应的数据结构存储这些信息。
// 实现内置命令：根据任务2和任务3的要求，实现内置命令，例如get、goto、look等。
// 实现游戏动作：根据任务6的要求，实现游戏动作，确保游戏引擎可以正确响应游戏特定命令。
// 实现命令解析器：根据任务8的要求，设计并实现一个灵活的命令解析器，以处理来自用户的自然语言输入。
// 支持多用户：根据任务9的要求，扩展你的游戏引擎以支持多用户游戏。
// 测试和调试：使用提供的JUnit测试类以及你自己编写的测试用例测试游戏引擎。确保所有功能正常工作，并修复可能出现的问题。
// 优化和完善：在完成所有任务后，回顾你的代码，检查是否有可以优化的地方。确保代码结构清晰，易于阅读和维护。
package edu.uob;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
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

    public Hash getActionHashMap(){
        return hash;
    }
}


