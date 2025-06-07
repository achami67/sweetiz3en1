package com.example.production.comptabilite;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.production.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaisieRapportActivity extends AppCompatActivity {

    private EditText editTextClient, editTextQuantite, editTextMontant;
    private Switch switchPaiement;
    private Button buttonValider;
    private ComptabiliteDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saisie_rapport);

        // Références aux vues
        editTextClient = findViewById(R.id.editTextClient);
        editTextQuantite = findViewById(R.id.editTextQuantite);
        editTextMontant = findViewById(R.id.editTextMontant);
        switchPaiement = findViewById(R.id.switchPaiement);
        buttonValider = findViewById(R.id.buttonValider);

        dbHelper = new ComptabiliteDBHelper(this);

        buttonValider.setOnClickListener(v -> enregistrerRapport());
    }

    private void enregistrerRapport() {
        String client = editTextClient.getText().toString().trim();
        String quantiteStr = editTextQuantite.getText().toString().trim();
        String montantStr = editTextMontant.getText().toString().trim();
        boolean estPaye = switchPaiement.isChecked();

        if (client.isEmpty() || quantiteStr.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires.", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantite = Integer.parseInt(quantiteStr);
        double montant = montantStr.isEmpty() ? 0.0 : Double.parseDouble(montantStr);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        RapportLivraison rapport = new RapportLivraison(0, date, client, quantite, estPaye, montant);
        dbHelper.ajouterRapport(rapport);

        Toast.makeText(this, "Rapport enregistré avec succès.", Toast.LENGTH_SHORT).show();
        finish(); // Retour à la page précédente
    }
}
