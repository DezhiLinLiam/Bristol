class Map {
  final static int totalBackgrounds = 3;
  private PImage[] background = new PImage [totalBackgrounds];
  int backindex = int(random(totalBackgrounds));
  
  public Map(){
    for (int i = 0; i < totalBackgrounds; i++){
      background[i] = loadImage("../../resources/backgrounds/back"+i+".jpeg");
      background[i].resize(width,height);
    }
  }
  
  public PImage getBackground(){
    return background[backindex];
  }

  public PImage getBackground(int i) {
   return background[i]; 
  }

}
