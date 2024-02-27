import java.awt.*; 
import java.awt.event.*; 
import java.applet.*; 
import java.util.*; 
 
// A general-purpose graph class. 
public class Graphs extends Frame { 
  // Constants for type of graph. 
  public final static int BAR = 0; 
  public final static int SCATTER = 1; 
  public final static int REGPLOT = 2; 
 
  private int graphStyle; 
 
  /* These specify the amount of space to  
     leave between data and borders. */ 
  private final int leftGap = 2; 
  private final int topGap = 2; 
  private final int bottomGap = 2; 
  private int rightGap; // this value is computed  
 
  // These hold the min and max values of the data. 
  private double min, max; 
 
  // Refers to the data. 
  private double[] data;  
 
  // Colors used by the graph. 
  Color gridColor = new Color(0, 150, 150); 
  Color dataColor = new Color(0, 0, 0); 
  
  // Various values used to scale and display data. 
  private int hGap;   // space between data points 
  private int spread; // distance between min and max data 
  private double scale; // scaling factor 
  private int baseline; // vertical coordinate of baseline 
 
  // Location of data area within the window. 
  private int top, bottom, left, right; 
 
  public Graphs(double[] vals, int style) { 
    // Handle window-closing events. 
    addWindowListener(new WindowAdapter() { 
      public void windowClosing(WindowEvent we) { 
        setVisible(false); 
        dispose(); 
      } 
    }); 
 
    // Handle resize events. 
    addComponentListener(new ComponentAdapter() { 
      public void componentResized(ComponentEvent ce) { 
        repaint(); 
      } 
    }); 
 
    graphStyle = style; 
 
    data = vals; 
 
    // Sort the data to find min and max values. 
    double t[] = new double[vals.length]; 
    System.arraycopy(vals, 0, t, 0, vals.length); 
    Arrays.sort(t); 
    min = t[0]; 
    max = t[t.length-1]; 
 
    setSize(new Dimension(200, 120)); 
 
    switch(graphStyle) { 
      case BAR: 
        setTitle("Bar Graph"); 
        setLocation(25, 250); 
        break; 
      case SCATTER: 
        setTitle("Scatter Graph"); 
        setLocation(250, 250); 
        break; 
      case REGPLOT: 
        setTitle("Regression Plot"); 
        setLocation(475, 250); 
        break; 
    } 
 
    setVisible(true); 
  } 
 
  public void paint(Graphics g) { 
 
    Dimension winSize = getSize(); // size of window 
    Insets ins = getInsets(); // size of borders 
 
    // Get the size of the curently selected font. 
    FontMetrics fm = g.getFontMetrics(); 
 
    // Compute right gap. 
    rightGap = fm.stringWidth("" + data.length); 
 
    // Compute the total insets for the data region. 
    left = ins.left + leftGap + fm.charWidth('0'); 
    top = ins.top + topGap + fm.getAscent(); 
    bottom = ins.bottom + bottomGap + fm.getAscent(); 
    right = ins.right + rightGap; 
 
    /* If minimum value positive, then use 0 
       as the starting point for the graph. 
       if maximum value is negative, use 0. */ 
    if(min > 0) min = 0; 
    if(max < 0) max = 0; 
 
    /* Compute the distance between the minimum  
       and maximum values. */ 
    spread = (int) (max - min); 
 
    // Compute the scaling factor. 
    scale = (double) (winSize.height - bottom - top) / spread; 
 
    // Find where the baseline goes. 
    baseline = (int) (winSize.height - bottom + min * scale);  
 
    // Compute the spacing between data. 
    hGap = (winSize.width - left - right) / (data.length-1); 
 
    // Set the grid color. 
    g.setColor(gridColor); 
 
    // Draw the baseline. 
    g.drawLine(left, baseline, 
               left + (data.length-1) * hGap, baseline); 
 
    // Draw the Y axis. 
    if(graphStyle != BAR) 
      g.drawLine(left, winSize.height-bottom, left, top); 
 
    // Display the min, max, and 0 values. 
    g.drawString("0", ins.left, baseline+fm.getAscent()/2); 
 
    if(max != 0) 
      g.drawString("" + max, ins.left, baseline - 
                   (int) (max*scale) - 4); 
    if(min != 0) 
      g.drawString("" + min, ins.left, baseline - 
                   (int) (min*scale)+fm.getAscent()); 
 
    // Display number of values. 
    g.drawString("" + data.length, 
                 (data.length-1) * (hGap) + left, 
                 baseline + fm.getAscent()); 
 
    // Set the data color. 
    g.setColor(dataColor); 
 
    // Display the data. 
    switch(graphStyle) { 
      case BAR: 
        bargraph(g); 
        break; 
      case SCATTER: 
        scatter(g); 
        break; 
      case REGPLOT: 
        regplot(g); 
        break; 
    } 
  } 
 
  // Display a bar graph. 
  private void bargraph(Graphics g) { 
    int v; 
 
    for(int i=0; i < data.length; i++) { 
      v = (int) (data[i] * scale); 
 
      g.drawLine(i*hGap+left, baseline, 
                 i*hGap+left, baseline - v); 
    } 
  } 
 
  // Display a scatter graph. 
  private void scatter(Graphics g) { 
    int v; 
 
    for(int i=0; i < data.length; i++) { 
      v = (int) (data[i] * scale); 
      g.drawRect(i*hGap+left, baseline - v, 1, 1); 
    } 
  } 
 
  // Display a scatter graph with regression line. 
  private void regplot(Graphics g) { 
    int v; 
 
    RegData rd = Stats.regress(data); 
 
    for(int i=0; i < data.length; i++) { 
      v = (int) (data[i] * scale); 
      g.drawRect(i*hGap+left, baseline - v, 1, 1); 
    } 
 
    // Draw the regression line. 
    g.drawLine(left, baseline - (int) ((rd.a)*scale), 
               hGap*(data.length-1)+left+1, 
               baseline - (int) ((rd.a+(rd.b*(data.length-1)))*scale));     
  } 
}

