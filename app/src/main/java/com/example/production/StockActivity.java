package com.example.production;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StockActivity extends AppCompatActivity {

    EditText inputIngredient, inputDisponible, inputTournee;
    Button buttonAjouter, buttonValider;
    TextView textResultat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);  // nom exact du fichier XML

        inputIngredient = findViewById(R.id.inputIngredient);
        inputDisponible = findViewById(R.id.inputDisponible);
        inputTournee = findViewById(R.id.inputTournee);
        buttonAjouter = findViewById(R.id.buttonAjouter);
        buttonValider = findViewById(R.id.buttonValider);
        textResultat = findViewById(R.id.textResultat);

        buttonAjouter.setOnClickListener(v -> {
            String nom = inputIngredient.getText().toString();
            String dispoStr = inputDisponible.getText().toString();
            String tourneeStr = inputTournee.getText().toString();

            if (!nom.isEmpty() && !dispoStr.isEmpty() && !tourneeStr.isEmpty()) {
                try {
                    float disponible = Float.parseFloat(dispoStr);
                    float tournee = Float.parseFloat(tourneeStr);
                    int tournees = (int) (disponible / tournee);

                    textResultat.setText("Ingrédient ajouté : " + nom + "\nTournées possibles : " + tournees);
                } catch (NumberFormatException e) {
                    textResultat.setText("⚠️ Veuillez entrer des nombres valides.");
                }
            } else {
                textResultat.setText("⚠️ Remplir tous les champs.");
            }
        });

        buttonValider.setOnClickListener(v -> {
            textResultat.setText("✅ Stock validé !");
        });
    }
}
