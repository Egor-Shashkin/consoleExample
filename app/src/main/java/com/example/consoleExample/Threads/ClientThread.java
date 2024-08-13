/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample.Threads;

import com.example.consoleExample.SlowClient;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.example.consoleExample.App;
import java.util.Calendar;

/**
 *
 * @author Andrei
 */
public class ClientThread implements Runnable{
  private int id;
  private int port;
  public ClientThread(int port, int id) {
    this.id = id;
    this.port = port;
  }

  @Override
  public void run() {
    try {
      System.out.printf(Calendar.getInstance().getTimeInMillis()- App.startTime + "starting SlowClient n.%s%n", id);
      new SlowClient(port, id).start();
    } catch (IOException ex) {
      Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
    } catch (InterruptedException ex) {
      Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
}
