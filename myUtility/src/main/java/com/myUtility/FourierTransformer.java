/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.myUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
/**
 *
 * @author Andrei
 */
public class FourierTransformer {
  
  private Double xRe;
  private Double xIm;
  private Double r;
  private Double phi;
  private Double tg;

  public ArrayList<Double[]> inverseDiscreteTransform(List<Double[]> fOmega){
    ArrayList<Double[]> fTime = new ArrayList<>();
    int size = fOmega.size();
    
    //do the same but with streams?  
    for (int n = 0; n <= size; n++){
      xRe = 0.0;
      xIm = 0.0;
      tg = 0.0;
      
      for(int k = 0; k < size - 1; k++){
        r = Math.sqrt(Math.pow(fOmega.get(k)[0], 2) + Math.pow(fOmega.get(k)[1], 2));
        try {
          tg = fOmega.get(k)[1]/fOmega.get(k)[0];
          if (Double.isNaN(tg)) phi = 0.0;
          else phi = Math.atan(tg);
        } catch (ArithmeticException e) {
          phi = 0.0;
        }
        
        xRe += r/size * Math.cos(2 * Math.PI * k * n / size + phi);
        xIm += r/size * Math.sin(2 * Math.PI * k * n / size + phi);
      }
      fTime.add(new Double[]{xRe, xIm});
    }
    return fTime;
  }
  
  //TODO DFT?
  //may need in future?
  

  public Double inverseFourierSeries(ArrayList<Double[]> fOmega, Double x){
    Double fTime;
    fTime = fOmega.get(0)[0];
    fTime += fOmega.stream().skip(1)
      .map(n -> { return n[1] * Math.cos(n[0] * x) + n[2] * Math.sin(n[0] * x);})
      .collect(Collectors.summingDouble(Double::doubleValue));
    
    return fTime;
  }
}
