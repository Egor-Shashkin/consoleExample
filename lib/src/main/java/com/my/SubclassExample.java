/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.my;

/**
 *
 * @author Andrei
 */
public class SubclassExample extends ClassExample {
    double D2;
    public SubclassExample(){
        this.D2 = 0.5;
        System.out.printf("Subclass constructor has i = %d; S = %s; D = %f %n", i, S, D2);
    }
    
    public String addToS(String addition){
        S += " " + addition;
        return S;
    }
    public String addToS(String addition, boolean autospace){
        if (autospace)
            S += " " + addition;
        else
            S += addition;
        return S;
    }
    
    @Override
    public String getS(){
        return S;
    }
}