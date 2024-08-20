/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import static com.example.consoleExample.App.gson;
import static com.example.consoleExample.App.scan;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 *
 * @author Andrei
 */
public class JsonActions {
      public void gsonCollectionRead(String file){
        AppConfig config;
//        Collection collection = new ArrayList();
//        a.fillCollection(collection);
//        String json = gson.toJson(collection);
//        System.out.println("raw json: " + json);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            config = gson.fromJson(reader, AppConfig.class);
            
            System.out.printf("String prop1: %s, %n List prop2: %s, %n Array^2: %s, %n ",
                    config.getProp1(), config.getProp2(), config.getProp3());
            
        }
        catch (IOException |JsonIOException |JsonSyntaxException e){
            System.out.println("Something went wrong while reading");
        }
        
    }
      
      
    public Options ReadJsonOptions(String file){
      Options options = new Options();
      String shortName;
      String longName;
      String desc;
      boolean hasArg;
      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
        for (JsonElement m : array){
        shortName = m.getAsJsonObject().get("short").getAsString();
        longName = m.getAsJsonObject().get("long").getAsString();
        desc = m.getAsJsonObject().get("desc").getAsString();
        hasArg = m.getAsJsonObject().get("hasArg").getAsBoolean();

        options.addOption(shortName, longName, hasArg, desc);
        }
        return options;
      } catch (Exception e) {
        System.out.println("something went wrong while reading:\n" + e);
        return null;
      }
    }
    
    
    public void gsonCollectionWrite(String file){
        Collection<String> collection = new ArrayList<>();
        String json;
        System.out.print("input collection item or -1 to exit: ");
        String input = scan.nextLine();
        while(!input.equals("-1")){
            collection.add(input);
            System.out.print("input: ");
            input = scan.nextLine();
        }
        json = gson.toJson(collection);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            writer.write(json);
            writer.newLine();
            System.out.printf("successfully wrote to %s.json json file:%n %s %n", file, json);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
            
        }
    }
    
    public void gsonCollectionWrite(String file, String json){
        Collection<String> collection = new ArrayList<>();
        json = gson.toJson(collection);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            writer.write(json);
            writer.newLine();
            System.out.printf("successfully wrote to %s.json json file:%n %s %n", file, json);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
            
        }
    }
    
      public void gsonCollectionWrite(String file, Options options){



        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
          JsonArray array = new JsonArray();
          for (Option option : options.getOptions()){
          JsonObject pOption = new JsonObject();
          pOption.add("short", gson.toJsonTree(option.getOpt()));
          pOption.add("long", gson.toJsonTree(option.getLongOpt()));
          pOption.add("desc", gson.toJsonTree(option.getDescription()));
          pOption.add("hasArg", gson.toJsonTree(option.hasArg()));
          array.add(pOption);
          }
          writer.write(gson.toJson(array));

        } catch (Exception e) {
          System.out.println("something went wrong while writing \n" + e);
        }
        
    }
        
    
    
}
