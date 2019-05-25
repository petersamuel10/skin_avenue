package com.example.Skin_Avenus.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.Skin_Avenus.Common;
import com.example.Skin_Avenus.Constant;
import com.example.Skin_Avenus.MainActivity;
import com.example.Skin_Avenus.R;
import com.example.Skin_Avenus.model.SubCategoryModel;
import com.example.Skin_Avenus.productFragmnet.Products;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.ViewHolder> {

    ArrayList<SubCategoryModel> categoryList;
    Context context;

    public SubCategoryAdapter(ArrayList<SubCategoryModel> productList, Context context) {
        this.categoryList = productList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category,parent,false);
        context = parent.getContext();
        return new SubCategoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        holder.bind(categoryList.get(i));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment products = new Products();
                Bundle bundle = new Bundle();
                bundle.putString("categoryId", categoryList.get(i).getCategory_id());
                bundle.putString("tab_type", Constant.TAB_HOME);
                products.setArguments(bundle);

                ((MainActivity) Common.mActivity).pushFragments(Constant.TAB_HOME, products, true);
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.category_image)
        ImageView category_image;
        @BindView(R.id.category_name)
        TextView category_name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(SubCategoryModel categoryModel) {
            if (Common.isArabic)
                category_name.setText(categoryModel.getCategory_title_ar());
            else
                category_name.setText(categoryModel.getCategory_title_en());

            String link = context.getResources().getString(R.string.image_link) + categoryModel.getCategory_icon();

            Glide.with(context).load(link).placeholder(R.drawable.placeholder).into(category_image);


            Log.i("link", link);
        }
    }
}
