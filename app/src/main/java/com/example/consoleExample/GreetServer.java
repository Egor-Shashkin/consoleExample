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
public class GreetServer {
  private ServerSocket serverSocket;
  private Socket clientSocket;
  private PrintWriter out;
  private BufferedReader in;
  public void start(int port){
    try {
      serverSocket = new ServerSocket(port);
      clientSocket = serverSocket.accept();
      out = new PrintWriter(clientSocket.getOutputStream());
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      
      String greeting = in.readLine();
      if ("hello server".equals(greeting)){
        out.println("hello client");
      }
      else {
        out.println("unrecognised greeting");
      }   
    } catch (IOException ex) {
      Logger.getLogger(GreetServer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
    
  public void stop() {
    try {
      in.close();
      out.close();
      clientSocket.close();
      serverSocket.close();
    } catch (IOException ex) {
      Logger.getLogger(GreetServer.class.getName()).log(Level.SEVERE, null, ex);
    }
  } 
    
}
