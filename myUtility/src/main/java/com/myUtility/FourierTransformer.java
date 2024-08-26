/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.myUtility;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.concurrent.GuardedBy;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.commons.numbers.complex.Complex;
/**
 *
 * @author Andrei
 */
public class FourierTransformer {
  private static ExecutorService exec = Executors.newCachedThreadPool();


  public HashMap<Double, Double> inverseDiscreteTransform(List<Complex> fOmega){
    HashMap<Double, Double> fTime = new HashMap<>();
    Complex xn = Complex.ZERO;
    Complex inverseTransformExp;
    int size = fOmega.size();
    
    //do the same but with streams?  
    for (int n = 0; n <= size; n++){

      for(int k = 0; k < size - 1; k++){
        inverseTransformExp = Complex.I.multiply(2 * Math.PI * k * n / size).exp();
        xn = xn.add(fOmega.get(k).multiply(inverseTransformExp).divide(size));
      }
      fTime.put((double) n, xn.real());
    }
    return fTime;
  }
  
  public ArrayList<Complex> discreteTransform(List<Double> fTime){
    int N = fTime.size();
    Complex p;
    Complex q;
    ArrayList<Complex> fOmega = (ArrayList<Complex>) Stream.generate(() -> Complex.ZERO).limit(N).collect(Collectors.toList());
    ArrayList<Complex> joinedList = new ArrayList<>();
    AtomicInteger counter = new AtomicInteger(0);
    Map<Boolean, List<Double>> collect = fTime.stream()
            .collect(Collectors.partitioningBy(e -> counter.getAndIncrement() < fTime.size() / 2,
            Collectors.toList()));
    if (collect.get(false).size() > 1){
      joinedList = (ArrayList<Complex>) Stream.concat(discreteTransform(collect.get(false)).stream(),
              discreteTransform(collect.get(true)).stream()).collect(Collectors.toList());
      for (int i = 0; i <= N/2-1; i++){
        p = joinedList.get(i);
        q = Complex.I.multiply(-2 * Math.PI * i / N ).exp().multiply(joinedList.get(i+N/2));
        fOmega.add(i, p.add(q));
        fOmega.add(i+N/2, p.subtract(q));
      }
    } else {
      fOmega.add(Complex.ZERO.add(fTime.get(0)));
      //fOmega.add(Complex.ZERO.add(fTime.get(1)));
    }
    
    return fOmega;
  }

  public Double inverseFourierSeries(ArrayList<Double[]> fOmega, Double x){
    return inverseFourierSeries(fOmega, x, Math.PI);
  }
    public Double inverseFourierSeries(ArrayList<Double[]> fOmega, Double x, double period){
    Double fTime;
    fTime = fOmega.get(0)[1];
    fTime += fOmega.stream().skip(1)
      .map(n -> { return (n[1] * Math.cos(Math.PI * n[0] * x / period) + n[2] * Math.sin(Math.PI * n[0] * x / period));})
      .collect(Collectors.summingDouble(Double::doubleValue));
    
    return fTime;
  }
    
  public List<Point2D> inverseFourierSeries(int startingPoint, int range, ArrayList<Double[]> fOmega){
    return inverseFourierSeries(startingPoint, range, fOmega, Math.PI);
  }
  public List<Point2D> inverseFourierSeries(int startingPoint, int range, ArrayList<Double[]> fOmega, double period){
    double step = 0.1;
    int nSteps = (int) (range/step);
    ArrayList<Point2D> fTime;
    
    fTime = (ArrayList<Point2D>) Stream.iterate(0, i -> i + 1)
            .limit(nSteps)
            .map(x -> {
              double abscissa = x*step + startingPoint;
              double ordinate = fOmega.stream()
                      .map(n -> { return (n[1] * Math.cos(Math.PI * n[0] * abscissa / period) + n[2] * Math.sin(Math.PI * n[0] * abscissa / period));})
                      .collect(Collectors.summingDouble(Double::doubleValue));
              Point2D point = new Point2D.Double(abscissa, ordinate);
              return point;
                      })
            .collect(Collectors.toList());
    
    return fTime;
  }
  
  public ArrayList<Double[]> FourierSeries(String equation, double range){
    //equation = "sin(x)";
    ArrayList<Double[]> fOmega = new ArrayList<>();
    ArrayList<Callable<Double[]>> tasks = new ArrayList<>();
    Expression e = new ExpressionBuilder(equation).variable("x").build();
    Integer i;
    Expression expressionA;
    Expression expressionB;
    double a0 = 2/range * integrate(e, range);
    fOmega.add(new Double[]{0.0, a0, 0.0});
    
    for (i = 1; i < 10; i++){
      tasks.add(new getNthFreqMultiplier(equation, range, i));
      //fOmega.add(new Double[]{i.doubleValue(), an, bn});
    }
    try {
    fOmega.addAll((ArrayList<Double[]>) exec.invokeAll(tasks).stream().map(n -> {
    try
    {
        return n.get();
    }
    catch (InterruptedException | ExecutionException ex)
    {
        throw new RuntimeException(ex);
    }
}).collect(Collectors.toList()));
    return fOmega;
    } catch (InterruptedException ex){
      System.out.println("integrating interrupted");
      return null;
    }
  }
  public ArrayList<Double[]> FourierSeries(String equation){
    return FourierSeries(equation, Math.PI);
  }

  
  double integrate(Expression expression, double range){
    double integralValue = 0.0;
    double step = 0.01;
    for (double x = -range; x <= range-step; x += step){
      integralValue += step * (expression.setVariable("x", x).evaluate() + expression.setVariable("x", x+step).evaluate())/2;
    }
    return integralValue;
  }
  
}

class getNthFreqMultiplier implements Callable<Double[]>{
  String equation;
  double range;
  int i;
  FourierTransformer t;
  Expression expressionA;
  Expression expressionB;

  public getNthFreqMultiplier(String equation, double range, int i) {
    this.equation = equation;
    this.range = range;
    this.i = i;
    t = new FourierTransformer();
    expressionA = new ExpressionBuilder(String.format("(%s) * cos(%s * %s * x / %s)", equation, Math.PI, i, range)).variable("x").build();
    expressionB = new ExpressionBuilder(String.format("(%s) * sin(%s * %s * x / %s)", equation, Math.PI, i, range)).variable("x").build();
  }

  @Override
  public Double[] call() throws Exception {

    double an  = t.integrate(expressionA, range)/range;
    double bn = t.integrate(expressionB, range)/range;
    return new Double[]{(double) i, an, bn};
  }
  
}