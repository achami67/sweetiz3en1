package com.example.production.livraison;
import com.example.production.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class CommandesSpecialesActivity extends AppCompatActivity {

    private LinearLayout layoutClients;
    private Button buttonAjouterClient, buttonValiderCommandesSpeciales;
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commandes_speciales);

        layoutClients = findViewById(R.id.layoutClients);
        buttonAjouterClient = findViewById(R.id.buttonAjouterClient);
        buttonValiderCommandesSpeciales = findViewById(R.id.buttonValiderCommandesSpeciales);

        chargerCommandesEnregistrees();

        buttonAjouterClient.setOnClickListener(v -> ajouterBlocClient(null));

        buttonValiderCommandesSpeciales.setOnClickListener(v -> {
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

        List<String> typesDisponibles = new ArrayList<>();
        typesDisponibles.add("Pot");
        typesDisponibles.add("Boîte");
        typesDisponibles.add("Verrine");

        final Runnable[] majListeTypes = new Runnable[1];

        majListeTypes[0] = () -> {
            layoutTypes.removeAllViews();
            for (String type : typesDisponibles) {
                View ligne = LayoutInflater.from(CommandesSpecialesActivity.this).inflate(R.layout.item_type_liste, layoutTypes, false);
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
        EditText editNom = ligneGout.findViewById(R.id.editNomGout);
        EditText editQuantite = ligneGout.findViewById(R.id.editQuantiteGout);
        Button btnSupprGout = ligneGout.findViewById(R.id.btnSupprimerGout);

        editNom.setText(nom);
        editQuantite.setText(String.valueOf(quantite));
        btnSupprGout.setOnClickListener(xx -> parent.removeView(ligneGout));

        parent.addView(ligneGout);
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
                    EditText nom = goutView.findViewById(R.id.editNomGout);
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
