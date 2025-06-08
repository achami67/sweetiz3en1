package com.example.production;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.production.StockActivity;
import com.example.production.comptabilite.ComptabiliteAccueilActivity;
import com.example.production.livraison.PourcentagesActivity;
import com.example.production.livraison.ResumeCommandesActivity;

public class MotDePasseActivity extends AppCompatActivity {

    private EditText editTextPassword;
    private Button buttonValider;
    private CheckBox checkBoxLivreur;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mot_de_passe);

        // Références aux éléments de l'interface
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonValider = findViewById(R.id.buttonValider);
        checkBoxLivreur = findViewById(R.id.checkbox_livreur);

        // Section demandée
        String section = getIntent().getStringExtra("section");

        buttonValider.setOnClickListener(v -> {
            String mdp = editTextPassword.getText().toString().trim();
            boolean estLivreur = checkBoxLivreur.isChecked();
            Intent intent = null;

            if (section == null) {
                Toast.makeText(this, "Section non définie.", Toast.LENGTH_SHORT).show();
                return;
            }

            switch (section.toLowerCase()) {
                case "livraison":
                    if (mdp.equals("livraison123")) {
                        String mode = estLivreur ? "lecture" : "ecriture";
                        Class<?> cible = estLivreur ? ResumeCommandesActivity.class : PourcentagesActivity.class;

                        intent = new Intent(this, cible);
                        intent.putExtra("mode", mode);
                        intent.putExtra("section", "LIVRAISON");
                    }
                    break;

                case "production":
                    if (mdp.equals("prod456")) {
                        intent = new Intent(this, StockActivity.class);
                        intent.putExtra("section", "PRODUCTION");
                    }
                    break;

                case "comptabilite":
                    if (mdp.equals("compta789")) {
                        intent = new Intent(this, ComptabiliteAccueilActivity.class);
                        intent.putExtra("section", "COMPTABILITE");
                    }
                    break;
            }

            if (intent != null) {
                Log.d("DEBUG", "Accès autorisé : section=" + section + ", mode=" + intent.getStringExtra("mode"));
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Mot de passe incorrect", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
