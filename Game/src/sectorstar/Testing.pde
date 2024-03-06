class Testing
{ 
  public void runTest()
  {
    //Firing Weapon
    GunPlayer gunPlayer = new GunPlayer(0,1);
    gunPlayer.fire(50,50,PI);
    assert(gunPlayer.projectiles.size() == 1);
    assert(gunPlayer.projectiles.get(0).directionFired == PI);
    assert(gunPlayer.projectiles.get(0).xLoc == 50);
    assert(gunPlayer.projectiles.get(0).yLoc == 50);
    assert(!gunPlayer.projectiles.get(0).beenHit);
    
    Player player;
    //Loadout
    player = new Player(0,0);
    assert(player.weaponOption == 0);
    assert(player.weaponOptionTurret == 0);
    assert(player.fireRate == 1000);
    assert(player.fireRateTurret == 1000);
    assert(player.gunPlayer.fireRate == 1000);
    assert(player.secondTurretGun.fireRate == 1000);
    assert(player.gunPlayer.projectileType == 1);
    assert(player.secondTurretGun.projectileType == 1);
    
    player = new Player(1,0);
    assert(player.weaponOption == 1);
    assert(player.weaponOptionTurret == 0);
    assert(player.fireRate == 1000);
    assert(player.fireRateTurret == 1000);
    assert(player.gunPlayer.fireRate == 1000);
    assert(player.secondTurretGun.fireRate == 1000);
    assert(player.gunPlayer.projectileType == 1);
    assert(player.secondTurretGun.projectileType == 1);
    
    player = new Player(2,0);
    assert(player.weaponOption == 2);
    assert(player.weaponOptionTurret == 0);
    assert(player.fireRate == 500);
    assert(player.fireRateTurret == 1000);
    assert(player.gunPlayer.fireRate == 500);
    assert(player.secondTurretGun.fireRate == 1000);
    assert(player.gunPlayer.projectileType == 2);
    assert(player.secondTurretGun.projectileType == 1);
    
    player = new Player(3,0);
    assert(player.weaponOption == 3);
    assert(player.weaponOptionTurret == 0);
    assert(player.fireRate == 2000);
    assert(player.fireRateTurret == 1000);
    assert(player.gunPlayer.fireRate == 2000);
    assert(player.secondTurretGun.fireRate == 1000);
    assert(player.gunPlayer.projectileType == 3);
    assert(player.secondTurretGun.projectileType == 1);
    
    player = new Player(0,1);
    assert(player.weaponOption == 0);
    assert(player.weaponOptionTurret == 1);
    assert(player.fireRate == 1000);
    assert(player.fireRateTurret == 1000);
    assert(player.gunPlayer.fireRate == 1000);
    assert(player.secondTurretGun.fireRate == 1000);
    assert(player.gunPlayer.projectileType == 1);
    assert(player.secondTurretGun.projectileType == 1);
    
    player = new Player(0,2);
    assert(player.weaponOption == 0);
    assert(player.weaponOptionTurret == 2);
    assert(player.fireRate == 1000);
    assert(player.fireRateTurret == 500);
    assert(player.gunPlayer.fireRate == 1000);
    assert(player.secondTurretGun.fireRate == 500);
    assert(player.gunPlayer.projectileType == 1);
    assert(player.secondTurretGun.projectileType == 2);
    
    player = new Player(0,3);
    assert(player.weaponOption == 0);
    assert(player.weaponOptionTurret == 3);
    assert(player.fireRate == 1000);
    assert(player.fireRateTurret == 2000);
    assert(player.gunPlayer.fireRate == 1000);
    assert(player.secondTurretGun.fireRate == 2000);
    assert(player.gunPlayer.projectileType == 1);
    assert(player.secondTurretGun.projectileType == 3);
    
    player = new Player(1,1);
    assert(player.weaponOption == 1);
    assert(player.weaponOptionTurret == 1);
    assert(player.fireRate == 1000);
    assert(player.fireRateTurret == 1000);
    assert(player.gunPlayer.fireRate == 1000);
    assert(player.secondTurretGun.fireRate == 1000);
    assert(player.gunPlayer.projectileType == 1);
    assert(player.secondTurretGun.projectileType == 1);
    
    player = new Player(1,2);
    assert(player.weaponOption == 1);
    assert(player.weaponOptionTurret == 2);
    assert(player.fireRate == 1000);
    assert(player.fireRateTurret == 500);
    assert(player.gunPlayer.fireRate == 1000);
    assert(player.secondTurretGun.fireRate == 500);
    assert(player.gunPlayer.projectileType == 1);
    assert(player.secondTurretGun.projectileType == 2);
    
    player = new Player(1,3);
    assert(player.weaponOption == 1);
    assert(player.weaponOptionTurret == 3);
    assert(player.fireRate == 1000);
    assert(player.fireRateTurret == 2000);
    assert(player.gunPlayer.fireRate == 1000);
    assert(player.secondTurretGun.fireRate == 2000);
    assert(player.gunPlayer.projectileType == 1);
    assert(player.secondTurretGun.projectileType == 3);
    
    player = new Player(2,1);
    assert(player.weaponOption == 2);
    assert(player.weaponOptionTurret == 1);
    assert(player.fireRate == 500);
    assert(player.fireRateTurret == 1000);
    assert(player.gunPlayer.fireRate == 500);
    assert(player.secondTurretGun.fireRate == 1000);
    assert(player.gunPlayer.projectileType == 2);
    assert(player.secondTurretGun.projectileType == 1);
        
    player = new Player(2,2);
    assert(player.weaponOption == 2);
    assert(player.weaponOptionTurret == 2);
    assert(player.fireRate == 500);
    assert(player.fireRateTurret == 500);
    assert(player.gunPlayer.fireRate == 500);
    assert(player.secondTurretGun.fireRate == 500);
    assert(player.gunPlayer.projectileType == 2);
    assert(player.secondTurretGun.projectileType == 2);
    
    player = new Player(2,3);
    assert(player.weaponOption == 2);
    assert(player.weaponOptionTurret == 3);
    assert(player.fireRate == 500);
    assert(player.fireRateTurret == 2000);
    assert(player.gunPlayer.fireRate == 500);
    assert(player.secondTurretGun.fireRate == 2000);
    assert(player.gunPlayer.projectileType == 2);
    assert(player.secondTurretGun.projectileType == 3);
    
    player = new Player(3,1);
    assert(player.weaponOption == 3);
    assert(player.weaponOptionTurret == 1);
    assert(player.fireRate == 2000);
    assert(player.fireRateTurret == 1000);
    assert(player.gunPlayer.fireRate == 2000);
    assert(player.secondTurretGun.fireRate == 1000);
    assert(player.gunPlayer.projectileType == 3);
    assert(player.secondTurretGun.projectileType == 1);
    
    player = new Player(3,2);
    assert(player.weaponOption == 3);
    assert(player.weaponOptionTurret == 2);
    assert(player.fireRate == 2000);
    assert(player.fireRateTurret == 500);
    assert(player.gunPlayer.fireRate == 2000);
    assert(player.secondTurretGun.fireRate == 500);
    assert(player.gunPlayer.projectileType == 3);
    assert(player.secondTurretGun.projectileType == 2);
    
    player = new Player(3,3);
    assert(player.weaponOption == 3);
    assert(player.weaponOptionTurret == 3);
    assert(player.fireRate == 2000);
    assert(player.fireRateTurret == 2000);
    assert(player.gunPlayer.fireRate == 2000);
    assert(player.secondTurretGun.fireRate == 2000);
    assert(player.gunPlayer.projectileType == 3);
    assert(player.secondTurretGun.projectileType == 3);
 
    //Multiplayer
    player = new Player(1,1);
    player.gunPlayer.fireRate = 0;
    player.secondTurretGun.fireRate = 0;
    player.gunPlayer.fire(50,50,PI);
    player.secondTurretGun.fire(60,60,PI/2);
    assert(player.gunPlayer.projectiles.size() == 1);
    assert(player.secondTurretGun.projectiles.size() == 1);
    assert(player.gunPlayer.projectiles.get(0).directionFired == PI);
    assert(player.secondTurretGun.projectiles.get(0).directionFired == PI/2);
    
    //Leaderboard
    
  }
}
