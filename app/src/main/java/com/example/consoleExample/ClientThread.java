/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrei
 */
public class ClientThread implements Runnable{
  private final String id;
  private final int port;
  
  public ClientThread(int port, String id) {
    this.id = id;
    this.port = port;
  }

  @Override
  public void run() {
    try {
      System.out.printf("starting SlowClient n.%s%n", id);
      new SlowClient(port, id).start();
    } catch (IOException | InterruptedException ex) {
      Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
  
}
