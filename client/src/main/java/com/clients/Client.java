/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.clients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.telemetry.SensorData;
import com.telemetry.TelemetryMessage;
import com.myUtility.JsonActions;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Andrei
 */
public class Client {
  private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
  
  public static void main(String[] args) {
    
    JsonActions jAct = new JsonActions();
    Options options;
    options = jAct.ReadJsonOptions("ClientConfig.json");
    
    try {
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd = parser.parse(options, args);
      System.out.println("executing options");
      new Client().executeOptions(cmd);
    } catch (Exception e) {
    }
  }

  private void sendTelemetryMessage(int port, int id){
    Socket clientSocket;
    DataOutputStream out;
    try {
      clientSocket = new Socket(InetAddress.getLocalHost(), port);
      out = new DataOutputStream(clientSocket.getOutputStream());
      TelemetryMessage message = new TelemetryMessage();
      message.generatingSensorData();
      String json = gson.toJson(message, TelemetryMessage.class);
      System.out.println(json);
      out.writeBytes(String.format("send %d%n%s", id, json));
      out.flush();
      clientSocket.close();
    } catch (IOException ex) {
      Logger.getLogger(SendingClient.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  
  private void getTelemetryMessage(int port, int id){
    Socket clientSocket;
    DataOutputStream out;
    ObjectInputStream in;
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
      ArrayList<SensorData> data = gson.fromJson(jsonObject.get("data").getAsJsonArray(), new TypeToken<List<SensorData>>(){}.getType());
      TelemetryMessage message = new TelemetryMessage(timeStamp, deviceId, data);
      System.out.printf(" id: %s %n timeStamp: %s %n dataValues: %s"
              ,message.getDeviceId(), message.getTimeStamp(), message.processingSensorData(SensorData::getValue));
      out.flush();
      clientSocket.close();
    } catch (IOException | ClassNotFoundException ex) {
      Logger.getLogger(SendingClient.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
  
  private void executeOptions(CommandLine cmd) throws ParseException{
    if (cmd.hasOption("g")){
      String[] parsedValues = cmd.getOptionValues("s");
      int port = Integer.getInteger(parsedValues[0]);
      int id = Integer.getInteger(parsedValues[1]);
      getTelemetryMessage(port, id);
    }
    else if (cmd.hasOption("s")){
      String[] parsedValues = cmd.getOptionValues("s");
      int port = Integer.getInteger(parsedValues[0]);
      int id = Integer.getInteger(parsedValues[1]);
      sendTelemetryMessage(port, id);
    }
    else System.out.println("no options to execute");
  }
}
