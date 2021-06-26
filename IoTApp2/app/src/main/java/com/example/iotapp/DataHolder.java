package com.example.iotapp;

public class DataHolder {
    private int data;
    public int getData()
    {
        return data;
    }

    public void setData(int data)
    {
        this.data = data;
    }

    public static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance()
    {
        return holder;
    }

}
