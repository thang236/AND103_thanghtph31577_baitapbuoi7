package com.duyle.lap1.ui.bt7;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.duyle.lap1.R;
import com.duyle.lap1.Retrofit.RetrofitBuilder;
import com.duyle.lap1.Retrofit.RetrofitCar;
import com.duyle.lap1.adapter.CarAdapter;
import com.duyle.lap1.databinding.ActivityListXeBinding;
import com.duyle.lap1.models.bt7.Car;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListXeActivity extends AppCompatActivity implements CarAdapter.CarClick {

    private ActivityListXeBinding binding;
    private List<Car> carList = new ArrayList<>();
    private CarAdapter carAdapter;
    private RetrofitCar retrofitCar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListXeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupSwipeRefreshLayout();
        setupRecyclerView();
        fetchCarList();

        binding.animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListXeActivity.this, AddCar.class));
            }
        });
    }

    private void setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Khi người dùng kéo xuống để làm mới, gọi fetchCarList() để lấy dữ liệu mới
                fetchCarList();
            }
        });
    }

    private void setupRecyclerView() {
        carAdapter = new CarAdapter(carList, this, this);
        binding.recyclerViewCities.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewCities.setAdapter(carAdapter);
    }

    private void fetchCarList() {
        binding.swipeRefreshLayout.setRefreshing(true);
        binding.animationViewLoading.setVisibility(View.VISIBLE);
        retrofitCar = RetrofitBuilder.getRetrofitInstance().create(RetrofitCar.class);
        Call<JsonObject> call = retrofitCar.getAllCar();
        call.enqueue(new Callback<JsonObject>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                binding.animationViewLoading.setVisibility(View.GONE);
                carList.clear();
                JsonObject res = response.body();
                if (res != null && res.has("data")) {
                    JsonArray dataArray = res.getAsJsonArray("data");
                    Gson gson = new Gson();
                    for (int i = 0; i < dataArray.size(); i++) {
                        JsonObject carObject = dataArray.get(i).getAsJsonObject();
                        Car car = gson.fromJson(carObject, Car.class);
                        carList.add(car);
                    }
                    if (carList.isEmpty()) {
                        binding.contentLoad.setText("Không có dữ liệu");
                        binding.contentLoad.setVisibility(View.VISIBLE);
                    } else {
                        binding.contentLoad.setVisibility(View.GONE);
                    }
                    carAdapter.notifyDataSetChanged();
                }
                // Kết thúc hiệu ứng làm mới
                binding.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                binding.animationViewLoading.setVisibility(View.GONE);
                binding.contentLoad.setText(t.getMessage());
                binding.contentLoad.setVisibility(View.VISIBLE);
                // Kết thúc hiệu ứng làm mới
                binding.swipeRefreshLayout.setRefreshing(false);
                Log.d("car", "onFailure: " + t.getMessage());
            }
        });
    }


    @Override
    public void delete(Car car) {
        //Dialog confirm delete car
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa không?");
        builder.setPositiveButton("Có", (dialog, which) -> {
            Call<Void> deleteCall = retrofitCar.deleteCar(car.get_id());
            deleteCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        handleDeleteSuccess();
                    } else {
                        handleDeleteFailure();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    handleDeleteFailure();
                }
            });
        });
        builder.setNegativeButton("Không", (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    private void handleDeleteSuccess() {
        Toast.makeText(this, "Xóa thành công", Toast.LENGTH_SHORT).show();
        fetchCarList();
    }

    private void handleDeleteFailure() {
        Toast.makeText(this, "Xóa không thành công", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateCar(Car car) {
        showUpdateDialog(car);
    }

    private void showUpdateDialog(Car car) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cập nhật thông tin xe");

        // Inflate layout for dialog
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update_car, null);
        builder.setView(view);

        // Get references to views in dialog layout
        EditText etName = view.findViewById(R.id.et_name);
        EditText etPrice = view.findViewById(R.id.et_price);
        EditText etImage = view.findViewById(R.id.et_image);
        EditText etType = view.findViewById(R.id.et_type);
        // Populate dialog with car data
        etName.setText(car.getName());
        etPrice.setText(car.getPrice());
        etImage.setText(car.getImage());
        etType.setText(car.getType());

        // Set up update button click listener
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedName = etName.getText().toString();
                String updatedPrice = etPrice.getText().toString();
                String updatedImage = etImage.getText().toString();
                String updatedType = etType.getText().toString();

                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("name", updatedName);
                    jsonBody.put("price", updatedPrice);
                    jsonBody.put("image", updatedImage);
                    jsonBody.put("type", updatedType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody.toString());

                Call<Void> updateCall = retrofitCar.updateCar(car.get_id(), requestBody);
                updateCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            handleUpdateSuccess();
                        } else {
                            handleUpdateFailure();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        handleUpdateFailure();
                    }
                });
            }
        });

        // Set up cancel button click listener
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Show dialog
        builder.create().show();
    }

    private void handleUpdateSuccess() {
        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
        fetchCarList();
    }

    private void handleUpdateFailure() {
        Toast.makeText(this, "Cập nhật không thành công", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSwipeRefreshLayout();
        setupRecyclerView();
        fetchCarList();
    }
}
