/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.numbers.complex.Complex;

/**
 *
 * @author Andrei
 */
public class Client {
  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private static final Scanner scan = new Scanner(System.in);
  private static final Plotter plot = new Plotter("Title", "Title");
  private static final ExecutorService exec = Executors.newCachedThreadPool();

  private static int port;
  private static InetAddress ip;
  @SuppressWarnings("null")
  public static void main(String[] args) {
    String id;
    String mode;
    String[] input;
    Future<?> sender = null;
    boolean senderRegistered = false;
    List<TelemetryMessage> message =  new ArrayList<>();
    BlockingQueue<TelemetryMessage> queue = new LinkedBlockingQueue<>();
    Protocol protocol = new Protocol();
    //getting connection parameters
    while (true){
      System.out.println("enter port and ip for connection (default: 7777 localhost):\n");
      input = scan.nextLine().split(" ");
      try {
        ip = InetAddress.getByName(input[1]);
        port = Integer.parseInt(input[0]);
        if (checkConnection(protocol)) break;
      } catch (ArrayIndexOutOfBoundsException e) {
        try {
          System.out.println("assigning default values");
          port = 7777;
          ip = InetAddress.getLocalHost();
          if (checkConnection(protocol)) break;
        } catch (UnknownHostException ex) {
          Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
      } catch (UnknownHostException ex) {
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    //running connection
    while (true){
      System.out.printf("enter connection mode and id or register periodic sender:%n%s [id] %n or exit to stop%n", Arrays.asList(ConnectionMode.values()));
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
              message = get(protocol, id);
              plot(message);
            } catch (JsonSyntaxException e) {
              System.out.println("file corrupted");
            } catch (FileNotFoundException e) {
              System.out.println("file not found");
            }
          }
          case "SEND" -> send(protocol, id);
          case "SENDER" -> {
            if (!senderRegistered){
              for (int i = 0; i < 5; i++){
                exec.submit(new PeriodicSensor(queue, Integer.toString(i)));
              }
              sender = exec.submit(new PeriodicSender(queue, ip, port));
              senderRegistered = true;
            } else System.out.println("sender already registered");
          }
          case "EXIT" -> {
            if (senderRegistered) sender.cancel(true);
            exec.shutdown();

            exit(0);
          }
        }
      } catch (ConnectException ex) {
        System.out.println("Connection error");
//        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
      } catch (IOException ex) {
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }


  private static boolean checkConnection(Protocol protocol) {
    protocol.setMode("connectioncheck");
    try {
      return connect(protocol).equals(Protocol.CONTINUE);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }


  private static String connect(Protocol protocol) throws ConnectException, IOException{
    try {
      String response;
      Socket clientSocket;
      clientSocket = new Socket(ip, port);
      response = protocol.connect(clientSocket);
      return response;
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
      return null;
    }
  }
  
  
  private static void send(Protocol protocol, String id) throws ConnectException, IOException{
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
      // memory efficiency vs readablility?
      // Protocol protocol = new Protocol(ConnectionMode.SEND.name(), id, json);
      protocol.setMode(ConnectionMode.SEND.name());
      protocol.setId(id);
      protocol.setMessage(json);
      connect(protocol);
  }
  

  
  private static List<TelemetryMessage> get(Protocol protocol, String id) throws ConnectException, IOException{
    List<TelemetryMessage> array;
    String json;
    System.out.println("connecting to server");
    // Protocol protocol = new Protocol(ConnectionMode.GET.name(), id);
    protocol.setMode(ConnectionMode.GET.name());
    protocol.setId(id);
    protocol.setMessage("");
    json = connect(protocol);
    System.out.println("parsing json");
    array = TelemetryParser.parseTelemetryJson(json, true);
    return array;
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
