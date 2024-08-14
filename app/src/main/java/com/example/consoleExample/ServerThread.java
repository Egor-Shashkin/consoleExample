/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.my.TelemetryMessage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
  Integer reservations;
  String fileName;
  File filePath;
  String[] meta;
  
  

  public ServerThread(int port, ServerSocket serverSocket, Socket clientSocket) throws IOException {
      this.port = port;
      this.serverSocket = serverSocket;
      this.clientSocket = clientSocket;
      fileName = "telemetry_data_default.json";
      filePath = new File("C:\\Users\\Andrei\\Documents\\ConsoleExampleDocs\\");
      App.reservations.incrementAndGet();
      
  }
  
  @Override
  public void run(){
    try {
      out = new ObjectOutputStream(clientSocket.getOutputStream());
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      //reading client input, first line is metadata "command id"
      System.out.printf("T%s: server: reading input %n", Thread.currentThread().getId());
      meta = in.readLine().split(" ");
      

      //chosing action according to command
      if (meta[0].equals("send")){
        System.out.printf("T%s: server: getting values from %s %n", Thread.currentThread().getId(), meta[1]);
        String json = in.lines().collect(Collectors.joining("\n"));
        fileName = String.format("telemetry_data_%s.json", meta[1]);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath, fileName)))){
          writer.write(json);
        } catch (Exception e) {
        }
      }

      else if (meta[0].equals("get")){
        fileName = String.format("telemetry_data_%s.json", meta[1]);
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(filePath, fileName)))) {
          System.out.println("server: sending values");
          out.writeObject(reader.lines().collect(Collectors.joining("\n")));
          System.out.println("server: values sent");
        } catch (Exception e) {
          System.out.println(e);
        }
        
        out.flush();
        out.close();
      }
      
      else if (meta[0].equals("getAll")){
        JsonArray array = new JsonArray();
        for (File file : filePath.listFiles()){
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            array.add(JsonParser.parseReader(reader));
        } catch (Exception e) {
          System.out.println(e);
        }
        }
        out.writeObject(App.gson.toJson(array));
        out.flush();
        out.close();
      }

      else {
        out.writeBytes("unknown connection");
        System.out.println("server: meta error");
      }
    clientSocket.close();

    } catch (IOException ex) {
      Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    //after ending connection decreasing upcoming connections counter
    App.reservations.decrementAndGet();
  }
}