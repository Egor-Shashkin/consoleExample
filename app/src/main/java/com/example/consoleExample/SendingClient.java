/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;


import com.my.TelemetryMessage;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrei
 */
public class SendingClient {
  

  
  public static void main(String[] args) throws IOException{
    int port = 7777;
    String id = "5";
    Socket clientSocket = null;
    PrintWriter out = null;
    
    try {
      clientSocket = new Socket(InetAddress.getLocalHost(), port);
      out = new PrintWriter(clientSocket.getOutputStream());
      TelemetryMessage message = new TelemetryMessage(id);
      message.generatingSensorData();
      String json = App.gson.toJson(message, TelemetryMessage.class);
      System.out.println(json);
      out.print(String.format("send %d%n%s", id, json));
      out.flush();
      out.close();
      clientSocket.close();
    } catch (IOException ex) {
      Logger.getLogger(SendingClient.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  

  
}
