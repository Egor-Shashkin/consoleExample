/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.telemetry.TelemetryMessage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Andrei
 */
public class FileWorker {
  private BufferedReader reader;
  private String result;
  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  
  public FileWorker() {
  }
  
  public String getFileData(File file) throws FileNotFoundException, IOException{
    synchronized (file) {
      reader = new BufferedReader(new FileReader(file));
      result = reader.lines().collect(Collectors.joining("\n"));
      reader.close();

    }
    return result;
  }
  
  
  //
  public String getAllFileData(File filePath) throws FileNotFoundException, IOException{
    JsonArray array = new JsonArray();
    for (File file : filePath.listFiles()){
      synchronized (file) {
        reader = new BufferedReader(new FileReader(file));
      }
      array.add(JsonParser.parseReader(reader));
      reader.close();
    }
    
    return gson.toJson(array, JsonArray.class);
  }
  
  public void writeFileData(File file, String json){
    synchronized (file) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
        writer.write(json);
      } catch (IOException ex) {
        Logger.getLogger(FileWorker.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
}
