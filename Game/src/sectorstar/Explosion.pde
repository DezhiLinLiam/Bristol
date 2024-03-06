
class Explosion {
    private int xCoor, yCoor; //x,y-coordinate 
    private PImage explosion;
   
    public Explosion(int x, int y) {
      if (explosion == null){
            explosion = loadImage("../../resources/explosion/explosionBoom.png");;
        }
        this.xCoor = x;
        this.yCoor = y;
    }
    
    public void showExplosion(){
      image(explosion,xCoor, yCoor);
    }
}
