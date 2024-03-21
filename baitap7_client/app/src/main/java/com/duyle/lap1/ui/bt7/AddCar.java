package com.duyle.lap1.ui.bt7;

import static java.security.AccessController.getContext;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.duyle.lap1.R;
import com.duyle.lap1.Retrofit.RetrofitBuilder;
import com.duyle.lap1.Retrofit.RetrofitCar;
import com.duyle.lap1.databinding.ActivityAddCarBinding;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCar extends AppCompatActivity {

    private ActivityAddCarBinding binding;

    private String path;
    private Uri img_uri;
    private RetrofitCar retrofitCar;
    Context context;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrofitCar = RetrofitBuilder.getRetrofitInstance().create(RetrofitCar.class); // Khởi tạo retrofitCar ở onCreate()
        context = this;

        binding.btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialocChonAnh();
            }
        });

        binding.clickSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.etName.getText().toString();
                String price = binding.etPrice.getText().toString();
                String type = binding.etType.getText().toString();
                binding.loading.setVisibility(View.VISIBLE);
                binding.clickSubmit.setVisibility(View.GONE);

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("name", name);
                    jsonBody.put("price", price);
                    jsonBody.put("type", type);
                    jsonBody.put("id_category", "65f8b8830137b7caeb14578b");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Tạo một tham chiếu đến Firebase Storage
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();

                // Tạo một tham chiếu đến Firebase Storage với tên file duy nhất
                String fileName = UUID.randomUUID().toString(); // Sử dụng UUID để tạo tên tập tin
                StorageReference imageRef = storageRef.child("images/" + fileName);

                // Tải ảnh lên Firebase Storage
                imageRef.putFile(img_uri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // Lấy URL của ảnh đã tải lên
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                try {
                                    jsonBody.put("image", imageUrl);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // Tạo một RequestBody từ JSON Object
                                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());

                                // Gọi phương thức postCar trong Retrofit với tham số mới
                                Call<Void> updateCall = retrofitCar.postCar(requestBody);
                                updateCall.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            handleUpdateSuccess();
                                        } else {
                                            handleUpdateFailure();
                                        }
                                        binding.loading.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        handleUpdateFailure();
                                        binding.loading.setVisibility(View.GONE);
                                    }
                                });
                            });
                        })
                        .addOnFailureListener(e -> {
                            // Xử lý khi tải ảnh lên không thành công
                            Toast.makeText(AddCar.this, "Tải ảnh lên không thành công", Toast.LENGTH_SHORT).show();
                        });
            }
        });

    }
    private void handleUpdateSuccess() {
        Toast.makeText(this, "Successfully", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private void handleUpdateFailure() {
        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
    }

    //pick image from gallery
    private void showDialocChonAnh() {
        String[] options = {"Máy Ảnh", "Thư Viện"};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddCar.this);
        builder.setTitle("Chọn").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    // người dùng chọn máy ảnh
                    pickMayAnhFuntion();
                } else if (i == 1) {
                    // người dùng chọn thư viện
                    pickThuVienFuntion();
                }
            }
        }).show();
    }
    // get path when pick image from gallery
    private void pickMayAnhFuntion() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Máy ảnh");
        contentValues.put(MediaStore.Images.Media.TITLE, "Máy ảnh");
        img_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, img_uri);
        cameraActivityResult.launch(intent);
    }
    private void pickThuVienFuntion() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResult.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();

                try {
                    Glide
                            .with(context)
                            .load(img_uri)
                            .centerCrop()
                            .placeholder(R.drawable.loading_viec)
                            .into(binding.ivImage);
                    binding.ivImage.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                }
            }
        }
    });

    private ActivityResultLauncher<Intent> galleryActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();

                img_uri = intent.getData();

                try {
                    Glide
                            .with(context)
                            .load(img_uri)
                            .centerCrop()
                            .placeholder(R.drawable.loading_viec)
                            .into(binding.ivImage);
                    binding.ivImage.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                }
            }
        }
    });
}