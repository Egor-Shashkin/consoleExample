/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.clients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Andrei
 */
public class GettingAllClient {
  private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
  
  public static void main(String[] args){
      int port = 7777;
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
        
        out = new PrintWriter(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
        
        System.out.println("waiting for spare server thread");
        in.readObject();
        System.out.println("sending request");
        out.println("getAll");
        out.flush();
        System.out.println("getting json");
        json = (String) in.readObject();
        clientSocket.close();

        System.out.println("parsing json");
        JsonObject jsonObject;
        ArrayList<TelemetryMessage> array = new ArrayList<>();
        

        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
        for (JsonElement jsonElement : jsonArray){
          jsonObject = jsonElement.getAsJsonObject();
          timeStamp = jsonObject.get("ts").getAsLong();
          deviceId = jsonObject.get("deviceId").getAsString();
          data = gson.fromJson(jsonObject.get("data").getAsJsonArray(), new TypeToken<List<SensorData>>(){}.getType());
          message = new TelemetryMessage(timeStamp, deviceId, data);
          System.out.printf(" id: %s %n timeStamp: %s %n dataValues: %s%n%n",
                  message.getDeviceId(), message.getTimeStamp(),
                  message.processingSensorData(SensorData::getValue).stream()
                          .map(s -> Arrays.toString(s))
                          .collect(Collectors.toList()));

          array.add(message);
        }
        in.close();
        out.close();
        clientSocket.close();
        
    } catch (IOException ex) {
      Logger.getLogger(GettingClient.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(GettingClient.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

 
}
