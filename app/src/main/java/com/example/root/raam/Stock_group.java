package com.example.root.raam;

import java.util.ArrayList;


class Stock_group
{
    public String string;
    final ArrayList<DATA_ITEM> children = new ArrayList<>();

    Stock_group(String string) {
        this.string = string;
    }
}
