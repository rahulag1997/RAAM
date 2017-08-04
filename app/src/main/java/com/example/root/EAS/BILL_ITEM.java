package com.example.root.EAS;

class BILL_ITEM
{
    final String stk_grp;
    final String stk_item;
    final String quantity;
    final String unit;
    final String rate;

    BILL_ITEM(String stk_grp,String stk_item,String quantity,String unit,String rate)
    {
        this.stk_grp=stk_grp;
        this.stk_item=stk_item;
        this.quantity=quantity;
        this.unit=unit;
        this.rate=rate;
    }
}
