/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
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

import javax.imageio.ImageIO;

/**
 *
 * @author Andrei
 */
public class FileWorker {
  private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
  private String result;

  private File getFile(File filePath, String id){
    String fileName = String.format("telemetry_data_%s.json", id);
    return new File(filePath, fileName);
  }
  
  public String getFileData(File filePath, String id) throws IOException, FileNotFoundException{
    File file = getFile(filePath, id);
    try (BufferedReader reader = new BufferedReader(new FileReader(file))){
      result = "[" + reader.lines().collect(Collectors.joining("\n")) + "]";
      return result;
    }
  }
  
  
  public String getAllFileData(File filePath) throws IOException, FileNotFoundException{
    JsonArray array = new JsonArray();
    for (File file : filePath.listFiles()){
      if (!file.isDirectory()){
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
          array.add(JsonParser.parseReader(reader));
        }
      }
    }
    return gson.toJson(array, JsonArray.class);
  }
  
  public void writeFileData(File filePath, String id, String json){
    File file = getFile(filePath, id);
    // synchronized (file) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
        writer.write(json);
      } catch (IOException ex) {
        Logger.getLogger(FileWorker.class.getName()).log(Level.SEVERE, null, ex);
      }
    // }
  }

  public byte[] jpgToByte(File img) throws IOException{
    BufferedImage bufferedImage = ImageIO.read(img);
    WritableRaster raster = bufferedImage.getRaster();
    DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
    return data.getData();
  }
}
