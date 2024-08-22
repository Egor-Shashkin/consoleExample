/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.telemetry;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author Andrei
 */
public class SensorData {
  @Expose
  @SerializedName("id")
  private String id;
  @Expose
  @SerializedName("value")
  private Double[] value;
  
  public SensorData(){
    
  }
  
  public SensorData(String id, Double[] value){
    this.id = id;
    this.value = value;
  }

  public Double[] getValue() {
    return value;
  }

  public String getId() {
    return id;
  }
  
  
}
