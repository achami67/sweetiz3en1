package com.example.production;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
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

        editQuantitePrevue = findViewById(R.id.editQuantitePrevue);
        editQuantiteProduite = findViewById(R.id.editQuantiteProduite);
        editEcarts = findViewById(R.id.editEcarts);
        radioGroupValidation = findViewById(R.id.radioGroupValidation);
        buttonValider = findViewById(R.id.buttonValiderSuiviProduction);

        editEcarts.setVisibility(View.GONE); // caché par défaut

        radioGroupValidation.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioOui) {
                editEcarts.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioNon) {
                editEcarts.setVisibility(View.VISIBLE);
            }
        });

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

            if (selectedId == R.id.radioNon) {
                String ecarts = editEcarts.getText().toString().trim();
                if (ecarts.isEmpty()) {
                    Toast.makeText(this, "Veuillez indiquer les écarts", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Ici, tu peux gérer les écarts
            }

            // Traitement ou sauvegarde ici
            Toast.makeText(this, "Production validée ✅", Toast.LENGTH_SHORT).show();
            // Tu peux rediriger ou fermer l'activité ici
            finish();
        });
    }
}
