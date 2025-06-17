package com.example.production.livraison;

import com.example.production.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.*;

public class CommandesNormalesActivity extends AppCompatActivity {

    protected LinearLayout layoutHabituelles, layoutPonctuelles;
    protected Button buttonAjouterHabituelle, buttonAjouterPonctuelle, buttonValiderCommandes;

    protected List<CommandeClient> commandesHabituelles = new ArrayList<>();
    protected Map<Integer, Boolean> inclusionMap = new HashMap<>();
    protected List<String> nomsGoutsDisponibles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commandes_normales);

        layoutHabituelles = findViewById(R.id.layoutHabituelles);
        layoutPonctuelles = findViewById(R.id.layoutPonctuelles);
        buttonAjouterHabituelle = findViewById(R.id.buttonAjouterHabituelle);
        buttonAjouterPonctuelle = findViewById(R.id.buttonAjouterPonctuelle);
        buttonValiderCommandes = findViewById(R.id.buttonValiderCommandes);

        nomsGoutsDisponibles = chargerNomsGoutsDepuisPrefs();
        chargerCommandesHabituelles();

        for (int i = 0; i < commandesHabituelles.size(); i++) {
            ajouterBlocCommande(commandesHabituelles.get(i), true, i);
        }

        buttonAjouterHabituelle.setOnClickListener(v -> ajouterBlocCommande(null, true, layoutHabituelles.getChildCount() / 2));
        buttonAjouterPonctuelle.setOnClickListener(v -> ajouterBlocCommande(null, false, -1));
        buttonValiderCommandes.setOnClickListener(v -> {
            sauvegarderCommandesHabituelles();
            sauvegarderCommandesPonctuelles();
            startActivity(new Intent(this, CommandesSpecialesActivity.class));
        });
    }

    protected void ajouterBlocCommande(CommandeClient commande, boolean estHabituelle, int index) {
        View bloc = LayoutInflater.from(this).inflate(R.layout.item_commande_client,
                estHabituelle ? layoutHabituelles : layoutPonctuelles, false);

        EditText editNomClient = bloc.findViewById(R.id.editNomClient);
        AutoCompleteTextView editExclusions = bloc.findViewById(R.id.editExclusions);
        EditText editQuantiteTotale = bloc.findViewById(R.id.editQuantiteTotale);
        LinearLayout layoutGouts = bloc.findViewById(R.id.layoutGoutsClient);
        Button btnAjouterGout = bloc.findViewById(R.id.btnAjouterGoutClient);
        Button btnSupprimerClient = bloc.findViewById(R.id.btnSupprimerCommande);

        initialiserAutoComplete(editExclusions);

        if (commande != null) {
            editNomClient.setText(commande.getNomClient());
            editQuantiteTotale.setText(String.valueOf(commande.getQuantiteTotale()));

            if (commande.getGoutsExclus() != null)
                editExclusions.setText(String.join(" ", commande.getGoutsExclus()));

            if (commande.getGouts() != null) {
                for (GoutQuantite gq : commande.getGouts()) {
                    View goutView = LayoutInflater.from(this).inflate(R.layout.item_gout_special, layoutGouts, false);
                    AutoCompleteTextView nom = goutView.findViewById(R.id.editNomGout);
                    EditText qte = goutView.findViewById(R.id.editQuantiteGout);
                    Button btnSup = goutView.findViewById(R.id.btnSupprimerGout);

                    initialiserAutoComplete(nom);
                    nom.setText(gq.getNom());
                    qte.setText(String.valueOf(gq.getQuantite()));
                    btnSup.setOnClickListener(x -> layoutGouts.removeView(goutView));
                    layoutGouts.addView(goutView);
                }
            }
        }

        btnAjouterGout.setOnClickListener(v -> {
            View goutView = LayoutInflater.from(this).inflate(R.layout.item_gout_special, layoutGouts, false);
            AutoCompleteTextView nom = goutView.findViewById(R.id.editNomGout);
            EditText qte = goutView.findViewById(R.id.editQuantiteGout);
            Button btnSup = goutView.findViewById(R.id.btnSupprimerGout);
            initialiserAutoComplete(nom);
            btnSup.setOnClickListener(x -> layoutGouts.removeView(goutView));
            layoutGouts.addView(goutView);
        });

        if (estHabituelle) {
            CheckBox checkboxInclure = new CheckBox(this);
            checkboxInclure.setText("Inclure dans la commande du jour");
            checkboxInclure.setChecked(commande != null && commande.isInclureAujourdHui());

            if (commande != null) {
                inclusionMap.put(index, commande.isInclureAujourdHui());
            }

            checkboxInclure.setOnCheckedChangeListener((buttonView, isChecked) -> inclusionMap.put(index, isChecked));

            layoutHabituelles.addView(checkboxInclure);
            layoutHabituelles.addView(bloc);

            btnSupprimerClient.setOnClickListener(v -> {
                layoutHabituelles.removeView(checkboxInclure);
                layoutHabituelles.removeView(bloc);
            });
        } else {
            layoutPonctuelles.addView(bloc);

            btnSupprimerClient.setOnClickListener(v -> layoutPonctuelles.removeView(bloc));
        }
    }

    private void initialiserAutoComplete(AutoCompleteTextView view) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, nomsGoutsDisponibles);
        view.setAdapter(adapter);
        view.setThreshold(1);

        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) validerGout(view);
        });

        view.setOnDismissListener(() -> validerGout(view));
    }

    private void validerGout(AutoCompleteTextView view) {
        String nom = view.getText().toString().trim();
        if (!nom.isEmpty() && !nomsGoutsDisponibles.contains(nom)) {
            view.setError("Goût inconnu");
        } else {
            view.setError(null);
        }
    }

    private List<String> chargerNomsGoutsDepuisPrefs() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String json = prefs.getString("liste_gouts", null);
        List<String> noms = new ArrayList<>();
        if (json != null) {
            List<PourcentageGout> gouts = new Gson().fromJson(json, new TypeToken<List<PourcentageGout>>() {}.getType());
            for (PourcentageGout g : gouts) {
                noms.add(g.getNomGout());
            }
        }
        return noms;
    }

    protected void sauvegarderCommandesHabituelles() {
        commandesHabituelles.clear();
        int index = 0;

        for (int i = 0; i < layoutHabituelles.getChildCount(); i++) {
            View view = layoutHabituelles.getChildAt(i);
            if (!(view instanceof LinearLayout)) continue;

            EditText editNom = view.findViewById(R.id.editNomClient);
            EditText editQte = view.findViewById(R.id.editQuantiteTotale);
            AutoCompleteTextView editExclu = view.findViewById(R.id.editExclusions);
            LinearLayout layoutGouts = view.findViewById(R.id.layoutGoutsClient);

            List<GoutQuantite> gouts = new ArrayList<>();
            boolean erreur = false;

            for (int j = 0; j < layoutGouts.getChildCount(); j++) {
                View gv = layoutGouts.getChildAt(j);
                AutoCompleteTextView nomG = gv.findViewById(R.id.editNomGout);
                EditText qtG = gv.findViewById(R.id.editQuantiteGout);
                String nom = nomG.getText().toString().trim();
                if (!nomsGoutsDisponibles.contains(nom)) {
                    nomG.setError("Goût inconnu");
                    erreur = true;
                }
                try {
                    int qG = Integer.parseInt(qtG.getText().toString());
                    gouts.add(new GoutQuantite(nom, qG));
                } catch (NumberFormatException ignored) {}
            }

            List<String> exclu = new ArrayList<>();
            String[] excluTokens = editExclu.getText().toString().split("\\s+");
            for (String ex : excluTokens) {
                if (!ex.trim().isEmpty()) {
                    if (!nomsGoutsDisponibles.contains(ex.trim())) {
                        editExclu.setError("Goût exclu inconnu : " + ex.trim());
                        erreur = true;
                    } else {
                        exclu.add(ex.trim());
                    }
                }
            }

            if (erreur) {
                Toast.makeText(this, "Erreur : goûts invalides détectés", Toast.LENGTH_SHORT).show();
                return;
            }

            String nom = editNom.getText().toString().trim();
            int qte = 0;
            try {
                qte = Integer.parseInt(editQte.getText().toString().trim());
            } catch (NumberFormatException ignored) {}

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

    protected void sauvegarderCommandesPonctuelles() {
        List<CommandeClient> ponctuelles = new ArrayList<>();

        for (int i = 0; i < layoutPonctuelles.getChildCount(); i++) {
            View bloc = layoutPonctuelles.getChildAt(i);

            EditText editNom = bloc.findViewById(R.id.editNomClient);
            EditText editQte = bloc.findViewById(R.id.editQuantiteTotale);
            AutoCompleteTextView editExclu = bloc.findViewById(R.id.editExclusions);
            LinearLayout layoutGouts = bloc.findViewById(R.id.layoutGoutsClient);

            List<GoutQuantite> gouts = new ArrayList<>();
            boolean erreur = false;

            for (int j = 0; j < layoutGouts.getChildCount(); j++) {
                View gv = layoutGouts.getChildAt(j);
                AutoCompleteTextView nomG = gv.findViewById(R.id.editNomGout);
                EditText qtG = gv.findViewById(R.id.editQuantiteGout);
                String nom = nomG.getText().toString().trim();
                if (!nomsGoutsDisponibles.contains(nom)) {
                    nomG.setError("Goût inconnu");
                    erreur = true;
                }
                try {
                    int qG = Integer.parseInt(qtG.getText().toString());
                    gouts.add(new GoutQuantite(nom, qG));
                } catch (NumberFormatException ignored) {}
            }

            List<String> exclu = new ArrayList<>();
            String[] excluTokens = editExclu.getText().toString().split("\\s+");
            for (String ex : excluTokens) {
                if (!ex.trim().isEmpty()) {
                    if (!nomsGoutsDisponibles.contains(ex.trim())) {
                        editExclu.setError("Goût exclu inconnu : " + ex.trim());
                        erreur = true;
                    } else {
                        exclu.add(ex.trim());
                    }
                }
            }

            if (erreur) {
                Toast.makeText(this, "Erreur : goûts invalides détectés", Toast.LENGTH_SHORT).show();
                return;
            }

            String nom = editNom.getText().toString().trim();
            int qte = 0;
            try {
                qte = Integer.parseInt(editQte.getText().toString().trim());
            } catch (NumberFormatException ignored) {}

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

    protected void chargerCommandesHabituelles() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String json = prefs.getString("commandes_habituelles", null);
        if (json != null) {
            commandesHabituelles = new Gson().fromJson(json, new TypeToken<List<CommandeClient>>(){}.getType());
        }
    }
}
