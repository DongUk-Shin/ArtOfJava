// Demonstrate the parser.  
import java.io.*; 
  
class PDemo1 {  
  public static void main(String args[])  throws IOException {  
      String expr; 
   
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in)); 
      Parser2 p = new Parser2();  
      p.init();
      System.out.println("Enter an empty expression to stop.");  
    
      for(;;) {  
          System.out.print("Enter expression: ");  
          expr = br.readLine();  
          
          if(expr.equals("")) //입력이 없으면 break
              break;  
          try { 
              System.out.println("Result: " + p.evaluate(expr));  //evaluate 메서드 호출
              System.out.println(); 
          } catch (ParserException2 exc) { 
              System.out.println(exc);
          } 
      }  
  }  
}

