import java.awt.event.*; 
import java.awt.*; 
 
// Display an array of numeric data. 
class DataWin extends Frame { 
  TextArea dataTA; 
 
  DataWin(double[] data) { 
    addWindowListener(new WindowAdapter() { 
      public void windowClosing(WindowEvent we) { 
        setVisible(false); 
        dispose(); 
      } 
    }); 
 
    dataTA = new TextArea(10, 10); 
    dataTA.setEditable(false); 
 
    for(int i=0; i < data.length; i++) 
      dataTA.append(data[i]+"\n"); 
 
    setSize(new Dimension(100, 140)); 
    setLocation(320, 100); 
 
    setTitle("Data"); 
    setResizable(false); 
 
    add(dataTA); 
     
    setVisible(true); 
  } 
}

