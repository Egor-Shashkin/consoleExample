/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample.Threads;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import com.example.consoleExample.App;

/**
 *
 * @author Andrei
 */
public class ServerThread implements Runnable{
  ServerSocket serverSocket;
  Socket clientSocket;
  ObjectOutputStream out;
  BufferedReader in;
  int port;
  String fileName;
  String filePath;
  

  public ServerThread(int port, ServerSocket serverSocket, Socket clientSocket) throws IOException {
      this.port = port;
      this.serverSocket = serverSocket;
      this.clientSocket = clientSocket;
      fileName = "telemetry_data_1.json";
      filePath = "C:\\Users\\Andrei\\Documents\\ConsoleExampleDocs\\";
      
  }
  
  @Override
  public void run(){
    try {
      out = new ObjectOutputStream(clientSocket.getOutputStream());
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      Date start_time;
      System.out.printf(Calendar.getInstance().getTimeInMillis()- App.startTime + "T%s: server: reading input %n", Thread.currentThread().getId());
      String[] meta = in.readLine().split(" ");
      

      if (meta[0].equals("send")){
        System.out.printf(Calendar.getInstance().getTimeInMillis()- App.startTime + "T%s: server: getting values from %s %n", Thread.currentThread().getId(), meta[1]);
        String json = in.lines().collect(Collectors.joining());
        fileName = String.format("telemetry_data_%s.json", meta[1]);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + fileName))){
          writer.write(json);
        } catch (Exception e) {
        }
      }

      else if (meta[0].equals("get")){
        fileName = String.format("telemetry_data_%s.json", meta[1]);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath + fileName))) {
          System.out.println("server: sending values");
          out.writeObject(reader.readLine());
          System.out.println("server: values sent");
        } catch (Exception e) {
          System.out.println(e);
        }
        
        out.flush();
      }

      else {
        out.writeBytes("unknown connection");
        System.out.println("server: meta error");
      }

    } catch (IOException ex) {
      Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
    }
    
  }
}