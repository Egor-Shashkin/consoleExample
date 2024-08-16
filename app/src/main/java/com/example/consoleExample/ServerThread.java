/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
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
  int timeout;
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
      timeout = 60000;
      fileName = "telemetry_data_default.json";
      filePath = new File("C:\\Users\\Andrei\\Documents\\ConsoleExampleDocs\\");
      App.reservations.incrementAndGet();
      
  }
  
  @Override
  public void run(){
    try {
      clientSocket.setSoTimeout(timeout);
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
        worker.writeFileData(file, json);
      }

      else if (meta[0].equals("get")){
        fileName = String.format("telemetry_data_%s.json", meta[1]);
        file = new File(filePath, fileName);
        System.out.println("server: sending values");
        out.writeObject(worker.getFileData(file));
        System.out.println("server: values sent");
      }
      
      else if (meta[0].equals("getAll")){
        
        out.writeObject(worker.getAllFileData(filePath));
      }

      else {
        out.writeBytes("unknown connection");
        System.out.println("server: meta error");
      }
      clientSocket.close();

    } catch (SocketTimeoutException ex) {
      System.out.printf("waiting socket input time exceed %s%nTerminating connection%n", TimeUnit.MILLISECONDS.toSeconds(timeout));
    } catch (IOException ex) {
      Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      try {
        out.close();
        in.close();
        clientSocket.close();
      } catch (IOException ex) {
        Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        return;
      }
      //after ending connection decrease upcoming connections counter
      App.reservations.decrementAndGet();
    }
    
  }
}
