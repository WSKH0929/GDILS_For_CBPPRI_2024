package com.wskh.classes;

import lombok.ToString;

@ToString
public class PlaceItem {
    public int id;
    public double x, y, w, h, s, x2, y2;

    public PlaceItem(int id, double x, double y, double w, double h, double s) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.s = s;
        this.x2 = x + w;
        this.y2 = y + h;
    }

}
