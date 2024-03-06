class StartScreen {
  final PImage backgroundImage = loadImage("../../resources/StartPage/StartPage.png"); 
  public StartScreen(int width, int height) {
   backgroundImage.resize(width,height);
  }
  
  public void show(int difficulty) {
    background(backgroundImage);
    //fill(150);
    textSize(38);
    textAlign(CENTER, CENTER);
    fill(220,211,213);
    text(": " + difficulty, width / 2 + 83, height / 2 + 183);
    
  }
  
}
