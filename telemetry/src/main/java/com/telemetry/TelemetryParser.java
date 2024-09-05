/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.telemetry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Andrei
 */
public class TelemetryParser {
  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  
  public static TelemetryMessage parseTelemetryJson(String json, boolean print){
    
    TelemetryMessage message;
    long timeStamp;
    String deviceId;
    ArrayList<SensorData> data;
    JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
    timeStamp = jsonObject.get("ts").getAsLong();
    deviceId = jsonObject.get("deviceId").getAsString();
    data = gson.fromJson(jsonObject.get("data").getAsJsonArray(), new TypeToken<List<SensorData>>(){}.getType());
    message = new TelemetryMessage(timeStamp, deviceId, data);
    if (print)
    System.out.printf(" id: %s %n timeStamp: %s %n dataValues: %s%n%n",
                message.getDeviceId(), message.getTimeStamp(),
                message.processingSensorData(SensorData::getValue).stream()
                        .map(s -> Arrays.toString(s))
                        .collect(Collectors.toList()));
    
    return message;
  }
  
  public static TelemetryMessage parseTelemetryJson(String json){
    return parseTelemetryJson(json, false);
  }
  
  public static ArrayList<TelemetryMessage> parseTelemetryJsons(String json, boolean print){
    TelemetryMessage message;
    long timeStamp;
    String deviceId;
    ArrayList<SensorData> data;
    JsonObject jsonObject;
    ArrayList<TelemetryMessage> array = new ArrayList<>();


    JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
    for (JsonElement jsonElement : jsonArray){
      jsonObject = jsonElement.getAsJsonObject();
      timeStamp = jsonObject.get("ts").getAsLong();
      deviceId = jsonObject.get("deviceId").getAsString();
      data = gson.fromJson(jsonObject.get("data").getAsJsonArray(), new TypeToken<List<SensorData>>(){}.getType());
      message = new TelemetryMessage(timeStamp, deviceId, data);
      if (print)
      System.out.printf(" id: %s %n timeStamp: %s %n dataValues: %s%n%n",
              message.getDeviceId(), message.getTimeStamp(),
              message.processingSensorData(SensorData::getValue).stream()
                      .map(s -> Arrays.toString(s))
                      .collect(Collectors.toList()));

      array.add(message);
    }
    return array;
  }
  
  public static ArrayList<TelemetryMessage> parseTelemetryJsons(String json){
    return parseTelemetryJsons(json, false);
  }
}
