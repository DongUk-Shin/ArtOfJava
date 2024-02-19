/* Compute the initial investment necessary for 
   a specified future value.  */ 
import java.awt.*;  
import java.awt.event.*;  
import java.applet.*;  
import java.text.*;  
/*  
  <applet code="InitInv" width=340 height=240>  
  </applet>  
*/  
  
public class InitInv extends Applet  
  implements ActionListener {  
  
  TextField targetText, initialText, periodText, 
            rateText, compText; 
  Button doIt;  
  
  double targetValue; // original targetValue 
  double rateOfRet;   // rate of return 
  double numYears;    // length of loan in years 
  int compPerYear;    // number of compoudings per year 
 
  NumberFormat nf; 
 
  public void init() {  
    // Use a grid bag layout. 
    GridBagLayout gbag = new GridBagLayout(); 
    GridBagConstraints gbc = new GridBagConstraints(); 
    setLayout(gbag); 
 
    Label heading = new  
          Label("Initial Investment Needed for " + 
                "Future Value"); 
  
    Label targetLab = new Label("Desired Future Value "); 
    Label periodLab = new Label("Years"); 
    Label rateLab = new Label("Rate of Return");  
    Label compLab = 
            new Label("Compounding Periods per Year"); 
    Label initialLab =  
            new  Label("Initial Investment Required"); 
 
    targetText = new TextField(16);  
    periodText = new TextField(16);  
    initialText = new TextField(16);  
    rateText = new TextField(16); 
    compText = new TextField(16); 
 
    // Initial value field for display only. 
    initialText.setEditable(false); 
 
    doIt = new Button("Compute");  
  
    // Define the grid bag. 
    gbc.weighty = 1.0; // use a row weight of 1 
 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbc.anchor = GridBagConstraints.NORTH; 
    gbag.setConstraints(heading, gbc); 
 
    // Anchor most components to the right. 
    gbc.anchor = GridBagConstraints.EAST; 
 
    gbc.gridwidth = GridBagConstraints.RELATIVE; 
    gbag.setConstraints(targetLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(targetText, gbc); 
 
    gbc.gridwidth = GridBagConstraints.RELATIVE; 
    gbag.setConstraints(periodLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(periodText, gbc); 
 
    gbc.gridwidth = GridBagConstraints.RELATIVE; 
    gbag.setConstraints(rateLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(rateText, gbc); 
 
    gbc.gridwidth = GridBagConstraints.RELATIVE; 
    gbag.setConstraints(compLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(compText, gbc); 
 
    gbc.gridwidth = GridBagConstraints.RELATIVE; 
    gbag.setConstraints(initialLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(initialText, gbc); 
 
    gbc.anchor = GridBagConstraints.CENTER; 
    gbag.setConstraints(doIt, gbc); 
 
    // Add all the components. 
    add(heading);  
    add(targetLab);  
    add(targetText);  
    add(periodLab);  
    add(periodText); 
    add(rateLab); 
    add(rateText); 
    add(compLab);  
    add(compText);  
    add(initialLab);  
    add(initialText);  
    add(doIt);  
  
    // Register to receive action events. 
    targetText.addActionListener(this);  
    periodText.addActionListener(this);  
    rateText.addActionListener(this);  
    compText.addActionListener(this);  
    doIt.addActionListener(this);      
 
    nf = NumberFormat.getInstance(); 
    nf.setMinimumFractionDigits(2); 
    nf.setMaximumFractionDigits(2); 
  }  
  
  /* User pressed Enter on a text field 
     or pressed Compute. */ 
  public void actionPerformed(ActionEvent ae) {  
    repaint();  
  }  
  
  public void paint(Graphics g) {  
    double result = 0.0; 
 
    String targetStr = targetText.getText();  
    String periodStr = periodText.getText();  
    String rateStr = rateText.getText(); 
    String compStr = compText.getText(); 
 
    try {  
      if(targetStr.length() != 0 && 
         periodStr.length() != 0 && 
         rateStr.length() != 0 && 
         compStr.length() != 0) { 
 
        targetValue = Double.parseDouble(targetStr); 
        numYears = Double.parseDouble(periodStr);      
        rateOfRet = Double.parseDouble(rateStr) / 100; 
        compPerYear = Integer.parseInt(compStr); 
 
        result = compute();   
  
        initialText.setText(nf.format(result));  
      } 
  
      showStatus(""); // erase any previous error message  
    } catch (NumberFormatException exc) {  
      showStatus("Invalid Data"); 
      initialText.setText("");  
    }  
  } 
 
  // Compute the required initial investment. 
  double compute() { 
    double b, e; 
 
    b = (1 + rateOfRet/compPerYear); 
    e = compPerYear * numYears; 
 
    return targetValue / Math.pow(b, e);   
  }    
  
}

