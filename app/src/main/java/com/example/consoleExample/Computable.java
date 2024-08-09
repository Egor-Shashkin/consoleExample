/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.consoleExample;

/**
 *
 * @author Andrei
 */
public interface Computable<A,V>{
  V compute(A arg) throws InterruptedException;
}