package com.uitcontest.studymanagement.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uitcontest.studymanagement.R;

public class ProcessedTextViewHolder extends RecyclerView.ViewHolder {

    public TextView tvProcessedText, tvProcessedTextDate;

    public ProcessedTextViewHolder(@NonNull View itemView) {
        super(itemView);
        tvProcessedText = itemView.findViewById(R.id.tvProcessedText);
        tvProcessedTextDate = itemView.findViewById(R.id.tvProcessedTextDate);
    }
}
