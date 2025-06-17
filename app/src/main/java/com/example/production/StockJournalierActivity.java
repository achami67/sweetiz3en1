package com.example.production;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.text.InputType;
import android.widget.LinearLayout.LayoutParams;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StockJournalierActivity extends AppCompatActivity {

    private LinearLayout layoutGoutsDynamiques;
    private EditText editTextStockTotal;
    private EditText editTextDate; // ✅ Ajout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_journalier);

        Toast.makeText(this, "StockJournalierActivity lancée", Toast.LENGTH_SHORT).show();

        // Récupération des données de l’intent
        Intent intent = getIntent();
        String ingredients = intent.getStringExtra("ingredients");
        int tournees = intent.getIntExtra("tournees", 0);

        String message = "Ingrédients reçus : " + (ingredients != null ? ingredients : "aucun")
                + "\nTournées possibles : " + tournees;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Initialisation des vues
        layoutGoutsDynamiques = findViewById(R.id.layoutGoutsDynamiques);
        editTextStockTotal = findViewById(R.id.editTextStockTotal);
        editTextDate = findViewById(R.id.editTextDate); // ✅ récupération

        // ✅ Remplir automatiquement la date du jour
        String currentDate = new SimpleDateFormat("dd / MM / yyyy", Locale.getDefault()).format(new Date());
        editTextDate.setText(currentDate);

        // Bouton : Ajouter un goût
        Button buttonAjouterGout = findViewById(R.id.buttonAjouterGout);
        buttonAjouterGout.setOnClickListener(v -> ajouterChampGout());

        // Bouton : Valider → ConversionTourneesActivity
        Button buttonValider = findViewById(R.id.buttonValider);
        buttonValider.setOnClickListener(v -> {
            String stockTotalStr = editTextStockTotal.getText().toString().trim();

            if (stockTotalStr.isEmpty()) {
                Toast.makeText(this, "Veuillez saisir le stock total", Toast.LENGTH_SHORT).show();
                return;
            }

            int stockTotal = Integer.parseInt(stockTotalStr);

            Intent intentVersTournees = new Intent(StockJournalierActivity.this, ConversionTourneesActivity.class);
            intentVersTournees.putExtra("stockTotal", stockTotal);
            startActivity(intentVersTournees);
        });
    }

    private void ajouterChampGout() {
        LinearLayout ligneGout = new LinearLayout(this);
        ligneGout.setOrientation(LinearLayout.HORIZONTAL);
        ligneGout.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        ligneGout.setPadding(0, 8, 0, 8);

        EditText editNom = new EditText(this);
        editNom.setHint("Nom du goût");
        editNom.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        editNom.setInputType(InputType.TYPE_CLASS_TEXT);

        EditText editQuantite = new EditText(this);
        editQuantite.setHint("Qté");
        editQuantite.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1));
        editQuantite.setInputType(InputType.TYPE_CLASS_NUMBER);

        TextView suffixe = new TextView(this);
        suffixe.setText(" tiramisus");
        suffixe.setGravity(Gravity.CENTER_VERTICAL);
        suffixe.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        suffixe.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));

        ligneGout.addView(editNom);
        ligneGout.addView(editQuantite);
        ligneGout.addView(suffixe);

        layoutGoutsDynamiques.addView(ligneGout);
    }
}
