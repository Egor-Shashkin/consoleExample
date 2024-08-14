/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.my.SensorData;
import com.my.TelemetryMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
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
      PrintWriter out;
      ObjectInputStream in;
      String json;
      long timeStamp;
      String deviceId;
      ArrayList<SensorData> data;
      TelemetryMessage message;

      System.out.println("connecting to server");
      try {
        clientSocket = new Socket(InetAddress.getLocalHost(), port);
        System.out.println("sending request");
        out = new PrintWriter(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());

        out.print(String.format("get %d%n", id));
        out.flush();

        System.out.println("getting json");
        json = (String) in.readObject();
        clientSocket.close();

        System.out.println("parsing json");
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        timeStamp = jsonObject.get("ts").getAsLong();
        deviceId = jsonObject.get("deviceId").getAsString();
        data = App.gson.fromJson(jsonObject.get("data").getAsJsonArray(), new TypeToken<List<SensorData>>(){}.getType());
        message = new TelemetryMessage(timeStamp, deviceId, data);
        System.out.printf(" id: %s %n timeStamp: %s %n dataValues: %s",message.getDeviceId(), message.getTimeStamp(), message.processingSencorData(SensorData::getValue));
        in.close();
        
    } catch (IOException ex) {
      Logger.getLogger(GettingClient.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(GettingClient.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

 
}
