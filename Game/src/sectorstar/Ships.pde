class ShipSprite {
    protected int xCoor, yCoor;//x,y-coordinate 
    protected PImage sprite;

  
   
    public void show(int transx, int transy, float rotation){
         int x,y;
         pushMatrix();
         imageMode(CENTER);
         x=xCoor + transx;
         y=yCoor + transy;
         translate(x, y);
         rotate(rotation + PI/2);
         image(sprite,0,0);
         popMatrix();
    }
}

class Hero extends ShipSprite {
  
  public Hero(int x, int y) {
    if (sprite == null) {
      sprite = loadImage("../../resources/ships/turret.png");
    }
    this.xCoor = x;
    this.yCoor = y;
  }
  
  public void show(int transx, int transy, float rotation) {
   int x,y;
   pushMatrix();
   imageMode(CENTER);
   x = xCoor + transx;
   y = yCoor + transy;
   translate(x,y);
   rotate(rotation - PI/2);
   image(sprite,0,0);
   popMatrix();
  }
}
class NewShip extends ShipSprite {

  public NewShip(int x, int y) {
    if (sprite == null) {
      sprite = loadImage("../../resources/ships/newShip.png");
      sprite.resize(100, 100); 
    }
    this.xCoor = x;
    this.yCoor = y;
  }

  public void show(int transx, int transy, float rotation) {
    int x, y;
    pushMatrix();
    imageMode(CENTER);
    x = xCoor + transx;
    y = yCoor + transy;
    translate(x, y);
    rotate(rotation - PI / 2);
    image(sprite, 0, 0);
    popMatrix();
  }
}



class SecondTurret extends ShipSprite {
  
  public SecondTurret(int x, int y) {
    if (sprite == null) {
      sprite = loadImage("../../resources/ships/turret.png");
      sprite.resize(50, 50);
    }
    this.xCoor = x;
    this.yCoor = y;
  }
  
  public void show(int transx, int transy, float rotation) {
   int x,y;
   pushMatrix();
   imageMode(CENTER);
   x = xCoor + transx;
   y = yCoor + transy;
   translate(x,y);
   rotate(rotation - PI/2);
   image(sprite,0,0);
   popMatrix();
  }
}

class Enemy1Sprite extends ShipSprite {
    
    public Enemy1Sprite(int x, int y) {
      if (sprite == null){
            sprite = loadImage("../../resources/ships/enermy1.png");
        }
        this.xCoor = x;
        this.yCoor = y;
    }
    // public int getxCoor(){
    // return xCoor; 
    //}
}

//And so on...
class Enemy2Sprite extends ShipSprite {
    
    public Enemy2Sprite(int x, int y) {
      if (sprite == null){
            sprite = loadImage("../../resources/ships/enermy2.png");;
        }
        this.xCoor = x;
        this.yCoor = y;
    }
    // public int getxCoor(){
    // return xCoor; 
    //}
}



class Enemy3Sprite extends ShipSprite {

   
    public Enemy3Sprite(int x, int y) {
      if (sprite == null){
            sprite = loadImage("../../resources/ships/enermy3.png");;
        }
        this.xCoor = x;
        this.yCoor = y;
    }
}

class Enemy4Sprite extends ShipSprite {
    public Enemy4Sprite(int x, int y) {
      if (sprite == null){
            sprite = loadImage("../../resources/ships/enermy4.png");;
        }
        this.xCoor = x;
        this.yCoor = y;
    }
}
