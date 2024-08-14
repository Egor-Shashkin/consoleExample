/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

/**
 *
 * @author Andrei
 */
public class EchoClient {
  public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    
    out = null;
    in = null;

    
    System.out.println("sending message to the server");
    for (int i = 0; i < 5; i++){
    socket = new Socket(InetAddress.getLocalHost(),7777);
    out = new ObjectOutputStream(socket.getOutputStream());
    in = new ObjectInputStream(socket.getInputStream());
    out.writeObject("Hi server " + i);
    String response = (String) in.readObject();
    System.out.println(response);
    }
    
    //out.writeObject(".");


  }
}
