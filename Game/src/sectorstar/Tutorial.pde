class Tutorial {
   PImage up;
 PImage down;
 PImage mouse;
 PImage aKey;
 PImage dKey;
 PImage lmb;
 PImage x;
 PImage space;
 PImage enter;
 PImage play;
 Map map;
 int stageNumber = 0;
 
 public Tutorial(Map map) {
   up = loadImage("../../resources/Tutorial/UP.png");
   down = loadImage("../../resources/Tutorial/DOWN.png");
  mouse = loadImage("../../resources/Tutorial/Mouse_icon1.png");
  aKey = loadImage("../../resources/Tutorial/A.png");
  dKey = loadImage("../../resources/Tutorial/D.png");
  lmb = loadImage("../../resources/Tutorial/Mouse_icon_lmb.png");
  x = loadImage("../../resources/Tutorial/X.png");
  space = loadImage("../../resources/Tutorial/Space.png");
  enter = loadImage("../../resources/Tutorial/Enter.png");
  play = loadImage("../../resources/Tutorial/play.png");
  stageNumber = 0;
  
  this.map = map;
 }
 
 public void draw(Player player) {
  background(map.getBackground(1));
  player.draw();
  player.move();
  image(x,100,100);
  if (stageNumber == 0) {
    //mid top
     textSize(38);fill(220,192,82); 
     text("Player 1 controls the movement of the ship", 400, height/5-120);
     text("and the first turret", 400, height/5-70);
  
     textSize(38);fill(255,255,255); 
     //may need change
     image(mouse,width/6,height/5+50);
     text("Move                                to change direction", 30, height/5+50);
     
     image(up,width/9,height/5+220);
     text("Use                   to move forward", 30, height/5+200);  
     
     image(down,width/9,height/5+370);
     text("Use                   to go back", 30, height/5+350);  

  
     image(lmb,width/6,height/5+500);
     text("Click the                   to fire weapon", 30, height/5+500);
     
  }
  
  image(enter, width-100, 4*height/5);
  text("Continue: ", width -350, 4*height/5-20);
  
  if (stageNumber == 1) {
     textSize(38);fill(76,195,227); 
     text("Player 2 controls the front turret with the keyboard", 300, 100);
         
     textSize(38);fill(255,255,255); 
          image(aKey,width/9,height/5+70);
     text("Use                   key to rotate left ", 30, height/5+50);
  
          image(dKey, width/9,2*height/5+50);
     text("Use                   key to rotate right", 30, 2*height/5+30);
     
          image(space, width/7+5, 3*height/5+40);
     text("Use                                to fire weapon", 30, 3*height/5+20);
  
  }
  if (stageNumber == 2) {
   image(play, width/2, 2*height/5);
  }
  if(stageNumber == 3){
    stageNumber = 0;
    gameState = 0;
  }
 }
 
 
 
 public void incrementStage() {
   stageNumber++;
 }
}
