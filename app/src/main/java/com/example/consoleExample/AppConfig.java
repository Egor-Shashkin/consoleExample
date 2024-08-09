/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import static com.example.consoleExample.App.jAct;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.my.CustomLib;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 *
 * @author Andrei
 */
public class AppConfig {
    @Expose
    @SerializedName("prop1") 
    private String prop1;
    @Expose
    @SerializedName("pArray")
    private ArrayList<String> prop2;
    @Expose
    @SerializedName("pArrayOfArrays")
    private ArrayList prop3;
    @Expose
    @SerializedName("pObject")
    private CustomLib pObj;
    
    
    
    public AppConfig() {
        prop2 = new ArrayList<String>();
        prop3 = new ArrayList<ArrayList>();
        pObj = new CustomLib();
    }
    
    

    public String getProp1() {
        return prop1;
    }

    public ArrayList<String> getProp2() {
        return prop2;
    }

    public Collection getProp3() {
        return prop3;
    }

    public CustomLib getpObj() {
        return pObj;
    }
    
    public void optionsExecution(Options options, String[] args){
      SimpleDateFormat sdf;
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd;
      HelpFormatter formatter = new HelpFormatter.Builder().get();
      formatter.printHelp("Command line syntax:", options);
      try {
          cmd = parser.parse(options, args);
          if (cmd.hasOption("r")){
            jAct.gsonCollectionRead(cmd.getOptionValue("r"));
          }
          if (cmd.hasOption("w")){
            jAct.gsonCollectionWrite(cmd.getOptionValue("w"));
          }

          if (cmd.hasOption("h")){
            formatter.printHelp("command line syntax", options);
          }

          if (cmd.hasOption("c")){
            String countryCode = cmd.getOptionValue("c");
            if (countryCode.equals("KKona")) {
              sdf = new SimpleDateFormat("MM.dd.yyyy");
            }
            else {
              sdf = new SimpleDateFormat("dd.MM.yyyy");
            }
          }
          else
            sdf = new SimpleDateFormat("dd.MM.yyyy");


          if(cmd.hasOption("t")) {
            System.out.printf("Date and time are: %s %n", Calendar.getInstance().getTime());
          }
      } catch (Exception ex) {
        Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        System.out.print("parse err");
      } 
    }
    
    public Options configureOptions(){
      Options options = jAct.ReadJsonOptions("C:\\Users\\Andrei\\Documents\\option.config.json");
      
      if (options != null){
        return options;
      }
      
        Option t = new Option("t", false, "display time");
      /*Option n = Option.builder("n").deprecated(DeprecatedAttributes.builder()
              .setDescription("Use '-t' instead").setForRemoval(true).setSince("now").get())
              .hasArg().desc("Old time option").build();*/
      Option wfile = Option.builder("w").longOpt("wfile").hasArg().desc("write to arg file <arg>").build();
      Option rfile = Option.builder("r").longOpt("rfile").hasArg().desc("print json file <arg>").build();
      Option c = Option.builder("c").longOpt("country").hasArg().desc("country date format").build();
      Option help = Option.builder("h").longOpt("getHelp").desc("view commands").build();

      options = new Options();
      options.addOption(wfile);
      options.addOption(rfile);
      //options.addOption(n);
      options.addOption(t);
      options.addOption(c);
      options.addOption(help);
      jAct.gsonCollectionWrite("C:\\Users\\Andrei\\Documents\\option.config.json", options);

      return options;
      }

}
