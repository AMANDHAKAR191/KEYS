package com.example.keys;

import java.util.concurrent.atomic.AtomicLong;

public class GetNextNumber {
    static int initialValue;
    public GetNextNumber(int initialValue){
        this.initialValue = initialValue;
    }
    private static AtomicLong numberGenrater = new AtomicLong(initialValue);
    public int getNext(){
        return (int) numberGenrater.getAndIncrement();
    }
}
