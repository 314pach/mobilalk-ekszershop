package com.example.ekszerwebshop;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> implements Filterable {
    private static final String LOG_TAG = ItemAdapter.class.getName();
    private ArrayList<ProductItem> items;
    private ArrayList<ProductItem> allItems;
    private Context context;
    private int lastPos = -1;
    private FirebaseUser currentUser;
    private FirebaseAuth auth;

    private SharedPreferences sh;
    private static final String PREF_KEY = ItemAdapter.class.getPackage().toString();
    private Set<String> product_names;

    public ItemAdapter(Context context, ArrayList<ProductItem> items) {
        this.items = items;
        this.allItems = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        sh = context.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.product, parent, false));
    }

    @Override
    public void onBindViewHolder( ItemAdapter.ViewHolder holder, int position) {
        ProductItem current = items.get(position);

        holder.bindTo(current);

        if(holder.getBindingAdapterPosition() > lastPos){
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in);
            holder.itemView.startAnimation(animation);
            lastPos = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public Filter getFilter() {
        return productFilter;
    }

    private Filter productFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ProductItem> filtered = new ArrayList<>();
            FilterResults res = new FilterResults();

            if(charSequence == null || charSequence.length() == 0){
                res.count = allItems.size();
                res.values = allItems;
            } else {
                String pattern = charSequence.toString().toLowerCase().trim();
                for(ProductItem item: allItems){
                    if(item.getName().toLowerCase().contains(pattern)){
                        filtered.add(item);
                    }
                }

                res.count = filtered.size();
                res.values = filtered;
            }

            return res;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            items = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nameText;
        private TextView priceText;
        private ImageView productImage;

        public ViewHolder(View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.productName);
            priceText = itemView.findViewById(R.id.productPrice);
            productImage = itemView.findViewById(R.id.productImage);

            Button button = itemView.findViewById(R.id.addToCart);
            if (currentUser == null) {
                button.setVisibility(View.GONE);
            } else {
                button.setVisibility(View.VISIBLE);
                itemView.findViewById(R.id.addToCart).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int count = 0;
                        if(sh != null) {
                            product_names = sh.getStringSet("products", new HashSet<String>());
                            count = sh.getInt(nameText.getText().toString(), 0);
                        }
                        SharedPreferences.Editor editor = sh.edit();

                        product_names.add(nameText.getText().toString());
                        editor.putStringSet("products", product_names);

                        editor.putInt(nameText.getText().toString(), count+1);

                        editor.apply();

                        ((ProductList) context).update();
                        Log.i(LOG_TAG, "SH-ba kerult!!!! : " + nameText.getText().toString() + " " + count);
                        Log.i(LOG_TAG, product_names.toString());
                    }
                });
            }
        }

        public void bindTo(ProductItem current) {
            nameText.setText(current.getName());
            priceText.setText(current.getPrice());

            Glide.with(context).load(current.getImageRes()).into(productImage);
        }
    }


}


