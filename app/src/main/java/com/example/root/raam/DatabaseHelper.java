package com.example.root.raam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String TABLE_NAME="Data_Table";
    private int numVal;
    private String[] keys;
    public DatabaseHelper(Context context, String name,int numVal,String[] keys)
    {
        super(context, name, null, 1);
        this.numVal=numVal;
        this.keys=keys;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String create_query="CREATE TABLE "+TABLE_NAME+" (ID INTEGER PRIMARY KEY AUTOINCREMENT";
        for (int i=0;i<numVal;i++)
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
        for (int i=0;i<numVal;i++)
        {
            contentValues.put(keys[i],data[i]);
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
        for (int i=0;i<numVal;i++)
        {
            contentValues.put(keys[i],data[i]);
        }
        db.update(TABLE_NAME,contentValues,"ID = ?",new String[] {c.getString(0)});
    }
}
