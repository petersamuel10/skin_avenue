package com.example.Skin_Avenus.mainFragments;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.Skin_Avenus.Cart;
import com.example.Skin_Avenus.Common;
import com.example.Skin_Avenus.Constant;
import com.example.Skin_Avenus.MainActivity;
import com.example.Skin_Avenus.R;
import com.example.Skin_Avenus.adapter.ProductAdapter;
import com.example.Skin_Avenus.adapter.SubCategoryAdapter;
import com.example.Skin_Avenus.model.ProductModel;
import com.example.Skin_Avenus.model.SubCategoryModel;
import com.example.Skin_Avenus.productFragmnet.Products;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SubCategory extends Fragment {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.subCat_rec)
    RecyclerView product_rec;
    String categoryId;
    AlertDialog alertDialog;
    @OnClick(R.id.back)
    public void back() {
        ((MainActivity) getActivity()).onBackPressed();
    }
    @OnClick(R.id.ic_cart)
    public void cart() {
        ((MainActivity) getActivity()).pushFragments(Constant.TAB_HOME, new Cart(), true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sub_category, container, false);
        ButterKnife.bind(this,view);
        alertDialog = Common.alert(getActivity());
        categoryId = getArguments().getString("categoryId");

        if (Common.isConnectToTheInternet(getContext())) {
            new GetProducts(getActivity()).execute();
        } else
            Common.showErrorAlert(getActivity(), getString(R.string.error_no_internet_connection));

        if(Common.isArabic)
            back.setRotation(180);

        return view;
    }

    private class GetProducts extends AsyncTask<String, Void, String> {

        public JSONObject jsonObject = null;
        public ArrayList<SubCategoryModel> subCategoryList;

        GetProducts(Activity activity) {
            subCategoryList = new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            alertDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String signin_url = getResources().getString(R.string.api)+"GetCategory.php";
            try {
                URL url = new URL(signin_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("cache-control", "application/json");
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);


                String str = "{\"level\": " + categoryId + "}";
                Log.i("ccc", str);

                byte[] outputInBytes = str.getBytes("UTF-8");

                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream OS = httpURLConnection.getOutputStream();
                OS.write(outputInBytes);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

                bufferedWriter.flush();
                bufferedWriter.close();
                OS.close();

                InputStream IS = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));

                String response = "";
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    response += line;
                }

                bufferedReader.close();
                IS.close();
                httpURLConnection.disconnect();
                return response;

            } catch (IOException e) {
                Common.showErrorAlert(getActivity(), getString(R.string.error_please_try_again_later));
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            alertDialog.dismiss();
            Log.d("result",result);
            try {
                jsonObject = new JSONObject(new String(result));
                // String status = jsonObject.getString("message");
                JSONArray categoryItems = jsonObject.getJSONArray("category_item");

                if (categoryItems.length() != 0) {

                    for (int i = 0; i < categoryItems.length(); i++) {
                        JSONObject jsonObject1 = categoryItems.getJSONObject(i);

                        String category_id = jsonObject1.getString("category_id");
                        String category_icon = jsonObject1.getString("category_icon");
                        String category_title_ar = jsonObject1.getString("category_title_ar");
                        String category_title_en = jsonObject1.getString("category_title_en");

                        SubCategoryModel subCategory = new SubCategoryModel(category_id,category_title_ar,category_title_en,category_icon);
                        subCategoryList.add(subCategory);
                    }

                    SubCategoryAdapter adapter = new SubCategoryAdapter(subCategoryList, getContext());
                    product_rec.setAdapter(adapter);
                } else {
                    Common.showErrorAlert(getActivity(), getString(R.string.error_please_try_again_later));
                }
            } catch (JSONException e) {
                Common.showErrorAlert(getActivity(), e.getMessage());
            }

        }
    }

}
