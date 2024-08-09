/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.*;

/**
 *
 * @author Andrei
 */
public class StreamPractice {
  private Stream<Integer> myStream;
  int counter;
  List<Integer> myList;
  public StreamPractice() {
    counter = 0;
    myList = Arrays.asList( 1,5,7,2,3,6,2,6,2,4);
    myStream = myList.stream().distinct();
    List<String> result = Stream.generate(() -> "a").limit(200)              // Flatten the list of lists into a single stream
            .map(String::toUpperCase)            // Transform each element to uppercase
            .sorted()                            // Sort elements
            .collect(Collectors.toList());
//    Double avg = 
    List<String> result2 = Stream.generate(() -> (Math.round(Math.random()*10)))
            .limit(100)
            .map((n) -> {System.out.println(String.format("map1 - %s", n)); return n.doubleValue();})
            .map((n) -> {System.out.println(String.format("map2 - %s", n)); return n.toString();})
            .collect(Collectors.toList());
    
    
    System.out.println(result2);
    
  }
  public int count(){
    return ++counter;
  }

  public Stream<Integer> getMyStream() {
    return myStream;
  }

  
  
  
}
