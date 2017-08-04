package com.example.root.EAS;

class DATA_ITEM
{
    public final String name;
    final String bal;
    final String dr;
    final String cr;
    String type;
    String num;

    DATA_ITEM(String name,String dr, String cr, String bal)
    {
        this.name=name;
        this.bal=bal;
        this.dr=dr;
        this.cr=cr;
    }
    DATA_ITEM(String name,String dr, String cr, String bal,String type,String num)
    {
        this.name=name;
        this.bal=bal;
        this.dr=dr;
        this.cr=cr;
        this.type=type;
        this.num=num;
    }

}
