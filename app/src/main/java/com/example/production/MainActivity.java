package com.example.production;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.production.livraison.CommandesNormalesActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // ton layout XML d'accueil

        Button buttonLivraison = findViewById(R.id.buttonLivraison);
        Button buttonProduction = findViewById(R.id.buttonProduction);
        Button buttonCompta = findViewById(R.id.buttonCompta);

        // Ouvre l’écran Livraison → CommandesNormalesActivity
        buttonLivraison.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CommandesNormalesActivity.class);
            startActivity(intent);
        });

        // Ouvre l’écran Production → StockActivity
        buttonProduction.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StockActivity.class);
            startActivity(intent);
        });

        // Comptabilité (à venir)
        buttonCompta.setOnClickListener(v ->
                Toast.makeText(this, "Comptabilité (à venir)", Toast.LENGTH_SHORT).show()
        );
    }
}
