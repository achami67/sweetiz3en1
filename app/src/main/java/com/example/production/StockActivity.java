package com.example.production;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StockActivity extends AppCompatActivity {

    EditText inputIngredient, inputDisponible, inputTournee;
    Button buttonAjouter, buttonValider;
    TextView textTournees, textIngredientsAjoutes, textListeIngredients;

    StringBuilder ingredientsAjoutes = new StringBuilder();
    int totalTournees = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        // Initialiser les composants
        inputIngredient = findViewById(R.id.inputIngredient);
        inputDisponible = findViewById(R.id.inputDisponible);
        inputTournee = findViewById(R.id.inputTournee);
        buttonAjouter = findViewById(R.id.buttonAjouter);
        buttonValider = findViewById(R.id.buttonValider);
        textTournees = findViewById(R.id.textTournees);
        textIngredientsAjoutes = findViewById(R.id.textIngredientsAjoutes);
        textListeIngredients = findViewById(R.id.textListeIngredients);

        // Action bouton "Ajouter l'ingrédient"
        buttonAjouter.setOnClickListener(v -> {
            String nom = inputIngredient.getText().toString().trim();
            String dispoStr = inputDisponible.getText().toString().trim();
            String tourneeStr = inputTournee.getText().toString().trim();

            if (!nom.isEmpty() && !dispoStr.isEmpty() && !tourneeStr.isEmpty()) {
                try {
                    float disponible = Float.parseFloat(dispoStr);
                    float parTournee = Float.parseFloat(tourneeStr);

                    if (parTournee <= 0) {
                        textTournees.setText("⚠️ Qté par tournée doit être > 0.");
                        return;
                    }

                    int tournees = (int) (disponible / parTournee);
                    totalTournees += tournees;

                    // Ajouter ligne à la liste
                    ingredientsAjoutes.append(nom)
                            .append(" – ")
                            .append((int) disponible).append(" Kg – ")
                            .append((int) parTournee).append(" Kg/tournée\n");

                    // Mise à jour UI
                    textListeIngredients.setText(ingredientsAjoutes.toString());
                    textTournees.setText("Tournées possibles : " + totalTournees);

                    // Réinitialiser les champs
                    inputIngredient.setText("");
                    inputDisponible.setText("");
                    inputTournee.setText("");

                } catch (NumberFormatException e) {
                    textTournees.setText("⚠️ Veuillez entrer des nombres valides.");
                }
            } else {
                textTournees.setText("⚠️ Remplir tous les champs.");
            }
        });

        // Action bouton "Valider"
        buttonValider.setOnClickListener(v -> {
            if (ingredientsAjoutes.length() == 0) {
                textTournees.setText("⚠️ Aucun ingrédient ajouté.");
            } else {
                // Démarre la nouvelle activité avec les données
                Intent intent = new Intent(StockActivity.this, StockJournalierActivity.class);
                intent.putExtra("ingredients", ingredientsAjoutes.toString());
                intent.putExtra("tournees", totalTournees);
                startActivity(intent);
            }
        });
    }
}
