package edu.uob;

public class Artefact extends GameEntity {

    private boolean canMoving;
    public Artefact(String name, String description){
        super(name, description);
        canMoving = true;
    }

    @Override
    public boolean getCanMoving(){
        return canMoving;
    }
    
    @Override
    public String toString() {
        return "Artefact{name='" + getName() + "', description='" + getDescription() + "'}";
    }

}
