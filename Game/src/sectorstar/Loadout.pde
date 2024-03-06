class Loadout {
  PImage navigationSquare;
  PImage rightArrow;
  PImage leftArrow;
  PImage projectileSprite1;
  PImage projectileSprite2;
  PImage projectileSprite3;
  PImage turretSwitch;
  PImage shipSwitch;
  PImage background;
  PImage shipSprite;
  PImage turretSprite;
  PImage confirm;
  PImage quit;
  PImage turretSwitchChecked;
  PImage shipSwitchChecked;
  PImage confirmChecked;
  PImage start;
  PImage startChecked;
  int time1 =0, time2 = 0;
  
  GunPlayer gun1 = new GunPlayer(1000,1);
  GunPlayer gun2 = new GunPlayer(500,2);
  GunPlayer gun3 = new GunPlayer(2000,3);
  
  int weaponSelect = 0;
  boolean playerSelect = true;
  
  public Loadout(PImage background) {
   this.background = background;
   shipSprite = loadImage("../../resources/ships/newShip.png");
   turretSprite = loadImage("../../resources/ships/turret.png");
   navigationSquare = loadImage("../../resources/Loadout/navigationSquare.png");
   rightArrow = loadImage("../../resources/Loadout/rightArrow.png");
   leftArrow = loadImage("../../resources/Loadout/leftArrow.png");
   projectileSprite1 = loadImage("../../resources/weapons/gun.png");
   projectileSprite2 = loadImage("../../resources/weapons/gun2.png");
   projectileSprite3 = loadImage("../../resources/weapons/gun3.png");
   confirm = loadImage("../../resources/Loadout/confirm.png");
   confirmChecked = loadImage("../../resources/Loadout/confirmChecked.png");
   start= loadImage("../../resources/Loadout/StartGame.png");
   startChecked = loadImage("../../resources/Loadout/StartGameChecked.png");
   turretSwitch = loadImage("../../resources/Loadout/turretSwitch.png");
   shipSwitch = loadImage("../../resources/Loadout/shipSwitch.png");
   turretSwitchChecked = loadImage("../../resources/Loadout/turretSwitchChecked.png");
   shipSwitchChecked = loadImage("../../resources/Loadout/shipSwitchChecked.png");
   quit = loadImage("../../resources/Loadout/X.png");
  }
  
  public void draw() {
 
    background(background);
    image(quit,25,25);
    mouseOperations();
    loadImages();
    if (playerSelect) {
      pushMatrix();
      translate(width/2+55,height/2-35);
      rotate(PI/2);
      image(turretSprite,0,0);
      popMatrix();
    }
    else {
     pushMatrix();
     imageMode(CENTER);
     translate(width/2+55, height/2);
     rotate(PI);
     image(shipSprite,0,0);
     popMatrix();
    }
     switch(weaponSelect) {
      case 0:
         gun1.fire(width/2,height/2,PI);
         break;
      case 1:
         gun2.fire(width/2,height/2,PI);
         break;
      case 2:
         gun3.fire(width/2,height/2,PI);
         break;
      default:
         break;
     }
     gun1.updateProjectiles();
     gun2.updateProjectiles();
     gun3.updateProjectiles();
  }
  
  private void loadImages() {
    imageMode(CENTER);
    image(navigationSquare, width/2 - 200, height/2);
    image(rightArrow, width/2 + 500, height/2);
    if(millis() - time1 >= 100){
      image(confirm, width/2-300, height/2 + 400);
    }else{
      image(confirmChecked, width/2-300, height/2 + 400);
    }
    if(millis() - time2 >= 100){
      start.resize(300,60);
      image(start, width/2+300, height/2 + 400);
    }else{
      startChecked.resize(300,60);
      image(startChecked, width/2+300, height/2 + 400);
    }
    text(1+weaponSelect,width/2+90,height/2-195);
    
    if (playerSelect) {
      imageMode(CORNER);
      image(turretSwitchChecked, 50, 150);
      image(shipSwitch, 300, 150); 
    }
    if (!playerSelect) {
      imageMode(CORNER);
      image(turretSwitch, 50, 150);
       image(shipSwitchChecked, 300, 150); 
    }
  }
  // code checking: Operators - 10. For each expression with more than one operator, 
  // are the assumptions about order of evaluation and precedence correct? Use brackets to clear up ambiguities.
  //  for example : in 'if' statement, (height/2 + 20)
  void mouseOperations() {
     if (mousePressed) {
     if (mouseY > 150 && mouseY < 210 && mouseX > 50 && mouseX < 233) {
      playerSelect = true;
      delay(100);
     }
     if (mouseY > 150 && mouseY < 210 && mouseX > 300 && mouseX < 583) {
      playerSelect = false;
      delay(100);
     }
     
     if (mouseY < height/2 + 20 && mouseY > height/2 -20) {
      if (mouseX > width/2 + 450 && mouseX < width/2 + 550) {
        weaponSelect = (weaponSelect+1)%3;
        gun1.clearProjectiles();
        gun2.clearProjectiles();
        gun3.clearProjectiles();
        delay(100);
      }
     }
     if (mouseY > height/2 + 350 && mouseY < height/2 + 450 && mouseX > width/2 -400 && mouseX < width/2 - 200) {
       time1 = millis();
       if (playerSelect) {
          
          setWeaponOptionTurret(weaponSelect+1);
          
       }
       else {
        setWeaponOption(weaponSelect+1);
         
        
       }
       
     }
     
     if (mouseY > height/2 + 350 && mouseY < height/2 + 450 && mouseX > width/2 +200 && mouseX < width/2 +400) {
       time2 = millis();
       gameState = 5;
       player = new Player(weaponOption, weaponOptionTurret);
     }
     if (mouseY < 50 && mouseX < 50) {
       gameState = 0;
     }
    }
  }
}
