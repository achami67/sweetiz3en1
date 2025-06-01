package com.example.production.livraison;
import com.example.production.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandesNormalesActivity extends AppCompatActivity {

    private LinearLayout layoutHabituelles, layoutPonctuelles;
    private Button buttonAjouterHabituelle, buttonAjouterPonctuelle, buttonValiderCommandes;
    private List<CommandeClient> commandesHabituelles = new ArrayList<>();
    private Map<Integer, Boolean> inclusionMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commandes_normales);

        layoutHabituelles = findViewById(R.id.layoutHabituelles);
        layoutPonctuelles = findViewById(R.id.layoutPonctuelles);
        buttonAjouterHabituelle = findViewById(R.id.buttonAjouterHabituelle);
        buttonAjouterPonctuelle = findViewById(R.id.buttonAjouterPonctuelle);
        buttonValiderCommandes = findViewById(R.id.buttonValiderCommandes);

        chargerCommandesHabituelles();

        for (int i = 0; i < commandesHabituelles.size(); i++) {
            ajouterBlocCommande(commandesHabituelles.get(i), true, i);
        }

        buttonAjouterHabituelle.setOnClickListener(v -> ajouterBlocCommande(null, true, layoutHabituelles.getChildCount()));
        buttonAjouterPonctuelle.setOnClickListener(v -> ajouterBlocCommande(null, false, -1));

        buttonValiderCommandes.setOnClickListener(v -> {
            sauvegarderCommandesHabituelles();
            sauvegarderCommandesPonctuelles();
            startActivity(new Intent(this, CommandesSpecialesActivity.class));
        });
    }

    private void ajouterBlocCommande(CommandeClient commande, boolean estHabituelle, int index) {
        View bloc = LayoutInflater.from(this).inflate(R.layout.item_commande_client,
                estHabituelle ? layoutHabituelles : layoutPonctuelles, false);

        EditText editNomClient = bloc.findViewById(R.id.editNomClient);
        EditText editQuantiteTotale = bloc.findViewById(R.id.editQuantiteTotale);
        EditText editExclusions = bloc.findViewById(R.id.editExclusions);
        LinearLayout layoutGouts = bloc.findViewById(R.id.layoutGoutsClient);
        Button btnAjouterGout = bloc.findViewById(R.id.btnAjouterGoutClient);
        Button btnSupprimerClient = bloc.findViewById(R.id.btnSupprimerCommande);

        if (commande != null) {
            editNomClient.setText(commande.getNomClient());
            editQuantiteTotale.setText(String.valueOf(commande.getQuantiteTotale()));
            if (commande.getGoutsExclus() != null)
                editExclusions.setText(String.join(",", commande.getGoutsExclus()));
            if (commande.getGouts() != null) {
                for (GoutQuantite gq : commande.getGouts()) {
                    View goutView = LayoutInflater.from(this).inflate(R.layout.item_gout_special, layoutGouts, false);
                    EditText nom = goutView.findViewById(R.id.editNomGout);
                    EditText qte = goutView.findViewById(R.id.editQuantiteGout);
                    Button btnSup = goutView.findViewById(R.id.btnSupprimerGout);
                    nom.setText(gq.getNom());
                    qte.setText(String.valueOf(gq.getQuantite()));
                    btnSup.setOnClickListener(x -> layoutGouts.removeView(goutView));
                    layoutGouts.addView(goutView);
                }
            }
        }

        btnAjouterGout.setOnClickListener(v -> {
            View goutView = LayoutInflater.from(this).inflate(R.layout.item_gout_special, layoutGouts, false);
            Button btnSup = goutView.findViewById(R.id.btnSupprimerGout);
            btnSup.setOnClickListener(x -> layoutGouts.removeView(goutView));
            layoutGouts.addView(goutView);
        });

        if (estHabituelle) {
            CheckBox checkboxInclure = new CheckBox(this);
            checkboxInclure.setText("Inclure dans la commande du jour");
            checkboxInclure.setChecked(commande != null && commande.isInclureAujourdHui());
            if (commande != null) inclusionMap.put(index, commande.isInclureAujourdHui());

            checkboxInclure.setOnCheckedChangeListener((buttonView, isChecked) -> {
                inclusionMap.put(index, isChecked);
            });

            layoutHabituelles.addView(checkboxInclure);
            layoutHabituelles.addView(bloc);
        } else {
            layoutPonctuelles.addView(bloc);
        }

        btnSupprimerClient.setOnClickListener(v -> {
            if (estHabituelle) {
                layoutHabituelles.removeView(bloc);
            } else {
                layoutPonctuelles.removeView(bloc);
            }
        });
    }

    private void sauvegarderCommandesHabituelles() {
        commandesHabituelles.clear();
        int index = 0;

        for (int i = 0; i < layoutHabituelles.getChildCount(); i++) {
            View view = layoutHabituelles.getChildAt(i);
            if (!(view instanceof LinearLayout)) continue;

            EditText editNom = view.findViewById(R.id.editNomClient);
            EditText editQte = view.findViewById(R.id.editQuantiteTotale);
            EditText editExclu = view.findViewById(R.id.editExclusions);
            LinearLayout layoutGouts = view.findViewById(R.id.layoutGoutsClient);

            if (editNom == null || editQte == null || layoutGouts == null) continue;

            String nom = editNom.getText().toString().trim();
            int qte = 0;
            try {
                qte = Integer.parseInt(editQte.getText().toString().trim());
            } catch (NumberFormatException ignored) {}

            List<String> exclu = new ArrayList<>();
            if (!editExclu.getText().toString().trim().isEmpty()) {
                exclu = Arrays.asList(editExclu.getText().toString().split(","));
            }

            List<GoutQuantite> gouts = new ArrayList<>();
            for (int j = 0; j < layoutGouts.getChildCount(); j++) {
                View gv = layoutGouts.getChildAt(j);
                EditText nomG = gv.findViewById(R.id.editNomGout);
                EditText qtG = gv.findViewById(R.id.editQuantiteGout);
                try {
                    int qG = Integer.parseInt(qtG.getText().toString());
                    gouts.add(new GoutQuantite(nomG.getText().toString(), qG));
                } catch (NumberFormatException ignored) {}
            }

            if (!nom.isEmpty()) {
                CommandeClient cc = new CommandeClient(nom);
                cc.setQuantiteTotale(qte);
                cc.setGoutsExclus(exclu);
                cc.setGouts(gouts);
                cc.setInclureAujourdHui(inclusionMap.getOrDefault(index, false));
                commandesHabituelles.add(cc);
            }

            index++;
        }

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        prefs.edit().putString("commandes_habituelles", new Gson().toJson(commandesHabituelles)).apply();
    }

    private void sauvegarderCommandesPonctuelles() {
        List<CommandeClient> ponctuelles = new ArrayList<>();

        for (int i = 0; i < layoutPonctuelles.getChildCount(); i++) {
            View bloc = layoutPonctuelles.getChildAt(i);

            EditText editNom = bloc.findViewById(R.id.editNomClient);
            EditText editQte = bloc.findViewById(R.id.editQuantiteTotale);
            EditText editExclu = bloc.findViewById(R.id.editExclusions);
            LinearLayout layoutGouts = bloc.findViewById(R.id.layoutGoutsClient);

            if (editNom == null || editQte == null || layoutGouts == null) continue;

            String nom = editNom.getText().toString().trim();
            int qte = 0;
            try {
                qte = Integer.parseInt(editQte.getText().toString().trim());
            } catch (NumberFormatException ignored) {}

            List<String> exclu = new ArrayList<>();
            if (!editExclu.getText().toString().trim().isEmpty()) {
                exclu = Arrays.asList(editExclu.getText().toString().split(","));
            }

            List<GoutQuantite> gouts = new ArrayList<>();
            for (int j = 0; j < layoutGouts.getChildCount(); j++) {
                View gv = layoutGouts.getChildAt(j);
                EditText nomG = gv.findViewById(R.id.editNomGout);
                EditText qtG = gv.findViewById(R.id.editQuantiteGout);
                try {
                    int qG = Integer.parseInt(qtG.getText().toString());
                    gouts.add(new GoutQuantite(nomG.getText().toString(), qG));
                } catch (NumberFormatException ignored) {}
            }

            if (!nom.isEmpty()) {
                CommandeClient cc = new CommandeClient(nom);
                cc.setQuantiteTotale(qte);
                cc.setGoutsExclus(exclu);
                cc.setGouts(gouts);
                ponctuelles.add(cc);
            }
        }

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        prefs.edit().putString("commandes_ponctuelles", new Gson().toJson(ponctuelles)).apply();
    }

    private void chargerCommandesHabituelles() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String json = prefs.getString("commandes_habituelles", null);
        if (json != null) {
            commandesHabituelles = new Gson().fromJson(json, new TypeToken<List<CommandeClient>>() {}.getType());
        }
    }
}
