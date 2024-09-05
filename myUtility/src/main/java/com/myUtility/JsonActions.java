/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.myUtility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 *
 * @author Andrei
 */
public class JsonActions {
  private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
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

    
    public void gsonCollectionWrite(String file, String json){
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
