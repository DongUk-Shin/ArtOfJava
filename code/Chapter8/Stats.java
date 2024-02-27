import java.util.*; 
import java.text.*; 
 
// This class holds the regression analysis data. 
class RegData { 
  public double a, b; 
  public double cor; 
  public String equation; 
 
  public RegData(double i, double j, double k, String str) { 
    a = i; 
    b = j; 
    cor = k; 
    equation = str; 
  } 
} 
 
// This is the exception thrown by Mode() 
class NoModeException extends Exception { 
  public String toString() { 
    return "Set contains no mode."; 
  } 
} 
 
// A general-purpose statistics class. 
public class Stats { 
  // Return the average of a set of values. 
  public static double mean(double[] vals) { 
    double avg = 0.0; 
 
    for(int i=0; i < vals.length; i++) 
      avg += vals[i]; 
 
    avg /= vals.length; 
 
    return avg; 
  } 
 
  // Return the median of a set of values. 
  public static double median(double[] vals) { 
    double temp[] = new double[vals.length]; 
 
    System.arraycopy(vals, 0, temp, 0, vals.length); 
 
    Arrays.sort(temp); // sort the data 
 
    // Return the middle value. 
    if((vals.length)%2==0) { 
      // If even number of values, find average 
      return (temp[temp.length/2] + 
              temp[(temp.length/2)-1]) /2; 
    } else return 
      temp[temp.length/2]; 
  } 
 
  /* Returns the mode of a set of values. 
     A NoModeException is thrown if no value 
     occurs more frequently than any other. 
     If two or more values occur with the 
     same frequency, the first value is  
     returned. */ 
  public static double mode(double[] vals) 
      throws NoModeException 
  { 
    double m, modeVal = 0.0; 
    int count, oldcount = 0; 
 
    for(int i=0; i < vals.length; i++) { 
      m = vals[i]; 
      count = 0; 
 
      // Count how many times each value occurs. 
      for(int j=i+1; j < vals.length; j++) 
        if(m == vals[j]) count++; 
 
      /* If this value occurs more frequently than  
         the previous candidate, save it. */ 
      if(count > oldcount) { 
        modeVal = m; 
        oldcount = count; 
      } 
    } 
 
    if(oldcount == 0) 
      throw new NoModeException(); 
    else 
      return modeVal; 
  } 
 
  // Return the standard deviation of a set of values. 
  public static double stdDev(double[] vals) { 
    double std = 0.0; 
    double avg = mean(vals); 
 
    for(int i=0; i < vals.length; i++) 
      std += (vals[i]-avg) * (vals[i]-avg); 
 
    std /= vals.length; 
    std = Math.sqrt(std); 
 
    return std; 
  } 
 
  /* Compute the regression equaltion and coefficient 
     of correlation for a set of values.  The  
     values represent the Y coordinate.  The X 
     cooridinate is time (i.e., ascending increments 
     of 1). */ 
  public static RegData regress(double[] vals) { 
    double a, b, yAvg, xAvg, temp, temp2, cor; 
    double vals2[] = new double[vals.length]; 
 
    // Create number format with 2 decimal digits. 
    NumberFormat nf = NumberFormat.getInstance(); 
    nf.setMaximumFractionDigits(2); 
 
    // Find mean of Y values. 
    yAvg = mean(vals); 
 
    // Find mean of X component. 
    xAvg = 0.0; 
    for(int i=0; i < vals.length; i++) xAvg += i; 
    xAvg /= vals.length; 
 
    // Find b. 
    temp = temp2 = 0.0; 
    for(int i=0; i < vals.length; i++) { 
      temp += (vals[i]-yAvg) * (i-xAvg); 
      temp2 += (i-xAvg) * (i-xAvg); 
    } 
 
    b = temp/temp2; 
 
    // Find a. 
    a = yAvg - (b*xAvg); 
 
    // Compute the coefficient of correlation. 
    for(int i=0; i < vals.length; i++) vals2[i] = i+1; 
    cor = temp/vals.length; 
    cor /= stdDev(vals) * stdDev(vals2); 
 
    return new RegData(a, b, cor, "Y = " + 
                       nf.format(a) +  " + " + 
                       nf.format(b) + " * X"); 
  } 
}

