package com.example.root.raam;

public class BILL_ITEM
{
    public String stk_grp;
    public String stk_item;
    public String quantity;
    public String unit;
    public String rate;
    BILL_ITEM(String stk_grp,String stk_item,String quantity,String unit,String rate)
    {
        this.stk_grp=stk_grp;
        this.stk_item=stk_item;
        this.quantity=quantity;
        this.unit=unit;
        this.rate=rate;
    }
}
