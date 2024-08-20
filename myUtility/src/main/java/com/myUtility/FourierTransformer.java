/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

import com.codepoetics.protonpack.StreamUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 *
 * @author Andrei
 */
public class FourierTransformer {

  public static ArrayList<Double[]> inverseDiscreteTransform(List<Double[]> fOmega){
    ArrayList<Double[]> fTime = new ArrayList<>();
    int size = fOmega.size();
    Double xRe;
    Double xIm;
    Double r;
    Double phi;
    
    //do the same but with streams?  
    for (int n = 0; n <= size; n++){
      xRe = 0.0;
      xIm = 0.0;
      
      for(int k = 0; k < size - 1; k++){
        r = Math.sqrt(Math.pow(fOmega.get(k)[0], 2) + Math.pow(fOmega.get(k)[1], 2));
        phi = Math.atan(fOmega.get(k)[1]/fOmega.get(k)[0]);
        
        xRe += r/size * Math.cos(2 * Math.PI * k * n / size + phi);
        xIm += r/size * Math.sin(2 * Math.PI * k * n / size + phi);
      }
      fTime.add(new Double[]{xRe, xIm});
    }
    return fTime;
  }
  
  //TODO DFT?
  //may need in future?
  
}
