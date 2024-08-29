/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.telemetry;

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
  @SerializedName("valueRange")
  private int range;
  @Expose
  @SerializedName("data")
  private ArrayList<SensorData> data;
  private static final int NDOTS = 32; //must be power of 2
  private static final int NFREQ = 10; // number of frequencies for fourier series
  private static final int DEFAULT_RANGE = 20; //range on which function was transformed
  
  
  
  public TelemetryMessage(){
    data = new ArrayList<>();
    this.range = DEFAULT_RANGE;
    deviceId = "1";
    timeStamp = Calendar.getInstance().getTimeInMillis();
    
  }
  
  public TelemetryMessage(long timeStamp, String deviceId, ArrayList<SensorData> data){
    this.data = data;
    this.range = DEFAULT_RANGE;
    this.deviceId = deviceId;
    this.timeStamp = timeStamp;
  }
  
  public TelemetryMessage(String deviceId){
    this.data = new ArrayList<>();
    this.range = DEFAULT_RANGE;
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

  public int getRange() {
    return range;
  }
  
  public List<Double[]> processingSensorData(Function<SensorData, Double[]> function){
    return data.stream().map(function::apply).collect(Collectors.toList());
  }
  
  public void generatingSensorData(){
    double an;
    double bn;
    double alpha = 0.2;

    for (int i = 0; i < NFREQ; i++){
      an = Math.random() * 10 * Math.exp(-i * alpha);
      bn = Math.random() * 10 * Math.exp(-i * alpha);
      data.add(new SensorData(String.format("Sensor_%s", i), new Double[]{(double)i, an, bn}));
    }
  }
  
    public void generatingfftData(){
    for (int i = 0; i < NDOTS; i++){
      data.add(new SensorData(String.format("Sensor_%s", i), new Double[]{10 * Math.random(), 10 * Math.random()}));
    }
  }
    
  public static TelemetryMessage getDefaultMessage(){
    TelemetryMessage message = new TelemetryMessage("default");
    for (int i = 0; i < NDOTS; i++){
      message.data.add(new SensorData(String.format("Sensor_%s", i), new Double[]{0.0, 0.0}));
    }
    return message;
  }
}