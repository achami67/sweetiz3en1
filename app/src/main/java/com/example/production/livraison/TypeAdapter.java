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

public class TypeAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final List<String> types;
    private final OnTypeDeleteListener listener;

    public interface OnTypeDeleteListener {
        void onDelete(String type);
    }

    public TypeAdapter(Context context, List<String> types, OnTypeDeleteListener listener) {
        super(context, 0, types);
        this.context = context;
        this.types = types;
        this.listener = listener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // important pour que la dropdown ait aussi le bouton croix
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        String type = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_type_suggestion, parent, false);
        }

        TextView textType = convertView.findViewById(R.id.textType);
        ImageView btnDelete = convertView.findViewById(R.id.btnDeleteType);

        textType.setText(type);

        btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(type);
            }
        });

        return convertView;
    }
}
