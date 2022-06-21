package utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import model.Dico;

public class OpenHelpher extends SQLiteOpenHelper {

    private  static  String DATABASE_NAME = "creolise.db";
    private  static Integer VERSION=1;
    private  String createTable="CREATE TABLE dico(mot_creole VARCHAR(255),traduction VARCHAR(255))";




    public OpenHelpher(@Nullable Context context) {

        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void add(Dico dico) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values= new ContentValues();
        values.put("mot_creole",dico.getMot_creole());
        values.put("traduction",dico.getTraduction());


        db.insert("dico",null,values);
    }

    public List<Dico> findAll(){
        List<Dico> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor iterateur = db.query("dico",new String[]{"mot_creole","traduction"},null,null,null,null,null,null);

        iterateur.moveToFirst();
        while (!iterateur.isAfterLast()){
            Dico u = new Dico();
            u.setMot_creole(iterateur.getString(1));
            u.setMot_creole(iterateur.getString(2));
            result.add(u);
            iterateur.moveToNext();

        }

        return result;
    }
}
