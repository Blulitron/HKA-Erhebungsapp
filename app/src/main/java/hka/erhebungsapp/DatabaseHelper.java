package hka.erhebungsapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Datenbank.db";
    public static final String TABLE_NAME_FAHRTEN = "fahrten_table";
    public static final String TABLE_NAME_EREIGNISSE = "ereignisse_table";
    public static final String TABLE_NAME_EVALUATION = "evaluation_table";
    public static final String COL_FAHRT_ID = "FAHRT_ID";
    public static final String COL_START = "START";
    public static final String COL_ZIEL = "ZIEL";
    public static final String COL_TRANSPORT = "TRANSPORTMITTEL";
    public static final String COL_GEPAECK = "GEPAECK";
    public static final String COL_STANDORT = "STANDORT";
    public static final String COL_DATUM_ZEIT = "DATUM_ZEIT";
    public static final String COL_FOTO_ID = "FOTO_ID";
    public static final String COL_EREIGNISS = "EREIGNIS_BESCHREIBUNG";
    public static final String COL_ABGESCHLOSSEN = "FAHRT_ABGESCHLOSSEN";
    public static final String COL_FRAGE_1 = "FRAGE_1";
    public static final String COL_FRAGE_2 = "FRAGE_2";
    public static final String COL_FRAGE_3 = "FRAGE_3";
    public static final String COL_FRAGE_4 = "FRAGE_4";
    public static final String COL_FRAGE_5 = "FRAGE_5";
    public static final String COL_FRAGE_6 = "FRAGE_6";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME_FAHRTEN + " (FAHRT_ID INTEGER PRIMARY KEY AUTOINCREMENT, START TEXT, ZIEL TEXT, TRANSPORTMITTEL TEXT, GEPAECK TEXT, STANDORT TEXT, DATUM_ZEIT TEXT, FOTO_ID TEXT, FAHRT_ABGESCHLOSSEN TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_NAME_EREIGNISSE + " (FAHRT_ID INTEGER, STANDORT TEXT, DATUM_ZEIT TEXT, FOTO_ID TEXT, EREIGNIS_BESCHREIBUNG TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_NAME_EVALUATION + " (FAHRT_ID INTEGER, STANDORT TEXT, DATUM_ZEIT TEXT, FOTO_ID TEXT, FRAGE_1 TEXT, FRAGE_2 TEXT, FRAGE_3 TEXT, FRAGE_4 TEXT, FRAGE_5 TEXT, FRAGE_6 TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FAHRTEN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_EREIGNISSE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_EVALUATION);
        onCreate(db);
    }

    public boolean insertDataFahrten (String start, String ziel, String transportmittel, String gepaeck, String standort, String datum_zeit, String foto_ID, boolean fahrt_abgeschlossen) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_START, start);
        contentValues.put(COL_ZIEL, ziel);
        contentValues.put(COL_TRANSPORT, transportmittel);
        contentValues.put(COL_GEPAECK, gepaeck);
        contentValues.put(COL_STANDORT, standort);
        contentValues.put(COL_DATUM_ZEIT, datum_zeit);
        contentValues.put(COL_FOTO_ID, foto_ID);
        String strFahrtAbgeschlossen;
        if (fahrt_abgeschlossen) {
            strFahrtAbgeschlossen = "Ja";
            contentValues.put(COL_ABGESCHLOSSEN, strFahrtAbgeschlossen);
        } else {
            strFahrtAbgeschlossen = "Nein";
            contentValues.put(COL_ABGESCHLOSSEN, strFahrtAbgeschlossen);
        }
        long result = db.insert(TABLE_NAME_FAHRTEN, null, contentValues);
        return result != -1;
    }

    public boolean insertDataEreignisse (int fahrtID, String standort, String datum_zeit, String foto_ID, String ereigniss){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_FAHRT_ID, fahrtID);
        contentValues.put(COL_STANDORT, standort);
        contentValues.put(COL_DATUM_ZEIT, datum_zeit);
        contentValues.put(COL_FOTO_ID, foto_ID);
        contentValues.put(COL_EREIGNISS, ereigniss);
        long result = db.insert(TABLE_NAME_EREIGNISSE, null, contentValues);
        return result != -1;
    }

    public boolean insertDataEvaluation (int fahrtID, String standort, String datum_zeit, String foto_ID, String frage1, String frage2, String frage3, String frage4, String frage5, String frage6){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_FAHRT_ID, fahrtID);
        contentValues.put(COL_STANDORT, standort);
        contentValues.put(COL_DATUM_ZEIT, datum_zeit);
        contentValues.put(COL_FOTO_ID, foto_ID);
        contentValues.put(COL_FRAGE_1, frage1);
        contentValues.put(COL_FRAGE_2, frage2);
        contentValues.put(COL_FRAGE_3, frage3);
        contentValues.put(COL_FRAGE_4, frage4);
        contentValues.put(COL_FRAGE_5, frage5);
        contentValues.put(COL_FRAGE_6, frage6);
        long result = db.insert(TABLE_NAME_EVALUATION, null, contentValues);
        return result != -1;
    }

    public boolean updateFahrtAbgeschlossen (int fahrtID, boolean status){
        String strStatus, strFahrtID;
        strFahrtID = String.valueOf(fahrtID);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if (status) {
            strStatus = "Ja";
        } else {
            strStatus = "Nein";
        }
        contentValues.put(COL_ABGESCHLOSSEN, strStatus);
        long result = db.update(TABLE_NAME_FAHRTEN, contentValues, COL_FAHRT_ID + "=?", new String[] { strFahrtID });
        return result != -1;
    }

    public boolean deleteFahrt(int fahrtID) {
        String strFahrtID = String.valueOf(fahrtID);
        SQLiteDatabase db = this.getWritableDatabase();
        long[] results = new long[3];
        boolean success = true;
        results[0] = db.delete(TABLE_NAME_FAHRTEN, COL_FAHRT_ID + "=?", new String[]{strFahrtID});
        results[1] = db.delete(TABLE_NAME_EREIGNISSE, COL_FAHRT_ID + "=?", new String[]{strFahrtID});
        results[2] = db.delete(TABLE_NAME_EVALUATION, COL_FAHRT_ID + "=?", new String[]{strFahrtID});
        for (long result : results) {
            if (result == -1) {
                success = false;
                break;
            }
        }
        return success;
    }

    public Cursor getData(String query){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery(query, null);
        return data;
    }
}
