/* Compute the initial investment necessary for 
   a desired annuity. In other words, it finds 
   the initial amount needed to allow the regular 
   withdrawals of a desired amount over a period 
   of time.  */ 
import java.awt.*;  
import java.awt.event.*;  
import java.applet.*;  
import java.text.*;  
/*  
  <applet code="Annuity" width=340 height=260>  
  </applet>  
*/  
  
public class Annuity extends Applet  
  implements ActionListener {  
  
  TextField regWDText, initialText, periodText, 
            rateText, numWDText; 
  Button doIt;  
  
  double regWDAmount; // amount of each withdrawal 
  double rateOfRet;   // rate of return 
  double numYears;    // length of time in years 
  int numPerYear;     // number of withdrawals per year 
 
  NumberFormat nf; 
 
  public void init() {  
    // Use a grid bag layout. 
    GridBagLayout gbag = new GridBagLayout(); 
    GridBagConstraints gbc = new GridBagConstraints(); 
    setLayout(gbag); 
 
    Label heading = new  
          Label("Initial Investment Needed for " + 
                "Regular Withdrawals");  
  
    Label regWDLab = new Label("Desired Withdrawal"); 
    Label periodLab = new Label("Years"); 
    Label rateLab = new Label("Rate of Return"); 
    Label numWDLab = 
            new Label("Number of Withdrawals per Year "); 
    Label initialLab = 
            new Label("Initial Investment Required"); 
 
    regWDText = new TextField(16);  
    periodText = new TextField(16);  
    initialText = new TextField(16);  
    rateText = new TextField(16); 
    numWDText = new TextField(16); 
 
    // Initial invesetment field for display only. 
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
    gbag.setConstraints(regWDLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(regWDText, gbc); 
 
    gbc.gridwidth = GridBagConstraints.RELATIVE; 
    gbag.setConstraints(periodLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(periodText, gbc); 
 
    gbc.gridwidth = GridBagConstraints.RELATIVE; 
    gbag.setConstraints(rateLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(rateText, gbc); 
 
    gbc.gridwidth = GridBagConstraints.RELATIVE; 
    gbag.setConstraints(numWDLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(numWDText, gbc); 
 
    gbc.gridwidth = GridBagConstraints.RELATIVE; 
    gbag.setConstraints(initialLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(initialText, gbc); 
 
    gbc.anchor = GridBagConstraints.CENTER; 
    gbag.setConstraints(doIt, gbc); 
 
    // Add all the components. 
    add(heading);  
    add(regWDLab);  
    add(regWDText);  
    add(periodLab);  
    add(periodText); 
    add(rateLab); 
    add(rateText); 
    add(numWDLab);  
    add(numWDText);  
    add(initialLab);  
    add(initialText);  
    add(doIt);  
  
    // Register to receive text field action events. 
    regWDText.addActionListener(this);  
    periodText.addActionListener(this);  
    rateText.addActionListener(this);  
    numWDText.addActionListener(this);  
    doIt.addActionListener(this);      
 
    nf = NumberFormat.getInstance(); 
    nf.setMinimumFractionDigits(2); 
    nf.setMaximumFractionDigits(2); 
  }  
  
  // User pressed Enter on a text field.  
  public void actionPerformed(ActionEvent ae) {  
    repaint();  
  }  
  
  public void paint(Graphics g) {  
    double result = 0.0; 
 
    String regWDStr = regWDText.getText();  
    String periodStr = periodText.getText();  
    String rateStr = rateText.getText(); 
    String numWDStr = numWDText.getText(); 
 
    try {  
      if(regWDStr.length() != 0 && 
         periodStr.length() != 0 && 
         rateStr.length() != 0 && 
         numWDStr.length() != 0) { 
 
        regWDAmount = Double.parseDouble(regWDStr); 
        numYears = Double.parseDouble(periodStr);      
        rateOfRet = Double.parseDouble(rateStr) / 100; 
        numPerYear = Integer.parseInt(numWDStr); 
 
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
    double t1, t2; 
 
    t1 = (regWDAmount * numPerYear) / rateOfRet; 
 
    b = (1 + rateOfRet/numPerYear); 
    e = numPerYear * numYears; 
 
    t2 = 1 - (1 / Math.pow(b, e)); 
     
    return t1 * t2; 
  }    
}

