package com.example.production.livraison;
import com.example.production.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

import java.util.List;

public class SousCommandeAdapter extends BaseAdapter {

    private final Context context;
    private final List<SousCommande> sousCommandes;

    public SousCommandeAdapter(Context context, List<SousCommande> sousCommandes) {
        this.context = context;
        this.sousCommandes = sousCommandes;
    }

    @Override
    public int getCount() {
        return sousCommandes.size();
    }

    @Override
    public Object getItem(int position) {
        return sousCommandes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SousCommande sousCommande = sousCommandes.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sous_commande, parent, false);
        }

        EditText editTypeSelectionne = convertView.findViewById(R.id.editTypeSelectionne);
        editTypeSelectionne.setText(sousCommande.getTypeNom());

        // Tu peux aussi remplir les goûts ici si besoin
        // LinearLayout layoutGouts = convertView.findViewById(R.id.layoutGouts);
        // ...

        return convertView;
    }
}
