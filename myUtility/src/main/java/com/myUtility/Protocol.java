/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.myUtility;

import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
/**
 *
 * @author Andrei
 */


public class Protocol{

  private final String mode;
  private final String id;
  private final String message;

  public Protocol(String mode, String id, String message) {
    this.mode = mode;
    this.id = id;
    this.message = message;
  }
  
  public Protocol(String mode, String id){
    this.mode = mode;
    this.id = id;
    message = "";
  }
  
  public Protocol(String mode){
    this.mode = mode;
    id = "";
    message = "";
  }
  
  public String connectionMessage(){
    return String.format("%s %s%n %s", mode, id, message);
  }
  
  public String connect(Socket socket) throws IOException, ClassNotFoundException{
    String response = null;
    DataOutputStream out;
    ObjectInputStream in;
    out = new DataOutputStream(socket.getOutputStream());
    in = new ObjectInputStream(socket.getInputStream());
    try {
      System.out.println("waiting for spare server thread");
      in.readObject();
      System.out.println("sending request");
      out.writeBytes(connectionMessage());
      out.flush();
      if (!mode.equalsIgnoreCase(ConnectionMode.SEND.name())){
      System.out.println("getting response");
      response = (String) in.readObject();
      }
    } catch (EOFException e) {
      System.out.println("input read error");
    }
    
    socket.close();
    in.close();
    out.close();
    System.out.println("Connection closed");
    return response;
  }
  
}

