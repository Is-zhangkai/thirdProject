package com.example.thirdproject.tool;

import android.content.Context;
import android.icu.text.IDNA;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thirdproject.R;

import java.util.List;
import java.util.regex.Pattern;

public class ErrorAdapter extends RecyclerView.Adapter<ErrorAdapter.ViewHolder> {
    private Context context;
    public ErrorAdapter(Context context) {

        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_error, viewGroup, false);
        return new ErrorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ErrorAdapter.ViewHolder holder, int i) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}