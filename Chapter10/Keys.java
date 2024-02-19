// Find the lost keys!
import java.util.*;
import java.io.*;

// Room information.
class RoomInfo {
  String from;
  String to;
  boolean skip;

  RoomInfo(String f, String t) {
    from = f;
    to = t;
    skip = false;
  }
}

class Keys {
  final int MAX = 100;

  // This array holds the room information.
  RoomInfo room[] = new RoomInfo[MAX]; 

  int numRooms = 0; // number of rooms

  Stack btStack = new Stack(); // backtrack stack

  public static void main(String args[])
  {    
    String to, from;
    Keys ob = new Keys();

    ob.setup();  

    from = "front_door";
    to = "keys";

    ob.iskeys(from, to);

    if(ob.btStack.size() != 0)
      ob.route(to);
  }
  
  // Initialize the room database.
  void setup()
  {
    addRoom("front_door", "lr");
    addRoom("lr", "bath");
    addRoom("lr", "hall");
    addRoom("hall", "bd1");
    addRoom("hall", "bd2");
    addRoom("hall", "mb");
    addRoom("lr", "kitchen");
    addRoom("kitchen", "keys");
  }
  
  // Put rooms into the database.
  void addRoom(String from, String to)
  {
    if(numRooms < MAX) {
      room[numRooms] = new RoomInfo(from, to);
      numRooms++;
    }
    else System.out.println("Room database full.\n");
  }

  // Show the route and total distance.
  void route(String to)
  {
    Stack rev = new Stack();
    RoomInfo r;
    int num = btStack.size();

    // Reverse the stack to display path.
    for(int i=0; i < num; i++) 
      rev.push(btStack.pop());

    for(int i=0; i < num; i++) {
      r = (RoomInfo) rev.pop();
      System.out.print(r.from + " to ");
    }

    System.out.println(to);
  }

  /* If there is a path between from and to,
     return true, otherwise return false. */
  boolean match(String from, String to)
  {
    for(int i=numRooms-1; i > -1; i--) {
      if(room[i].from.equals(from) &&
         room[i].to.equals(to) &&
         !room[i].skip)
      {
        room[i].skip = true; // prevent reuse
        return true;
      }
    }

    return false; // not found 
  }
  
  // Given from, find any path.
  RoomInfo find(String from)
  {
    for(int i=0; i < numRooms; i++) {
      if(room[i].from.equals(from) &&
         !room[i].skip)
      {
        RoomInfo r = new RoomInfo(room[i].from,
                           room[i].to);
        room[i].skip = true; // prevent reuse

        return r;
      }
    }
    return null;
  }
  
  // Determine if there is a path between from and to. 
  void iskeys(String from, String to)
  {
    int dist;
    RoomInfo r;

    // See if at destination.
    if(match(from, to)) {
      btStack.push(new RoomInfo(from, to));
      return;
    }

    // Try another connection.
    r = find(from);
    if(r != null) {
      btStack.push(new RoomInfo(from, to));
      iskeys(r.to, to);
    }
    else if(btStack.size() > 0) {
      // Backtrack and try another connection.
      r = (RoomInfo) btStack.pop();
      iskeys(r.from, r.to);
    }
  }
}  
  