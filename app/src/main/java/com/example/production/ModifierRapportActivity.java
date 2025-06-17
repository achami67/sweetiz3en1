package com.example.production.comptabilite;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.production.R;

public class ModifierRapportActivity extends AppCompatActivity {

    private EditText editTextClient, editTextQuantite, editTextMontant;
    private Switch switchPaiement;
    private Button buttonModifier;
    private ComptabiliteDBHelper dbHelper;
    private int rapportId;
    private RapportLivraison rapport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_rapport);

        // Initialisation des vues
        editTextClient = findViewById(R.id.editTextClient);
        editTextQuantite = findViewById(R.id.editTextQuantite);
        editTextMontant = findViewById(R.id.editTextMontant);
        switchPaiement = findViewById(R.id.switchPaiement);
        buttonModifier = findViewById(R.id.buttonModifier);

        dbHelper = new ComptabiliteDBHelper(this);

        // Récupérer l’ID passé en paramètre
        rapportId = getIntent().getIntExtra("rapport_id", -1);
        if (rapportId == -1) {
            Toast.makeText(this, "Erreur : rapport introuvable.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Charger les données du rapport
        rapport = dbHelper.getRapportParId(rapportId);
        if (rapport == null) {
            Toast.makeText(this, "Erreur lors du chargement.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Pré-remplissage du formulaire
        editTextClient.setText(rapport.getClient());
        editTextQuantite.setText(String.valueOf(rapport.getQuantite()));
        editTextMontant.setText(String.valueOf(rapport.getMontant()));
        switchPaiement.setChecked(rapport.isEstPaye());

        // Gestion du bouton de modification
        buttonModifier.setOnClickListener(v -> enregistrerModifications());
    }

    private void enregistrerModifications() {
        String client = editTextClient.getText().toString().trim();
        String quantiteStr = editTextQuantite.getText().toString().trim();
        String montantStr = editTextMontant.getText().toString().trim();
        boolean estPaye = switchPaiement.isChecked();

        if (client.isEmpty() || quantiteStr.isEmpty()) {
            Toast.makeText(this, "Champs obligatoires manquants.", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantite;
        double montant;
        try {
            quantite = Integer.parseInt(quantiteStr);
            montant = montantStr.isEmpty() ? 0.0 : Double.parseDouble(montantStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valeurs invalides.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mise à jour des champs
        rapport.setClient(client);
        rapport.setQuantite(quantite);
        rapport.setMontant(montant);
        rapport.setEstPaye(estPaye);

        // Enregistrer dans la base
        dbHelper.modifierRapport(rapport);
        Toast.makeText(this, "Rapport modifié avec succès.", Toast.LENGTH_SHORT).show();
        finish(); // Quitter l'activité
    }
}
