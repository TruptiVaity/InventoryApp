package com.example.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.inventoryapp.data.ProductContract;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.product_name);
        TextView priceTextView = view.findViewById(R.id.product_price);
        final TextView quantityTextView = view.findViewById(R.id.product_qty);

        final int columnID = cursor.getColumnIndex(ProductContract.ProductEntry._ID);
        final int columnQuantity = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        Button saleButton = view.findViewById(R.id.sale_button);
        final int position = cursor.getPosition();
        String quantity = cursor.getString(columnQuantity);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String quantityString = quantityTextView.getText().toString().trim();
                ContentValues values = new ContentValues();
                cursor.moveToPosition(position);
                //int newQuantity=0;
                int oldQuantity = (cursor.getInt(columnQuantity));
                if(oldQuantity!=0){
                    oldQuantity --;}
                //int newQuantity = Integer.parseInt(quantityString);
                //newQuantity--;
                values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,oldQuantity);
                String selection = ProductContract.ProductEntry._ID + "=?";
                //Get the item id which should be updated
                int item_id = cursor.getInt(columnID);
                String itemIDArgs = Integer.toString(item_id);

                String[] selectionArgs = {itemIDArgs};
                //Update the value
                int something = context.getContentResolver().update(ProductContract.ProductEntry.CONTENT_URI,values,selection,selectionArgs);
                Log.v("Updated"," Row"+ something);
                //quantityTextView.setText(Integer.toString(newQuantity));

                String newQu = cursor.getString(columnQuantity);
                quantityTextView.setText(newQu);
            }
        });
        int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);

        String productName = cursor.getString(nameColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        String productQuantity = cursor.getString(quantityColumnIndex);

        nameTextView.setText(productName);
        priceTextView.setText(productPrice);
        quantityTextView.setText(productQuantity);
    }


}
