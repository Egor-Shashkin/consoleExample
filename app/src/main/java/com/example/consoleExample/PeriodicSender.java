package com.example.consoleExample;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PeriodicSender implements Runnable {
  private static final int SOCKET_TIMEOUT = 10000;
  private static final int DEFAULT_TIMEOUT = 10000;
  private static final double TIMEOUT_INCREASE = 0.6;
  private static final int MAX_TIMEOUT = 60000;
  private static final FileWorker worker = new FileWorker();
  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private static final File FILE_PATH = new File("C:\\Users\\TOPKEK\\Desktop\\ConsoleFiles\\ClientBackups");
  private BlockingQueue<TelemetryMessage> queue = new LinkedBlockingQueue<>();
  private String response;
  private TelemetryMessage telemetry;
  private String json;
  private InetAddress ip;
  private int port; 
  private Protocol protocol;
  private Socket socket;
  private int timeout;
  public PeriodicSender(BlockingQueue<TelemetryMessage> queue, InetAddress ip, int port) {
    this.queue = queue;
    this.ip = ip;
    this.port = port;
    protocol = new Protocol(ConnectionMode.SEND.name());
  }
  @Override
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        timeout = DEFAULT_TIMEOUT;
        telemetry = queue.take();
        json = gson.toJson(telemetry, TelemetryMessage.class);  
        protocol.setId(telemetry.getDeviceId());
        protocol.setMessage(json);    
        tryConnect(protocol);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
    backup();
  }

  private String tryConnect(Protocol protocol) throws InterruptedException{
    while (true) {
      try {
        socket = new Socket(ip, port);
        socket.setSoTimeout(SOCKET_TIMEOUT);
        response = protocol.connect(socket);
        if (response.equals(Protocol.OK)){
            return response;
        }
        throw new IOException();
      } catch (IOException | ClassNotFoundException e) {
        System.out.printf("sending error. next try in %s seconds %n", TimeUnit.MILLISECONDS.toSeconds(timeout));
        TimeUnit.MILLISECONDS.sleep(timeout);
        timeout = (int) Math.round(timeout * (1 + TIMEOUT_INCREASE));
        timeout = timeout > MAX_TIMEOUT ? MAX_TIMEOUT : timeout;
      }
    }
  }

  private void backup(){
    TelemetryMessage msg;
    while (!queue.isEmpty()){
      msg = queue.poll();
      worker.writeFileData(FILE_PATH, msg.getDeviceId(), gson.toJson(msg, TelemetryMessage.class));
    }
  }
}
