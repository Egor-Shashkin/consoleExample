/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.clients;

import com.telemetry.TelemetryParser;
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
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
  private static final Plotter plot = new Plotter("Title", "Title");
  private static final ExecutorService exec = Executors.newCachedThreadPool();

  private static int port;
  private static InetAddress ip;
  //TODO is in notepad++
  public static void main(String[] args) {
    TelemetryMessage message;
    List<TelemetryMessage> array;
    String id;
    String mode;
    String[] input;
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
      System.out.printf("enter connection mode and id (only for send and get):%n%s [id] %n or exit to stop%n", Arrays.asList(ConnectionMode.values()));
      input = scan.nextLine().split(" ");
      mode = input[0].toUpperCase();
      try {
        id = input[1];
      } catch (ArrayIndexOutOfBoundsException ex) {
        id = "default";
      }
      try {
        switch (mode) {
          case "GET" -> {
            try {
              message = get(id);
              plot(message);
            } catch (JsonSyntaxException e) {
              System.out.println("file not found or corrupted");
            }
          }
          case "SEND" -> send(id);
          case "GETALL" -> {
            array = getAll();
            plot(array);
          }
          case "EXIT" -> exit(0);
        }
      } catch (ConnectException ex) {
        System.out.println("Connection error");
//        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }


  private static String connect(Protocol protocol) throws ConnectException, IOException{
    try {
      String response;
      Socket clientSocket;
      clientSocket = new Socket(ip, port);
      response = protocol.connect(clientSocket);
//      message.generatingSensorData();
      return response;
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }
  }
  
  
  private static void send(String id) throws ConnectException, IOException{
      TelemetryMessage message = new TelemetryMessage(id);
      String type;
      String json;
      
      System.out.println("select type of data: FS (fourier series)/ FFT");
      type = scan.nextLine().toUpperCase();
    switch (type) {
      case "FS" -> message.generatingSensorData();
      case "FFT" -> message.generatingfftData();
      default -> {
        System.out.println("unknown data type. cancelling sending operation");
        return;
      }
    }
      json = gson.toJson(message, TelemetryMessage.class);
      Protocol protocol = new Protocol(ConnectionMode.SEND.name(), id, json);
      connect(protocol);
  }
  
  
  private static TelemetryMessage get(String id) throws ConnectException, IOException{
    TelemetryMessage message;
    String json;
    System.out.println("connecting to server");
    Protocol protocol = new Protocol(ConnectionMode.GET.name(), id);
    json = connect(protocol);
    System.out.println("parsing json");
    if (json != null)
      message = TelemetryParser.parseTelemetryJson(json, true);
    else
      message = TelemetryMessage.getDefaultMessage();
    return message;
  }
  
  private static List<TelemetryMessage> getAll() throws ConnectException, IOException{
    List<TelemetryMessage> array;
    String json;
    System.out.println("connecting to server");
    Protocol protocol = new Protocol(ConnectionMode.GETALL.name());
    json = connect(protocol);
    System.out.println("parsing json");
    array = TelemetryParser.parseTelemetryJsons(json, true);
    return array;
  }
  
  
  private static void plot(TelemetryMessage message){
    List<TelemetryMessage> messages = new ArrayList<>();
    messages.add(message);
    plot(messages);
  }
  private static void plot(List<TelemetryMessage> messages){
    int range;
    System.out.println("plot the response data? y/n \n default: n\n");
    if (scan.nextLine().equals("y")){
        range = messages.get(0).getRange();
      plot.clearPlot();
      for (TelemetryMessage msg : messages) {
        exec.execute(new MessagePlot(msg, range));
      }
      plot.pack();
      plot.setVisible(true);
    }
  }
  
  private static class MessagePlot implements Runnable{
    int range;
    double period;
    TelemetryMessage msg;
    List<Double[]> fOmega;
    List<Point2D> fTimeRe;

    public MessagePlot(TelemetryMessage msg, int range, double period) {
      this.msg = msg;
      this.range = range;
      this.period = period;
    }
    
    public MessagePlot(TelemetryMessage msg, int range){
      this.msg = msg;
      this.range = range;
      period = 2 * range;
    }
    
    @Override
    public void run(){
      try {
        fOmega = msg.processingSensorData(SensorData::getValue);
        if (fOmega.get(0).length == 3) {
          fTimeRe = FourierTransformer.inverseFourierSeries(range, fOmega, period);
        } else {
          Complex[] array = fOmega.stream().map(n -> Complex.ofCartesian(n[0], n[1])).toArray(size -> new Complex[size]);
          fTimeRe = FourierTransformer.getFuncPoints(FourierTransformer.ifft(array), range, array.length);
        }
        try {
          plot.addSeries(fTimeRe, msg.getDeviceId());
        } catch (IllegalArgumentException ex) {
          plot.addSeries(fTimeRe);
        }
      } catch (ArrayIndexOutOfBoundsException ex) {
        System.err.println("unknown plotting data format");
      }
    }
  }
}
