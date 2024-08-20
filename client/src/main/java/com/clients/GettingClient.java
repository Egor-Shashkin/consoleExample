/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.clients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.my.SensorData;
import com.my.TelemetryMessage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import com.myUtility.*;
import java.util.HashMap;
import java.util.stream.Stream;

/**
 *
 * @author Andrei
 */
public class GettingClient {
  private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
   

  
  public static void main(String[] args){
    int port = 7777;
    int id = 5;
    long timeStamp;
    Socket clientSocket;
    PrintWriter out;
    ObjectInputStream in;
    String json;
    String deviceId;
    ArrayList<SensorData> data;
    TelemetryMessage message;
    final ArrayList<Double[]> fOmega;
    ArrayList<Double[]> fTime = null;
    HashMap<Double, Double> fTimeRe;
    FourierTransformer transform = new FourierTransformer();

    System.out.println("connecting to server");
    try {
      clientSocket = new Socket(InetAddress.getLocalHost(), port);

      out = new PrintWriter(clientSocket.getOutputStream());
      in = new ObjectInputStream(clientSocket.getInputStream());

      System.out.println("waiting for spare server thread");
      in.readObject();
      System.out.println("sending request");
      out.print(String.format("get %s%n", id));
      out.flush();
      System.out.println("getting json");
      json = (String) in.readObject();
      clientSocket.close();

      System.out.println("parsing json");
      JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();


      timeStamp = jsonObject.get("ts").getAsLong();
      deviceId = jsonObject.get("deviceId").getAsString();
      data = gson.fromJson(jsonObject.get("data").getAsJsonArray(), new TypeToken<List<SensorData>>(){}.getType());
      fOmega = (ArrayList<Double[]>) data.stream().map(SensorData::getValue).collect(Collectors.toList());
      message = new TelemetryMessage(timeStamp, deviceId, data);
      System.out.printf(" id: %s %n timeStamp: %s %n dataValues: %s%n%n",
                message.getDeviceId(), message.getTimeStamp(),
                message.processingSensorData(SensorData::getValue).stream()
                        .map(s -> Arrays.toString(s))
                        .collect(Collectors.toList()));
      in.close();
      out.close();

    
    
    fTimeRe = new HashMap<>();
    Stream.iterate(-20.0, i -> i + 0.1).limit(1000).forEach(x -> fTimeRe.put(x, transform.inverseFourierSeries(fOmega, x)));
    
    Plotter plot = new Plotter("Title", "Title", fTimeRe);
    plot.pack();
    plot.setVisible(true);
//    JFrame frame = new JFrame();
//    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    frame.add(new Plotter("title", "title", fTimeRe));
//    frame.setSize(400,400);
//    frame.setLocation(200, 200);
//    frame.setVisible(true);
    } catch(SocketException ex) {
      System.out.println("Could not send data");
    } catch (IOException ex) {
      Logger.getLogger(GettingClient.class.getName()).log(Level.SEVERE, null, ex);
    } catch (ClassNotFoundException ex) {
      Logger.getLogger(GettingClient.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}

