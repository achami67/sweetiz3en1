package com.example.production;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Boutons dans le layout
        Button buttonLivraison = findViewById(R.id.buttonLivraison);
        Button buttonProduction = findViewById(R.id.buttonProduction);
        Button buttonCompta = findViewById(R.id.buttonCompta);

        // Clic sur "Livraison"
        buttonLivraison.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MotDePasseActivity.class);
            intent.putExtra("section", "LIVRAISON"); // 🔐 mot de passe pour livraison
            startActivity(intent);
        });

        // Clic sur "Production"
        buttonProduction.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MotDePasseActivity.class);
            intent.putExtra("section", "PRODUCTION"); // 🔐 mot de passe pour production
            startActivity(intent);
        });

        // Clic sur "Comptabilité"
        buttonCompta.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MotDePasseActivity.class);
            intent.putExtra("section", "COMPTABILITE"); // 🔐 mot de passe pour comptabilité
            startActivity(intent);
        });
    }
}
