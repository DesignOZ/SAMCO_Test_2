package com.tisotry.overimagine.samco_test_2.useless;

/**
 * Created by Horyeong Park on 2017-12-10.
 */

public class Mission {
    private int Type;
    /* Type
    * 0 : PASS
    * 1 : TURN CW
    * 2 : TURN CCW
    */

    private float Latitude;
    private float Longtitude;
    private int Altitude;
    private int Speed;
    private int Radius;
    private int NTurn;

    public Mission(int type, float latitude, float longtitude, int altitude, int speed) {
        this.Type = type;
        this.Latitude = latitude;
        this.Longtitude = longtitude;
        this.Altitude = altitude;
        this.Speed = speed;
        this.Radius = 0;
        this.NTurn = 0;
    }

    public Mission(int type, float latitude, float longtitude, int altitude, int speed, int radius, int nturn) {
        this.Type = type;
        this.Latitude = latitude;
        this.Longtitude = longtitude;
        this.Altitude = altitude;
        this.Speed = speed;
        this.Radius = radius;
        this.NTurn = nturn;
    }

    public int getType() {
        return Type;
    }

    public float getLatitude() {
        return Latitude;
    }

    public float getLongtitude() {
        return Longtitude;
    }

    public int getAltitude() {
        return Altitude;
    }

    public int getRadius() {
        return Radius;
    }

    public int getSpeed() {
        return Speed;
    }

    public int getNTurn() {
        return NTurn;
    }
}

