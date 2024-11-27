package com.example.and103_lab5;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.and103_lab5.adapter.Adapter_Item_District_Select_GHN;
import com.example.and103_lab5.adapter.Adapter_Item_Province_Select_GHN;
import com.example.and103_lab5.adapter.Adapter_Item_Ward_Select_GHN;
import com.example.and103_lab5.databinding.ActivityLocation2Binding;
import com.example.and103_lab5.model.Distributor;
import com.example.and103_lab5.model.District;
import com.example.and103_lab5.model.DistrictRequest;
import com.example.and103_lab5.model.Fruit;
import com.example.and103_lab5.model.GHNItem;
import com.example.and103_lab5.model.GHNOrderRespone;
import com.example.and103_lab5.model.Order;
import com.example.and103_lab5.model.Province;
import com.example.and103_lab5.model.ResponseGHN;
import com.example.and103_lab5.model.Ward;
import com.example.and103_lab5.model.sendOrderRequest;
import com.example.and103_lab5.services.GHNRequest;
import com.example.and103_lab5.services.GHNServices;
import com.example.and103_lab5.services.HttpRequest;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Location2 extends AppCompatActivity {

    private ActivityLocation2Binding binding;
    private GHNRequest request;
    private GHNServices ghnServices;
    private String productId, productTypeId, productName, description, WardCode;
    private double rate, price;
    private int image, DistrictID, ProvinceID ;
    Button btn_order;
    HttpRequest httpRequest;
    Fruit fruit;
    EditText edt_name, edt_phone, edt_location;
    private Adapter_Item_Province_Select_GHN adapter_item_province_select_ghn;
    private Adapter_Item_District_Select_GHN adapter_item_district_select_ghn;
    private Adapter_Item_Ward_Select_GHN adapter_item_ward_select_ghn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocation2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        httpRequest = new HttpRequest();
        request = new GHNRequest();
        btn_order = findViewById(R.id.btn_order);
        edt_name = findViewById(R.id.edt_name);
        edt_phone = findViewById(R.id.edt_phone);
        edt_location = findViewById(R.id.edt_location);
        Intent intent = getIntent();
        intent.getExtras();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            productId = bundle.getString("productId");
            productTypeId = bundle.getString("productTypeId");
            productName = bundle.getString("productName");
            description = bundle.getString("description");
            rate = bundle.getDouble("rate");
            price = bundle.getDouble("price");
            image = bundle.getInt("image");
        }

        request.callAPI().getListProvince().enqueue(responseProvince);
        binding.spProvince.setOnItemSelectedListener(onItemSelectedListener);
        binding.spDistrict.setOnItemSelectedListener(onItemSelectedListener);
        binding.spWard.setOnItemSelectedListener(onItemSelectedListener);

        binding.spProvince.setSelection(1);
        binding.spDistrict.setSelection(1);
        binding.spWard.setSelection(1);


        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (WardCode.equals("")) return;
                fruit = (Fruit) intent.getSerializableExtra("fruit");
                GHNItem ghnItem = new GHNItem();
                ghnItem.setName(fruit.getName());
                ghnItem.setPrice(Integer.parseInt(fruit.getPrice()));
                ghnItem.setCode(fruit.get_id());
                ghnItem.setQuantity(1);
                ghnItem.setWeight(50);
                ArrayList<GHNItem> items = new ArrayList<>();
                items.add(ghnItem);
                sendOrderRequest sendOrderRequest = new sendOrderRequest(
                    edt_name.getText().toString(),
                    edt_phone.getText().toString(),
                    edt_location.getText().toString(),
                    WardCode,
                    DistrictID,
                    items
                );
                request.callAPI().GHNOrder(sendOrderRequest).enqueue(responseOrder);
            }
        });



    }

    Callback<ResponseGHN<GHNOrderRespone>> responseOrder = new Callback<ResponseGHN<GHNOrderRespone>>() {
        @Override
        public void onResponse(Call<ResponseGHN<GHNOrderRespone>> call, Response<ResponseGHN<GHNOrderRespone>> response) {
            if (response.isSuccessful()){
                if (response.body().getCode() == 200){
                    Toast.makeText(Location2.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                    Order order = new Order();
                    order.setOrder_code(response.body().getData().getOrder_code());
                    order.setId_user(getSharedPreferences("INFO",MODE_PRIVATE).getString("id",""));
                    httpRequest.callAPI().order(order).enqueue(new Callback<com.example.and103_lab5.model.Response<ArrayList<Distributor>>>() {
                        @Override
                        public void onResponse(Call<com.example.and103_lab5.model.Response<ArrayList<Distributor>>> call, Response<com.example.and103_lab5.model.Response<ArrayList<Distributor>>> response) {
                            if (response.isSuccessful()){
                                if (response.body().getStatus() == 200){
                                    Toast.makeText(Location2.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<com.example.and103_lab5.model.Response<ArrayList<Distributor>>> call, Throwable t) {

                        }
                    });
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseGHN<GHNOrderRespone>> call, Throwable t) {

        }
    };

    AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (parent.getId() == R.id.sp_province) {
                ProvinceID = ((Province) parent.getAdapter().getItem(position)).getProvinceID();
                DistrictRequest districtRequest = new DistrictRequest(ProvinceID);
                request.callAPI().getListDistrict(districtRequest).enqueue(responseDistrict);
            } else if (parent.getId() == R.id.sp_district) {
                DistrictID = ((District) parent.getAdapter().getItem(position)).getDistrictID();
                request.callAPI().getListWard(DistrictID).enqueue(responseWard);
            } else if (parent.getId() == R.id.sp_ward) {
                WardCode = ((Ward) parent.getAdapter().getItem(position)).getWardCode();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    Callback<ResponseGHN<ArrayList<Province>>> responseProvince = new Callback<ResponseGHN<ArrayList<Province>>>() {
        @Override
        public void onResponse(Call<ResponseGHN<ArrayList<Province>>> call, Response<ResponseGHN<ArrayList<Province>>> response) {
            if(response.isSuccessful()){
                if(response.body().getCode() == 200){
                    ArrayList<Province> ds = new ArrayList<>(response.body().getData());
                    SetDataSpinProvince(ds);
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseGHN<ArrayList<Province>>> call, Throwable t) {
            Toast.makeText(Location2.this, "Lấy dữ liệu bị lỗi", Toast.LENGTH_SHORT).show();
        }
    };

    Callback<ResponseGHN<ArrayList<District>>> responseDistrict = new Callback<ResponseGHN<ArrayList<District>>>() {
        @Override
        public void onResponse(Call<ResponseGHN<ArrayList<District>>> call, Response<ResponseGHN<ArrayList<District>>> response) {
            if(response.isSuccessful()){
                if(response.body().getCode() == 200){
                    ArrayList<District> ds = new ArrayList<>(response.body().getData());
                    SetDataSpinDistrict(ds);
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseGHN<ArrayList<District>>> call, Throwable t) {

        }
    };

    Callback<ResponseGHN<ArrayList<Ward>>> responseWard = new Callback<ResponseGHN<ArrayList<Ward>>>() {
        @Override
        public void onResponse(Call<ResponseGHN<ArrayList<Ward>>> call, Response<ResponseGHN<ArrayList<Ward>>> response) {
            if(response.isSuccessful()){
                if(response.body().getCode() == 200){

                    if(response.body().getData() == null)
                        return;

                    ArrayList<Ward> ds = new ArrayList<>(response.body().getData());

                    ds.addAll(response.body().getData());
                    SetDataSpinWard(ds);
                }
            }
        }

        @Override
        public void onFailure(Call<ResponseGHN<ArrayList<Ward>>> call, Throwable t) {
            Toast.makeText(Location2.this, "Lỗi", Toast.LENGTH_SHORT).show();
        }
    };

    private void SetDataSpinProvince(ArrayList<Province> ds){
        adapter_item_province_select_ghn = new Adapter_Item_Province_Select_GHN(this, ds);
        binding.spProvince.setAdapter(adapter_item_province_select_ghn);
    }

    private void SetDataSpinDistrict(ArrayList<District> ds){
        adapter_item_district_select_ghn = new Adapter_Item_District_Select_GHN(this, ds);
        binding.spDistrict.setAdapter(adapter_item_district_select_ghn);
    }

    private void SetDataSpinWard(ArrayList<Ward> ds){
        adapter_item_ward_select_ghn = new Adapter_Item_Ward_Select_GHN(this, ds);
        binding.spWard.setAdapter(adapter_item_ward_select_ghn );
    }
}