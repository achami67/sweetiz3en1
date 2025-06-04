package com.example.production;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StockJournalierActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_journalier);

        // Debug : vérifier que l'activité démarre
        Toast.makeText(this, "StockJournalierActivity lancée", Toast.LENGTH_SHORT).show();

        // Récupérer les données de l'intent
        Intent intent = getIntent();
        String ingredients = intent.getStringExtra("ingredients");
        int tournees = intent.getIntExtra("tournees", 0);

        // Afficher les données en Toast pour confirmation (ou log si tu préfères)
        String message = "Ingrédients reçus : " + (ingredients != null ? ingredients : "aucun") +
                "\nTournées possibles : " + tournees;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
