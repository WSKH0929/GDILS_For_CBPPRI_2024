package com.wskh.classes;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    public int id;
    public double w, h, s;

    public Item(int id, double w, double h) {
        this.id = id;
        this.w = w;
        this.h = h;
        this.s = w * h;
    }

    public Item copy() {
        return new Item(id, w, h, s);
    }

}