/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Andrei
 */
public class EchoServer {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private String inputLine;


  public void start(int port) {

    try {
      serverSocket = new ServerSocket(port);

      while (true) {
        clientSocket = serverSocket.accept();
      }
    } catch (IOException ex) {
      Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
    }

  }
  
  public void holdConnection(){
      try {
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
        inputLine = (String) in.readObject();
        out.writeObject(inputLine);
        out.close();
        in.close();

      } catch (IOException | ClassNotFoundException ex) {
        Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
      }
  }
  
  public void stop(){
      try {
        in.close();
        out.close();
        serverSocket.close();
        clientSocket.close();
      } catch (IOException ex) {
        Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
      }
  }
}
