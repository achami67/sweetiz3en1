package com.example.production;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StockJournalierActivity extends AppCompatActivity {

    TextView textIngredients, textTournees;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_journalier);

        // Debug : vérifier que l'activité démarre
        Toast.makeText(this, "StockJournalierActivity lancée", Toast.LENGTH_SHORT).show();

        // Lier les éléments de l'interface
        textIngredients = findViewById(R.id.textIngredients);
        textTournees = findViewById(R.id.textTournees);

        // Récupérer les données de l'intent
        Intent intent = getIntent();
        String ingredients = intent.getStringExtra("ingredients");
        int tournees = intent.getIntExtra("tournees", 0);

        // Afficher les données
        textIngredients.setText(ingredients != null ? ingredients : "Aucun ingrédient transmis.");
        textTournees.setText("Total tournées possibles : " + tournees);
    }
}
