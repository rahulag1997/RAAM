package com.example.root.raam;

public class DATA_ITEM
{
    public String name;
    String bal;
    String dr;
    String cr;
    int sno;

    DATA_ITEM(String name,String dr, String cr, String bal)
    {
        this.name=name;
        this.bal=bal;
        this.dr=dr;
        this.cr=cr;
    }
    DATA_ITEM(int sno,String name,String dr, String cr, String bal)
    {
        this.sno=sno;
        this.name=name;
        this.bal=bal;
        this.dr=dr;
        this.cr=cr;
    }

}
