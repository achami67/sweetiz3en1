package com.example.production;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.production.livraison.CommandesHabituellesActivity;

public class ConversionTourneesActivity extends AppCompatActivity {

    private EditText inputTournee1, inputTournee2;
    private Button btnValider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversion_tournees_activity);

        inputTournee1 = findViewById(R.id.inputTournee1);
        inputTournee2 = findViewById(R.id.inputTournee2);
        btnValider = findViewById(R.id.btnValider);

        // Récupération du stock total envoyé depuis StockJournalierActivity
        int totalStock = getIntent().getIntExtra("stockTotal", 0);
        Toast.makeText(this, "Stock reçu : " + totalStock, Toast.LENGTH_SHORT).show();

        // Suggestion automatique (facultative)
        int nombreTournee1 = 0;
        int nombreTournee2 = 0;
        if (totalStock >= 100 && totalStock < 200) {
            nombreTournee1 = 1;
        } else if (totalStock >= 200 && totalStock < 300) {
            nombreTournee2 = 1;
        } else if (totalStock >= 300) {
            nombreTournee1 = 1;
            nombreTournee2 = 1;
        }

        // Remplir les champs avec suggestion
        inputTournee1.setText(String.valueOf(nombreTournee1));
        inputTournee2.setText(String.valueOf(nombreTournee2));

        // Action bouton "Valider"
        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tournee1 = inputTournee1.getText().toString().trim();
                String tournee2 = inputTournee2.getText().toString().trim();

                Toast.makeText(
                        ConversionTourneesActivity.this,
                        "Tournées validées :\n1 tournée = " + tournee1 + "\n2 tournées = " + tournee2,
                        Toast.LENGTH_SHORT
                ).show();

                // Redirection vers la page des commandes enregistrées
                Intent intent = new Intent(ConversionTourneesActivity.this, CommandesHabituellesActivity.class);
                startActivity(intent);
            }
        });
    }
}
