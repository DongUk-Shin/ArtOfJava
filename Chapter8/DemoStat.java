// Demonstrate the Stats and Graphs. 
import java.io.*;  
import java.awt.*; 
   
class DemoStat {   
  public static void main(String args[])   
    throws IOException  
  {   
    double nums[] = { 10, 10, 11, 9, 8, 8, 9, 
                      10, 10, 13, 11, 11, 11, 
                      11, 12, 13, 14, 16, 17, 
                      15, 15, 16, 14, 16 }; 
 
    new StatsWin(nums); 
  }   
}

