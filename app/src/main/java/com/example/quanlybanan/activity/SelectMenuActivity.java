package com.example.quanlybanan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.database.Cursor;

import com.example.quanlybanan.R;
import com.example.quanlybanan.adapter.RecyclerViewAdapter;
import com.example.quanlybanan.database.DBHelper;
import com.example.quanlybanan.model.Item;
import com.example.quanlybanan.model.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * SelectMenuActivity (phiên bản đơn giản hóa)
 * - Không cần SelectedItemAdapter.
 * - Hiển thị các món đã chọn trong một TextView.
 */
public class SelectMenuActivity extends AppCompatActivity implements RecyclerViewAdapter.OnOrderUpdateListener {

    // UI Elements
    private TextView txtTableInfo;
    private Button btnThanhToan;
    private RecyclerView recyclerViewMenu;
    private TextView txtSelectedItems; // << THAY ĐỔI: Dùng TextView thay vì RecyclerView
    private TextView txtTotalPrice;

    // Data & Helpers
    private Table table;
    private RecyclerViewAdapter menuAdapter;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_menu);

        dbHelper = new DBHelper(this);

        table = (Table) getIntent().getSerializableExtra("selectedTable");
        if (table == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin bàn!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Ánh xạ các thành phần giao diện
        txtTableInfo = findViewById(R.id.txtTableInfo);
        btnThanhToan = findViewById(R.id.btnThanhToan);
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu);
        txtSelectedItems = findViewById(R.id.txtSelectedItems); // << THAY ĐỔI: Ánh xạ TextView mới
        txtTotalPrice = findViewById(R.id.txtTotalPrice);

        txtTableInfo.setText("Đang gọi món cho Bàn số: " + table.getId());

        // Cài đặt RecyclerView cho MENU của quán
        setupMenuRecyclerView();

        // Cập nhật giao diện lần đầu (hiển thị món đã có sẵn nếu có)
        updateUi();

        // Cài đặt sự kiện cho nút Thanh toán
        btnThanhToan.setOnClickListener(v -> {
            if (table.getItems() == null || table.getItems().isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn món trước khi thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(SelectMenuActivity.this, ThanhToanActivity.class);
            intent.putExtra("selectedTable", table);
            startActivity(intent);
        });
    }

    private void setupMenuRecyclerView() {
        List<Item> menuItems = getMenuItemsFromDB();
        menuAdapter = new RecyclerViewAdapter(menuItems, table, this);
        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMenu.setAdapter(menuAdapter);
    }

    /**
     * Hàm trung tâm để cập nhật giao diện, đã được sửa đổi để dùng TextView.
     */
    private void updateUi() {
        // 1. Cập nhật danh sách các món đã chọn
        StringBuilder selectedItemsText = new StringBuilder();
        if (table.getItems() != null && !table.getItems().isEmpty()) {
            for (Item item : table.getItems()) {
                // Tạo chuỗi dạng: "- Phở Bò (x2)\n"
                selectedItemsText.append("- ")
                        .append(item.getName())
                        .append(" (x")
                        .append(item.getQuantity())
                        .append(")\n");
            }
        } else {
            selectedItemsText.append("Chưa có món nào được chọn.");
        }
        txtSelectedItems.setText(selectedItemsText.toString());

        // 2. Báo cho Adapter của MENU QUÁN biết để cập nhật số lượng "Đã chọn".
        if (menuAdapter != null) {
            menuAdapter.notifyDataSetChanged();
        }

        // 3. Tính lại tổng tiền và hiển thị.
        int total = table.calculateTotal();
        txtTotalPrice.setText("Tổng tiền: " + total + " đ");
    }

    /**
     * Hàm được gọi từ Adapter mỗi khi có thay đổi.
     */
    @Override
    public void onOrderUpdated() {
        updateUi();
    }

    private List<Item> getMenuItemsFromDB() {
        List<Item> items = new ArrayList<>();
        Cursor cursor = dbHelper.getAllMenuItems();
        if (cursor != null) {
            int nameCol = cursor.getColumnIndex("name");
            int priceCol = cursor.getColumnIndex("price");
            int imageCol = cursor.getColumnIndex("image_path");
            if (nameCol != -1 && priceCol != -1 && imageCol != -1) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(nameCol);
                    int price = cursor.getInt(priceCol);
                    String imagePath = cursor.getString(imageCol);
                    items.add(new Item(name, price, imagePath));
                }
            }
            cursor.close();
        }
        return items;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}