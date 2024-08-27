/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.clients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myUtility.ConnectionMode;
import com.myUtility.FourierTransformer;
import com.telemetry.TelemetryMessage;
import com.myUtility.Plotter;
import com.myUtility.Protocol;
import com.telemetry.SensorData;
import java.awt.geom.Point2D;
import java.io.IOException;
import static java.lang.System.exit;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.validator.routines.InetAddressValidator;

/**
 *
 * @author Andrei
 */
public class Client {
  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private static final Scanner scan = new Scanner(System.in);
  private static final InetAddressValidator ipValidator = new InetAddressValidator();
  private static Socket clientSocket;
  private static int port;
  private static InetAddress ip;
  private static String id;
  private static String json;
  private static String mode;
  private static String[] input;
  private static TelemetryMessage message;
  public static void main(String[] args) {
    //getting connection parameters
    while (true){
      System.out.println("enter port and ip for connection (default: 7777 localhost):\n");
      input = scan.nextLine().split(" ");
      try {
        if (ipValidator.isValid(input[1])){
          port = Integer.parseInt(input[0]);
          ip = InetAddress.getByName(input[1]);
          break;
        }
        else {
          System.out.println("invalid ip");
        }
      } catch (ArrayIndexOutOfBoundsException e) {
        try {
          System.out.println("assigning default values");
          port = 7777;
          ip = InetAddress.getLocalHost();
          break;
        } catch (UnknownHostException ex) {
          Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
      } catch (UnknownHostException ex) {
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    //running connection
    while (true){
      json = "";
      port = 7777;
      System.out.printf("enter connection mode and id (only for send and get):%n%s [id] %n or exit to stop%n", Arrays.asList(ConnectionMode.values()));
      input = scan.nextLine().split(" ");
      mode = input[0].toUpperCase();
      try {
        id = input[1];
      } catch (ArrayIndexOutOfBoundsException ex) {
        id = "default";
      }
      switch (mode) {
        case "GET":
          get();
          System.out.println("plot the response data? y/n \n default: n\n");
          if (scan.nextLine().equals("y")){
            System.out.println("enter: starting point, range of plotting, period of plotting function (default: -10, 20, 2pi)%n");
            input = scan.nextLine().split(" ");
            plot(input, message);
          }
          break;
        case "SEND":
          send();
          break;
        case "GETALL":
          getAll();
          break;
        case "EXIT":
          exit(0);
          break;
      }
    }
  }


  private static String connect(){
    String response = null;
    try {
      clientSocket = new Socket(ip, port);
      Protocol protocol = new Protocol(mode.toUpperCase(), id, json);
      response = protocol.connect(clientSocket);
      message.generatingSensorData();
    } catch (SocketException ex) {
      System.out.println("Connection error");
      Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
    } finally {
      return response;
    }
  }
  
  
  private static void send(){
      message = new TelemetryMessage(id);
      message.generatingSensorData();
      json = gson.toJson(message, TelemetryMessage.class);
      connect();
  }
  
  
  private static TelemetryMessage get(){
    System.out.println("connecting to server");
    json = connect();
    System.out.println("parsing json");
    message = TelemetryParser.parseTelemetryJson(json, true);
    return message;
  }
  
  private static List<TelemetryMessage> getAll(){
    ArrayList<TelemetryMessage> array;
    System.out.println("connecting to server");
    json = connect();
    System.out.println("parsing json");
    array = TelemetryParser.parseTelemetryJsons(json, true);
    return array;
  }
  
  
  private static void plot(String[] plotParams, TelemetryMessage message){
    int startingPoint;
    int range;
    double period;
    ArrayList<Double[]> fOmega;
    FourierTransformer transform = new FourierTransformer();
    ArrayList<Point2D> fTimeRe;
    
    try {
      startingPoint = Integer.parseInt(plotParams[0]);
      range = Integer.parseInt(plotParams[1]);
      try {
        period = Double.parseDouble(plotParams[2]);
      } catch (ArrayIndexOutOfBoundsException ex) {
        period = 2 * Math.PI;
      }
    } catch (Exception e) {
      System.out.println("plot parameters missing or corrupted\nassigning default values");
      startingPoint = -10;
      range = 20;
      period = 2 * Math.PI;
    }
    
    fOmega = (ArrayList<Double[]>) message.processingSensorData(SensorData::getValue);
    fTimeRe = (ArrayList<Point2D>) transform.inverseFourierSeries(startingPoint, range, fOmega, period);
    Plotter plot = new Plotter("Title", "Title", fTimeRe);
    plot.pack();
    plot.setVisible(true);

  }
}
