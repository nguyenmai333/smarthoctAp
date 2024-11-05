package com.uitcontest.studymanagement.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uitcontest.studymanagement.R;
import com.uitcontest.studymanagement.models.ProcessedTextModel;

import java.util.List;


public class AddDocumentAdapter extends RecyclerView.Adapter<AddDocumentAdapter.AddDocumentViewHolder> {
    private List<ProcessedTextModel> processedTextModelList;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private Context context;

    public AddDocumentAdapter(Context context, List<ProcessedTextModel> processedTextModelList) {
        this.context = context;
        this.processedTextModelList = processedTextModelList;
    }

    @NonNull
    @Override
    public AddDocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_document_item_view, parent, false);
        return new AddDocumentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddDocumentViewHolder holder, int position) {
        ProcessedTextModel processedTextModel = processedTextModelList.get(position);
        holder.documentTextView.setText(processedTextModel.getText());
        holder.documentDateTextView.setText(processedTextModel.getDate());

        // Highlight selected item
        holder.itemView.setBackgroundColor(selectedPosition == position ? Color.LTGRAY : Color.WHITE);

        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPosition);
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(selectedPosition);
        });
    }

    public ProcessedTextModel getSelectedDocument() {
        if (selectedPosition != RecyclerView.NO_POSITION) {
            return processedTextModelList.get(selectedPosition);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return processedTextModelList.size();
    }

    public static class AddDocumentViewHolder extends RecyclerView.ViewHolder {
        TextView documentTextView, documentDateTextView;

        public AddDocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            documentTextView = itemView.findViewById(R.id.tvAddDocumentText);
            documentDateTextView = itemView.findViewById(R.id.tvAddDocumentTextDate);
        }
    }
}
