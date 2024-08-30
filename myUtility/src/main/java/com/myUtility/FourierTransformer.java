/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.myUtility;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.apache.commons.numbers.complex.Complex;
/**
 *
 * @author Andrei
 */
public class FourierTransformer {
  private static final int NDOTS = 1024;

  private static final ExecutorService exec = Executors.newCachedThreadPool();
   public static Complex[] fft(Complex[] x) {
        int n = x.length;

        // base case
        if (n == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (n % 2 != 0) {
            throw new IllegalArgumentException("n is not a power of 2");
        }

        // compute FFT of even terms
        Complex[] even = new Complex[n/2];
        for (int k = 0; k < n/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] evenFFT = fft(even);

        // compute FFT of odd terms
        Complex[] odd  = even;  // reuse the array (to avoid n log n space)
        for (int k = 0; k < n/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] oddFFT = fft(odd);

        // combine
        Complex[] y = new Complex[n];
//        for (int k = 0; k < n/2; k++) {
//            double kth = -2 * k * Math.PI / n;
//            Complex wk = Complex.ofCartesian(Math.cos(kth), Math.sin(kth));
//            y[k]       = evenFFT[k].add(wk.multiply(oddFFT[k]));
//            y[k + n/2] = evenFFT[k].subtract(wk.multiply(oddFFT[k]));
//        }
        IntStream.range(0, n/2).parallel().forEach(k -> {
          double kth = -2 * k * Math.PI / n;
          Complex wk = Complex.ofCartesian(Math.cos(kth), Math.sin(kth));
          y[k]       = evenFFT[k].add(wk.multiply(oddFFT[k]));
          y[k + n/2] = evenFFT[k].subtract(wk.multiply(oddFFT[k]));
        });
        return y;
    }


    // compute the inverse FFT of x[], assuming its length n is a power of 2
    public static Complex[] ifft(Complex[] x) {
        int n = x.length;
        Complex[] y = new Complex[n];

        // take conjugate
        for (int i = 0; i < n; i++) {
            y[i] = x[i].conj();
        }

        // compute forward FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < n; i++) {
            y[i] = y[i].conj();
        }

        // divide by n
        for (int i = 0; i < n; i++) {
            y[i] = y[i].divide(n);
        }

        return y;

    }


  
  public static Complex[] getFuncValues(String equation, int range){
    double step =  2 * (double) range/NDOTS;
    Expression expression = new ExpressionBuilder(equation).variable("x").build();
    Complex[] values = (Complex[]) IntStream.range(0, NDOTS).mapToObj(n -> {
      double x = n*step - range;
      return Complex.ZERO.add(expression.setVariable("x", x).evaluate());})
      .toArray(nDots -> new Complex[nDots]);
    return values;
  }
  
  public static List<Point2D> getFuncPoints(Complex[] fTime, int range, int nDots){
    List<Point2D> points = IntStream.range(0, fTime.length).parallel()
            .mapToObj(n -> {
            double x = (double) 2 * range/nDots * n - range;
            Point2D point = new Point2D.Double(x, fTime[n].real());
            return point;})
            .collect(Collectors.toList());
    return points;
  }
  
  public static List<Point2D> getFuncPoints(Complex[] fTime, int range){
    return getFuncPoints(fTime, range, NDOTS);
  }


  public static Double inverseFourierSeries(List<Double[]> fOmega, Double x){
    return inverseFourierSeries(fOmega, x, Math.PI);
  }
    public static Double inverseFourierSeries(List<Double[]> fOmega, Double x, double period){
    Double fTime;
    fTime = fOmega.get(0)[1];
    fTime += fOmega.stream().skip(1).parallel()
      .map(n -> { return (n[1] * Math.cos(Math.PI * n[0] * x / period) + n[2] * Math.sin(Math.PI * n[0] * x / period));})
      .collect(Collectors.summingDouble(Double::doubleValue));
    
    return fTime;
  }
    
  public static List<Point2D> inverseFourierSeries(int range, List<Double[]> fOmega){
    return inverseFourierSeries(range, fOmega, Math.PI);
  }
  public static List<Point2D> inverseFourierSeries(int range, List<Double[]> fOmega, double period){
    double step = 0.1;
    int nSteps = 2 * (int) (range/step);
    List<Point2D> fTime;
    
    fTime = Stream.iterate(0, i -> i + 1)
            .limit(nSteps).parallel()
            .map(x -> {
              double abscissa = x*step - range;
              double ordinate = fOmega.stream().parallel()
                      .map(n -> { return (n[1] * Math.cos(2 * Math.PI * n[0] * abscissa / period) + n[2] * Math.sin(2 * Math.PI * n[0] * abscissa / period));})
                      .collect(Collectors.summingDouble(Double::doubleValue));
              Point2D point = new Point2D.Double(abscissa, ordinate);
              return point;
                      })
            .collect(Collectors.toList());
    
    return fTime;
  }
  
  public static List<Double[]> FourierSeries(String equation, double period){
    //increasing nFreq can increase integration error which causes artifacts on graph
    int nFreq = 100;
    List<Double[]> fOmega = new ArrayList<>();
    List<Callable<Double[]>> tasks = new ArrayList<>();
    Expression e = new ExpressionBuilder(equation).variable("x").build();
    Integer i;
    double a0 = 1/period * integrate(e, period);
    fOmega.add(new Double[]{0.0, a0, 0.0});
    
    for (i = 1; i < nFreq; i++){
      tasks.add(new getNthFreqMultiplier(equation, period, i));
      //fOmega.add(new Double[]{i.doubleValue(), an, bn});
    }
    try {
    fOmega.addAll((List<Double[]>) exec.invokeAll(tasks).stream().map(n -> {
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
  public static List<Double[]> FourierSeries(String equation){
    return FourierSeries(equation, Math.PI);
  }

  
  static double integrate(Expression expression, double period){
    double integralValue = 0.0;
    double step = 0.001;
    for (double x = -period/2; x <= period/2-step; x += step){
      integralValue += step * (expression.setVariable("x", x).evaluate() + expression.setVariable("x", x+step).evaluate())/2;
    }
    return integralValue;
  }
  
  
  private static class getNthFreqMultiplier implements Callable<Double[]>{
  double period;
  int i;
  Expression expressionA;
  Expression expressionB;

  public getNthFreqMultiplier(String equation, double period, int i) {
    this.period = period;
    this.i = i;
    expressionA = new ExpressionBuilder(String.format("(%s) * cos(%s * %s * x / %s)", equation, 2 * Math.PI, i, period)).variable("x").build();
    expressionB = new ExpressionBuilder(String.format("(%s) * sin(%s * %s * x / %s)", equation, 2 * Math.PI, i, period)).variable("x").build();
  }

  @Override
  public Double[] call() throws Exception {

    double an = 2 * integrate(expressionA, period)/period;
    double bn = 2 * integrate(expressionB, period)/period;
    return new Double[]{(double) i, an, bn};
  }
  
}
}

