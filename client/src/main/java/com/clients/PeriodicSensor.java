package com.clients;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.telemetry.TelemetryMessage;

public class PeriodicSensor implements Runnable {
  private static final int SLEEP_TIME = 20;
  private BlockingQueue<TelemetryMessage> queue = new LinkedBlockingQueue<>();
  private String id;
  private TelemetryMessage message;
  
  public PeriodicSensor(BlockingQueue<TelemetryMessage> queue, String id) {
    this.queue = queue;
    this.id = id;
    // protocol = new Protocol(ConnectionMode.SEND.name(), id);  
  }


  @Override
  public void run() {
    while (true) {
      message = new TelemetryMessage(id);
      message.generatingSensorData();
      while (true) {
        try {
          queue.add(message);
          break;
        } catch (Exception e) {
          try {
            TimeUnit.SECONDS.sleep(SLEEP_TIME);
          } catch (InterruptedException e1) {
            e1.printStackTrace();
          }
        }
      }
      try {
        TimeUnit.SECONDS.sleep(SLEEP_TIME);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

}
