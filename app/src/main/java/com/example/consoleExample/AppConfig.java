/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import static com.example.consoleExample.App.jAct;
import java.util.Calendar;
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
  
    public void optionsExecution(Options options, String[] args){
      CommandLineParser parser = new DefaultParser();
      CommandLine cmd;
      HelpFormatter formatter = new HelpFormatter.Builder().get();
      //formatter.printHelp("Command line syntax:", options);
      try {
          cmd = parser.parse(options, args);
          if (cmd.hasOption("h")){
            formatter.printHelp("command line syntax", options);
          }

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

      Option help = Option.builder("h").longOpt("getHelp").desc("view commands").build();

      options = new Options();
      options.addOption(t);
      options.addOption(help);
      jAct.gsonCollectionWrite("C:\\Users\\Andrei\\Documents\\option.config.json", options);

      return options;
      }

}
