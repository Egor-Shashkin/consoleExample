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
  private String id;
  private int port;
  private Socket clientSocket;
  private DataOutputStream out;
  private TelemetryMessage message;
  private String json;
  
  public SlowClient(int port, String id) {
    this.port = port;
    this.id = id;
    message = new TelemetryMessage(id);

  }
  

  
  public void start() throws IOException, InterruptedException{
      try {
      clientSocket = new Socket(InetAddress.getLocalHost(), port);
      message.generatingSensorData();
      json = App.gson.toJson(message, TelemetryMessage.class);
      System.out.println("sending data slowly");
      TimeUnit.SECONDS.sleep(20);
      out = new DataOutputStream(clientSocket.getOutputStream());
      out.writeBytes(String.format("send %s%n%s", id, json));
      out.close();
      clientSocket.close();
    } catch (IOException ex) {
      Logger.getLogger(SendingClient.class.getName()).log(Level.SEVERE, null, ex);
    }
    System.out.println("Data sent. Connection closed");
  }
}
