// Find connections using a breadth-first search.
import java.util.*;
import java.io.*;

// Flight information.
class FlightInfo {
  String from;
  String to;
  int distance;
  boolean skip; // used in backtracking

  FlightInfo(String f, String t, int d) {
    from = f;
    to = t;
    distance = d;
    skip = false;
  }
}

class Breadth {
  final int MAX = 100;

  // This array holds the flight information.
  FlightInfo flights[] = new FlightInfo[MAX]; 

  int numFlights = 0; // number of entries in flight array

  Stack btStack = new Stack(); // backtrack stack

  public static void main(String args[])
  {
    String to, from;
    Breadth ob = new Breadth();
    BufferedReader br = new 
      BufferedReader(new InputStreamReader(System.in)); 
 
    ob.setup();  

    try { 
      System.out.print("From? ");
      from = br.readLine(); 
      System.out.print("To? ");
      to = br.readLine(); 

      ob.isflight(from, to);

      if(ob.btStack.size() != 0)
        ob.route(to);
    } catch (IOException exc) { 
      System.out.println("Error on input.");
    }
  }
  
  // Initialize the flight database.
  void setup()
  {
    addFlight("New York", "Chicago", 900);
    addFlight("Chicago", "Denver", 1000);
    addFlight("New York", "Toronto", 500);
    addFlight("New York", "Denver", 1800);
    addFlight("Toronto", "Calgary", 1700);
    addFlight("Toronto", "Los Angeles", 2500);
    addFlight("Toronto", "Chicago", 500);
    addFlight("Denver", "Urbana", 1000);
    addFlight("Denver", "Houston", 1000);
    addFlight("Houston", "Los Angeles", 1500);
    addFlight("Denver", "Los Angeles", 1000);
  }
  
  // Put flights into the database.
  void addFlight(String from, String to, int dist)
  {  
    if(numFlights < MAX) {
      flights[numFlights] =
        new FlightInfo(from, to, dist);

      numFlights++;
    }
    else System.out.println("Flight database full.\n");
  }

  // Show the route and total distance.
  void route(String to)
  {
    Stack rev = new Stack();
    int dist = 0;
    FlightInfo f;
    int num = btStack.size();

    // Reverse the stack to display route.
    for(int i=0; i < num; i++) 
      rev.push(btStack.pop());

    for(int i=0; i < num; i++) {
      f = (FlightInfo) rev.pop();
      System.out.print(f.from + " to ");
      dist += f.distance;
    }

    System.out.println(to);
    System.out.println("Distance is " + dist);
  }

  /* If there is a flight between from and to,
     return the distance of flight;
     otherwise, return 0. */
  int match(String from, String to)
  {
    for(int i=numFlights-1; i > -1; i--) {
      if(flights[i].from.equals(from) &&
         flights[i].to.equals(to) &&
         !flights[i].skip)
      {
        flights[i].skip = true; // prevent reuse
        return flights[i].distance;
      }
    }

    return 0; // not found 
  }
  
  // Given from, find any connection.
  FlightInfo find(String from)
  {
    for(int i=0; i < numFlights; i++) {
      if(flights[i].from.equals(from) &&
         !flights[i].skip)
      {
        FlightInfo f = new FlightInfo(flights[i].from,
                             flights[i].to,
                             flights[i].distance);
        flights[i].skip = true; // prevent reuse

        return f;
      }
    }

    return null;
  }
  
  /* Determine if there is a route between from and to
     using breadth-first search. */
  void isflight(String from, String to)
  {
    int dist, dist2;
    FlightInfo f;

    // This stack is needed by the breadth-first search.
    Stack resetStck = new Stack();

    // See if at destination.
    dist = match(from, to);
    if(dist != 0) {
      btStack.push(new FlightInfo(from, to, dist));
      return;
    }

    /* Following is the first part of the breadth-first
       modification.  It checks all connecting flights
       from a specified node. */
    while((f = find(from)) != null) {
      resetStck.push(f);
      if((dist = match(f.to, to)) != 0) {
        resetStck.push(f.to);
        btStack.push(new FlightInfo(from, f.to, f.distance));
        btStack.push(new FlightInfo(f.to, to, dist));
        return;
      }
    }

    /* The following code resets the skip fields set by
       preceding while loop. This is also part of the
       breadth-first modifiction. */
    int i = resetStck.size();
    for(; i!=0; i--)
      resetSkip((FlightInfo) resetStck.pop());

    // Try another connection.
    f = find(from);
    if(f != null) {
      btStack.push(new FlightInfo(from, to, f.distance));
      isflight(f.to, to);
    }
    else if(btStack.size() > 0) {
      // Backtrack and try another connection.
      f = (FlightInfo) btStack.pop();
      isflight(f.from, f.to);
    }
  }

  // Reset skip field of specified flight.
  void resetSkip(FlightInfo f) {
    for(int i=0; i< numFlights; i++)
      if(flights[i].from.equals(f.from) &&
         flights[i].to.equals(f.to))
           flights[i].skip = false;
  }
}
