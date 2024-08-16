/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import com.google.errorprone.annotations.concurrent.GuardedBy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Andrei
 */
public class FileWorker {
  @GuardedBy("this")
  private File file;
  private BufferedReader reader;
  private String result;
  private Gson gson;
  public FileWorker() {
    gson = new GsonBuilder().setPrettyPrinting().create();
  }
  
  public String getFileData(File file) throws FileNotFoundException, IOException{
    result = null;
    reader = null;

    synchronized (this) {
      reader = new BufferedReader(new FileReader(file));
    }
    if (reader == null) return null;
    result = reader.lines().collect(Collectors.joining("\n")); 
    reader.close();
    return result;
  }
  
  public String getAllFileData(File filePath) throws FileNotFoundException, IOException{
    JsonArray array = new JsonArray();
    for (File currentFile : filePath.listFiles()){
      synchronized (this) {
        reader = new BufferedReader(new FileReader(currentFile));
      }
      array.add(JsonParser.parseReader(reader));
      reader.close();
    }
    result = gson.toJson(array);
    return result;
  }
  
  public void writeMessage(String json, File file){
    synchronized (this) {
      BufferedWriter writer = null;
      try {
        writer = new BufferedWriter(new FileWriter(file));
        writer.write(json);
        writer.close();
      } catch (IOException ex) {
        Logger.getLogger(FileWorker.class.getName()).log(Level.SEVERE, null, ex);
      } finally {
        try {
          writer.close();
        } catch (IOException ex) {
          Logger.getLogger(FileWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }
  }
}
