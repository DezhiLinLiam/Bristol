class Leaderboard{
  int count = 0;
  
  void add(){
    count++;
  }
  int get(){
    return count;
  }
  void show(){
    textSize(50); 
    textAlign(LEFT, TOP); 
    text("Kill: " + count, 30, 30); 
  }
   
  void save(){
   String filepath = "../../resources/MaximumKills.txt";
   String[] data = {str(count)};
   String[] loadedData = loadStrings(filepath); 
   int loadedNumber = int(loadedData[0]);
   if(count > loadedNumber ){ 
   saveStrings( filepath, data);
  }
  }
  
  void read(){
    String filepath = "../../resources/MaximumKills.txt"; 

    String[] loadedData = loadStrings(filepath); 
    int loadedNumber = int(loadedData[0]); 
    textSize(40); 
    textAlign(LEFT, TOP); 
    text("High Score: " + loadedNumber, 30, 30); 
  }
}
