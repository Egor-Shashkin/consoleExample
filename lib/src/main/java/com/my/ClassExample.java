/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.my;

/**
 *
 * @author Andrei
 */
public class ClassExample {
    public int i;
    protected String S;
    static private double D;
    
    
    public ClassExample(){
        i = 5;
        S = "parent string";
        D = 12.4;
    }
    
    public static double getD(){
       return D;
    }
    public String getS(){
        return S;
    }
}

