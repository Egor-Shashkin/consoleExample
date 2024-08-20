/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.my;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * @author Andrei
 */
public class TelemetryMessage {
  @Expose
  @SerializedName("ts")
  private long timeStamp;
  @Expose
  @SerializedName(value = "deviceId", alternate = {"ID", "Id"})
  private String deviceId;
  @Expose
  @SerializedName("data")
  private ArrayList<SensorData> data;
  
  
  public TelemetryMessage(){
    data = new ArrayList<>();
    deviceId = "1";
    timeStamp = Calendar.getInstance().getTimeInMillis();
    
  }
  
  public TelemetryMessage(long timeStamp, String deviceId, ArrayList<SensorData> data){
    this.data = data;
    this.deviceId = deviceId;
    this.timeStamp = timeStamp;
  }
  
  public TelemetryMessage(String deviceId){
    this.data = new ArrayList<>();
    this.deviceId = deviceId;
    this.timeStamp = Calendar.getInstance().getTimeInMillis();
  }

//  public ArrayList<SensorData> getData() {
//    return data;
//  }

  public String getDeviceId() {
    return deviceId;
  }

  public long getTimeStamp() {
    return timeStamp;
  }
  
  public List<Double> processingSencorData(Function<SensorData, Double> function){
    return data.stream().map(function::apply).collect(Collectors.toList());
  }
  
  public void generatingSensorData(){
    for (int i = 0; i < 10; i++){
      data.add(new SensorData(String.format("Sensor_%s", i), Math.random()));
    }
  }
}