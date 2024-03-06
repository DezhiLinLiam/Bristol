
boolean jud =true;
boolean jud1 =true;
boolean add, isadding = false;
int gameState = 0; // 0: start screen, 1: game in progress, 4: tutorial
int difficulty = 1, iniDifficulty = 1; // Difficulty level (1 to 4)
int count = 0;
int time;
int weaponOption = 1;
int weaponOptionTurret = 1;
int time0;
int a;
Tutorial tutorial;
Loadout loadout;
Map map;
StartScreen startScreen;
Healthbars healthbar;
Leaderboard leaderboard = new Leaderboard();
Testing test = new Testing();


void setup(){
  test.runTest();
  size(1400,1000);
  frameRate(60);
  map = new Map();
  healthbar = new Healthbars();
  startScreen = new StartScreen(width,height);
}

void draw(){
  
  background(map.getBackground());
  
  if (gameState == 0) {
    startScreen.show(difficulty);

    leaderboard.read();
    return;
  }
  
  if (gameState == 4) {
    tutorial.draw(player);
    if (keyPressed) {
     if (key == ENTER) { tutorial.incrementStage(); delay(50); } 
    }
  }
  
  if (gameState == 1) {
     loadout.draw();  
    if(gameState == 5){
      delay(200);
    }
  }
  
  if (gameState == 5) {
    
    player.draw();
    player.move();
    if(player.hitcount == 0) healthbar.draw100();
    if(player.hitcount == 1) {
      healthbar.draw050();
      if(count == 0){
        time = millis();
        count++;
      }
      if(millis() - time < 2000){
        beenHit();
      }
     }
    showadd();
   
    switch(difficulty) {
      case 1:
      if(jud){
        enemy1.isUsing = true;
        jud =false;
      }
      enemy1.draw();
      enemy1.auto();
      break;
    case 2:
      if(jud){
          enemy1.isUsing = true;
          enemy2.isUsing = true;
          jud =false;
      }
      enemy1.draw();
      enemy1.auto();
      enemy2.draw();
      enemy2.auto();
      break;
    case 3: 
      if(jud){
          enemy1.isUsing = true;
          enemy2.isUsing = true;
          enemy3.isUsing = true;
          jud =false;
      }
      enemy1.draw();
      enemy1.auto();
      enemy2.draw();
      enemy2.auto();
      enemy3.draw();
      enemy3.auto();
      break;
    case 4:
    if(jud){
          enemy1.isUsing = true;
          enemy2.isUsing = true;
          enemy3.isUsing = true;
          enemy4.isUsing = true;
          jud =false;
      }
      enemy1.draw();
      enemy1.auto();
      enemy2.draw();
      enemy2.auto();
      enemy3.draw();
      enemy3.auto();
      enemy4.draw();
      enemy4.auto();
      break;
    }
    // code checking:  Control flow - 16. Does each switch statement have a default case?
    //                 In general, provide a default label for every switch statement.
    
    if(player.hitcount == 2 || player.isExploded){healthbar.draw000(); dead();}
    if(player.isExploded) { gameState = 2; }
    if(!enemy1.isUsing &&!enemy2.isUsing &&!enemy3.isUsing &&!enemy4.isUsing && !isadding ) { gameState = 3; }
    leaderboard.show();
  }else{
       if (gameState == 2){ drawGameOver(); }
        if(gameState == 3){ youWin(); }
  }
  
  
}

public void setWeaponOption(int i) {
  weaponOption = i;
}

public void setWeaponOptionTurret(int i) {
 weaponOptionTurret = i; 
}

void mousePressed() {  
  if (gameState == 0) {
    if (mouseY > height / 2 - 20 + 50 && mouseY < height / 2 + 20 + 50 && mouseX > width / 2 - 100  && mouseX < width / 2 + 100) {
      loadout = new Loadout(map.getBackground(1));
      gameState = 1;
    } 
    if (mouseY > height / 2 - 20 + 190 && mouseY < height / 2 + 20 + 190 && mouseX > width / 2 - 100  && mouseX < width / 2 + 100) {
      difficulty = (difficulty % 4) + 1;
       
      iniDifficulty = difficulty;

    }
    if (mouseY > 780 && mouseY < 850 && mouseX > 580  && mouseX < 780) {
      tutorial = new Tutorial(map);
      player = new Player(weaponOption, weaponOptionTurret);
      gameState = 4;
    }
    
  }
  if(gameState == 2 || gameState == 3){
      if (mouseX > width/2+220-100 && mouseX < width/2+220+100 && mouseY > height/2+100-50 && mouseY < height/2+100+50) {
      gameState = 5;
      player = new Player(weaponOption, weaponOptionTurret);
      enemy1 = new Enemy1();
      enemy2 = new Enemy2();
      enemy3 = new Enemy3();
      enemy4 = new Enemy4();
      jud = true;
      jud1 =true;
      leaderboard = new Leaderboard();
      difficulty=iniDifficulty;
      count = 0;
    }
    // code checking: Operators - 10. For each expression with more than one operator, 
    // are the assumptions about order of evaluation and precedence correct? Use brackets to clear up ambiguities.
    //  for example : in 'if' statement, (width/2+220-100)
    
    if (mouseX > width/2-220-100 && mouseX < width/2 -220+100 && mouseY > height/2+100 -50 && mouseY < height/2+100+50) {
      gameState = 0;
      player = new Player(weaponOption, weaponOptionTurret);
      enemy1 = new Enemy1();
      enemy2 = new Enemy2();
      enemy3 = new Enemy3();
      enemy4 = new Enemy4();
      difficulty = 1;
      jud = true;
      count = 0;
      jud1 =true;
     }
  }
  if (gameState == 4) {
   if (mouseX > 50 && mouseY > 50 && mouseX < 150 && mouseY < 150) {
    gameState = 0;
    player = new Player(weaponOption, weaponOptionTurret);
    jud = true;
    count = 0;
    jud1 = true;
   }
  }
}

void drawGameOver(){
    delay(1500);
    PImage img1,img2,img3;
     img1 = loadImage("../../resources/GameOver/GameOver.png");
     img2 = loadImage("../../resources/GameOver/Restart.png");
     img3 = loadImage("../../resources/GameOver/Menu.png");
     image(img1, width/2, height/2 -150, 400, 200);
     image(img2, width/2+220, height/2 +100, 200, 100);
     image(img3, width/2-220, height/2 +100, 185, 75);
     score();

}
void youWin(){
  delay(1000);
  score();
 PImage img1,img2,img3;
     img1 = loadImage("../../resources/YouWin/YouWin.png");
     img2 = loadImage("../../resources/YouWin/Restart.png");
     img3 = loadImage("../../resources/YouWin/Menu.png");
     image(img1, width/2, height/2 -150, 400, 200);
     image(img2, width/2+220, height/2 +100, 200, 100);
     image(img3, width/2-220, height/2 +100, 185, 75);
}
void beenHit(){
     PImage img;
     img = loadImage("../../resources/explosion/beenHit.png");
     image(img, width/2, height/2 -250, 400, 200);
}
void dead(){
     
     PImage img;
     img = loadImage("../../resources/GameOver/Dead.png");
     image(img, width/2, height/2 -400, 400, 200);
}

void score(){
  
  PFont font = createFont("Times", 32);  
  fill(color(255, 255, 0)); 
  textFont(font);  
  textAlign(CENTER, CENTER);  
  
  leaderboard.save();
  String text = "You defeated "+ leaderboard.get()+ " enmeies!";  
  text(text, width/2, height/2); 

}

int add(){
  

  if(!(!enemy1.isExploded && !enemy2.isExploded && !enemy3.isExploded && !enemy4.isExploded && difficulty == 4 ) ){

    if(!enemy1.isUsing) {
      isadding = true;
      enemy1.isUsing = true;
      time0 = millis();
      return 1;  
     }
      
    if(!enemy2.isUsing) {
      isadding = true;
      enemy2.isUsing = true;
      time0 = millis();
      return 2;
       
    }
    if(!enemy3.isUsing) {
      isadding = true;
      enemy3.isUsing = true;
      time0 = millis();
      return 3;
     }
    if(!enemy4.isUsing) {
      isadding = true;
      enemy4.isUsing = true;
      time0 = millis();
      return 4;
    }
  }
  add = false;
  return 0;
}

void showadd(){
  if(!isadding){
    if(add) a = add();
   }
  
  if(a == 0){
   return ;
  }

  if(a != 5 ){
    println(difficulty);

    if(millis()-time0 < 3000){
      isadding = true;
      PImage sprite = loadImage("../../resources/ships/enermy"+a+".png");
      tint(255, 128);  
      if(a ==1 ) image(sprite, 600+475, 600+105);
      if(a ==2 ) image(sprite, 50+475, 50+105);
      if(a ==3 ) image(sprite, 600+475, 700+105);
      if(a ==4 ) image(sprite, 800+475, 100+105);
      noTint();
      
    }else{
      isadding = false;
      if(a ==1 ) {
        
        enemy1 = new Enemy1();
        enemy1.isUsing = true;
        difficulty = difficulty >= 1 ? difficulty : 1;
      }
      if(a ==2 ) {
        enemy2 = new Enemy2();
        difficulty = difficulty >= 2 ? difficulty : 2;
        enemy2.isUsing = true;
      }
      if(a ==3 ) {
        enemy3 = new Enemy3();
        difficulty = difficulty >= 3 ? difficulty : 3;
        enemy3.isUsing = true;
      }
      if(a ==4 ) {
        enemy4 = new Enemy4();
        difficulty = difficulty >= 4 ? difficulty : 4;
        enemy4.isUsing = true;
      }
      a = 0;
    }
  }
  
}
