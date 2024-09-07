/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import com.myUtility.ConnectionMode;
import com.myUtility.FileWorker;
import com.myUtility.Protocol;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Andrei
 */
public class ServerThread implements Runnable{

  private Socket clientSocket;
  private ObjectOutputStream out;
  private ObjectInputStream in;
  private int timeout;
  private File filePath;
  private String[] meta;
  private FileWorker worker;
  private String output;
  private String[] input;
  private String id;  

  public ServerThread(Socket clientSocket, FileWorker worker) throws IOException {
      this.clientSocket = clientSocket;
      this.worker = worker;
      timeout = 60000;
      filePath = new File("C:\\Users\\TOPKEK\\Desktop\\ConsoleFiles\\ServerFiles");
      App.reservations.incrementAndGet();
      
  }
  
  @Override
  public void run(){
    try {
      clientSocket.setSoTimeout(timeout);
      out = new ObjectOutputStream(clientSocket.getOutputStream());
      in = new ObjectInputStream(clientSocket.getInputStream());

      
      out.writeObject("ready");
      out.flush();
      
      //reading client input, first line is metadata "request id"
      System.out.printf("T%s: server: reading input %n", Thread.currentThread().getId());
      input = ((String) in.readObject()).split("\n", 2);
      meta = input[0].split(" ");
      id = meta[1].strip();
      
      //choosing action according to command
      if (meta[0].equals(ConnectionMode.SEND.name())){
        System.out.printf("T%s: server: getting values from %s %n", Thread.currentThread().getId(), id);
        String json = input[1];
        worker.writeFileData(filePath, id, json);
        out.writeObject(Protocol.OK);
      }

      else if (meta[0].equals(ConnectionMode.GET.name())){
        try {
          if (id.toUpperCase().equals("ALL")) {
            output = worker.getAllFileData(filePath);
          } else {
            System.out.println("server: sending values");
            output = worker.getFileData(filePath, id);
          }
        } catch (FileNotFoundException e) {
          output = Protocol.NOT_FOUND;
        }
        out.writeObject(output);
        System.out.println("server: values sent");
      }
      

      else {
        out.writeBytes("unknown connection");
        System.out.println("server: meta error");
      }
      //clientSocket.close();

    } catch (SocketTimeoutException ex) {
      System.out.printf("waiting socket input time exceed %s%nTerminating connection%n", TimeUnit.MILLISECONDS.toSeconds(timeout));
    } catch (IOException | ClassNotFoundException ex) {
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
