/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.clients;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myUtility.ConnectionMode;
import com.myUtility.Protocol;
import com.telemetry.TelemetryMessage;

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
  private final String id;
  private final int port;
  private Socket clientSocket;
  private TelemetryMessage message;
  private String json;
  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  
  public SlowClient(int port, String id) {
    this.port = port;
    this.id = id;
    message = new TelemetryMessage(id);

  }
  

  
  public void start() throws IOException, InterruptedException{
      try {
      clientSocket = new Socket(InetAddress.getLocalHost(), port);
      message.generatingSensorData();
      json = gson.toJson(message, TelemetryMessage.class);
      Protocol protocol = new Protocol(ConnectionMode.SEND.name(), id, json);
      TimeUnit.SECONDS.sleep(10);
      protocol.connect(clientSocket);
    } catch (SocketException ex) {
      System.out.println("Could not send data");
    } catch (IOException ex) {
      Logger.getLogger(SendingClient.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(SlowClient.class.getName()).log(Level.SEVERE, null, ex);
    }
    System.out.println("Data sent. Connection closed");
  }
}
