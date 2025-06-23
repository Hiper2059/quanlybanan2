package com.example.quanlybanan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.quanlybanan.model.Item;
import com.example.quanlybanan.model.Payment;
import com.example.quanlybanan.model.RevenueReportItem;
import com.example.quanlybanan.model.Table;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "dining.db";
    // TĂNG VERSION LÊN 4 ĐỂ KÍCH HOẠT onUpgrade và tạo bảng mới
    private static final int DB_VERSION = 4;
    private static final String TABLE_CURRENT_ORDERS = "current_orders";
    private static final String KEY_CURRENT_ORDER_ID = "id";
    private static final String KEY_CURRENT_ORDER_TABLE_ID = "table_id";
    private static final String KEY_CURRENT_ORDER_ITEM_NAME = "item_name";
    private static final String KEY_CURRENT_ORDER_ITEM_PRICE = "item_price";
    private static final String KEY_CURRENT_ORDER_ITEM_QUANTITY = "quantity";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE, password TEXT, is_owner INTEGER)");
        db.execSQL("CREATE TABLE tables (id INTEGER PRIMARY KEY AUTOINCREMENT, seats INTEGER, status TEXT)");
        db.execSQL("CREATE TABLE payments (payment_id INTEGER PRIMARY KEY AUTOINCREMENT, tableId TEXT, amount REAL, timestamp INTEGER)");
        db.execSQL("CREATE TABLE order_details (detail_id INTEGER PRIMARY KEY AUTOINCREMENT, payment_id INTEGER, item_name TEXT, item_price INTEGER, quantity INTEGER, FOREIGN KEY(payment_id) REFERENCES payments(payment_id))");
        // BẢNG MỚI CHO MÓN ĂN
        db.execSQL("CREATE TABLE menu_items (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, price INTEGER, image_path TEXT)");
        String CREATE_CURRENT_ORDERS_TABLE = "CREATE TABLE " + TABLE_CURRENT_ORDERS + "("
                + KEY_CURRENT_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_CURRENT_ORDER_TABLE_ID + " INTEGER,"
                + KEY_CURRENT_ORDER_ITEM_NAME + " TEXT,"
                + KEY_CURRENT_ORDER_ITEM_PRICE + " INTEGER,"
                + KEY_CURRENT_ORDER_ITEM_QUANTITY + " INTEGER, "
                + "FOREIGN KEY(" + KEY_CURRENT_ORDER_TABLE_ID + ") REFERENCES tables(id))";
        db.execSQL(CREATE_CURRENT_ORDERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS tables");
        db.execSQL("DROP TABLE IF EXISTS payments");
        db.execSQL("DROP TABLE IF EXISTS order_details");
        db.execSQL("DROP TABLE IF EXISTS menu_items"); // THÊM LỆNH XÓA

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENT_ORDERS); // << MỚI >> Xóa bảng mới
        onCreate(db);
    }

    // ==========================================================
    // CÁC PHƯƠNG THỨC QUẢN LÝ MÓN ĂN
    // ==========================================================

    public long insertMenuItem(String name, int price, String imagePath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("price", price);
        values.put("image_path", imagePath);
        long id = db.insert("menu_items", null, values);
        db.close();
        return id;
    }

    public int updateMenuItem(int id, String name, int price, String imagePath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("price", price);
        values.put("image_path", imagePath);
        int rows = db.update("menu_items", values, "id=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public int deleteMenuItem(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete("menu_items", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public Cursor getAllMenuItems() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM menu_items ORDER BY name ASC", null);
    }

    // ==========================================================
    // CÁC PHƯƠNG THỨC KHÁC GIỮ NGUYÊN
    // ==========================================================

    public long insertUser(String username, String password, int isOwner) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("is_owner", isOwner);
        return db.insert("users", null, values);
    }

    public Cursor login(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE username=? AND password=?", new String[]{username, password});
    }

    public int changePassword(int userId, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        return db.update("users", values, "id=?", new String[]{String.valueOf(userId)});
    }

    public int deleteUser(int userId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("users", "id=? AND is_owner=0", new String[]{String.valueOf(userId)});
    }

    public Cursor getAllManagers() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE is_owner=0", null);
    }

    public long insertTable(int seats, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("seats", seats);
        values.put("status", status);
        return db.insert("tables", null, values);
    }

    public Cursor getAllTables() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM tables", null);
    }

    public int updateTable(int id, int seats, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("seats", seats);
        values.put("status", status);
        return db.update("tables", values, "id=?", new String[]{String.valueOf(id)});
    }

    public int deleteTable(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("tables", "id=?", new String[]{String.valueOf(id)});
    }

    public void updateTableOrderAndStatus(Table table) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction(); // Bắt đầu transaction để đảm bảo toàn vẹn dữ liệu
        try {
            // Bước 1: Cập nhật trạng thái của bàn trong bảng "tables"
            ContentValues tableValues = new ContentValues();
            tableValues.put("status", table.getStatus());
            db.update("tables", tableValues, "id = ?", new String[]{String.valueOf(table.getId())});

            // Bước 2: Xóa tất cả các món ăn cũ của bàn này trong bảng "current_orders"
            db.delete(TABLE_CURRENT_ORDERS, KEY_CURRENT_ORDER_TABLE_ID + " = ?", new String[]{String.valueOf(table.getId())});

            // Bước 3: Thêm lại danh sách món ăn mới vào bảng "current_orders"
            for (Item item : table.getItems()) {
                if (item.getQuantity() > 0) {
                    ContentValues itemValues = new ContentValues();
                    itemValues.put(KEY_CURRENT_ORDER_TABLE_ID, table.getId());
                    itemValues.put(KEY_CURRENT_ORDER_ITEM_NAME, item.getName());
                    itemValues.put(KEY_CURRENT_ORDER_ITEM_PRICE, item.getPrice());
                    itemValues.put(KEY_CURRENT_ORDER_ITEM_QUANTITY, item.getQuantity());
                    db.insert(TABLE_CURRENT_ORDERS, null, itemValues);
                }
            }

            db.setTransactionSuccessful(); // Đánh dấu transaction thành công
        } catch (Exception e) {
            Log.e("DBHelper", "Error updating table order and status", e);
        } finally {
            db.endTransaction(); // Kết thúc transaction
            db.close();
        }
    }
    public ArrayList<Item> getItemsForTable(int tableId) {
        ArrayList<Item> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CURRENT_ORDERS, null, KEY_CURRENT_ORDER_TABLE_ID + " = ?",
                new String[]{String.valueOf(tableId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int nameCol = cursor.getColumnIndex(KEY_CURRENT_ORDER_ITEM_NAME);
            int priceCol = cursor.getColumnIndex(KEY_CURRENT_ORDER_ITEM_PRICE);
            int quantityCol = cursor.getColumnIndex(KEY_CURRENT_ORDER_ITEM_QUANTITY);

            do {
                String name = cursor.getString(nameCol);
                int price = cursor.getInt(priceCol);
                int quantity = cursor.getInt(quantityCol);
                // Giả sử Item có constructor phù hợp
                Item item = new Item(name, price, ""); // imagePath có thể rỗng
                item.setQuantity(quantity);
                items.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return items;
    }
    public void clearTableItems(int tableId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_CURRENT_ORDERS, KEY_CURRENT_ORDER_TABLE_ID + " = ?", new String[]{String.valueOf(tableId)});
        } catch (Exception e) {
            Log.e("DBHelper", "Error clearing table items", e);
        } finally {
            db.close();
        }
    }
    public void updateTableStatus(Table table) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", table.getStatus());
        db.update("tables", values, "id = ?", new String[]{String.valueOf(table.getId())});
        db.close();
    }

    public void insertPaymentWithDetails(Payment payment, ArrayList<Item> orderedItems) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long paymentId = -1;
        try {
            ContentValues paymentValues = new ContentValues();
            paymentValues.put("tableId", payment.getTableId());
            paymentValues.put("amount", payment.getAmount());
            paymentValues.put("timestamp", payment.getTimestamp());
            paymentId = db.insert("payments", null, paymentValues);

            if (paymentId == -1) {
                throw new Exception("Failed to insert into payments table");
            }

            for (Item item : orderedItems) {
                if (item.getQuantity() > 0) {
                    ContentValues detailValues = new ContentValues();
                    detailValues.put("payment_id", paymentId);
                    detailValues.put("item_name", item.getName());
                    detailValues.put("item_price", item.getPrice());
                    detailValues.put("quantity", item.getQuantity());
                    db.insert("order_details", null, detailValues);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DBHelper", "Error inserting payment with details", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<RevenueReportItem> getRevenueReport() {
        List<RevenueReportItem> reportList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +
                "item_name, " +
                "SUM(quantity) as total_quantity, " +
                "SUM(quantity * item_price) as total_revenue " +
                "FROM order_details " +
                "GROUP BY item_name " +
                "ORDER BY total_revenue DESC";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameCol = cursor.getColumnIndex("item_name");
                int quantityCol = cursor.getColumnIndex("total_quantity");
                int revenueCol = cursor.getColumnIndex("total_revenue");
                do {
                    String itemName = cursor.getString(nameCol);
                    int totalQuantity = cursor.getInt(quantityCol);
                    int totalRevenue = cursor.getInt(revenueCol);
                    reportList.add(new RevenueReportItem(itemName, totalQuantity, totalRevenue));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DBHelper", "Error getting revenue report", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return reportList;
    }
}