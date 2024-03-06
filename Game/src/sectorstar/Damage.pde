class Damage {
    private int xCoor, yCoor;//x,y-coordinate 
    private PImage damage;
   
    public Damage(int x, int y) {
      if (damage == null){
            damage = loadImage("../../resources/damage/damage.png");;
        }
        this.xCoor = x;
        this.yCoor = y;
    }
    
    public void show(){
      image(damage,xCoor, yCoor);
    }
}

class Defend{
    private int xCoor, yCoor;//x,y-coordinate 
    private PImage defend;
   
    public Defend(int x, int y) {
      if (defend == null){
            defend = loadImage("../../resources/damage/defend.png");;
        }
        this.xCoor = x;
        this.yCoor = y;
    }
    
    public void show(){
      image(defend,xCoor, yCoor);
    }
}
