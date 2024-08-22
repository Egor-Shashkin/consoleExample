/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.myUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
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
    return inverseFourierSeries(fOmega, x, Math.PI);
  }
    public Double inverseFourierSeries(ArrayList<Double[]> fOmega, Double x, double range){
    Double fTime;
    fTime = fOmega.get(0)[1];
    fTime += fOmega.stream().skip(1)
      .map(n -> { return (n[1] * Math.cos(Math.PI * n[0] * x / range) + n[2] * Math.sin(Math.PI * n[0] * x / range));})
      .collect(Collectors.summingDouble(Double::doubleValue));
    
    return fTime;
  }
  
  public ArrayList<Double[]> FourierSeries(String equation, double range){
    //equation = "sin(x)";
    ArrayList<Double[]> fOmega = new ArrayList<>();
    Expression expression = new ExpressionBuilder(equation).variable("x").build();
    double an;
    double bn;
    double a0 = 2/range * integrate(expression, range);
    fOmega.add(new Double[]{0.0, a0, 0.0});
    for (int i = 1; i < 10; i++){
      expression = new ExpressionBuilder(String.format("(%s) * cos(%s * %s * x / %s)", equation, Math.PI, i, range)).variable("x").build();
      an  = integrate(expression, range)/range;
      expression = new ExpressionBuilder(String.format("(%s) * sin(%s * %s * x / %s)", equation, Math.PI, i, range)).variable("x").build();
      bn = integrate(expression, range)/range;
      fOmega.add(new Double[]{(double) i, an, bn});
    }
    return fOmega;
  }
  public ArrayList<Double[]> FourierSeries(String equation){
    return FourierSeries(equation, Math.PI);
  }

  
  private double integrate(Expression expression, double range){
    double integralValue = 0.0;
    double step = 0.1;
    for (double x = -range; x <= range-step; x += step){
      integralValue += step * (expression.setVariable("x", x).evaluate() + expression.setVariable("x", x+step).evaluate())/2;
    }
    return integralValue;
  }
  
}
