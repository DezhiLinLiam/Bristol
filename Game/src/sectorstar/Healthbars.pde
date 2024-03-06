 class Healthbars{
  private PImage healthbar000;
  private PImage healthbar025;
  private PImage healthbar050;
  private PImage healthbar075;
  private PImage healthbar100;
   
  public Healthbars() {
    healthbar000 = loadImage("../../resources/healthbars/healthbar000.png");
    healthbar025 = loadImage("../../resources/healthbars/healthbar025.png");
    healthbar050 = loadImage("../../resources/healthbars/healthbar050.png");
    healthbar075 = loadImage("../../resources/healthbars/healthbar075.png");
    healthbar100 = loadImage("../../resources/healthbars/healthbar100.png");
  }
  
  void draw000(){
    image(healthbar000, 150, 900); 
  }
  void draw025(){
    image(healthbar025, 150, 900); 
  }
  void draw050(){
    image(healthbar050, 150, 900); 
  }
  void draw075(){
    image(healthbar075, 150, 900); 
  }
  void draw100(){
    image(healthbar100, 150, 900); 
  }
   
}
