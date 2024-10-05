package com.uitcontest.studymanagement.activities;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.uitcontest.studymanagement.R;

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
        String mindmapDataJson = getIntent().getStringExtra("mindmapData");
        //String mindmapDataJson = "{ \"root\":{ \"content\":\"Gia đình luôn là nguồn hỗ trợ và động viên lớn nhất trong những thời điểm khó khăn. Cha mẹ và anh chị em của tôi đã dành thời gian trò chuyện với tôi, chia sẻ kinh nghiệm của tôi và giúp tôi tìm ra giải pháp hiệu quả.\", \"childs\":[ { \"content\":\"Gia đình là nơi mọi người tìm thấy sự an ủi và hỗ trợ vô điều kiện. Những giờ phút quây quần bên nhau, cùng tiếng cười và những cuộc trò chuyện vui vẻ, tạo ra những kỷ niệm quý giá mà chúng ta trân trọng.\", \"childs\":[ { \"content\":\"Gia đình tôi luôn là nguồn động viên và khích lệ lớn nhất trong những lúc khó khăn. Mỗi gia đình đều có truyền thống và phong tục riêng, và gia đình tôi cũng không ngoại lệ. Sự ủng hộ và tình yêu thương vô điều kiện từ gia đình đã giúp tôi cảm thấy mạnh mẽ và mạnh mẽ hơn để đối mặt với thử thách.\", \"childs\":[] }, { \"content\":\"Gia đình là nơi mọi người tìm thấy sự an ủi và hỗ trợ vô điều kiện. Những bữa ăn này không chỉ là cơ hội để thưởng thức những món ăn ngon mà còn là thời gian để chúng ta chia sẻ những câu chuyện, cảm xúc và trải nghiệm trong tuần.\", \"childs\":[] } ] }, { \"content\":\"Vào cuối tuần, gia đình chúng tôi thường tổ chức các bữa tiệc đoàn tụ, nơi mọi người có thể ngồi lại sau một tuần bận rộn. Các bữa ăn thường được chuẩn bị công phu với các món ăn truyền thống như thịt nướng, kem chua và cơm gà. Một trong những truyền thống yêu thích của chúng tôi là tổ chức tiệc mừng Tết Nguyên đán tại nhà. Nhân dịp này, mọi người trong gia đình đến từ những nơi khác nhau, cùng nhau chuẩn bị các món ăn truyền thống.\", \"childs\":[] } ] } }";
        Log.d("Mindmap", "Mindmap data: " + mindmapDataJson);

        // Parse the JSON object
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(mindmapDataJson, JsonObject.class);

        // Create root node from JSON
        JsonObject rootNode = jsonObject.getAsJsonObject("root");

        // Create root node
        rootNodeItem = new Item(MindmapViewActivity.this, null, rootNode.get("content").getAsString(), true);

        // Render the mind map starting from the root node
        renderMindmap(rootNodeItem, rootNode);

        // Set the connection lines color and width
        String colorHex = "#" + Integer.toHexString(ContextCompat.getColor(this, R.color.teal_200) & 0x00ffffff);
        mindMappingView.setConnectionColor(colorHex);
        mindMappingView.setConnectionWidth(10);
        mindMappingView.setConnectionCircRadius(5);

        // Format the mind map structure
        formatStructure(rootNodeItem);
    }


    private void renderMindmap(Item rootNodeItem, JsonObject rootNode) {
        // Format the root node
        formatNodeItem(rootNodeItem);

        // Add root node to MindMappingView
        mindMappingView.addCentralItem(rootNodeItem, true);

        // Add child nodes
        if (rootNode.has("childs")) {
            for (JsonElement childElement : rootNode.get("childs").getAsJsonArray()) {
                addChildNodes(rootNodeItem, childElement.getAsJsonObject());
            }
        }
    }

    private void addChildNodes(Item rootNodeItem, JsonObject childNode) {
        // Create child node model
        Item childNodeItem = new Item(MindmapViewActivity.this, null, childNode.get("content").getAsString(), true);

        // Format the child node
        formatNodeItem(childNodeItem);

        // Add child node to MindMappingView
        mindMappingView.addItem(childNodeItem, rootNodeItem, 0, 0, ItemLocation.BOTTOM, true, null);

        // Add child of child nodes
        if (childNode.has("childs")) {
            for (JsonElement grandChildElement : childNode.get("childs").getAsJsonArray()) {
                addChildNodes(childNodeItem, grandChildElement.getAsJsonObject());
            }
        }
    }

    private void formatNodeItem(Item nodeItem) {
        // Adjust the layout of the Item
        nodeItem.setBackground(ContextCompat.getDrawable(MindmapViewActivity.this, R.drawable.rounded_transparent_bg));
        nodeItem.setBackgroundTintList(ContextCompat.getColorStateList(MindmapViewActivity.this, R.color.teal_200));
        nodeItem.setPadding(20, 20, 20, 20);
        nodeItem.setLayoutParams(layoutParams);

        // Set the text color
        nodeItem.getContent().setTextColor(ContextCompat.getColor(MindmapViewActivity.this, R.color.black));
        nodeItem.getContent().setTextSize(16);
    }

    private void formatStructure(Item rootNodeItem) {
        // Set the child of root to space evenly
        rootNodeItem.getBottomChildItems().forEach(childItem -> {
            // Set child to left and right of root alternatively
            if (rootNodeItem.getBottomChildItems().indexOf(childItem) % 2 == 0) {
                childItem.setX(rootNodeItem.getX() - childItem.getMeasuredWidth() / 3);
            } else {
                childItem.setX(rootNodeItem.getX() + childItem.getMeasuredWidth() / 5);
            }
            childItem.setY(rootNodeItem.getY() * 2 + childItem.getMeasuredHeight() * 3 + rootNodeItem.getMeasuredHeight() * 3);
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