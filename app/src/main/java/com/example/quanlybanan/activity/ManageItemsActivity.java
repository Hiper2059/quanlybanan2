// --- THAY THẾ TOÀN BỘ FILE ManageItemsActivity.java ---

package com.example.quanlybanan.activity;
import androidx.annotation.NonNull;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.quanlybanan.adapter.ManageItemsAdapter;
import com.example.quanlybanan.R;
import com.example.quanlybanan.database.DBHelper;
import com.example.quanlybanan.model.MenuItemObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ManageItemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton fabAddItem;
    private ManageItemsAdapter adapter;
    private ArrayList<MenuItemObject> menuItemsList;
    private DBHelper dbHelper;

    // Biến tạm để lưu URI ảnh
    private Uri currentImageUri = null;
    private ImageView dialogImageView;

    // Launcher để chọn ảnh từ gallery
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    currentImageUri = result.getData().getData();
                    if (dialogImageView != null) {
                        Glide.with(this).load(currentImageUri).into(dialogImageView);
                    }
                }
            });

    // Launcher để chụp ảnh
    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (success) {
                    if (dialogImageView != null && currentImageUri != null) {
                        Glide.with(this).load(currentImageUri).into(dialogImageView);
                    }
                } else {
                    // Nếu người dùng hủy chụp, xóa file tạm nếu có
                    if (currentImageUri != null) {
                        getContentResolver().delete(currentImageUri, null, null);
                        currentImageUri = null;
                    }
                }
            });

    // Launcher để xin quyền camera
    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Quyền sử dụng camera bị từ chối.", Toast.LENGTH_SHORT).show();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            currentImageUri = savedInstanceState.getParcelable("key_image_uri");
        }
        setContentView(R.layout.activity_manage_items);

        dbHelper = new DBHelper(this);
        recyclerView = findViewById(R.id.recyclerMenuItems);
        fabAddItem = findViewById(R.id.fabAddItem);

        menuItemsList = new ArrayList<>();
        adapter = new ManageItemsAdapter(this, menuItemsList, new ManageItemsAdapter.OnItemListener() {
            @Override
            public void onItemClick(int position) {
                showAddEditDialog(menuItemsList.get(position));
            }

            @Override
            public void onDeleteClick(int position) {
                confirmDeleteItem(position);
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAddItem.setOnClickListener(v -> showAddEditDialog(null));

        loadMenuItems();
    }

    private void loadMenuItems() {
        menuItemsList.clear();
        Cursor cursor = dbHelper.getAllMenuItems();
        if (cursor != null) {
            int idCol = cursor.getColumnIndex("id");
            int nameCol = cursor.getColumnIndex("name");
            int priceCol = cursor.getColumnIndex("price");
            int imageCol = cursor.getColumnIndex("image_path");
            if (idCol != -1 && nameCol != -1 && priceCol != -1 && imageCol != -1) {
                while (cursor.moveToNext()) {
                    menuItemsList.add(new MenuItemObject(
                            cursor.getInt(idCol),
                            cursor.getString(nameCol),
                            cursor.getInt(priceCol),
                            cursor.getString(imageCol)
                    ));
                }
            }
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    private void showAddEditDialog(final MenuItemObject itemToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_item, null);

        final EditText edtName = dialogView.findViewById(R.id.edtItemName);
        final EditText edtPrice = dialogView.findViewById(R.id.edtItemPrice);
        dialogImageView = dialogView.findViewById(R.id.imgItemPreview);
        Button btnSelectImage = dialogView.findViewById(R.id.btnSelectImage);
        Button btnTakePhoto = dialogView.findViewById(R.id.btnTakePhoto);

        builder.setView(dialogView);
        builder.setTitle(itemToEdit == null ? "Thêm món ăn mới" : "Sửa món ăn");
        currentImageUri = null; // Reset URI mỗi khi mở dialog

        if (itemToEdit != null) {
            edtName.setText(itemToEdit.getName());
            edtPrice.setText(String.valueOf(itemToEdit.getPrice()));
            if (itemToEdit.getImagePath() != null && !itemToEdit.getImagePath().isEmpty()) {
                currentImageUri = Uri.parse(itemToEdit.getImagePath()); // Chuyển String path thành Uri
                Glide.with(this).load(currentImageUri).into(dialogImageView);
            }
        }

        btnSelectImage.setOnClickListener(v -> pickImageFromGallery());
        btnTakePhoto.setOnClickListener(v -> checkCameraPermissionAndOpenCamera());

        builder.setPositiveButton(itemToEdit == null ? "Thêm" : "Cập nhật", (dialog, which) -> {
            String name = edtName.getText().toString().trim();
            String priceStr = edtPrice.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            int price = Integer.parseInt(priceStr);
            // Lưu URI dưới dạng String. Nếu không có URI mới, dùng URI cũ (nếu có)
            String imagePathString = (currentImageUri != null) ? currentImageUri.toString() : (itemToEdit != null ? itemToEdit.getImagePath() : "");

            if (itemToEdit == null) {
                dbHelper.insertMenuItem(name, price, imagePathString);
            } else {
                dbHelper.updateMenuItem(itemToEdit.getId(), name, price, imagePathString);
            }
            loadMenuItems();
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        try {
            currentImageUri = createImageFileUri();
            if (currentImageUri != null) {
                takePictureLauncher.launch(currentImageUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể tạo file ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri createImageFileUri() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        return FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", imageFile);
    }

    private void confirmDeleteItem(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa món ăn này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int id = menuItemsList.get(position).getId();
                    dbHelper.deleteMenuItem(id);
                    loadMenuItems();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Nếu currentImageUri không null, hãy lưu nó vào Bundle (cái cặp)
        if (currentImageUri != null) {
            outState.putParcelable("key_image_uri", currentImageUri);
        }
    }
}