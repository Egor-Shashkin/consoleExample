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
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrei
 */
public class GettingAllClient {
  private static Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
  
  public static void main(String[] args){
      int port = 7777;
      Socket clientSocket;
      String json;
      ArrayList<TelemetryMessage> array;

      System.out.println("connecting to server");
      try {
        clientSocket = new Socket(InetAddress.getLocalHost(), port);
        Protocol protocol = new Protocol(ConnectionMode.GETALL.name());
        json = protocol.connect(clientSocket);
        System.out.println("parsing json");
        array = new ArrayList<>();
        array = TelemetryParser.parseTelemetryJsons(json, true);

    } catch (IOException ex) {
      Logger.getLogger(GettingClient.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(GettingClient.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

 
}
