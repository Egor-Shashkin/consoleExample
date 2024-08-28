/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
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
  private final String NOFILE;
  
  public FileWorker() {
    NOFILE = "no such file";
  }
  
  public String getFileData(File file) throws IOException{
    try (BufferedReader reader = new BufferedReader(new FileReader(file))){
      result = reader.lines().collect(Collectors.joining("\n"));
      return result;
    } catch (FileNotFoundException ex) {
      return NOFILE;
    }
  }
  
  
  //
  public String getAllFileData(File filePath) throws IOException{
    JsonArray array = new JsonArray();
    for (File file : filePath.listFiles()){
      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        array.add(JsonParser.parseReader(reader));
      } catch (FileNotFoundException ex) {
        return NOFILE;
      }
    }
    
    return gson.toJson(array, JsonArray.class);
  }
  
  public void writeFileData(File file, String json){
    synchronized (file) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
        writer.write(json);
      } catch (IOException ex) {
        Logger.getLogger(FileWorker.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
}
