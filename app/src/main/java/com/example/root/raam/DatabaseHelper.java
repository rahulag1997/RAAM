package com.example.root.raam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String TABLE_NAME="Data_Table";
    private int numVal;
    private String[] keys={"ID"};
    public DatabaseHelper(Context context, String name,int numVal,String[] keys)
    {
        super(context, name, null, 1);
        this.numVal=numVal;
        ArrayList<String> temp=new ArrayList<>();
        temp.add("ID");
        temp.addAll(Arrays.asList(keys));
        this.keys=temp.toArray(new String[temp.size()]);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String create_query="CREATE TABLE "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT";
        for (int i=1;i<=numVal;i++)
            create_query+="," + keys[i]+ " TEXT";
        create_query+=")";
        db.execSQL(create_query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    void insertData(String[] data)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        for (int i=1;i<=numVal;i++)
        {
            contentValues.put(keys[i],data[i-1]);
        }
        db.insert(TABLE_NAME,null,contentValues);
    }

    public Cursor getData()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_NAME,null);
    }

    void updateData(String[] data)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor c=this.getData();
        while (c.moveToNext())
        {
            if(c.getString(1).equals(data[0]))
                break;
        }
        ContentValues contentValues=new ContentValues();
        contentValues.put("ID",c.getInt(0));
        for (int i=1;i<=numVal;i++)
        {
            contentValues.put(keys[i],data[i-1]);
        }
        db.update(TABLE_NAME,contentValues,"ID = ?",new String[] {c.getString(0)});
    }
    /*
    void sortByDate(int dateColumn)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        db.query(TABLE_NAME,keys,null,null,null,null,"Date ASC","LIMIT 1");
    }*/

    Cursor sortByName()
    {
        SQLiteDatabase db=this.getWritableDatabase();
        return db.query(TABLE_NAME,keys,null,null,null,null,"Name ASC");
    }

    Cursor getRow(int num)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        num--;
        String n=Integer.toString(num);
        return db.rawQuery("select * from "+TABLE_NAME+" limit 1 offset "+n,null);
    }
}
