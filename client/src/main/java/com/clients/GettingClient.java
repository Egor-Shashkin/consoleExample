/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.clients;

import com.myUtility.*;
import java.awt.geom.Point2D;
import java.util.List;

/**
 *
 * @author Andrei
 */

//deprecated, use Client instead
public class GettingClient {
  public static void main(String[] args){
    List<Point2D> fTimeRe;
    List<Double[]> fOmega;
      int range = 20;
//      double period = Math.PI * 2;
//      range = (int)Math.round(period);
      fTimeRe = FourierTransformer.getFuncPoints(FourierTransformer.ifft(
              (FourierTransformer.fft(FourierTransformer.getFuncValues("exp(x)", range)))), range);
      fOmega = FourierTransformer.FourierSeries("x", range);
      fTimeRe = FourierTransformer.inverseFourierSeries(range, fOmega, 2 * range);
      Plotter plot = new Plotter("Title", "Title", fTimeRe);
      plot.pack();
      plot.setVisible(true);
  }
}

