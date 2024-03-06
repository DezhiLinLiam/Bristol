package edu.uob;

import java.io.File;
import java.security.DrbgParameters.Reseed;
import java.util.ArrayList;

public class Players {
    int health;
    String uname;
    String positionName = "cabin";
    ArrayList<String> inv = new ArrayList<>();
    ArrayList<String> storeRoom = new ArrayList<>();

    public Players(String uname){
        this.uname = uname;
        health = 3;
    }


    

    public void getHurt(){
        health--;
    }

    public void addHealth(){
        health++;
    }

    public void setPosition(String CurrenPosition){
       
        positionName = CurrenPosition;
       
    }

    public String getPosition(){
        return positionName;
    }

    public String getName(){
        return uname;
    }
    
    public void addInv(String entity){
        inv.add(entity);
    }

    public void removeInv(String entity){
        inv.remove(entity);
    }

    public String getInv(){
        String reString = "";
        if(inv.isEmpty()){
            return "Your inventory is empty!";
        }else{
            for(String entity : inv){
                reString = reString + entity;
                reString = reString + ", ";
            }
            reString = reString.substring(0, reString.length()-2);
            reString = "You have: " + reString;
            return reString;
        }
    }

    public void iniStoreRoom(ArrayList<String> store){
        this.storeRoom = store;
    }

    public void addStoreRoom(String add){
        storeRoom.add(add);
    }

    public void removeStoreRoom(String remove){
        storeRoom.remove(remove);
    }

    public String getStoreRoom(){
        String reString = "";
        for(String store : storeRoom){
            reString = reString + store;
            reString = reString + ", ";
        }
        reString = reString.substring(0, reString.length()-2);
        return "The store Room has: " + reString;
    }
}
