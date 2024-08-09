///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.example.consoleExample;
//
//import java.util.concurrent.Callable;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Future;
//import java.util.concurrent.FutureTask;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// *
// * @author Andrei
// */
//
//
//
//public class Preloader<A,V> implements Computable<A,V>{
//  private final ConcurrentMap<A,Future<V>> cache = new ConcurrentHashMap<A, Future<V>>();
//  private final Computable<A,V> c;
//  
//  public Preloader(Computable<A,V> c){
//    this.c = c;
//  }
//  public V compute(final A arg){
//    while (true){
//      Future<V> f = cache.get(arg);
//      if (f == null){
//        Callable<V> eval = new Callable<V>(){
//          public V call(){
//            return c.compute(arg);
//          }
//        };
//        FutureTask ft = new FutureTask<V>(eval);
//        f = cache.putIfAbsent(arg, ft);
//        if (f == null) {
//          f = ft;
//          ft.run();
//        }
//        try {
//          f.get();
//        } catch (InterruptedException ex) {
//          Logger.getLogger(Preloader.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (ExecutionException ex) {
//          Logger.getLogger(Preloader.class.getName()).log(Level.SEVERE, null, ex);
//        }
//      }
//    }
//  }
//}
