package com.example.sergey_kurapov_3025265_ass1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

//the idea taken from https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
public class ProductAdapter extends ArrayAdapter<Product> {

    public ProductAdapter(Context context, ArrayList<Product> products){
        super(context,0, products);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Product product = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product, parent, false);
        }
        // Lookup view for data population
        TextView productId = (TextView) convertView.findViewById(R.id.productId);
        TextView productName = (TextView) convertView.findViewById(R.id.productName);
        TextView price = (TextView) convertView.findViewById(R.id.price);

        // Populate the data into the template view using the data object
        productId.setText(Integer.toString(product.getProductId()));
        productName.setText(product.getProductName());

        DecimalFormat df = new DecimalFormat("#.00");
        price.setText("€" + df.format(product.getPricePerItem()));

        // Return the completed view to render on screen
        return convertView;
    }
}
