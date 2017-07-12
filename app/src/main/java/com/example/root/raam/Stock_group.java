package com.example.root.raam;

import android.util.Pair;

import java.util.ArrayList;


public class Stock_group
{
    public String string;
    public final ArrayList<DATA_ITEM> children = new ArrayList<DATA_ITEM>();

    public Stock_group(String string) {
        this.string = string;
    }
}
