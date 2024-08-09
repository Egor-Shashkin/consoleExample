package com.my;

import java.util.ArrayList;
import java.util.Calendar;

public class CustomLib implements SomeInterface{
    
    private String identifier;
    private ArrayList values;
    
    public CustomLib() {
        identifier = String.format("I'm a String from a lib - %s", Calendar.getInstance().getTime());
        values = new ArrayList<Integer>();
        
    }

    public String getIdentifier() {
        return identifier;
    }

    public ArrayList getValues() {
        return values;
    }
    
    
    
    @Override
    public void interfaceusage(){
        System.out.printf("Method I had to make because of interface with %s", IString);
    }
    
    
    
}

interface SomeInterface{
    public String IString = "Some text";
    
    public void interfaceusage();
}