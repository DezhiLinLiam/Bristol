package edu.uob;

public abstract class GameEntity
{
    private String name;
    private String description;
    private boolean canMoving;

    public GameEntity(String name, String description)
    {
        this.name = name;
        this.description = description;
        canMoving = false;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public boolean getCanMoving(){
        return canMoving;
    }
}
