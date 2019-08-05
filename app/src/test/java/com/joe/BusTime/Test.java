package com.joe.BusTime;

public class Test {
    public static void main(String[] args){
        String s="360(aa-bb)";
        System.out.println(s.split("[()]")[1]);
    }
}
