package com.example.production.comptabilite;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class RapportAdapter extends ArrayAdapter<RapportLivraison> {

    public RapportAdapter(Context context, List<RapportLivraison> rapports) {
        super(context, 0, rapports);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        RapportLivraison rapport = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        }

        TextView text1 = convertView.findViewById(android.R.id.text1);
        TextView text2 = convertView.findViewById(android.R.id.text2);

        if (rapport != null) {
            text1.setText(rapport.getClient() + " - " + rapport.getQuantite() + " unités");
            text2.setText("Montant : " + rapport.getMontant() + " € | " + (rapport.isEstPaye() ? "Payé" : "Non payé"));
        }

        return convertView;
    }
}
