package com.example.production.livraison;
import com.example.production.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TypeListeAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> types;
    private final OnTypeClickListener listener;

    public interface OnTypeClickListener {
        void onTypeSelected(String type);
        void onTypeDeleted(String type);
    }

    public TypeListeAdapter(Context context, List<String> types, OnTypeClickListener listener) {
        super(context, 0, types);
        this.context = context;
        this.types = types;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String type = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_type_liste, parent, false);
        }

        TextView textNomType = convertView.findViewById(R.id.textNomType);
        ImageView btnSupprimer = convertView.findViewById(R.id.btnSupprimerType);

        textNomType.setText(type);

        textNomType.setOnClickListener(v -> {
            if (listener != null) listener.onTypeSelected(type);
        });

        btnSupprimer.setOnClickListener(v -> {
            if (listener != null) listener.onTypeDeleted(type);
        });

        return convertView;
    }
}
