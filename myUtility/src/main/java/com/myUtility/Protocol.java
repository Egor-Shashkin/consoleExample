/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.myUtility;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
/**
 *
 * @author Andrei
 */


public class Protocol{
  public static final String CONTINUE = "100 Continue";
  public static final String OK = "200 OK";
  public static final String NOT_FOUND = "404 File not found";
  private String mode;
  private String id;
  private String message;

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

  

  public Protocol() {
    mode = ConnectionMode.GET.name();
    id = "";
    message = "";
  }

  
  public void setMode(String mode) {
    this.mode = mode;
  }

  public void setMessage(String msg){
    this.message = msg;
  }

  public void setId(String id){
    this.id = id;
  }
  
  public String connectionMessage(){
    return String.format("%s %s%n %s", mode, id, message);
  }
  
  public String connect(Socket socket) throws IOException, ClassNotFoundException{
    String response = null;
    ObjectOutputStream out;
    ObjectInputStream in;
    out = new ObjectOutputStream(socket.getOutputStream());
    in = new ObjectInputStream(socket.getInputStream());
    try {
      System.out.println("waiting for spare server thread");
      in.readObject();
      System.out.println("sending request");
      out.writeObject(connectionMessage());
      System.out.println("getting response");
      response = (String) in.readObject();
      if (response.equals(NOT_FOUND)) throw new FileNotFoundException();
      System.out.println(response);
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

