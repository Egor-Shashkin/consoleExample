/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.clients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.telemetry.SensorData;
import com.telemetry.TelemetryMessage;
import com.myUtility.Protocol;
import com.myUtility.ConnectionMode;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.myUtility.*;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Andrei
 */
public class GettingClient {
  private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
   

  
  public static void main(String[] args){
    int port = 7777;
    String id = "5";
    Socket clientSocket;
    String json;
    TelemetryMessage message;
    final ArrayList<Double[]> fOmega;
    HashMap<Double, Double> fTimeRe;
    FourierTransformer transform = new FourierTransformer();

    try {
      clientSocket = new Socket(InetAddress.getLocalHost(), port);
      Protocol protocol = new Protocol(ConnectionMode.GET, id);
      json = protocol.connect(clientSocket);
      System.out.println("parsing json");
      message = TelemetryParser.parseTelemetryJson(json, true);
      //fOmega = (ArrayList<Double[]>) message.processingSensorData(SensorData::getValue);
      fTimeRe = new HashMap<>();
      int range = 5;
      double step = 0.0005;
      fOmega = transform.FourierSeries("x", range);

      Stream.iterate((double) -range, i -> i + step).limit(Math.round(2*range/step)).forEach(x -> fTimeRe.put(x, transform.inverseFourierSeries(fOmega, x, range)));

      Plotter plot = new Plotter("Title", "Title", fTimeRe);
      plot.pack();
      plot.setVisible(true);
    } catch(SocketException ex) {
      System.out.println("Could not get data");
    } catch (IOException | ClassNotFoundException ex) {
      Logger.getLogger(GettingClient.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}

