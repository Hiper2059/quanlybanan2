package com.example.quanlybanan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlybanan.R;
import com.example.quanlybanan.database.DBHelper;
import com.example.quanlybanan.model.Item;
import com.example.quanlybanan.model.Payment;
import com.example.quanlybanan.model.Table;

public class ThanhToanActivity extends AppCompatActivity {

    private TextView txtTotalPrice;
    private TextView txtItems;
    private Button btnConfirmPayment;

    private Button btnPayLater;
    private DBHelper db;
    private Table table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thanh_toan);

        // Initialize DB Helper
        db = new DBHelper(this);

        // Find views by their IDs
        txtTotalPrice = findViewById(R.id.txtTotalPrice);
        txtItems = findViewById(R.id.txtItems);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        btnPayLater = findViewById(R.id.btnPayLater);
        // Get the Table object from the intent (This is standard Java)
        // Dòng này cần được giữ nguyên, nó đã đúng với Java
        table = (Table) getIntent().getSerializableExtra("selectedTable");

        // Check if the table object is valid and display its details
        if (table != null) {
            displayOrderDetails();
        } else {
            // Handle error case where table is null
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin bàn.", Toast.LENGTH_LONG).show();
            finish(); // Close activity if there's no data
        }

        // Set up the click listener for the confirm payment button
        btnConfirmPayment.setOnClickListener(v -> handlePaymentConfirmation());
        btnPayLater.setOnClickListener(v -> handlePayLater());
    }

    /**
     * Sửa lại hàm này bằng cú pháp Java thuần túy.
     */
    private void displayOrderDetails() {
        if (table != null) {
            // Calculate total price from the table object
            int totalPrice = table.calculateTotal();
            txtTotalPrice.setText("Tổng tiền: " + totalPrice + " đ");

            // Build the string for the list of ordered items
            StringBuilder itemsStringBuilder = new StringBuilder();
            itemsStringBuilder.append("Danh sách món ăn:\n");

            // Dùng vòng lặp for-each chuẩn của Java
            for (Item item : table.getItems()) {
                if (item.getQuantity() > 0) { // Only show items that were actually ordered
                    itemsStringBuilder.append("- ")
                            .append(item.getName())
                            .append(": ")
                            .append(item.getQuantity())
                            .append(" x ")
                            .append(item.getPrice())
                            .append(" đ\n");
                }
            }

            txtItems.setText(itemsStringBuilder.toString());
        }
    }

    /**
     * Hàm này cũng cần được viết bằng Java thuần túy.
     */
    private void handlePaymentConfirmation() {
        // Gán table vào một biến final để có thể dùng trong lambda (nếu cần)
        // hoặc cứ dùng trực tiếp vì nó là biến instance.
        if (table == null) return; // Exit if table is somehow null

        // 1. Create a Payment object to record the transaction
        double totalAmount = table.calculateTotal();
        String tableIdString = String.valueOf(table.getId());
        long timestamp = System.currentTimeMillis();
        Payment payment = new Payment(tableIdString, totalAmount, timestamp);

        // 2. Gọi phương thức mới để lưu cả thanh toán và chi tiết món ăn
        db.insertPaymentWithDetails(payment, table.getItems());

        // 3. Update the table's status to "Trống" (Empty) or similar
        table.setStatus("Đã thanh toán");
        db.updateTableStatus(table);

        // 4. Show a confirmation message to the user
        Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();

        // 5. Navigate back to MainActivity and clear the activity stack
        Intent intent = new Intent(this, MainActivity.class);
        // This flag clears the stack and brings MainActivity to the front
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish the current activity
    }
    private void handlePayLater() {
        if (table == null) {
            Toast.makeText(this, "Lỗi: Không có thông tin bàn.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Cập nhật trạng thái của bàn thành "Đang có khách"
        // Trạng thái này cho biết bàn có đơn hàng nhưng chưa thanh toán.
        table.setStatus("Đang có khách");

        // 2. Gọi một phương thức trong DBHelper để cập nhật thông tin bàn VÀ các món đã gọi.
        // Đây là bước quan trọng: bạn cần một phương thức để lưu lại cả trạng thái và danh sách món ăn hiện tại của bàn.
        // Xem giải thích về phương thức này ở mục 3.
        db.updateTableOrderAndStatus(table);

        // 3. Hiển thị thông báo cho người dùng
        Toast.makeText(this, "Đã lưu thông tin bàn. Chờ thanh toán.", Toast.LENGTH_LONG).show();

        // 4. Quay lại MainActivity
        navigateBackToMain();
    }

    /**
     * Hàm tiện ích để quay về MainActivity
     */
    private void navigateBackToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
