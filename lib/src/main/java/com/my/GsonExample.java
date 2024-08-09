/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.my;

import com.google.gson.Gson;
import java.util.Collection;
import java.util.Scanner;

/**
 *
 * @author Andrei
 */
@SuppressWarnings("unchecked")
public class GsonExample {
    Scanner scan = new Scanner(System.in);
    CustomLib lib = new CustomLib();
    Gson gson = new Gson();
    private String S = gson.toJson(lib.getValues());
    
    public String getID() {
        return gson.toJson(lib.getIdentifier());
    }
    
    public String getArray() {
        return S;
    }
    
        static class Event {
        private String name;
        private String source;
        

        private Event(String full_name){
            String[] S = full_name.split(" ");
            this.name = S[0];
            this.source = S[1];
        }
        
        @Override
        public String toString() {
            return String.format("(name=%s, source=%s)", name, source);
        }
    }
    
    
    public void fillCollection(Collection collection){
        
        collection.add("hi");
        collection.add(5);
        for (int i = 0; i<2; i++){
            collection.add(new Event(scan.nextLine()));
        }
    }
    
}
