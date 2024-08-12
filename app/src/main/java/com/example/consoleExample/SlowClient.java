/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;


import com.my.TelemetryMessage;
import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrei
 */
public class SlowClient {
  private int id;
  private int port;
  public SlowClient(int port, int id) {
    this.port = port;
    this.id = id;
  }
  
  

  
  public void start() throws IOException, InterruptedException{
    
    Socket clientSocket = null;
    DataOutputStream out = null;
    BufferedReader in = null;
    InetAddress host;
    try {
      clientSocket = new Socket(InetAddress.getLocalHost(), port);
      out = new DataOutputStream(clientSocket.getOutputStream());
      TelemetryMessage message = new TelemetryMessage();
      message.generatingSensorData();
      String json = App.gson.toJson(message, TelemetryMessage.class);
      System.out.println("sending data slowly");
      TimeUnit.SECONDS.sleep(5);
      out.writeBytes(String.format("send %d%n%s", id, json));
      out.flush();
      clientSocket.close();
    } catch (IOException ex) {
      Logger.getLogger(SendingClient.class.getName()).log(Level.SEVERE, null, ex);
    }
    System.out.println("Data sent. Connection closed");
  }
  

  
}
