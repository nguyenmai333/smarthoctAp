package com.uitcontest.studymanagement.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.uitcontest.studymanagement.R;
import com.uitcontest.studymanagement.models.MindmapModel;
import com.uitcontest.studymanagement.models.MindmapNodeModel;

import java.util.List;

import me.jagar.mindmappingandroidlibrary.Views.Item;
import me.jagar.mindmappingandroidlibrary.Views.ItemLocation;
import me.jagar.mindmappingandroidlibrary.Views.MindMappingView;
import me.jagar.mindmappingandroidlibrary.Zoom.ZoomLayout;

public class MindmapViewActivity extends AppCompatActivity {

    private MindMappingView mindMappingView;
    private LinearLayout.LayoutParams layoutParams;
    private ZoomLayout zoomLayout;
    private Item rootNodeItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mindmap_view);

        // Initialize view
        initializeView();

        // Retrieve the mindmap data from the previous activity
        String mindmapDataJson = getIntent().getStringExtra("mindmap");
        Log.d("MindmapModel received", "MindmapModel data: " + mindmapDataJson);

        // Parse the JSON object
        Gson gson = new Gson();
        MindmapModel mindmapModel = gson.fromJson(mindmapDataJson, MindmapModel.class);
        Log.d("MindmapModel Main Topic", "Main Topic: " + mindmapModel.getMainTopic());
        Log.d("MindmapModel Childs Nodes", "Childs Nodes: " + mindmapModel.getChildNodes().size());

        // Create root node from MindMap
        rootNodeItem = new Item(MindmapViewActivity.this, null, mindmapModel.getMainTopic(), true);
        Log.d("Root Node", "Root Node: " + rootNodeItem.getContent().getText().toString());

        // Render the mind map starting from the root node
        renderMindmap(rootNodeItem, mindmapModel.getChildNodes());

        // Set the connection lines color and width
        String colorHex = "#" + Integer.toHexString(ContextCompat.getColor(this, R.color.purple_200) & 0x00ffffff);
        mindMappingView.setConnectionArrowSize(10);
        mindMappingView.setConnectionColor(colorHex);
        mindMappingView.setConnectionWidth(10);
        mindMappingView.setConnectionCircRadius(5);

        // Format the mind map structure
        formatStructure(rootNodeItem);

        zoomLayout.zoomTo(1.0f, true);
    }


    private void renderMindmap(Item rootNodeItem, List<MindmapNodeModel> childNodes) {
        // Format the root node
        formatNodeItem(rootNodeItem);

        // Add root node to MindMappingView
        mindMappingView.addCentralItem(rootNodeItem, true);

        // Add child nodes
        for (MindmapNodeModel childNode : childNodes) {
            addChildNodes(rootNodeItem, childNode);
        }
    }

    private void addChildNodes(Item rootNodeItem, MindmapNodeModel childNode) {
        // Create child node model
        Item childNodeItem = new Item(MindmapViewActivity.this, null, childNode.getMainContent(), true);

        // Format the child node
        formatNodeItem(childNodeItem);

        // Add child node to MindMappingView
        mindMappingView.addItem(childNodeItem, rootNodeItem, 0, 0, ItemLocation.BOTTOM, true, null);

        // Add child of child nodes
        if (childNode.getChilds() != null) {
            for (String childOfChildNode : childNode.getChilds()) {
                // Create child of child node model
                MindmapNodeModel childOfChildNodeModel = new MindmapNodeModel(childOfChildNode, null);

                // Add child of child node to MindMappingView
                addChildNodes(childNodeItem, childOfChildNodeModel);
            }
        }
    }

    private void formatNodeItem(Item nodeItem) {
        // Adjust the layout of the Item
        nodeItem.setBackground(ContextCompat.getDrawable(MindmapViewActivity.this, R.drawable.rounded_transparent_bg));
        nodeItem.setBackgroundTintList(ContextCompat.getColorStateList(MindmapViewActivity.this, R.color.teal_200));
        nodeItem.setPadding(20, 20, 20, 20);
        nodeItem.setLayoutParams(layoutParams);

        // Set LayoutParams with fixed width and wrapping content height
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 330, getResources().getDisplayMetrics()),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 330, getResources().getDisplayMetrics());
        nodeItem.setLayoutParams(params);

        // Set the text color
        nodeItem.getContent().setTextColor(ContextCompat.getColor(MindmapViewActivity.this, R.color.black));
        nodeItem.getContent().setTextSize(16);

        // Set the text style
        nodeItem.getContent().setTypeface(nodeItem.getContent().getTypeface(), Typeface.NORMAL);

        // Set maximum width to ensure it doesn't exceed 330dp
        nodeItem.getContent().setMaxWidth(params.width);
    }

    private void formatStructure(Item rootNodeItem) {
        // Set the child of root to space evenly
        rootNodeItem.getBottomChildItems().forEach(childItem -> {
            // Set child to left and right of root alternatively
            if (rootNodeItem.getBottomChildItems().indexOf(childItem) % 2 == 0) {
                childItem.setX(rootNodeItem.getX() - childItem.getMeasuredWidth());
            } else {
                childItem.setX(rootNodeItem.getX() + childItem.getMeasuredWidth());
            }
            childItem.setY(rootNodeItem.getY() * 2 + childItem.getMeasuredHeight() * 5 + rootNodeItem.getMeasuredHeight() * 3);
            if (childItem.getBottomChildItems().size() > 0) {
                formatStructure(childItem);
            }
        });
    }

    private void initializeView() {
        mindMappingView = findViewById(R.id.mindMappingView);
        zoomLayout = findViewById(R.id.zoomLayout);

        // Layout params for the MindMappingView
        layoutParams = new LinearLayout.LayoutParams(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 330, getResources().getDisplayMetrics()),
                LinearLayout.LayoutParams.WRAP_CONTENT);
    }


}