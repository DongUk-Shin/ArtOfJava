// Find the remaining balance on a loan. 
import java.awt.*;  
import java.awt.event.*;  
import java.applet.*;  
import java.text.*;  
/*  
  <applet code="RemBal" width=340 height=260>  
  </applet>  
*/  
  
public class RemBal extends Applet  
  implements ActionListener {  
  
  TextField orgPText, paymentText, remBalText, 
            rateText, numPayText; 
  Button doIt;  
  
  double orgPrincipal; // original princial 
  double intRate;      // interest rate 
  double payment;      // amount of each payment 
  double numPayments;  // number of payments made 
 
  /* Number of payments per year.  You could 
     allow this value to be set by the user. */ 
  final int payPerYear = 12; 
 
  NumberFormat nf; 
 
  public void init() {  
    // Use a grid bag layout. 
    GridBagLayout gbag = new GridBagLayout(); 
    GridBagConstraints gbc = new GridBagConstraints(); 
    setLayout(gbag); 
 
    Label heading = new  
          Label("Find Loan Balance "); 
  
    Label orgPLab = new Label("Original Principal"); 
    Label paymentLab = new Label("Amount of Payment"); 
    Label numPayLab = new Label("Number of Payments Made"); 
    Label rateLab = new Label("Interest Rate"); 
    Label remBalLab = new Label("Remaining Balance"); 
 
    orgPText = new TextField(16);  
    paymentText = new TextField(16);  
    remBalText = new TextField(16); 
    rateText = new TextField(16); 
    numPayText = new TextField(16); 
 
    // Payment field for display only. 
    remBalText.setEditable(false); 
 
    doIt = new Button("Compute");  
 
    // Define the grid bag. 
 
    gbc.weighty = 1.0; // use a row weight of 1 
 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbc.anchor = GridBagConstraints.NORTH; 
    gbag.setConstraints(heading, gbc); 
 
    // Anchor most components to the right. 
    gbc.anchor = GridBagConstraints.EAST; 
 
    gbc.gridwidth = GridBagConstraints.RELATIVE; 
    gbag.setConstraints(orgPLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(orgPText, gbc); 
 
    gbc.gridwidth = GridBagConstraints.RELATIVE; 
    gbag.setConstraints(paymentLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(paymentText, gbc); 
 
    gbc.gridwidth = GridBagConstraints.RELATIVE; 
    gbag.setConstraints(rateLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(rateText, gbc); 
 
    gbc.gridwidth = GridBagConstraints.RELATIVE; 
    gbag.setConstraints(numPayLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(numPayText, gbc); 
 
    gbc.gridwidth = GridBagConstraints.RELATIVE; 
    gbag.setConstraints(remBalLab, gbc); 
    gbc.gridwidth = GridBagConstraints.REMAINDER; 
    gbag.setConstraints(remBalText, gbc); 
 
    gbc.anchor = GridBagConstraints.CENTER; 
    gbag.setConstraints(doIt, gbc); 
 
    // Add all the components.  
    add(heading);  
    add(orgPLab);  
    add(orgPText);  
    add(paymentLab);  
    add(paymentText); 
    add(numPayLab);  
    add(numPayText); 
    add(rateLab); 
    add(rateText); 
    add(remBalLab);  
    add(remBalText);  
    add(doIt);  
  
    // Register to receive action events. 
    orgPText.addActionListener(this);  
    numPayText.addActionListener(this);  
    rateText.addActionListener(this);  
    paymentText.addActionListener(this);  
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
 
    String orgPStr = orgPText.getText();  
    String numPayStr = numPayText.getText();  
    String rateStr = rateText.getText(); 
    String payStr = paymentText.getText(); 
 
    try {  
      if(orgPStr.length() != 0 && 
         numPayStr.length() != 0 && 
         rateStr.length() != 0 && 
         payStr.length() != 0) { 
 
        orgPrincipal = Double.parseDouble(orgPStr); 
        numPayments = Double.parseDouble(numPayStr);      
        intRate = Double.parseDouble(rateStr) / 100; 
        payment = Double.parseDouble(payStr); 
 
        result = compute();   
  
        remBalText.setText(nf.format(result));  
      } 
  
      showStatus(""); // erase any previous error message  
    } catch (NumberFormatException exc) {  
      showStatus("Invalid Data"); 
      remBalText.setText("");  
    }  
  } 
 
  // Compute the loan balance. 
  double compute() { 
    double bal = orgPrincipal; 
    double rate = intRate / payPerYear; 
 
    for(int i = 0; i < numPayments; i++) 
      bal -= payment - (bal * rate); 
 
    return bal; 
  }    
}

