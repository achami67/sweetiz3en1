package com.example.production.comptabilite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ComptabiliteDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "comptabilite.db";
    private static final int DB_VERSION = 1;

    public ComptabiliteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE rapports (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "date TEXT," +
                "client TEXT," +
                "quantite INTEGER," +
                "est_paye INTEGER," +
                "montant REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS rapports");
        onCreate(db);
    }

    public void ajouterRapport(RapportLivraison rapport) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date", rapport.getDate());
        values.put("client", rapport.getClient());
        values.put("quantite", rapport.getQuantite());
        values.put("est_paye", rapport.isEstPaye() ? 1 : 0);
        values.put("montant", rapport.getMontant());
        db.insert("rapports", null, values);
        db.close();
    }

    public List<RapportLivraison> getRapportsDuJour() {
        List<RapportLivraison> rapports = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String dateDuJour = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Cursor cursor = db.rawQuery("SELECT * FROM rapports WHERE date = ?", new String[]{dateDuJour});

        if (cursor.moveToFirst()) {
            do {
                rapports.add(cursorVersRapport(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return rapports;
    }

    public List<RapportLivraison> getTousLesRapports() {
        List<RapportLivraison> rapports = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM rapports ORDER BY date DESC", null);
        if (cursor.moveToFirst()) {
            do {
                rapports.add(cursorVersRapport(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return rapports;
    }

    public void marquerCommePaye(int idRapport) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("est_paye", 1);
        db.update("rapports", values, "id = ?", new String[]{String.valueOf(idRapport)});
        db.close();
    }

    private RapportLivraison cursorVersRapport(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        String client = cursor.getString(cursor.getColumnIndexOrThrow("client"));
        int quantite = cursor.getInt(cursor.getColumnIndexOrThrow("quantite"));
        boolean estPaye = cursor.getInt(cursor.getColumnIndexOrThrow("est_paye")) == 1;
        double montant = cursor.getDouble(cursor.getColumnIndexOrThrow("montant"));

        return new RapportLivraison(id, date, client, quantite, estPaye, montant);
    }

    // 🔍 Obtenir un rapport par ID
    public RapportLivraison getRapportParId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM rapports WHERE id = ?", new String[]{String.valueOf(id)});
        RapportLivraison rapport = null;

        if (cursor.moveToFirst()) {
            rapport = cursorVersRapport(cursor);
        }

        cursor.close();
        db.close();
        return rapport;
    }

    // ✏️ Modifier un rapport existant
    public void modifierRapport(RapportLivraison rapport) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("client", rapport.getClient());
        values.put("quantite", rapport.getQuantite());
        values.put("montant", rapport.getMontant());
        values.put("est_paye", rapport.isEstPaye() ? 1 : 0);

        db.update("rapports", values, "id = ?", new String[]{String.valueOf(rapport.getId())});
        db.close();
    }
}
