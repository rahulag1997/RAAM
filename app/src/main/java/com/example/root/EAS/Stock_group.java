package com.example.root.EAS;

import java.util.ArrayList;


class Stock_group
{
    public final String string;
    final ArrayList<DATA_ITEM> children = new ArrayList<>();

    Stock_group(String string) {
        this.string = string;
    }
}
