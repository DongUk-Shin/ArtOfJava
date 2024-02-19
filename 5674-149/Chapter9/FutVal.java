// Compute the future value of an investment. 
import java.awt.*;  
import java.awt.event.*;  
import java.applet.*;  
import java.text.*;  
/*  
  <applet code="FutVal" width=340 height=240>  
  </applet>  
*/  
  
public class FutVal extends Applet  
  implements ActionListener {  
  
  TextField amountText, futvalText, periodText, 
            rateText, compText; 
  Button doIt;  
  
  double principal; // original principal 
  double rateOfRet; // rate of return 
  double numYears;  // length of investment in years 
  int compPerYear;  // number of compoudings per year 
 
  NumberFormat nf; 
 
  public void init() {  
    // Use a grid bag layout. 
    GridBagLayout gbag = new GridBagLayout(); 
    GridBagConstraints gbc = new GridBagConstraints(); 
    setLayout(gbag); 
 
    Label heading = new  
          Label("Future Value of an Investment"); 
  
    Label amountLab = new Label("Principal");  
    Label periodLab = new Label("Years");  
    Label rateLab = new Label("Rate of Return");  
    Label futvalLab = 
            new Label("Future Value of Investment"); 
    Label compLab = 
            new Label("Compounding Periods per Year "); 
 
    amountText = new TextField(16);  
    periodText = new TextField(16);  
    futvalText = new TextField(16);  
    rateText = new TextField(16); 
    compText = new TextField(16); 
 
    // Future value field for display only. 
    futvalText.setEditable(false); 
 
    doIt = new Button("Compute");  
 
    // Define the grid bag. 
    gbc.weighty = 1.0; // use a row weight of 1 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbc.anchor = GridBagConstraints.NORTH; 
    gbag.setConstraints(heading, gbc); 
 
    // Anchor most components to the right. 
    gbc.anchor = GridBagConstraints.EAST; 
 
    gbc.gridwidth = GridBagConstraints.RELATIVE; 
    gbag.setConstraints(amountLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(amountText, gbc); 
 
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
    gbag.setConstraints(futvalLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(futvalText, gbc); 
 
    gbc.anchor = GridBagConstraints.CENTER; 
    gbag.setConstraints(doIt, gbc); 
  
    add(heading);  
    add(amountLab);  
    add(amountText);  
    add(periodLab);  
    add(periodText); 
    add(rateLab); 
    add(rateText); 
    add(compLab);  
    add(compText);  
    add(futvalLab);  
    add(futvalText);  
    add(doIt);  
  
    // Register to receive action events. 
    amountText.addActionListener(this);  
    periodText.addActionListener(this);  
    rateText.addActionListener(this);  
    compText.addActionListener(this);  
    doIt.addActionListener(this);      
 
    nf = NumberFormat.getInstance(); 
    nf.setMinimumFractionDigits(2); 
    nf.setMaximumFractionDigits(2); 
  }  
  
  /* User pressed Enter on a text field or 
     pressed Compute. */ 
  public void actionPerformed(ActionEvent ae) {  
    repaint();  
  }  
  
  public void paint(Graphics g) {  
    double result = 0.0; 
 
    String amountStr = amountText.getText();  
    String periodStr = periodText.getText();  
    String rateStr = rateText.getText(); 
    String compStr = compText.getText(); 
 
    try {  
      if(amountStr.length() != 0 && 
         periodStr.length() != 0 && 
         rateStr.length() != 0 && 
         compStr.length() != 0) { 
 
        principal = Double.parseDouble(amountStr); 
        numYears = Double.parseDouble(periodStr);      
        rateOfRet = Double.parseDouble(rateStr) / 100; 
        compPerYear = Integer.parseInt(compStr); 
 
        result = compute();   
  
        futvalText.setText(nf.format(result));  
      } 
  
      showStatus(""); // erase any previous error message  
    } catch (NumberFormatException exc) {  
      showStatus("Invalid Data"); 
      futvalText.setText("");  
    }  
  } 
 
  // Compute the future value. 
  double compute() { 
    double b, e; 
 
    b = (1 + rateOfRet/compPerYear); 
    e = compPerYear * numYears; 
 
    return principal * Math.pow(b, e);   
  }    
}

