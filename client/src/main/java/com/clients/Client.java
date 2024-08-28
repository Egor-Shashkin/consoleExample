/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.clients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.myUtility.ConnectionMode;
import com.myUtility.FourierTransformer;
import com.telemetry.TelemetryMessage;
import com.myUtility.Plotter;
import com.myUtility.Protocol;
import com.telemetry.SensorData;
import java.awt.geom.Point2D;
import java.io.IOException;
import static java.lang.System.exit;
import java.net.ConnectException;
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
import org.apache.commons.numbers.complex.Complex;
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
      System.out.printf("enter connection mode and id (only for send and get):%n%s [id] (default id: default) %n or exit to stop%n", Arrays.asList(ConnectionMode.values()));
      input = scan.nextLine().split(" ");
      mode = input[0].toUpperCase();
      try {
        id = input[1];
      } catch (ArrayIndexOutOfBoundsException ex) {
        id = "default";
      }
      try {
        switch (mode) {
          case "GET":
            try {
              get();
              System.out.println("plot the response data? y/n \n default: n\n");
              if (scan.nextLine().equals("y")){
                plot(input, message);
              }
            } catch (JsonSyntaxException e) {
              System.out.println("file not found or corrupted");
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
      } catch (ConnectException ex) {
        System.out.println("Connection error");
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }


  private static String connect() throws ConnectException, IOException{
    try {
      String response = null;
      clientSocket = new Socket(ip, port);
      Protocol protocol = new Protocol(mode.toUpperCase(), id, json);
      response = protocol.connect(clientSocket);
//      message.generatingSensorData();
      return response;
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }
  }
  
  
  private static void send() throws ConnectException, IOException{
      message = new TelemetryMessage(id);
      System.out.println("select type of data:\n FS (fourier series)/ FFT");
      if (scan.nextLine().toUpperCase().equals("FS"))
        message.generatingSensorData();
      else
        message.generatingfftData();
      json = gson.toJson(message, TelemetryMessage.class);
      connect();
  }
  
  
  private static TelemetryMessage get() throws ConnectException, IOException{
    System.out.println("connecting to server");
    json = connect();
    System.out.println("parsing json");
    if (json != null)
      message = TelemetryParser.parseTelemetryJson(json, true);
    else
      message = TelemetryMessage.getDefaultMessage();
    return message;
  }
  
  private static List<TelemetryMessage> getAll() throws ConnectException, IOException{
    ArrayList<TelemetryMessage> array;
    System.out.println("connecting to server");
    json = connect();
    System.out.println("parsing json");
    array = TelemetryParser.parseTelemetryJsons(json, true);
    return array;
  }
  
  
  private static void plot(String[] plotParams, TelemetryMessage message){
    int range;
    double period;
    ArrayList<Double[]> fOmega;
    ArrayList<Point2D> fTimeRe;
    System.out.println("enter: range of plotting, period of plotting function (default: 10, 20)");
    input = scan.nextLine().split(" ");
    try {
      range = 2 * Integer.parseInt(plotParams[0]);
      period = Double.parseDouble(plotParams[1]);
    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
      System.out.println("plot parameters missing or corrupted\nassigning default values");
      range = 20;
      period = range;
    }
    
    fOmega = (ArrayList<Double[]>) message.processingSensorData(SensorData::getValue);
    if (fOmega.get(0).length == 3) {
    fTimeRe = (ArrayList<Point2D>) FourierTransformer.inverseFourierSeries(range, fOmega, period);
    } else {
      Complex[] array = fOmega.stream().map(n -> Complex.ofCartesian(n[0], n[1])).toArray(size -> new Complex[size]);
      fTimeRe = (ArrayList<Point2D>) FourierTransformer.getFuncPoints(FourierTransformer.ifft(array), range);
    }
    Plotter plot = new Plotter("Title", "Title", fTimeRe);
    plot.pack();
    plot.setVisible(true);

  }
}
