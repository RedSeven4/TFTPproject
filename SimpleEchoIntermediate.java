// SimpleEchoClient.java
// This class is the intermediate device host for a simple echo server based on
// UDP/IP. The client sends a packet to the device, and then the device to the echo server.
// This intermediate device will run continuously.

//Andrew Gujarati
// Last edited May 4th, 2016

import java.io.*;
import java.net.*;

public class SimpleEchoIntermediate {
  
  //Class variable definitions
  DatagramPacket sendPacket, receivePacket;
  DatagramSocket sendSocket, sendReceiveSocket, receiveSocket;
  
  public SimpleEchoIntermediate()
  {
    try {
      
      // Construct a datagram socket and bind it to port 23 
      // on the local host machine. This socket will be used to
      // receive UDP Datagram packets from client.
      receiveSocket = new DatagramSocket(23);
      
      // Construct a datagram socket and bind it to any port 
      // on the local host machine. This socket will be used to
      // send and receive UDP Datagram packets to the server.
      sendReceiveSocket = new DatagramSocket();
      
      // to test socket timeout (2 seconds)
      //receiveSocket.setSoTimeout(2000);
    } catch (SocketException se) {
      se.printStackTrace();
      System.exit(1);
    } 
  }
  
  public void sendReceive()
  {
    // Construct a DatagramPacket for receiving packets up 
    // to 100 bytes long (the length of the byte array).
    
    byte data[] = new byte[100];
    receivePacket = new DatagramPacket(data, data.length);
    
    System.out.println("\nIntermediate Device: Waiting for Packet.\n");
    
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
    
    int clientPort = receivePacket.getPort();
    
    // Process the received datagram.
    System.out.println("Intermediate Device: Packet received from client:");
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
    
    // Slow things down (wait 5 seconds)
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e ) {
      e.printStackTrace();
      System.exit(1);
    }
    
    // Create a new datagram packet containing the string received from the client
    // This packet will be sent to the server
    sendPacket = new DatagramPacket(data, receivePacket.getLength(),
                                    receivePacket.getAddress(), 69);
    
    // Show what will be sent to the server (the packet received from the client)
    System.out.println("Intermediate Device: Sending packet to server:");
    System.out.println("To host: " + sendPacket.getAddress());
    System.out.println("Destination host port: " + sendPacket.getPort());
    len = sendPacket.getLength();
    System.out.println("Length: " + len);
    
    System.out.print("Containing (bytes): ");
    
    //Show the byte array data.
    for(int i=0; i<len;i++)
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
    
    System.out.print("\nContaining (string): ");
    System.out.println(new String(sendPacket.getData(),0,len));
    // or (as we should be sending back the same thing)
    // System.out.println(received); 
    
    // Slow things down (wait 5 seconds)
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e ) {
      e.printStackTrace();
      System.exit(1);
    }
    
    // Send the datagram packet to the client via the sendReceive socket. 
    try {
      sendReceiveSocket.send(sendPacket);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    System.out.println("\nIntermediate Device: packet sent to server");
    
    // Construct a DatagramPacket for receiving packets up 
    // to 100 bytes long (the length of the byte array).
    
    byte dataReceive[] = new byte[100];
    receivePacket = new DatagramPacket(dataReceive, dataReceive.length);
    
    // Slow things down (wait 5 seconds)
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e ) {
      e.printStackTrace();
      System.exit(1);
    }
    
    try {
      // Block until a datagram is received via sendReceiveSocket.  
      sendReceiveSocket.receive(receivePacket);
    } catch(IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    // Process the received datagram.
    System.out.println("\nIntermediate Device: Packet received from server:");
    System.out.println("From host: " + receivePacket.getAddress());
    System.out.println("Host port: " + receivePacket.getPort());
    len = receivePacket.getLength();
    System.out.println("Length: " + len);
    System.out.print("Containing (bytes): ");
    
    //Show the byte array data.
    for(int i=0; i<len;i++)
    {
      System.out.print(dataReceive[i] + " ");
    }
    
    System.out.print("\nContaining (string): ");
    
    // Form a String from the byte array.
    String receivedServer = new String(dataReceive,0,len);   
    System.out.println(receivedServer);
    
    // Slow things down (wait 5 seconds)
    try {
      Thread.sleep(5000);
    } catch (InterruptedException e ) {
      e.printStackTrace();
      System.exit(1);
    }
    
    try {
      
      // Construct a datagram socket and bind it to any port 
      // on the local host machine. This socket will be used to
      // send UDP Datagram packets to the client.
      sendSocket = new DatagramSocket();
      
      // to test socket timeout (2 seconds)
      //receiveSocket.setSoTimeout(2000);
    } catch (SocketException se) {
      se.printStackTrace();
      System.exit(1);
    } 
    
    //Create a packet to be sent to the client, with the server acknowledgement
    sendPacket = new DatagramPacket(dataReceive, receivePacket.getLength(),
                                    receivePacket.getAddress(), clientPort);
    
    //Show the packet being sent to the client.
    System.out.println("\nIntermediate Device: Sending packet to client:");
    System.out.println("To host: " + sendPacket.getAddress());
    System.out.println("Destination host port: " + sendPacket.getPort());
    len = sendPacket.getLength();
    System.out.println("Length: " + len);
    
    System.out.print("Containing (bytes): ");
    
    //Show the byte array data.
    for(int i=0; i<len;i++)
    {
      
      //If there are two zeros in a row, assume the rest of the byte array is empty. 
      //In this case, stop displaying the empty locations.
      if(dataReceive[i] == 0)
      {
        if(dataReceive[i+1] == 0)
        {
          System.out.print(dataReceive[i]);
          break;
        }
      }
      
      //Otherwise, keep showing the next byte in the array.
      System.out.print(dataReceive[i] + " ");
    }
    
    System.out.print("\nContaining (string): ");
    System.out.println(new String(sendPacket.getData(),0,len));
    // or (as we should be sending back the same thing)
    
    // Send the datagram packet to the client via the send socket. 
    try {
      sendSocket.send(sendPacket);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    System.out.println("Intermediate Device: packet sent to client");
    
    // We're finished, so close the send socket.
    sendSocket.close();
  }
  
  public static void main( String args[] ) throws Exception
  {
    
    boolean bcontinue = true;
    
    SimpleEchoIntermediate c = new SimpleEchoIntermediate();
    
    //Run the intermediate device 'forever' (the loop will never be false)
    while(bcontinue){ 
      c.sendReceive();
    }
  }
}