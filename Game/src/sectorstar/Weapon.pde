class GunPlayer {
  int fireRate; 
  int lastFired = 0;
  int projectileType = 1;
  ArrayList<Projectile> projectiles = new ArrayList<>();
  
  public GunPlayer(int fireRate, int weaponOption) {
    this.fireRate = fireRate;
    if (weaponOption >= 1 && weaponOption <= 3) {
       this.projectileType = weaponOption;
    }
  }
  
  public GunPlayer(int x) {
   fireRate = x; 
  }
  
  public void fire(int x, int y, float rotate) {
    if (fireRate <= (millis() - lastFired)){
      lastFired = millis();
      if (projectileType == 1) {projectiles.add(new Projectile(x,y,rotate));}
      if (projectileType == 2) {projectiles.add(new Projectile2(x,y,rotate));}
      if (projectileType == 3) {projectiles.add(new Projectile3(x,y,rotate));}
    }
  }
  public void updateProjectiles() {
   projectiles.forEach(x -> x.drawProjectile());
  }
  
  public void clearProjectiles() {
    projectiles.forEach(x -> x.clearProjectile());
  }
}

class Projectile {
  int speed = 10, xLoc, yLoc;
  float directionFired;
  PImage sprite = loadImage("../../resources/weapons/gun.png");
  boolean beenHit = false;
  
  public Projectile(int x, int y, float rotation) {
    xLoc = x;
    yLoc = y;
    directionFired = rotation;
    pushMatrix();
    imageMode(CENTER);
     if (xLoc < 20) {
        xLoc = 20;
     }
     if (yLoc < 20) {
        yLoc = 20;
     }
     if (xLoc > width-20) {
        xLoc = width-20;
     }
     if (yLoc > height-20) {
        yLoc = height-20;
     }
    //rotate(directionFired);
    translate(xLoc,yLoc);  
    image(sprite,0,0);
    popMatrix();
    
  }
  
  public void drawProjectile() {
    if(!beenHit){
      xLoc += speed*cos(directionFired);
      yLoc += speed*sin(directionFired);
      pushMatrix();
      imageMode(CENTER);
      translate(xLoc, yLoc);
      rotate(directionFired + radians(90));
      image(sprite,0, 0);
      popMatrix();
    }
  }
  
   public void clearProjectile(){
     beenHit = true;
   }
}

class Projectile2 extends Projectile {
 public Projectile2(int x, int y, float rotation) {
   super(x, y, rotation);
   super.speed = 5;
   super.sprite = loadImage("../../resources/weapons/gun2.png");
 }
}

class Projectile3 extends Projectile {
 public Projectile3(int x, int y, float rotation) {
  super(x, y, rotation);
  super.speed = 15;
  super.sprite = loadImage("../../resources/weapons/gun3.png");
 }
}

class Gunenemy {
  int fireRate; 
  
  ArrayList<EnemyGun> enemyguns = new ArrayList<>();
  
  public Gunenemy(int x) {
    fireRate = x;
  }
  
  public void fire(int x, int y, float rotate) {
   
      enemyguns.add(new EnemyGun(x, y, rotate));
    
  }
  public void updateProjectiles() {
   enemyguns.forEach(x -> x.drawEnemyProjectile());
  }
  
}
class EnemyGun {
  int speed = 4, xLoc, yLoc;
  float directionFired;
  PImage sprite = loadImage("../../resources/weapons/enemyGun.png");
  boolean beenHit = false;
  
  public EnemyGun (int x, int y, float rotation) {
    sprite.resize(15,30);
    xLoc = x;
    yLoc = y;
    directionFired = rotation;
    pushMatrix();
    imageMode(CENTER);
     if (xLoc < 20) {
        xLoc = 20;
     }
     if (yLoc < 20) {
        yLoc = 20;
     }
     if (xLoc > width-20) {
        xLoc = width-20;
     }
     if (yLoc > height-20) {
        yLoc = height-20;
     }
    //rotate(directionFired);
    translate(xLoc,yLoc);  
    image(sprite,0,0);
    popMatrix();
    
  }
  
  public void drawEnemyProjectile() {
    if(!beenHit){
      xLoc += speed*cos(directionFired);
      yLoc += speed*sin(directionFired);
      pushMatrix();
      imageMode(CENTER);
      translate(xLoc, yLoc);
      rotate(directionFired + radians(90));
      image(sprite,0, 0);
      popMatrix();
    }
  }
  
  
}

class Shield{
   float a = 30; 
  float b = 50; 
  int centerX = 400;
  int centerY =400;
  int count = 0;
  float xMax,yMax,xMin,yMin;
   public void show(int transX, int transY, float rotate) {
    xMax=-1000;
    yMax=-1000;
    xMin=1000;
    yMin=1000;
    ArrayList<Float> rotatedPointsX = new ArrayList<Float>();
    ArrayList<Float> rotatedPointsY = new ArrayList<Float>();
    pushMatrix();
    translate(centerX + transX, centerY + transY);
    rotate(rotate);

    fill(135, 206, 250, 100);
    noStroke();
    beginShape();
    for (float angle = -72.5; angle <= 73.5; angle++) {
        float x = a * cos(radians(angle));
        float y = b * sin(radians(angle));
      
        float rotatedX = x * cos(rotate) - y * sin(rotate);
        float rotatedY = x * sin(rotate) + y * cos(rotate);

        rotatedPointsX.add(rotatedX + centerX + transX);
        rotatedPointsY.add(rotatedY + centerY + transY);

        vertex(x, y);
    
    }
    endShape(CLOSE);
    for (float coord : rotatedPointsX) {
        if (coord > xMax) {
            xMax = coord;
        }
        if (coord < xMin) {
            xMin = coord;
        }
    }

    for (float coord : rotatedPointsY) {
        if (coord > yMax) {
            yMax = coord;
        }
        if (coord < yMin) {
            yMin = coord;
        }
    }
    
    

    
    float highlightSize = 15;
    float highlightAngle = 20;
    float highlightX = a * cos(radians(highlightAngle))-10;
    float highlightY = b * sin(radians(highlightAngle))-10;
    fill(255, 255, 255, 180);
    ellipse(highlightX, highlightY, highlightSize, highlightSize / 2);
 
 
    popMatrix();
   
   }
   public float getxMax(){
    // set the line color black
     if (xMax == xMin) return xMax+5;
     return xMax;
   }
   public float getyMax(){
    
     if (yMax == yMin) return yMax+5;
     return yMax;
   }
   public float getxMin(){

     return xMin;
   }
   public float getyMin(){
    
     return yMin;
   }

}
