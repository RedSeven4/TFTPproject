// SimpleEchoClient.java
// This class is the client side for a simple echo server based on
// UDP/IP. The client sends a character string to the echo server, then waits 
// for the server to send it back to the client.

//Andrew Gujarati
// Last edited May 4th, 2016

import java.io.*;
import java.net.*;

public class SimpleEchoClient {
  
  //Class variable definitions
  DatagramPacket sendPacket, receivePacket;
  DatagramSocket sendReceiveSocket;
  
  public SimpleEchoClient()
  {
    try {
      // Construct a datagram socket and bind it to port 23 
      // on the local host machine. This socket will be used to
      // send and receive UDP Datagram packets.
      sendReceiveSocket = new DatagramSocket();
    } catch (SocketException se) {   // Can't create the socket.
      se.printStackTrace();
      System.exit(1);
    }
  }
  
  public void sendAndReceive(int packetType)
  {
    // Prepare a DatagramPacket and send it via sendReceiveSocket
    // to port 23 on the destination (intermediate device) host.
    
    String f = "README.TXT";
    String m = "netascii";
    
    // Java stores characters as 16-bit Unicode values, but 
    // DatagramPackets store their messages as byte arrays.
    // Convert the String into bytes according to the platform's 
    // default character encoding, storing the result into a new 
    // byte array.
    
    byte msg[] = new byte[100];
    
    //Insert the first two bytes in the array with a 0 and either 1 or 2.
    //In this case, the program will alternate between read and write packets
    //(Odd numbers read, even numbers write) And then finally send a bad packet.
    
    msg[0] = 0;
    
    //Bad packet type.
    if(packetType == 11)
    {
      msg[1] = 0;
    }
    
    //Read request
    else if(packetType % 2 == 1){
      msg[1] = 1;
    }
    
    //Write request
    else{
      msg[1] = 2;
    }
    
    //Create a temporary byte array with the file name and store the current
    //size of the array in a variable.
    
    byte filename[] = f.getBytes();
    int arraySize = 2;
    
    //Insert the file name into the master array
    
    for(int i=0; i<f.length(); i++)
    { 
      msg[2+i] = filename[i];
      arraySize++;
    }
    
    //Insert the 0 byte into the end of the array and increase the array size
    
    msg[arraySize] = 0;
    arraySize++;
    
    byte mode[] = m.getBytes();
    
    //Insert the mode type into the master array
    for(int i=0; i<m.length(); i++)
    { 
      msg[arraySize] = mode[i];
      arraySize++;
    }
    
    //Insert the 0 byte into the end of the array and increase the arraysize
    msg[arraySize] = 0;
    arraySize++;
    
    // Construct a datagram packet that is to be sent to a specified port 
    // on a specified host.
    // The arguments are:
    //  msg - the message contained in the packet (the byte array)
    //  msg.length - the length of the byte array
    //  arraySize - the actual size of the array in terms of used memory
    //  InetAddress.getLocalHost() - the Internet address of the 
    //     destination host.
    //     In this example, we want the destination to be the same as
    //     the source (i.e., we want to run the client and server on the
    //     same computer). InetAddress.getLocalHost() returns the Internet
    //     address of the local host.
    //  23 - the destination port number on the destination (intermediate device) host.
    try {
      sendPacket = new DatagramPacket(msg, arraySize,
                                      InetAddress.getLocalHost(), 23);
    } catch (UnknownHostException e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    //Print the current packet being sent to the console
    System.out.println("\nClient: Sending packet:");
    System.out.println("To host: " + sendPacket.getAddress());
    System.out.println("Destination host port: " + sendPacket.getPort());
    int len = arraySize;
    System.out.println("Length: " + len);
    System.out.print("Containing (bytes): ");
    
    //Show each byte in the array with spaces
    for(int i=0; i<arraySize;i++)
    {
      System.out.print(msg[i] + " ");
    }
    
    System.out.print("\nContaining (string): ");
    System.out.println(new String(sendPacket.getData(),0,len));
    
    // Send the datagram packet to the server via the send/receive socket. 
    
    try {
      sendReceiveSocket.send(sendPacket);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    System.out.println("\nClient: Packet sent.\n");
    
    // Construct a DatagramPacket for receiving packets up 
    // to 100 bytes long (the length of the byte array).
    
    byte data[] = new byte[100];
    receivePacket = new DatagramPacket(data, data.length);
    
    try {
      // Block until a datagram is received via sendReceiveSocket.  
      sendReceiveSocket.receive(receivePacket);
    } catch(IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    // Process the received datagram.
    System.out.println("Client: Packet received:");
    System.out.println("From host: " + receivePacket.getAddress());
    System.out.println("Host port: " + receivePacket.getPort());
    len = receivePacket.getLength();
    System.out.println("Length: " + len);
    System.out.print("Containing (bytes): ");
    
    //Show the byte array data.
    for(int i=0; i<len;i++)
    {
      System.out.print(data[i] + " ");
    }
    
    System.out.print("\nContaining (string): ");
    
    // Form a String from the byte array.
    String received = new String(data,0,len);   
    System.out.println(received);
    
    // On the last iteration, we're finished, so close the socket.
    if(packetType == 11){
      sendReceiveSocket.close();
    }   
  }
  
  public static void main(String args[])
  {
    
    SimpleEchoClient c = new SimpleEchoClient();
    
    //Keep running the sendAndReceive method until 11 packets have been sent.
    for(int i=1; i<=11; i++)
    { 
      c.sendAndReceive(i);
    } 
  }
}