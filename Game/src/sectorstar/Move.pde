// define a player
class Ship {
    boolean isUsing = false;
    public boolean[] movementKeys = new boolean[4];
    public boolean[] weaponFiring = new boolean[2];
    int hitcount = 0;
    int speed = 5, transy = 105, transx = 475,secondTurretTrans = 45,secondTurretTransx,secondTurretTransy;
    int shieldTurretTrans = 60, shieldTurretTransx, shieldTurretTransy;
    float secondTurretRotation = radians(-90);
    boolean secondTurretFiring = false;

    float rotation = radians(-90);
    int lastFired = 0;
    boolean jud =true;
    
   
}

class Player extends Ship {
  final int startX = 400;
  final int startY = 400;
    public boolean[] movementKeys = new boolean[4];
    int x = startX;
    int y = startY;
    int speed = 5;
    boolean isExploded = false;
    
    int weaponOption = 1;
    int fireRate = 1000;
    
    int weaponOptionTurret = 1;
    int fireRateTurret = 1000;
    GunPlayer gunPlayer;
    GunPlayer secondTurretGun;
    
    
    
    public Player(int weaponOption, int weaponOptionTurret) {
      
      
      
      this.weaponOption = weaponOption;
      this.weaponOptionTurret = weaponOptionTurret;
      
 
      if (weaponOption == 1) {fireRate = 1000;}
      if (weaponOption == 2) {fireRate = 500;}
      if (weaponOption == 3) {fireRate = 2000;}
      
     
      if (weaponOptionTurret == 1) {fireRateTurret = 1000;}
      if (weaponOptionTurret == 2) {fireRateTurret = 500;}
      if (weaponOptionTurret == 3) {fireRateTurret = 2000;}
     
    gunPlayer = new GunPlayer(fireRate, weaponOption);
    secondTurretGun = new GunPlayer(fireRateTurret, weaponOptionTurret);
  
   } 
    
    
   

    void draw() {
      
        float distance;
        if(!enemy1.isExploded){
            distance = dist(player.x,player.y, enemy1.getX(), enemy1.getY());
            if (distance < 50) {
                isExploded = true;
            }
        }
        if(!enemy2.isExploded){
            distance = dist(player.x, player.y, enemy2.getX(), enemy2.getY());
            if (distance < 50) {
                isExploded = true;
            }
        }
        if(!enemy3.isExploded){
            distance = dist(player.x, player.y, enemy3.getX(), enemy3.getY());
            if (distance < 50) {
                isExploded = true;
            }
        }
        if(!enemy4.isExploded){
            distance = dist(player.x, player.y, enemy4.getX(), enemy4.getY());
            if (distance < 50) {
                isExploded = true;
            }
        }
        if(isExploded){
            Explosion explosion = new Explosion(player.getX(),player.getY());
            explosion.showExplosion();
        }else{
          
            Shield shield = new Shield();
            Hero hero = new Hero(startX,startY);
            NewShip newShip = new NewShip(startX,startY);
            SecondTurret secondTurret = new SecondTurret(startX,startY);
            hero.show(transx, transy, rotation);
            newShip.show(transx, transy, rotation+PI/2);
           
            secondTurret.show(transx + secondTurretTransx, transy+secondTurretTransy, secondTurretRotation);
            shield.show(transx + shieldTurretTransx, transy+shieldTurretTransy, rotation);
  
            x = hero.xCoor+transx;
            y = hero.yCoor+transy;

        }
    }
     public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getTransx(){
        return transx;
    }
    public int getTransy(){
        return transy;
    }
    public int getShieldTurretTransx(){
        return shieldTurretTransx;
    }
    public int getshieldTurretTransy(){
        return shieldTurretTransy;
    }
    public float getRotation(){
        return rotation;
    }
    

    void move() {
      if (mouseX == x && mouseY == y) { return; }
      fireCheck();
      rotateToMouse();
        gunPlayer.updateProjectiles();
        secondTurretGun.updateProjectiles();
        //forward
        if (movementKeys[0]) {
          if(dist(mouseX, mouseY, x, y) >70){
            transx += speed*cos(rotation);
            transy += speed*sin(rotation);
          }
        }
        //back
        if (movementKeys[1]) {
            transx -= speed*cos(rotation);
            transy -= speed*sin(rotation);
        }
        if (movementKeys[0] || movementKeys[1]) {
            if (transx >= (width - startX-50)) {
                transx = (width - startX-50);
            }
            if (transy > (height - startY-50)) {
                transy = (height - startY-50);
            }
            if (transx < -startX+50) {
                transx = -startX+50;
            }
            if (transy < -startY+50) {
                transy = -startY+50;
            }
        }
        
        //second turret rotate
        if (movementKeys[2]){
           secondTurretRotation -= PI/20;
        }
        //second turret rotate
        if (movementKeys[3]){
           secondTurretRotation += PI/20;
        }
       
        enemyIsHit();
 
        if (weaponFiring[0]){
            gunPlayer.fire(transx+400, transy+400 , rotation);
        }
        if (secondTurretFiring) {
          secondTurretGun.fire(transx+400+secondTurretTransx, transy+400+secondTurretTransy, secondTurretRotation);
        }
    }
    
    void rotateToMouse() {
      //rotate to mouse location
      float xDifference = abs(mouseX - x);
      float yDifference = abs(mouseY - y);
      float angle = atan(xDifference/yDifference);
      if(dist(mouseX, mouseY, x, y) >70){
        if (mouseY > y) {
          if (mouseX >= x) { rotation = PI/2 - angle; }
          else { rotation = angle + PI/2; }
        } 
        else if (mouseX >= x) { rotation = angle - PI/2; }
        else if (mouseX < x) { rotation = 3*PI/2 - angle; } 
        secondTurretTransx =  int (secondTurretTrans*cos(rotation));
        secondTurretTransy =  int (secondTurretTrans*sin(rotation));
        shieldTurretTransx =  int (shieldTurretTrans*cos(rotation));
        shieldTurretTransy =  int (shieldTurretTrans*sin(rotation));
      }
    }


    void enemyIsHit(){
    for (Projectile projectile : gunPlayer.projectiles) {
            if(!projectile.beenHit){
                if (dist(projectile.xLoc, projectile.yLoc, enemy1.x, enemy1.y) < 40 && !enemy1.isExploded) {
                    enemy1.hitcount += 1;
                    projectile.beenHit = true;
                    if(enemy1.hitcount == 2){
                        enemy1.isUsing = false;
                        leaderboard.add();
                    }

                }

                if (dist(projectile.xLoc, projectile.yLoc, enemy2.x, enemy2.y) < 40 && !enemy2.isExploded) {
                    enemy2.hitcount += 1;
                    projectile.beenHit = true;
                     if(enemy2.hitcount == 2){
                        enemy2.isUsing = false;
                        leaderboard.add();
                    }
                }
                if (dist(projectile.xLoc, projectile.yLoc, enemy3.x, enemy3.y) < 40 && !enemy3.isExploded) {
                    enemy3.hitcount += 1;
                    projectile.beenHit = true;
                     if(enemy3.hitcount == 2){
                        enemy3.isUsing = false;
                        leaderboard.add();
                    }

                }
                if (dist(projectile.xLoc, projectile.yLoc, enemy4.x, enemy4.y) < 40 && !enemy4.isExploded) {
                    enemy4.hitcount += 1;
                    projectile.beenHit = true;
                     if(enemy4.hitcount == 2){
                        enemy4.isUsing = false;
                        leaderboard.add();
                    }

                }
            }
        }
        for (Projectile projectile : secondTurretGun.projectiles) {
            if(!projectile.beenHit){
                if (dist(projectile.xLoc, projectile.yLoc, enemy1.x, enemy1.y) < 40 && !enemy1.isExploded) {
                    enemy1.hitcount += 1;
                    projectile.beenHit = true;

                }

                if (dist(projectile.xLoc, projectile.yLoc, enemy2.x, enemy2.y) < 40 && !enemy2.isExploded) {
                    enemy2.hitcount += 1;
                    projectile.beenHit = true;
                }
                if (dist(projectile.xLoc, projectile.yLoc, enemy3.x, enemy3.y) < 40 && !enemy3.isExploded) {
                    enemy3.hitcount += 1;
                    projectile.beenHit = true;

                }
                if (dist(projectile.xLoc, projectile.yLoc, enemy4.x, enemy4.y) < 40 && !enemy4.isExploded) {
                    enemy4.hitcount += 1;
                    projectile.beenHit = true;

                }
            }
        }
        
        if(enemy1.hitcount == 2){
            enemy1.isExploding = true;
            enemy1.isExploded = true;
            enemy1.isUsing = false;
        }
        if(enemy2.hitcount == 2){
            enemy2.isExploding = true;
            enemy2.isExploded = true;
            enemy2.isUsing = false;
        }
        if(enemy3.hitcount == 2){
            enemy3.isExploding = true;
            enemy3.isExploded = true;
            enemy3.isUsing = false;
        }
        if(enemy4.hitcount == 2){
            enemy4.isExploding = true;
            enemy4.isExploded = true;
            enemy4.isUsing = false;
        }
     }

};

class Enemy1 extends Ship {
    float speed = 2.5;
    int x = 2000;
    int y = 2000;
    boolean isExploding = false;
    boolean isExploded = false;
    int explosionStartTime = 0 ,denfendStartTime=0;
    int explosionDuration = 900;
    int count = 0;
    int count1 =0;
    int count2 =0;
    int count3 = 0;
    int startTime ;
    int duration =900;
    boolean startTimeInitialized = false, defend = false;
    Gunenemy enemyGun = new Gunenemy(2000);
    

    void draw() {
        if(count1 == 0 && hitcount == 1){
            if (!startTimeInitialized) {
                startTime = millis();
                startTimeInitialized = true;
                count++;
            }
            if(count == 1){
                if(millis() - startTime < duration){
                    Damage damage = new Damage(enemy1.getX()-70,enemy1.getY()-70);
                    damage.show();
                }
            }
        }

        if (isExploding) {
            if(count2 == 0){
                explosionStartTime = millis();
                count2++;
            }
            if ((millis() - explosionStartTime )> explosionDuration) {
                isExploding = false;
                
            } else {
                Explosion explosion = new Explosion(enemy1.getX(),enemy1.getY());
                Damage damage = new Damage(enemy1.getX()-100,enemy1.getY()-50);
                damage.show();
                explosion.showExplosion();
            }
        } else {
            if(!isExploded){
               // enemyGun.updateProjectiles(shield1);
                ShipSprite enemy1 = new Enemy1Sprite(600,600);
                enemy1.show(transx,transy,rotation);
                x = enemy1.xCoor+transx;
                y = enemy1.yCoor+transy;
            }
        }
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }

    void auto(){
      enemyGun.updateProjectiles();
      int currentTime = millis();
      if(jud){
        lastFired = currentTime;
        jud =false;
      }
        if(!isExploded){
            if(player.getX() - enemy1.getX() >= 0 && Math.abs(player.getX() - enemy1.getX()) > 100){
                enemy1.rotation = atan2(player.getY()-enemy1.getY(), player.getX()-enemy1.getX());
                transx+=speed;
            }
            if(player.getX() - enemy1.getX() <= 0 && Math.abs(player.getX() - enemy1.getX()) > 100){
                enemy1.rotation = atan2(player.getY()-enemy1.getY(), player.getX()-enemy1.getX());
                transx-=speed;
            }
            if(player.getY() - enemy1.getY() >= 0 && Math.abs(player.getY() - enemy1.getY()) > 100){
                enemy1.rotation = atan2(player.getY()-enemy1.getY(), player.getX()-enemy1.getX());
                transy+=speed;
            }
            if(player.getY() - enemy1.getY() <= 0 && Math.abs(player.getY() - enemy1.getY()) > 100){
                enemy1.rotation = atan2(player.getY()-enemy1.getY(), player.getX()-enemy1.getX());
                transy-=speed;
            }
            float distance = dist(enemy1.getX(), enemy1.getY(), enemy2.getX(), enemy2.getY());
            float minDistance = 80;
            if (distance < minDistance) {
                float enemy1DistanceToPlayer = dist(enemy1.getX(), enemy1.getY(), player.getX(), player.getY());
                float enemy2DistanceToPlayer = dist(enemy2.getX(), enemy2.getY(), player.getX(), player.getY());
                if(enemy1DistanceToPlayer > enemy2DistanceToPlayer){
                    enemy1.speed = 0;
                }else{
                    enemy2.speed = 0;
                }
                if(enemy1.isExploded)
                {
                  enemy2.speed = 2;
                }
                if(enemy2.isExploded)
                {
                  enemy1.speed = 2;
                }
                if( enemy1.speed == 0 && enemy1.speed == 0){
                    if(enemy1DistanceToPlayer > enemy2DistanceToPlayer){
                        enemy2.speed = 1;
                    }else{
                        enemy1.speed = 1;
                    }
                }
            }
            if (distance > minDistance) {
                enemy1.speed = 2;
                enemy2.speed = 2;
            }
            if (currentTime-lastFired > 2000){
            enemyGun.fire(transx+600, transy+600 , rotation);
            lastFired =currentTime;
            }
        }
        
        thePlayerIsHit();
    }
     void thePlayerIsHit(){
        Shield shield = new Shield();
        shield.show(player.getTransx() + player.getShieldTurretTransx(), player.getTransy()+player.getshieldTurretTransy(), player.getRotation());
        for (EnemyGun enemygun : enemyGun.enemyguns) {
          if(!enemygun.beenHit){
            if (dist(enemygun.xLoc, enemygun.yLoc, player.getX(), player.getY()) < 40) {
              
              player.hitcount++;
              enemygun.beenHit = true;
              if(player.hitcount == 2) player.isExploded = true;
              if(player.isExploded){
                  Explosion explosion = new Explosion(player.getX(),player.getY());
                  explosion.showExplosion();
              }
              break;
            }
            if(enemygun.xLoc<shield.getxMax() && enemygun.xLoc>shield.getxMin() && enemygun.yLoc > shield.getyMin() && shield.getyMax() >  enemygun.yLoc){
              enemygun.beenHit = true;
              defend = true;
            }
          }
        }
        if(defend){
          if(count3 == 0){
                denfendStartTime = millis();
                count3++;
            }
            if ((millis() - denfendStartTime )> explosionDuration) {
                defend = false;
                count3 =0;
            } else {
                Defend defend = new Defend(int (shield.getxMax()),int (shield.getyMax()));
                defend.show();
            }
        }
    }
}

class Enemy2 extends Ship {
    float speed = 2;
    int x = 12000;
    int y = 2000;
    boolean isExploding = false;
    boolean isExploded = false;
    int explosionStartTime = 0;
    int explosionDuration = 1200,denfendStartTime = 0;
    int count = 0;
    int count1 = 0;
    int count2 = 0;
    int count3= 0;
    int startTime ;
    int duration =900;
    boolean startTimeInitialized = false, defend = false;
    Gunenemy enemyGun = new Gunenemy(3000);

    void draw() {
        if(count1 == 0 && hitcount == 1){
            if (!startTimeInitialized) {
                startTime = millis();
                startTimeInitialized = true;
                count++;
            }
            if(count == 1){
                if(millis() - startTime < duration){
                    Damage damage = new Damage(enemy2.getX()-70,enemy2.getY()-70);
                    damage.show();
                }
            }
        }
        if (isExploding) {
            if(count2 == 0){
                explosionStartTime = millis();
                count2++;
            }
            if ((millis() - explosionStartTime )> explosionDuration) {
                isExploding = false;
            } else {
                Explosion explosion = new Explosion(enemy2.getX(),enemy2.getY());
                Damage damage = new Damage(enemy2.getX()-100,enemy2.getY()-50);
                damage.show();
                explosion.showExplosion();
            }
        } else {
            if(!isExploded){
                ShipSprite enemy2 = new Enemy2Sprite(50,50);
                enemy2.show(transx,transy,rotation);
                x = enemy2.xCoor+transx;
                y = enemy2.yCoor+transy;
            }
        }
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }

    void auto(){
        enemyGun.updateProjectiles();
        int currentTime = millis();
        if(jud){
        lastFired = currentTime;
        jud =false;
      }
        if(!isExploded){
            if(player.getX() - enemy2.getX() >= 0 && Math.abs(player.getX() - enemy2.getX()) > 100){
                enemy2.rotation = atan2(player.getY()-enemy2.getY(), player.getX()-enemy2.getX());
                transx+=speed;
            }
            if(player.getX() - enemy2.getX() <= 0 && Math.abs(player.getX() - enemy2.getX()) > 100){
                enemy2.rotation = atan2(player.getY()-enemy2.getY(), player.getX()-enemy2.getX());
                transx-=speed;
            }
            if(player.getY() - enemy2.getY() >= 0 && Math.abs(player.getY() - enemy2.getY()) > 100){
                enemy2.rotation = atan2(player.getY()-enemy2.getY(), player.getX()-enemy2.getX());
                transy+=speed;
            }
            if(player.getY() - enemy2.getY() <= 0 && Math.abs(player.getY() - enemy2.getY()) > 100){
                enemy2.rotation = atan2(player.getY()-enemy2.getY(), player.getX()-enemy2.getX());
                transy-=speed;
            }
             if (currentTime-lastFired > 2500){
            enemyGun.fire(transx+50, transy+50 , rotation);
            lastFired =currentTime;
            }
            if(enemy1.isExploded){
                enemy2.speed = 2;
            }
        } 
        thePlayerIsHit();
    }
    void thePlayerIsHit(){
        Shield shield = new Shield();
        shield.show(player.getTransx() + player.getShieldTurretTransx(), player.getTransy()+player.getshieldTurretTransy(), player.getRotation());
        for (EnemyGun enemygun : enemyGun.enemyguns) {
          if(!enemygun.beenHit){
            if (dist(enemygun.xLoc, enemygun.yLoc, player.getX(), player.getY()) < 40) {
              
              player.hitcount++;
              enemygun.beenHit = true;
              if(player.hitcount == 2) player.isExploded = true;
               if(player.isExploded){
                  Explosion explosion = new Explosion(player.getX(),player.getY());
                  explosion.showExplosion();
              }
              break;
            }
            if(enemygun.xLoc<shield.getxMax() && enemygun.xLoc>shield.getxMin() && enemygun.yLoc > shield.getyMin() && shield.getyMax() >  enemygun.yLoc){
              enemygun.beenHit = true;
              defend = true;
            }
          }
        }
        if(defend){
          if(count3 == 0){
                denfendStartTime = millis();
                count3++;
            }
            if ((millis() - denfendStartTime )> explosionDuration) {
                defend = false;
                count3 =0;
            } else {
                Defend defend = new Defend(int (shield.getxMax()),int (shield.getyMax()));
                defend.show();
            }
        }
    }
}

class Enemy3 extends Ship {
    float direction;
    private float lastDirectionChangeTime = 0;
    private float directionChangeInterval = 5; 
    float speed = 3;
    int x = 2000;
    int y = 2000;
    boolean isExploding = false;
    boolean isExploded = false;
    int explosionStartTime = 0,denfendStartTime =0;
    int explosionDuration = 1200;
    int count = 0;
    int count1 = 0;
    int count2 = 0;
    int count3 = 3;
    int startTime ;
    int duration =900;
    boolean startTimeInitialized = false, defend = false;
    Gunenemy enemyGun = new Gunenemy(3000);
    void draw() {
        if(count1 == 0 && hitcount == 1){
            if (!startTimeInitialized) {
                startTime = millis();
                startTimeInitialized = true;
                count++;
            }
            if(count == 1){
                if(millis() - startTime < duration){
                    Damage damage = new Damage(enemy3.getX()-70,enemy3.getY()-70);
                    damage.show();
                }
            }
        }
        if (isExploding) {
            if(count2 == 0){
                explosionStartTime = millis();
                count2++;
            }
            if ((millis() - explosionStartTime )> explosionDuration) {
                isExploding = false;
            } else {
                Explosion explosion = new Explosion(enemy3.getX(),enemy3.getY());
                Damage damage = new Damage(enemy3.getX()-100,enemy3.getY()-50);
                damage.show();
                explosion.showExplosion();
            }
        } else {
            if(!isExploded){
                ShipSprite enemy3 = new Enemy3Sprite(600,700);
                enemy3.show(transx,transy,rotation);
                x = enemy3.xCoor+transx;
                y = enemy3.yCoor+transy;
            }
        }
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    void auto(){
        enemyGun.updateProjectiles();
        int currentTime1 = millis();
        if(jud){
        lastFired = currentTime1;
        jud =false;
      }
      float currentTime = millis() / 1000.0; 
      if (currentTime - lastDirectionChangeTime >= directionChangeInterval) {
            direction = random(0, TWO_PI); 
            lastDirectionChangeTime = currentTime;
        }
        bounceOff();

        if(!isExploded){
          enemy3.rotation = atan2(player.getY()-enemy3.getY(), player.getX()-enemy3.getX());
          transx = transx+round(speed * cos(direction));
          transy = transy+round(speed * sin(direction));
          if (currentTime1-lastFired > 3000){
            enemyGun.fire(transx+600, transy+700 , rotation);
            lastFired =currentTime1;
        }
        }
        
        thePlayerIsHit();
        
    }
    void thePlayerIsHit(){
        Shield shield = new Shield();
        shield.show(player.getTransx() + player.getShieldTurretTransx(), player.getTransy()+player.getshieldTurretTransy(), player.getRotation());
        for (EnemyGun enemygun : enemyGun.enemyguns) {
          if(!enemygun.beenHit){
            if (dist(enemygun.xLoc, enemygun.yLoc, player.getX(), player.getY()) < 40) {
              
              player.hitcount++;
              enemygun.beenHit = true;
              if(player.hitcount == 2) player.isExploded = true;
               if(player.isExploded){
                  Explosion explosion = new Explosion(player.getX(),player.getY());
                  explosion.showExplosion();
              }
              break;
            }
            if(enemygun.xLoc<shield.getxMax() && enemygun.xLoc>shield.getxMin() && enemygun.yLoc > shield.getyMin() && shield.getyMax() >  enemygun.yLoc){
              enemygun.beenHit = true;
              defend = true;
            }
          }
        }
        if(defend){
          if(count3 == 0){
                denfendStartTime = millis();
                count3++;
            }
            if ((millis() - denfendStartTime )> explosionDuration) {
                defend = false;
                count3 =0;
            } else {
                Defend defend = new Defend(int (shield.getxMax()),int (shield.getyMax()));
                defend.show();
            }
        }
    }
    void bounceOff(){
      
      if (x < 10 || x > 1390  || y < 10 || y > 990 ) {
            direction += PI/3;
        }
        if(enemy1.isUsing){
            float distance = dist(enemy3.getX(), enemy3.getY(), enemy1.getX(), enemy1.getY());
            if (distance < 50) {
                direction += PI/3;
            }
        }
      if(enemy2.isUsing){
            float distance = dist(enemy3.getX(), enemy3.getY(), enemy2.getX(), enemy2.getY());
            if (distance < 50) {
                direction += PI/3;
            }
        }
      if(enemy4.isUsing){
            float distance = dist(enemy3.getX(), enemy3.getY(), enemy4.getX(), enemy4.getY());
            if (distance < 50) {
                direction += PI/3;
             }
        }
    }
    
}
class Enemy4 extends Ship {
    float direction;
    private float lastDirectionChangeTime = 0;
    private float directionChangeInterval = 5; 
    float speed = 3;
    int x = 2000;
    int y = 2000;
    boolean isExploding = false;
    boolean isExploded = false;
    int explosionStartTime = 0,denfendStartTime=0;
    int explosionDuration = 1200;
    int count = 0;
    int count1 = 0;
    int count2 = 0;
    int count3 = 0;
    int startTime ;
    int duration =900;
    boolean startTimeInitialized = false,defend = false;
    Gunenemy enemyGun = new Gunenemy(3000);
    void draw() {
        if(count1 == 0 && hitcount == 1){
            if (!startTimeInitialized) {
                startTime = millis();
                startTimeInitialized = true;
                count++;
            }
            if(count == 1){
                if(millis() - startTime < duration){
                    Damage damage = new Damage(enemy4.getX()-70,enemy4.getY()-70);
                    damage.show();
                }
            }
        }
        if (isExploding) {
            if(count2 == 0){
                explosionStartTime = millis();
                count2++;
            }
            if ((millis() - explosionStartTime )> explosionDuration) {
                isExploding = false;
            } else {
                Explosion explosion = new Explosion(enemy4.getX(),enemy4.getY());
                Damage damage = new Damage(enemy4.getX()-100,enemy4.getY()-50);
                damage.show();
                explosion.showExplosion();
            }
        } else {
            if(!isExploded){
                ShipSprite enemy4 = new Enemy4Sprite(800,100);
                enemy4.show(transx,transy,rotation);
                x = enemy4.xCoor+transx;
                y = enemy4.yCoor+transy;
            }
        }
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    void auto(){
      enemyGun.updateProjectiles();
        int currentTime1 = millis();
        if(jud){
        lastFired = currentTime1;
        jud =false;
      }
      float currentTime = millis() / 1000.0; 
      if (currentTime - lastDirectionChangeTime >= directionChangeInterval) {
            direction = random(0, TWO_PI); 
            lastDirectionChangeTime = currentTime;
        }
        bounceOff();

        if(!isExploded){
          enemy4.rotation = atan2(player.getY()-enemy4.getY(), player.getX()-enemy4.getX());
          transx = transx+round(speed * cos(direction));
          transy = transy+round(speed * sin(direction));
          if (currentTime1-lastFired > 3000){
            enemyGun.fire(transx+800, transy+100 , rotation);
            lastFired =currentTime1;
          }
        }
         
        thePlayerIsHit();
    }
    void thePlayerIsHit(){
        Shield shield = new Shield();
        shield.show(player.getTransx() + player.getShieldTurretTransx(), player.getTransy()+player.getshieldTurretTransy(), player.getRotation());
        for (EnemyGun enemygun : enemyGun.enemyguns) {
          if(!enemygun.beenHit){
            if (dist(enemygun.xLoc, enemygun.yLoc, player.getX(), player.getY()) < 40) {
              
              player.hitcount++;
              enemygun.beenHit = true;
              if(player.hitcount == 2) player.isExploded = true;
               if(player.isExploded){
                  Explosion explosion = new Explosion(player.getX(),player.getY());
                  explosion.showExplosion();
              }
              break;
            }
            if(enemygun.xLoc<shield.getxMax() && enemygun.xLoc>shield.getxMin() && enemygun.yLoc > shield.getyMin()&& shield.getyMax()>  enemygun.yLoc){
              enemygun.beenHit = true;
              defend = true;
            }
          }
        }
        if(defend){
          if(count3 == 0){
                denfendStartTime = millis();
                count3++;
            }
            if ((millis() - denfendStartTime )> explosionDuration) {
                defend = false;
                count3 =0;
            } else {
                Defend defend = new Defend(int (shield.getxMax()),int (shield.getyMax()));
                defend.show();
            }
        }
    }
    void bounceOff(){
      
      if (x < 10 || x > 1390  || y < 10 || y > 990 ) {
            direction += PI/2;
        }
        if(enemy1.isUsing){
            float distance = dist(enemy4.getX(), enemy4.getY(), enemy1.getX(), enemy1.getY());
            if (distance <= 50) {
                direction += PI/2;
            }
        }
      if(enemy2.isUsing){
            float distance = dist(enemy4.getX(), enemy4.getY(), enemy2.getX(), enemy2.getY());
            if (distance <= 50) {
                direction += PI/2;
            }
        }
      if(enemy3.isUsing){
            float distance = dist(enemy4.getX(), enemy4.getY(), enemy3.getX(), enemy3.getY());
            if (distance <=50) {
                direction += PI/2;
             }
        }
    }
    
}


// new players
Player player = new Player(weaponOption,  weaponOptionTurret);
Enemy1 enemy1 = new Enemy1();
Enemy2 enemy2 = new Enemy2();
Enemy3 enemy3 = new Enemy3();
Enemy4 enemy4 = new Enemy4();

void keyPressed() {
    if (keyCode == UP) {player.movementKeys[0] = true;}
    if (keyCode == DOWN) {player.movementKeys[1] = true;}
    if (key == 'a') {player.movementKeys[2] = true;}
    if (key == 'd') {player.movementKeys[3] = true;}
    if (key == ' ') {player.secondTurretFiring = true;}
    if (key == '=') {add = true;}
    
}
void keyReleased() {
    if (keyCode == UP) {player.movementKeys[0] = false;}
    if (keyCode == DOWN) {player.movementKeys[1] = false;}
    if (key == 'a') {player.movementKeys[2] = false;}
    if (key == 'd') {player.movementKeys[3] = false;}
    if (key == ' ') {player.secondTurretFiring = false;}
    if (key == '=') {add = false;}
}

void fireCheck() {
 if (mousePressed) { 
    if (mouseButton == LEFT) { player.weaponFiring[0] = true; }
 }
}

void mouseReleased() {
  if (mouseButton == LEFT) { player.weaponFiring[0] = false; }
}
