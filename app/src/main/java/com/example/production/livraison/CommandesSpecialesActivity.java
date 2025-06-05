package com.example.production.livraison;

import com.example.production.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.*;

public class CommandesSpecialesActivity extends AppCompatActivity {

    private LinearLayout layoutClients;
    private Button buttonAjouterClient, buttonValiderCommandesSpeciales;
    private final Gson gson = new Gson();
    private List<String> nomsGoutsDisponibles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commandes_speciales);

        layoutClients = findViewById(R.id.layoutClients);
        buttonAjouterClient = findViewById(R.id.buttonAjouterClient);
        buttonValiderCommandesSpeciales = findViewById(R.id.buttonValiderCommandesSpeciales);

        nomsGoutsDisponibles = chargerNomsGoutsDepuisPrefs();

        chargerCommandesEnregistrees();

        buttonAjouterClient.setOnClickListener(v -> ajouterBlocClient(null));

        buttonValiderCommandesSpeciales.setOnClickListener(v -> {
            if (contientGoutInvalide()) {
                Toast.makeText(this, "Certains goûts ne sont pas valides, veuillez corriger avant de valider.", Toast.LENGTH_LONG).show();
                return;
            }
            enregistrerCommandes();
            Toast.makeText(this, "Commandes spéciales enregistrées", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ResumeCommandesActivity.class));
        });
    }

    private void ajouterBlocClient(String nomInitial) {
        View blocClient = LayoutInflater.from(this).inflate(R.layout.item_commande_speciale_client, layoutClients, false);
        layoutClients.addView(blocClient);

        EditText editNomClient = blocClient.findViewById(R.id.editNomClient);
        if (nomInitial != null) editNomClient.setText(nomInitial);

        LinearLayout layoutGouts = blocClient.findViewById(R.id.layoutGouts);
        Button btnAjouterType = blocClient.findViewById(R.id.buttonAjouterType);
        Button btnSupprimer = blocClient.findViewById(R.id.buttonSupprimerClient);

        btnAjouterType.setOnClickListener(v -> ajouterBlocSousCommande(layoutGouts, null));
        btnSupprimer.setOnClickListener(v -> layoutClients.removeView(blocClient));
    }

    private void ajouterBlocSousCommande(LinearLayout parent, SousCommande commandeExistante) {
        View blocType = LayoutInflater.from(this).inflate(R.layout.item_sous_commande, parent, false);
        parent.addView(blocType);

        EditText editNouveauType = blocType.findViewById(R.id.editNouveauType);
        Button btnAjouterTypePerso = blocType.findViewById(R.id.btnAjouterTypePerso);
        LinearLayout layoutTypes = blocType.findViewById(R.id.layoutTypes);
        EditText editTypeSelectionne = blocType.findViewById(R.id.editTypeSelectionne);
        LinearLayout layoutGoutsInterne = blocType.findViewById(R.id.layoutGouts);
        Button btnAjouterGout = blocType.findViewById(R.id.btnAjouterGout);
        ImageButton btnSupprimerBlocType = blocType.findViewById(R.id.btnSupprimerBlocType);

        btnSupprimerBlocType.setOnClickListener(v -> parent.removeView(blocType));

        List<String> typesDisponibles = new ArrayList<>(Arrays.asList("Pot", "Boîte", "Verrine"));

        final Runnable[] majListeTypes = new Runnable[1];
        majListeTypes[0] = () -> {
            layoutTypes.removeAllViews();
            for (String type : typesDisponibles) {
                View ligne = LayoutInflater.from(this).inflate(R.layout.item_type_liste, layoutTypes, false);
                TextView textNom = ligne.findViewById(R.id.textNomType);
                ImageView btnSupprimer = ligne.findViewById(R.id.btnSupprimerType);

                textNom.setText(type);
                textNom.setOnClickListener(v -> editTypeSelectionne.setText(type));
                btnSupprimer.setOnClickListener(v2 -> {
                    typesDisponibles.remove(type);
                    if (editTypeSelectionne.getText().toString().equals(type)) {
                        editTypeSelectionne.setText("");
                    }
                    majListeTypes[0].run();
                });

                layoutTypes.addView(ligne);
            }
        };

        btnAjouterTypePerso.setOnClickListener(v -> {
            String nouveauType = editNouveauType.getText().toString().trim();
            if (!nouveauType.isEmpty() && !typesDisponibles.contains(nouveauType)) {
                typesDisponibles.add(nouveauType);
                editTypeSelectionne.setText(nouveauType);
                editNouveauType.setText("");
                majListeTypes[0].run();
            }
        });

        if (commandeExistante != null) {
            String type = commandeExistante.getTypeNom();
            if (type != null && !typesDisponibles.contains(type)) {
                typesDisponibles.add(type);
            }
            editTypeSelectionne.setText(type);
            for (GoutQuantite gq : commandeExistante.getGouts()) {
                ajouterGout(layoutGoutsInterne, gq.getNom(), gq.getQuantite());
            }
        }

        majListeTypes[0].run();

        btnAjouterGout.setOnClickListener(g -> ajouterGout(layoutGoutsInterne, "", 0));
    }

    private void ajouterGout(LinearLayout parent, String nom, int quantite) {
        View ligneGout = LayoutInflater.from(this).inflate(R.layout.item_gout_special, parent, false);
        AutoCompleteTextView editNom = ligneGout.findViewById(R.id.editNomGout);
        EditText editQuantite = ligneGout.findViewById(R.id.editQuantiteGout);
        Button btnSupprGout = ligneGout.findViewById(R.id.btnSupprimerGout);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                nomsGoutsDisponibles
        );
        editNom.setAdapter(adapter);
        editNom.setThreshold(1); // suggestion dès 1 caractère

        editNom.setText(nom);
        editQuantite.setText(String.valueOf(quantite));

        btnSupprGout.setOnClickListener(xx -> parent.removeView(ligneGout));

        // Validation en temps réel
        editNom.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override public void afterTextChanged(Editable s) {
                String saisie = s.toString().trim();
                if (!nomsGoutsDisponibles.contains(saisie)) {
                    editNom.setBackgroundColor(Color.argb(60, 255, 0, 0)); // léger rouge
                    editNom.setError("Goût non reconnu");
                } else {
                    editNom.setBackgroundColor(Color.TRANSPARENT);
                    editNom.setError(null);
                }
            }
        });

        parent.addView(ligneGout);
    }

    private boolean contientGoutInvalide() {
        for (int i = 0; i < layoutClients.getChildCount(); i++) {
            View client = layoutClients.getChildAt(i);
            LinearLayout layoutTypes = client.findViewById(R.id.layoutGouts);

            for (int j = 0; j < layoutTypes.getChildCount(); j++) {
                View blocType = layoutTypes.getChildAt(j);
                LinearLayout layoutGouts = blocType.findViewById(R.id.layoutGouts);

                for (int k = 0; k < layoutGouts.getChildCount(); k++) {
                    View goutView = layoutGouts.getChildAt(k);
                    AutoCompleteTextView nom = goutView.findViewById(R.id.editNomGout);

                    if (!nomsGoutsDisponibles.contains(nom.getText().toString().trim())) {
                        return true;
                    }
                }
            }
        }
        return false;
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

    private void enregistrerCommandes() {
        List<CommandeSpecialeClient> sauvegarde = new ArrayList<>();

        for (int i = 0; i < layoutClients.getChildCount(); i++) {
            View blocClient = layoutClients.getChildAt(i);
            EditText editNom = blocClient.findViewById(R.id.editNomClient);
            LinearLayout layoutTypes = blocClient.findViewById(R.id.layoutGouts);

            CommandeSpecialeClient client = new CommandeSpecialeClient(editNom.getText().toString());

            for (int j = 0; j < layoutTypes.getChildCount(); j++) {
                View blocType = layoutTypes.getChildAt(j);
                EditText editTypeSelectionne = blocType.findViewById(R.id.editTypeSelectionne);
                LinearLayout layoutGouts = blocType.findViewById(R.id.layoutGouts);

                SousCommande sousCommande = new SousCommande();
                sousCommande.setTypeNom(editTypeSelectionne.getText().toString());

                for (int k = 0; k < layoutGouts.getChildCount(); k++) {
                    View goutView = layoutGouts.getChildAt(k);
                    AutoCompleteTextView nom = goutView.findViewById(R.id.editNomGout);
                    EditText qte = goutView.findViewById(R.id.editQuantiteGout);

                    if (!nom.getText().toString().isEmpty() && !qte.getText().toString().isEmpty()) {
                        try {
                            int quantite = Integer.parseInt(qte.getText().toString());
                            sousCommande.getGouts().add(new GoutQuantite(nom.getText().toString(), quantite));
                        } catch (NumberFormatException ignored) {}
                    }
                }

                client.getSousCommandes().add(sousCommande);
            }

            sauvegarde.add(client);
        }

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        prefs.edit().putString("commandes_speciales", gson.toJson(sauvegarde)).apply();
    }

    private void chargerCommandesEnregistrees() {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        String json = prefs.getString("commandes_speciales", null);

        if (json != null) {
            List<CommandeSpecialeClient> anciens = gson.fromJson(json, new TypeToken<List<CommandeSpecialeClient>>() {}.getType());
            for (CommandeSpecialeClient c : anciens) {
                View blocClient = LayoutInflater.from(this).inflate(R.layout.item_commande_speciale_client, layoutClients, false);
                layoutClients.addView(blocClient);

                EditText editNom = blocClient.findViewById(R.id.editNomClient);
                editNom.setText(c.getNomClient());

                LinearLayout layoutGouts = blocClient.findViewById(R.id.layoutGouts);

                for (SousCommande sc : c.getSousCommandes()) {
                    ajouterBlocSousCommande(layoutGouts, sc);
                }

                Button btnSupprimer = blocClient.findViewById(R.id.buttonSupprimerClient);
                btnSupprimer.setOnClickListener(v -> layoutClients.removeView(blocClient));

                Button btnAjouterType = blocClient.findViewById(R.id.buttonAjouterType);
                btnAjouterType.setOnClickListener(v -> ajouterBlocSousCommande(layoutGouts, null));
            }
        }
    }
}
