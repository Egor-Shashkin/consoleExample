/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.my.SensorData;
import com.my.TelemetryMessage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrei
 */
public class GettingClient {
   

  
  public static void main(String[] args){
    int port = 7777;
    int id = 3;
    Socket clientSocket;
    DataOutputStream out;
    ObjectInputStream in;
    InetAddress host;
    String json;
    try {
      System.out.println("connecting to server");
      clientSocket = new Socket(InetAddress.getLocalHost(), port);
      in = new ObjectInputStream(clientSocket.getInputStream());
      out = new DataOutputStream(clientSocket.getOutputStream());
      System.out.println("sending request");
      out.writeBytes(String.format("get %s%n", id));
      System.out.println("getting json");
      json = (String) in.readObject();
      System.out.println("parsing json");
      JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

      long timeStamp = jsonObject.get("ts").getAsLong();
      String deviceId = jsonObject.get("deviceId").getAsString();
      ArrayList<SensorData> data = App.gson.fromJson(jsonObject.get("data").getAsJsonArray(), new TypeToken<List<SensorData>>(){}.getType());
      TelemetryMessage message = new TelemetryMessage(timeStamp, deviceId, data);
      System.out.printf(" id: %s %n timeStamp: %s %n dataValues: %s",message.getDeviceId(), message.getTimeStamp(), message.processingSencorData(SensorData::getValue));
      out.flush();
      clientSocket.close();
    } catch (IOException | ClassNotFoundException ex) {
      Logger.getLogger(SendingClient.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  
}