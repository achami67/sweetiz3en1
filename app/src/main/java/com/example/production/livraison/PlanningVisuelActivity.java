package com.example.production.livraison;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.production.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class PlanningVisuelActivity extends AppCompatActivity {

    private Spinner spinnerJour;
    private TextView textCommande1, textCommande2;
    private Button btnValider;

    private SharedPreferences prefs;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning_visuel);

        spinnerJour = findViewById(R.id.spinner_jour);
        textCommande1 = findViewById(R.id.text_commande_1);
        textCommande2 = findViewById(R.id.text_commande_2);
        btnValider = findViewById(R.id.btn_valider_planning);

        // Adapter pour les jours
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.jours_semaine, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJour.setAdapter(adapter);

        // Référence prefs
        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);

        // Listener sur le jour sélectionné
        spinnerJour.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, android.view.View view, int position, long id) {
                String jour = parent.getItemAtPosition(position).toString();
                afficherCommandesDuJour(jour);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnValider.setOnClickListener(v -> {
            Intent intent = new Intent(PlanningVisuelActivity.this, CommandesHabituellesActivity.class);
            startActivity(intent);
        });

        // → Ajouter un jeu de données fictif si vide (au premier lancement)
        if (!prefs.contains("commandes_Lundi")) {
            seedDonneesExemple();
        }
    }

    private void afficherCommandesDuJour(String jour) {
        String json = prefs.getString("commandes_" + jour, null);
        if (json != null) {
            List<String> commandes = gson.fromJson(json, new TypeToken<List<String>>(){}.getType());
            textCommande1.setText(commandes.size() > 0 ? commandes.get(0) : "");
            textCommande2.setText(commandes.size() > 1 ? commandes.get(1) : "");
        } else {
            textCommande1.setText("Aucune commande");
            textCommande2.setText("");
        }
    }

    // ➕ Données exemple au premier lancement
    private void seedDonneesExemple() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("commandes_Lundi", gson.toJson(List.of("Client A : 10 Choco", "Client B : 5 Vanille")));
        editor.putString("commandes_Mardi", gson.toJson(List.of("Client C : 20 Speculoos")));
        editor.putString("commandes_Mercredi", gson.toJson(List.of("Client D : 15 Caramel", "Client E : 12 Fraise")));
        editor.apply();
    }
}
