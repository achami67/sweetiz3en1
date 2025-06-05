package com.example.production;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.production.livraison.PourcentagesActivity;
// 🔽 Corrigé : Suppression de ".production" en trop
import com.example.production.StockActivity;

public class MainActivity extends AppCompatActivity {

    // Méthode appelée à la création de l’activité (écran d’accueil)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Assure-toi que ce layout existe

        // Récupération des boutons depuis le layout XML
        Button buttonLivraison = findViewById(R.id.buttonLivraison);
        Button buttonProduction = findViewById(R.id.buttonProduction);
        Button buttonCompta = findViewById(R.id.buttonCompta);

        // Clic sur "Livraison" → ouvre d'abord l'écran de pourcentages
        buttonLivraison.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PourcentagesActivity.class);
            startActivity(intent);
        });

        // Clic sur "Production" → ouvre StockActivity
        buttonProduction.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StockActivity.class);
            startActivity(intent);
        });

        // Clic sur "Comptabilité" → affiche un toast temporaire
        buttonCompta.setOnClickListener(v ->
                Toast.makeText(this, "Comptabilité (à venir)", Toast.LENGTH_SHORT).show()
        );
    }
}
