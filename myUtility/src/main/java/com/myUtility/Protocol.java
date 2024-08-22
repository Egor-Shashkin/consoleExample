/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.myUtility;

import java.lang.annotation.Retention;
import org.checkerframework.common.value.qual.StringVal;
/**
 *
 * @author Andrei
 */


public class Protocol{

  private ConnectionMode mode;
  private String id;
  private String message;

  public Protocol(ConnectionMode mode, String id, String message) {
    this.mode = mode;
    this.id = id;
    this.message = message;
  }
  
  public Protocol(ConnectionMode mode, String id){
    this.mode = mode;
    this.id = id;
    message = "";
  }
  
  public Protocol(ConnectionMode mode){
    this.mode = mode;
    id = "";
    message = "";
  }
  
  public String connectionMessage(){
    return String.format("%s %s%n %s", mode.name(), id, message);
  }
  
}

