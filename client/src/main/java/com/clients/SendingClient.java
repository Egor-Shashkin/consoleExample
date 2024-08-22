/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.clients;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myUtility.ConnectionMode;
import com.telemetry.TelemetryMessage;
import com.myUtility.Protocol;
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
    
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Socket clientSocket = null;
    DataOutputStream out = null;
    ObjectInputStream in;
    TelemetryMessage message = new TelemetryMessage(id);
    String json;
    
    try {
      clientSocket = new Socket(InetAddress.getLocalHost(), port);
      message.generatingSensorData();
      json = gson.toJson(message, TelemetryMessage.class);
      System.out.println("waiting for spare server thread");
      in = new ObjectInputStream(clientSocket.getInputStream());
      in.readObject();
      System.out.println("sending data slowly");
      out = new DataOutputStream(clientSocket.getOutputStream());
      //out.writeBytes(String.format("send %s%n%s", id, json));
      out.writeBytes(new Protocol(ConnectionMode.SEND, id, json).connectionMessage());
      in.close();
      out.close();
      clientSocket.close();
      System.out.println("Data sent. Connection closed");
    } catch(SocketException ex) {
      System.out.println("Could not send data");
    } catch (IOException ex) {
      Logger.getLogger(SendingClient.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(SlowClient.class.getName()).log(Level.SEVERE, null, ex);
    }
    
  }
}
  

