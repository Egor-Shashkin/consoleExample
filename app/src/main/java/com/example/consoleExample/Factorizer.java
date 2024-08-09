///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.example.consoleExample;
//
//import java.math.BigInteger;
//
///**
// *
// * @author Andrei
// */
//public class Factorizer {
//  private final Computable<BigInteger, BigInteger[]> c = new Computable<BigInteger, BigInteger[]>() {
//    public BigInteger[] compute(BigInteger arg) {
//      return factor(arg);
//    }
//  };
//  private final Computable<BigInteger, BigInteger[]> cache = new Preloader<BigInteger, BigInteger[]>(c);
//  
//  public void service(ServletRequest req, ServletResponse resp){
//    BigInteger i = extractfromRequest(req);
//    encodeIntoResponse(resp, cache.compute(i));
//  }
//}
