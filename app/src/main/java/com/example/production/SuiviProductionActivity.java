package com.example.production;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SuiviProductionActivity extends AppCompatActivity {

    private EditText editQuantitePrevue, editQuantiteProduite, editEcarts;
    private RadioGroup radioGroupValidation;
    private Button buttonValider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suivi_production);

        // Initialisation des vues
        editQuantitePrevue = findViewById(R.id.editQuantitePrevue);
        editQuantiteProduite = findViewById(R.id.editQuantiteProduite);
        editEcarts = findViewById(R.id.editEcarts);
        radioGroupValidation = findViewById(R.id.radioGroupValidation);
        buttonValider = findViewById(R.id.buttonValiderSuiviProduction);

        editEcarts.setVisibility(View.GONE); // caché par défaut

        // Gestion du choix Oui / Non
        radioGroupValidation.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioOui) {
                editEcarts.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioNon) {
                editEcarts.setVisibility(View.VISIBLE);
            }
        });

        // Bouton : Valider
        buttonValider.setOnClickListener(v -> {
            String quantitePrevue = editQuantitePrevue.getText().toString().trim();
            String quantiteProduite = editQuantiteProduite.getText().toString().trim();

            if (quantitePrevue.isEmpty() || quantiteProduite.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir les champs requis", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = radioGroupValidation.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Veuillez valider la production", Toast.LENGTH_SHORT).show();
                return;
            }

            String ecarts = "";
            if (selectedId == R.id.radioNon) {
                ecarts = editEcarts.getText().toString().trim();
                if (ecarts.isEmpty()) {
                    Toast.makeText(this, "Veuillez indiquer les écarts", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // ➕ Passage vers l’activité suivante avec données
            Intent intent = new Intent(SuiviProductionActivity.this, ResumeJourneeActivity.class);
            intent.putExtra("quantitePrevue", Integer.parseInt(quantitePrevue));
            intent.putExtra("quantiteProduite", Integer.parseInt(quantiteProduite));
            intent.putExtra("ecarts", ecarts);
            startActivity(intent);

            // Pas de finish(); car l'utilisateur peut revenir
        });
    }
}
