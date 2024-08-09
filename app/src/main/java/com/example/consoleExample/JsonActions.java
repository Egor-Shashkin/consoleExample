/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import static com.example.consoleExample.App.gson;
import static com.example.consoleExample.App.scan;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.ProcessBuilder.Redirect.Type;
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
            
            System.out.printf("String prop1: %s, %n List prop2: %s, %n Array^2: %s, %n Object pObj: %n \t id: %s %n \t values: %s %n",
                    config.getProp1(), config.getProp2(), config.getProp3(), config.getpObj().getIdentifier(),config.getpObj().getValues());
            
        }
        catch (IOException |JsonIOException |JsonSyntaxException e){
            System.out.println("Something went wrong while reading");
        }
        
    }
      
      
    public Options ReadJsonOptions(String file){
      Options options = new Options();
      JsonParser parser = new JsonParser();
      String shortName;
      String longName;
      String desc;
      boolean hasArg;
      try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        JsonArray array = parser.parse(reader).getAsJsonArray();
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
    
    
    @SuppressWarnings("unchecked")
    public void gsonCollectionWrite(String file){
        Collection collection = new ArrayList();
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
    
    @SuppressWarnings("unchecked")
    public void gsonCollectionWrite(String file, String json){
        Collection collection = new ArrayList();
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
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Options.class, new OptionsAdapter());
        builder.registerTypeAdapter(Option.class, new OptionSerializer());
        builder.registerTypeAdapter(Options.class, new OptionsSerializer());



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
        
    
   public class OptionsAdapter extends TypeAdapter<Options> {
     JsonParser parser;

    public OptionsAdapter() {
      this.parser = new JsonParser();
    }
     @Override
     public Options read(JsonReader reader) throws IOException {
       if (reader.peek() == JsonToken.NULL) {
         reader.nextNull();
         return null;
       }
       Options options = new Options();
       String s = reader.nextString();
       JsonArray array = parser.parseString(s).getAsJsonArray();
       Option option;
       String shortname;
       String longname;
       boolean hasArg;
       String desc;
       for (JsonElement m : array){
         shortname = m.getAsJsonObject().get("short").getAsString();
         longname = m.getAsJsonObject().get("long").getAsString();
         hasArg = m.getAsJsonObject().get("hasArgs").getAsBoolean();
         desc = m.getAsJsonObject().get("desc").getAsString();
         options.addOption(shortname, longname, hasArg, desc);
       }
       
       return options;
     }
     @Override
     public void write(JsonWriter writer, Options options) throws IOException {
      if (options == null) {
        writer.nullValue();
        return;
      } 

      writer.beginArray();
      for (Option m : options.getOptions()){
        writer.beginObject();
        writer.name("short").value(m.getOpt());
        writer.name("long").value(m.getLongOpt());
        writer.name("desc").value(m.getDescription());
        writer.name("hasArgs").value(m.hasArg());
        writer.endObject();
      }
      writer.endArray();
     }
   }
    
  
    private class OptionsSerializer implements JsonSerializer<Options> {
        
    @Override
        public JsonElement serialize(Options t, java.lang.reflect.Type type, JsonSerializationContext jsc) {
          JsonArray result = new JsonArray();
          for (Option m : t.getOptions()){
          result.add(gson.toJson(m));
          }
          
          return new JsonPrimitive (gson.toJson(result));
      }
    }
    private class OptionSerializer implements JsonSerializer<Option> {

      @Override
      public JsonElement serialize(Option t, java.lang.reflect.Type type, JsonSerializationContext jsc) {
        JsonObject result = new JsonObject();
        result.add("short", gson.toJsonTree(t.getOpt()));
        result.add("long", gson.toJsonTree(t.getLongOpt()));
        result.add("desc", gson.toJsonTree(t.getDescription()));
        result.add("hasArg", gson.toJsonTree(t.hasArg()));

        return result;
      }
    }

    
//    private class OptionsDeserializer implements JsonDeserializer<Options>{
//
//    @Override
//    public Options deserialize(JsonElement je, java.lang.reflect.Type type, JsonDeserializationContext jdc) throws JsonParseException {
//      Options result = new Options();
//      Option m;
//      
//      for (JsonElement jsonElement : je.getAsJsonArray()) {
//        m = jsonElement;
//        
//      }
//      return result;
//    }
//      
//    }
    
//    private class OptionSerializer implements JsonSerializer<Option>{
//
//      @Override
//      public JsonElement serialize(Option t, java.lang.reflect.Type type, JsonSerializationContext jsc) {
//        t.get
//        
//      }
//    }
//    
//    private class OptionDeserializer implements JsonDeserializer<Option>{
//
//    @Override
//    public Option deserialize(JsonElement je, java.lang.reflect.Type type, JsonDeserializationContext jdc) throws JsonParseException {
//      Option option;
//      JsonPrimitive m;
//      
//      for (JsonElement jsonElement : je.getAsJsonArray()) {
//        
//      }
//      
//      return option;
//    }
//  
//    }
    
    
    
    

  public JsonActions() {
  }
    
}
