package com.example.root.raam;

class BILL_ITEM
{
    String stk_grp;
    String stk_item;
    String quantity;
    String unit;
    String rate;

    BILL_ITEM(String stk_grp,String stk_item,String quantity,String unit,String rate)
    {
        this.stk_grp=stk_grp;
        this.stk_item=stk_item;
        this.quantity=quantity;
        this.unit=unit;
        this.rate=rate;
    }
}
