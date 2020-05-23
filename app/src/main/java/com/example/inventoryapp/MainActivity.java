package com.example.inventoryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.inventoryapp.data.ProductContract;
import com.example.inventoryapp.data.ProductsDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.inventoryapp.data.ProductContract.ProductEntry.CONTENT_URI;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ProductsDbHelper mDbHelper;
    private static final int PRODUCT_LOADER = 0;
    ProductCursorAdapter mCursorAdapter;
    Button saleButton;
    TextView quantityTextView;
    private int newQuantityAfterSale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        saleButton = findViewById(R.id.sale_button);
        quantityTextView = findViewById(R.id.product_qty);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = new ProductsDbHelper(this);

        ListView productListView = (ListView) findViewById(R.id.list_view_products);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        mCursorAdapter = new ProductCursorAdapter(this,null);
        productListView.setAdapter(mCursorAdapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent editIntent = new Intent(MainActivity.this,EditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(CONTENT_URI,id);
                editIntent.setData(currentProductUri);
                startActivity(editIntent);
            }
        });

/**        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mQuantity = Integer.parseInt(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
                if(mQuantity!=0) {
                    mQuantity = mQuantity - 1;
                }
                quantityTextView.setText(Integer.toString(mQuantity));
            }
        });
**/
        //Kick off the loader
        getLoaderManager().initLoader(PRODUCT_LOADER,null,this);
    }

    private void insertProduct(){
        ContentValues values = new ContentValues();

        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,"Goods");
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER,"Amazon");
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,1);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,25);

        Uri newUri = getContentResolver().insert(CONTENT_URI,values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_insert_dummy_data:
                insertProduct();
                return true;
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY};
        return new CursorLoader(this,
                CONTENT_URI,
                projection,
                null,
                null,
                null);
        }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteAllPets();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteAllPets() {
        int rowsDeleted = getContentResolver().delete(ProductContract.ProductEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from products database");
    }

}
