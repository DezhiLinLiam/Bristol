package edu.uob;

import java.util.ArrayList;

public class GameAction{
    ArrayList<String> subjects = new ArrayList<String>();
    ArrayList<String> consumed = new ArrayList<String>();
    ArrayList<String> produced = new ArrayList<String>();
    ArrayList<String> narration = new ArrayList<String>();

    public void addSubjects(String entity){
        subjects.add(entity);
    }
    public void addConsumed(String entity){
        consumed.add(entity);
    }
    public void addProduced(String entity){
        produced.add(entity);
    }
    public void addNarration(String entity){
        narration.add(entity);
    }
    public ArrayList<String> getSubjects(){
        return subjects;
    }
    public ArrayList<String> getConsumed(){
        return consumed;
    }
    public ArrayList<String> getProduced(){
        return produced;
    }
    public ArrayList<String> getNarration(){
        return narration;
    }
}

