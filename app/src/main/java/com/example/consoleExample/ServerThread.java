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
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
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
  File file;
  FileWorker worker;
  
  

  public ServerThread(int port, ServerSocket serverSocket, Socket clientSocket, FileWorker worker) throws IOException {
      this.port = port;
      this.serverSocket = serverSocket;
      this.clientSocket = clientSocket;
      this.worker = worker;
      fileName = "telemetry_data_default.json";
      filePath = new File("C:\\Users\\Andrei\\Documents\\ConsoleExampleDocs\\");
      App.reservations.incrementAndGet();
      
  }
  
  @Override
  public void run(){
    try {
      clientSocket.setSoTimeout(60000);
      out = new ObjectOutputStream(clientSocket.getOutputStream());
      in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

      
      out.writeObject("ready");
      out.flush();
      
      //reading client input, first line is metadata "request id"
      System.out.printf("T%s: server: reading input %n", Thread.currentThread().getId());
      meta = in.readLine().split(" ");
      

      //choosing action according to command
      if (meta[0].equals("send")){
        System.out.printf("T%s: server: getting values from %s %n", Thread.currentThread().getId(), meta[1]);
        String json = in.lines().collect(Collectors.joining("\n"));
        fileName = String.format("telemetry_data_%s.json", meta[1]);
        file = new File(filePath, fileName);
        worker.writeMessage(json, file);
      }

      else if (meta[0].equals("get")){
        fileName = String.format("telemetry_data_%s.json", meta[1]);
        file = new File(filePath, fileName);
        System.out.println("server: sending values");
        out.writeObject(worker.getFileData(file));
        System.out.println("server: values sent");
        out.flush();
        out.close();
      }
      
      else if (meta[0].equals("getAll")){
        
        System.out.println("server: sending all values");
        out.writeObject(worker.getAllFileData(filePath));
        out.flush();
        out.close();
      }

      else {
        out.writeBytes("unknown connection");
        System.out.println("server: meta error");
      }
      

    } catch (SocketTimeoutException e) {
      System.out.println("read timeout. Closing connection");
      //Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, e);
    } catch (IOException ex) {
      Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        clientSocket.close();
      } catch (IOException ex) {
        Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        return;
      }
      App.reservations.decrementAndGet();
    }
    
    //after ending connection decrease upcoming connections counter
    
  }
}