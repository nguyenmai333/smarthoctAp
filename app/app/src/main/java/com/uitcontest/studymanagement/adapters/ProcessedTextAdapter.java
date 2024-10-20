package com.uitcontest.studymanagement.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uitcontest.studymanagement.R;
import com.uitcontest.studymanagement.models.ProcessedTextModel;
import com.uitcontest.studymanagement.viewholder.ProcessedTextViewHolder;

import java.util.List;

public class ProcessedTextAdapter extends RecyclerView.Adapter<ProcessedTextViewHolder> {

    Context context;
    List<ProcessedTextModel> processedTextModelList;

    public ProcessedTextAdapter(Context context, List<ProcessedTextModel> processedTextModelList) {
        this.context = context;
        this.processedTextModelList = processedTextModelList;
    }

    @NonNull
    @Override
    public ProcessedTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProcessedTextViewHolder(LayoutInflater.from(context).inflate(R.layout.processed_text_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProcessedTextViewHolder holder, int position) {
        holder.tvProcessedText.setText(processedTextModelList.get(position).getText());
        holder.tvProcessedTextDate.setText(processedTextModelList.get(position).getDate());

        // Set on click listener
        holder.itemView.setOnClickListener(v -> {
            // Show Dialog to show processed text content
            showProcessedTextContentDialog(processedTextModelList.get(position).getText());
        });
    }

    private void showProcessedTextContentDialog(String text) {
        View view = LayoutInflater.from(context).inflate(R.layout.processed_text_dialog, null);
        TextView tvProcessedTextContent = view.findViewById(R.id.tvProcessedTextDialog);
        tvProcessedTextContent.setText(text);

        // Show dialog
        new AlertDialog.Builder(context)
                .setView(view)
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                .show();

        // Set content copy listener
        tvProcessedTextContent.setOnClickListener(v -> {
            // Copy text to clipboard
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Processed Text", text);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Copied to Clipboard", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return processedTextModelList.size();
    }
}
