package com.uitcontest.studymanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new DocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        holder.documentTextView.setText(documentList.get(position));
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
            documentTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}