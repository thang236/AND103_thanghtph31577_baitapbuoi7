package com.duyle.lap1.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.duyle.lap1.R;
import com.duyle.lap1.databinding.ItemCarBinding;
import com.duyle.lap1.models.bt7.Car;

import java.text.DecimalFormat;
import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {
    private List<Car> carList;
    private Context context;
    private CarClick carClick;

    public CarAdapter(List<Car> carList, Context context, CarClick carClick) {
        this.carList = carList;
        this.context = context;
        this.carClick = carClick;
    }

    public interface CarClick {
        void delete(Car car);

        void updateCar(Car car);
    }

    @NonNull
    @Override
    public CarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCarBinding binding = ItemCarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarAdapter.ViewHolder holder, int position) {
        Car car = carList.get(position);
        Glide
                .with(context)
                .load(car.getImage())
                .centerCrop()
                .placeholder(R.drawable.loading_viec)
                .into(holder.binding.civAvatar);

        holder.binding.tvCarName.setText(car.getName());
        holder.binding.tvCarType.setText("Loại: "+car.getType());
// Định nghĩa một đối tượng DecimalFormat với mẫu định dạng "###,### VND"
        DecimalFormat decimalFormat = new DecimalFormat("###,### VND");

// Chuyển đổi giá tiền thành kiểu double (nếu chưa phải)
        double price = Double.parseDouble(car.getPrice());

// Định dạng giá tiền theo mẫu định dạng và gán vào TextView
        holder.binding.tvCarPrice.setText("Giá: "+decimalFormat.format(price));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.itemView, car);
            }
        });
    }
    private void showPopupMenu(View view, Car car) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.function_menu); // Định nghĩa menu trong file menu_car_item.xml
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_delete) {
                    carClick.delete(car);
                    return true;
                } else if (itemId == R.id.menu_update) {
                    carClick.updateCar(car);
                    return true;
                } else {
                    return false;
                }
            }
        });
        popupMenu.show();
    }


    @Override
    public int getItemCount() {
        return carList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCarBinding binding;

        public ViewHolder(@NonNull ItemCarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
