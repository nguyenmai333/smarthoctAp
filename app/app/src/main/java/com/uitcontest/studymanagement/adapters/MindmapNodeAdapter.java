package com.uitcontest.studymanagement.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uitcontest.studymanagement.R;

import java.util.List;

public class MindmapNodeAdapter extends RecyclerView.Adapter<MindmapNodeAdapter.DocumentViewHolder> {

    private List<String> documentList;
    private Context context;

    public MindmapNodeAdapter(Context context, List<String> documentList) {
        this.context = context;
        this.documentList = documentList;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.simple_item_view, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        holder.documentTextView.setText(documentList.get(position));

        holder.itemView.setOnClickListener(v -> {
            // Show dialog
            new AlertDialog.Builder(context)
                    .setTitle("Mindmap Nodes")
                    .setMessage(documentList.get(position))
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    public void updateDocuments(List<String> newDocuments) {
        this.documentList = newDocuments;
        notifyDataSetChanged();
    }

    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        TextView documentTextView;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            documentTextView = itemView.findViewById(R.id.tvItemText);
        }
    }
}