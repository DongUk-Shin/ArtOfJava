// An demonstration applet that uses Stats and Graphs. 
import java.awt.*;  
import java.awt.event.*;  
import java.applet.*;  
import java.util.*; 
/*  
  <applet code="StatApplet" width=120 height=50>  
  <param name=data value="1.2, 3.6, 5.7, 4.4, 7.1, 4.4, 
                          6.89, 8.9, 10.3, 9.45"> 
  </applet>  
*/  
  
public class StatApplet extends Applet implements ActionListener {  
  StatsWin sw; 
  Button show; 
 
  ArrayList al = new ArrayList(); 
 
  public void init() {  
    StringTokenizer st = new  
      StringTokenizer(getParameter("data"), ", \n\r"); 
 
    String v; 
 
    // Get the values from the HTML. 
    while(st.hasMoreTokens()) { 
      v = st.nextToken(); 
      al.add(v); 
    }  
 
    show = new Button("Display Statistics"); 
    add(show); 
 
    show.addActionListener(this);     
  }  
 
  public void actionPerformed(ActionEvent ae) { 
 
    if(sw == null) { 
      double nums[] = new double[al.size()]; 
      try { 
        for(int i=0; i<al.size(); i++) 
          nums[i] = Double.parseDouble((String)al.get(i)); 
      } catch(NumberFormatException exc) { 
        System.out.println("Error reading data."); 
        return; 
      } 
 
      sw = new StatsWin(nums); 
      show.setEnabled(false); 
 
      sw.addWindowListener(new WindowAdapter() { 
        public void windowClosing(WindowEvent we) { 
          sw = null; 
          show.setEnabled(true); 
        } 
      }); 
    } 
  } 
}

