package com.example.production;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.production.livraison.CommandesHabituellesActivity;

public class ConversionTourneesActivity extends AppCompatActivity {

    private EditText editT1Min, editT1Max;
    private EditText editT2Min, editT2Max;
    private EditText editResult;
    private Button buttonAjouter, buttonValider;
    private int stockTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversion_tournees_activity);

        // Initialisation des vues
        editT1Min = findViewById(R.id.editT1Min);
        editT1Max = findViewById(R.id.editT1Max);
        editT2Min = findViewById(R.id.editT2Min);
        editT2Max = findViewById(R.id.editT2Max);
        editResult = findViewById(R.id.editResult);
        buttonAjouter = findViewById(R.id.buttonAjouter);
        buttonValider = findViewById(R.id.buttonValider);

        // Récupération du stock
        stockTotal = getIntent().getIntExtra("stockTotal", 0);
        Toast.makeText(this, "Stock total reçu : " + stockTotal, Toast.LENGTH_SHORT).show();

        buttonAjouter.setOnClickListener(v -> calculerTournees());

        buttonValider.setOnClickListener(v -> {
            Toast.makeText(this, "Tournées enregistrées", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CommandesHabituellesActivity.class);
            startActivity(intent);
        });
    }

    private void calculerTournees() {
        try {
            int t1Min = Integer.parseInt(editT1Min.getText().toString().trim());
            int t1Max = Integer.parseInt(editT1Max.getText().toString().trim());
            int t2Min = Integer.parseInt(editT2Min.getText().toString().trim());
            int t2Max = Integer.parseInt(editT2Max.getText().toString().trim());

            int nbTournees = 0;

            if (stockTotal >= t1Min && stockTotal <= t1Max) {
                nbTournees = 1;
            } else if (stockTotal >= t2Min && stockTotal <= t2Max) {
                nbTournees = 2;
            } else {
                Toast.makeText(this, "Stock hors plage définie", Toast.LENGTH_SHORT).show();
                return;
            }

            editResult.setText(String.valueOf(nbTournees));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Veuillez remplir toutes les cases", Toast.LENGTH_SHORT).show();
        }
    }
}
