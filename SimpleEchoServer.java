// SimpleEchoServer.java
// This class is the server side of a simple echo server based on
// UDP/IP. The server receives from a client a packet containing a character
// string, then echoes the string back to the client.

//Andrew Gujarati
// Last edited May 4th, 2016

import java.io.*;
import java.net.*;

public class SimpleEchoServer {
  
  //Class variable definitions
  DatagramPacket sendPacket, receivePacket;
  DatagramSocket sendSocket, receiveSocket;
  byte success[] = new byte[4];
  
  public SimpleEchoServer()
  {
    try {
      
      // Construct a datagram socket and bind it to port 69
      // on the local host machine. This socket will be used to
      // receive UDP Datagram packets.
      receiveSocket = new DatagramSocket(69);
      
      // to test socket timeout (2 seconds)
      //receiveSocket.setSoTimeout(2000);
    } catch (SocketException se) {
      se.printStackTrace();
      System.exit(1);
    } 
  }
  
  public void receiveAndEcho() throws Exception
  {
    // Construct a DatagramPacket for receiving packets up 
    // to 100 bytes long (the length of the byte array).
    
    byte data[] = new byte[100];
    receivePacket = new DatagramPacket(data, data.length);
    System.out.println("\nServer: Waiting for Packet.\n");
    
    // Block until a datagram packet is received from receiveSocket.
    try {        
      System.out.println("Waiting...\n"); // so we know we're waiting
      receiveSocket.receive(receivePacket);
    } catch (IOException e) {
      System.out.print("IO Exception: likely:");
      System.out.println("Receive Socket Timed Out.\n" + e);
      e.printStackTrace();
      System.exit(1);
    }
    
    // Process the received datagram.
    System.out.println("Server: Packet received:");
    System.out.println("From host: " + receivePacket.getAddress());
    System.out.println("Host port: " + receivePacket.getPort());
    int len = receivePacket.getLength();
    System.out.println("Length: " + len);
    System.out.print("Containing (byte): " );
    
    //Show the byte array data.
    for(int i=0; i<len; i++)
    {
      
      //If there are two zeros in a row, assume the rest of the byte array is empty. 
      //In this case, stop displaying the empty locations.
      if(data[i] == 0)
      {
        if(data[i+1] == 0)
        {
          System.out.print(data[i]);
          break;
        }
      }
      
      //Otherwise, keep showing the next byte in the array.
      System.out.print(data[i] + " ");
    }
    
    // Form a String from the byte array.
    System.out.print("\nContaining (string): " );
    String received = new String(data,0,len);   
    System.out.println(received + "\n");
    
    //Error checking. 
    //First check if the first byte is a zero and the second byte is a 1 or 2.
    if(data[0] == 0)
    {
      if(data[1] == 1 || data[1] == 2)
      {
        //If first two bytes are correct, continue checking.
        //While there are no zeros, the file name is being read.
        //Once a zero is found, the mode should be next in 
        //the array.
        boolean bcontinue = true;
        int count = 1;
        
        //Run through file name until end is reached (zero).
        while(bcontinue)
        {
          count++;
          if(data[count] == 0)
          {
            bcontinue = false;
          }
        }
        
        //The last value looked at in the array was a zero. This
        //should not be the end of the array (yet). Check to make
        //sure the mode is listed in the array and then the final
        //zero.
        if(data[count] == 0 && data[count+1] == 0)
        {
          throw new Exception("\nERROR: Invalid packet format. Array ended early");
        }
        
        else{
          
          bcontinue = true;
          
          //Run through the array until a zero is found. If there is
          //a zero found, we know the next value should also be a zero
          //(to end the array). If not, the format is invalid.
          while(bcontinue)
          {
            count++;
            
            //If the value is zero.
            if(data[count+1] == 0)
            {
              
              //Array format is correct. Notify user through console.
              System.out.println("SUCCESS: Valid packet format.");
              
              //The server must give some feedback for the packet type.
              //If the second byte in the array is a 1, tell the user
              //it is a read request (0 3 0 1). If the second byte in the array is
              //a 2, tell the user it is a write request (0 4 0 0). 
              
              //If the second byte is a 1.
              if(data[1] == 1)
              {
                success[0] = 0;
                success[1] = 3;
                success[2] = 0;
                success[3] = 1;
                
                //Show return code 0 3 0 1.
                System.out.print("Return Code: ");
                for(int i=0; i<4; i++)
                {
                  System.out.print(success[i] + " ");
                }   
              }
              
              //If the second byte is a 2.
              else if(data[1] == 2)
              {
                success[0] = 0;
                success[1] = 4;
                success[2] = 0;
                success[3] = 0;
                
                //Show return code 0 4 0 0.
                System.out.print("Return Code: ");
                for(int i=0; i<4; i++)
                {
                  System.out.print(success[i] + " ");
                }   
              }
              bcontinue = false;
            }//If value is zero.
          }//While loop until zero.     
        }//If valid packet format.
      }//If valid packet type.
      
      //If the second byte didn't show a 1 or 2, it is an invalid packet.
      else{
        throw new Exception("\nERROR: Invalid packet format. Second byte is not one or two.");
      }
    }
    
    else{   
      throw new Exception("\nERROR: Invalid packet format. First byte is not zero.");     
    }
    
    // Create a new datagram packet containing the string received from the client.
    
    try {
      // Construct a datagram socket and bind it to any available
      // port on the local host machine. This socket will be used to
      // send UDP Datagram packets.
      sendSocket = new DatagramSocket();
      
      // to test socket timeout (2 seconds)
      //receiveSocket.setSoTimeout(2000);
    } catch (SocketException se) {
      se.printStackTrace();
      System.exit(1);
    } 
    
    //Send the packet received from the intermediate device back to it
    //with the return code.
    sendPacket = new DatagramPacket(success, 4,
                                    receivePacket.getAddress(), receivePacket.getPort());
    
    //Show what is being sent
    System.out.println("\n\nServer: Sending packet:");
    System.out.println("To host: " + sendPacket.getAddress());
    System.out.println("Host port: " + sendPacket.getPort());
    len = sendPacket.getLength();
    System.out.println("Length: " + len);
    
    System.out.print("Containing (bytes): ");
    
    //Show the byte array data.
    for(int i=0; i<len;i++)
    {
      System.out.print(success[i] + " ");
    }
    
    System.out.print("\nContaining (string): ");
    System.out.println(new String(sendPacket.getData(),0,len));
    
    // Send the datagram packet to the client via the send socket. 
    try {
      sendSocket.send(sendPacket);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    System.out.println("\nServer: packet sent");
    
    // We're finished, so close the send socket.
    sendSocket.close();
  }
  
  public static void main( String args[] ) throws Exception
  {  
    
    boolean bcontinue = true;
    
    SimpleEchoServer c = new SimpleEchoServer();
    
    //Run the server 'forever' (the loop will never be false)
    while(bcontinue)
    {
      c.receiveAndEcho();
    }
  }
}