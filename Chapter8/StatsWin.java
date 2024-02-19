import java.awt.*; 
import java.awt.event.*; 
import java.util.*; 
import java.text.*; 
 
// Process and display statistical data. 
public class StatsWin extends Frame 
    implements ItemListener, ActionListener  { 
 
  NumberFormat nf = NumberFormat.getInstance(); 
 
  TextArea statsTA; 
  Checkbox bar = new Checkbox("Bar Graph"); 
  Checkbox scatter = new Checkbox("Scatter Graph"); 
  Checkbox regplot = new Checkbox("Regression Line Plot"); 
  Checkbox datawin = new Checkbox("Show Data"); 
 
  double[] data; 
 
  Graphs bg; 
  Graphs sg; 
  Graphs rp; 
  DataWin da; 
 
  RegData rd; 
 
  public StatsWin(double vals[]) { 
    data = vals; // save reference to data 
 
    addWindowListener(new WindowAdapter() { 
      public void windowClosing(WindowEvent we) { 
        shutdown(); 
      } 
    }); 
 
    // Create the File menu. 
    createMenu(); 
 
    // Change to flow layout, centering components. 
    setLayout(new FlowLayout(FlowLayout.CENTER)); 
 
    setSize(new Dimension(300, 240)); 
    setTitle("Statistical Data"); 
    
    rd = Stats.regress(data); 
 
    // Set the number format to 2 decimal digits. 
    nf.setMaximumFractionDigits(2); 
 
 
    // Construct output. 
    String mstr; 
    try { 
      // Obtain mode, if there is one. 
      mstr = nf.format(Stats.mode(data)); 
    } catch(NoModeException exc) { 
      mstr = exc.toString(); 
    } 
 
    String str = "Mean: " + 
                  nf.format(Stats.mean(data)) + "\n" + 
                 "Median: " + 
                  nf.format(Stats.median(data)) + "\n" + 
                 "Mode: " + mstr + "\n" + 
                 "Standard Deviation: " + 
                  nf.format(Stats.stdDev(data)) + "\n\n" + 
                 "Regression equation: " + rd.equation + 
                 "\nCorrelation coefficient: " + 
                  nf.format(rd.cor); 
 
    // Put output in text area. 
    statsTA = new TextArea(str, 6, 38, TextArea.SCROLLBARS_NONE); 
    statsTA.setEditable(false); 
 
    // Add components to window. 
    add(statsTA);     
    add(bar); 
    add(scatter); 
    add(regplot); 
    add(datawin); 
 
    // Add component listeners. 
    bar.addItemListener(this); 
    scatter.addItemListener(this); 
    regplot.addItemListener(this); 
    datawin.addItemListener(this); 
 
    setVisible(true); 
  } 
 
  // Handle the Close menu option. 
  public void actionPerformed(ActionEvent ae) { 
    String arg = (String)ae.getActionCommand(); 
 
    if(arg == "Close") { 
      shutdown(); 
    } 
  } 
 
  // User changed a checkbox. 
  public void itemStateChanged(ItemEvent ie) { 
    if(bar.getState()) { 
      if(bg == null) { 
        bg = new Graphs(data, Graphs.BAR); 
        bg.addWindowListener(new WindowAdapter() { 
          public void windowClosing(WindowEvent we) { 
            bar.setState(false); 
            bg = null; 
          } 
        }); 
      } 
    } 
    else { 
      if(bg != null) { 
        bg.dispose(); 
        bg = null; 
      } 
    } 
 
    if(scatter.getState()) { 
      if(sg == null) { 
        sg = new Graphs(data, Graphs.SCATTER); 
        sg.addWindowListener(new WindowAdapter() { 
          public void windowClosing(WindowEvent we) { 
            scatter.setState(false); 
            sg = null; 
          } 
        }); 
      } 
    } 
    else { 
      if(sg != null) { 
        sg.dispose(); 
        sg = null; 
      } 
    } 
 
    if(regplot.getState()) { 
      if(rp == null) { 
        rp = new Graphs(data, Graphs.REGPLOT); 
        rp.addWindowListener(new WindowAdapter() { 
          public void windowClosing(WindowEvent we) { 
            regplot.setState(false); 
            rp = null; 
          } 
        }); 
      } 
    } 
    else { 
      if(rp != null) { 
        rp.dispose(); 
        rp = null; 
      } 
    }  
 
    if(datawin.getState()) { 
      if(da == null) { 
        da = new DataWin(data); 
        da.addWindowListener(new WindowAdapter() { 
          public void windowClosing(WindowEvent we) { 
            datawin.setState(false); 
            da = null; 
          } 
        }); 
      } 
    } 
    else { 
      if(da != null) { 
        da.dispose(); 
        da = null; 
      } 
    }  
  } 
 
  // Create the File menu. 
  private void createMenu() 
  { 
    MenuBar mbar = new MenuBar(); 
    setMenuBar(mbar); 
 
    Menu file = new Menu("File"); 
    MenuItem close = new MenuItem("Close"); 
    file.add(close); 
    mbar.add(file); 
    close.addActionListener(this); 
  } 
 
  // Shutdown the windows. 
  private void shutdown() { 
    if(bg != null) bg.dispose(); 
    if(sg != null) sg.dispose(); 
    if(rp != null) rp.dispose(); 
    if(da != null) da.dispose(); 
    setVisible(false); 
    dispose(); 
  } 
}

