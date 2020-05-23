package com.example.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.example.inventoryapp.data.ProductContract;

import java.util.List;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ORDER_TAG ="Editor Activity" ;
    private EditText nameEditText;
    private EditText priceEditText;
    private EditText supplierEditText;
    private EditText quantityEditText;
    private Button increaseQtyButton, decreaseQtyButton, orderProductButton, deleteProductButton;
    private boolean mProductHasChanged = false;
    public int mQuantity = 0;
    private boolean qtyIncreased = false,qtyDecreased=false;
    private static final int EXISTING_PRODUCT_LOADER = 0;
    private Uri mCurrentProductUri;
    private ProductCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mCursorAdapter = new ProductCursorAdapter(this, null);
        getIntent().getIntExtra("ID", 0);
        // Find all relevant views that we will need to read user input from
        nameEditText = (EditText) findViewById(R.id.edit_product_name);
        priceEditText = (EditText) findViewById(R.id.edit_product_price);
        supplierEditText = (EditText) findViewById(R.id.edit_product_supplier);
        quantityEditText = (EditText) findViewById(R.id.quantity_of_each_product);
        orderProductButton = findViewById(R.id.order_button);
        deleteProductButton = findViewById(R.id.delete_product_button);
        increaseQtyButton = findViewById(R.id.increase_quantity_button);
        decreaseQtyButton = findViewById(R.id.decrease_quantity_button);

        nameEditText.setOnTouchListener(mTouchListener);
        priceEditText.setOnTouchListener(mTouchListener);
        supplierEditText.setOnTouchListener(mTouchListener);
        quantityEditText.setOnTouchListener(mTouchListener);

        increaseQtyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuantity = mQuantity +1;
                quantityEditText.setText(Integer.toString(mQuantity));

            }
        });
        decreaseQtyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qtyDecreased=true;
                if(mQuantity!=0)
                {
                mQuantity = mQuantity-1;
                }
                quantityEditText.setText(Integer.toString(mQuantity));
                //saveProduct();
            }
        });
        deleteProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
        orderProductButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                Log.v(ORDER_TAG,"Order requested");
                /**
                 * Uri webpage = Uri.parse("https://www.google.com");
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(webIntent);

                 Intent intent = new Intent(Intent.ACTION_VIEW);
                 String title = getResources().getString(R.string.chooser_title);
                 Intent chooser = Intent.createChooser(intent, title);
                 if (intent.resolveActivity(getPackageManager()) != null) {
                 startActivity(chooser);}
                 **/
  //TOdo: Action_View works gives Amazon. ACtion Main gives google. ACTION_WEB_SEARCH opens google
                Intent intent = new Intent(Intent.ACTION_VIEW);
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);

                boolean isIntentSafe = activities.size() > 0;
                if (isIntentSafe=true){
                startActivity(intent);}
            }

        });

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }
    }

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };


    private void saveProduct() {
        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String supplierString = supplierEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplierString);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,quantityString);

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor
                , menu);
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_product));
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                finish();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);

                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
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
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER};
        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        if (data.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int supplierColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER);
            int quantityColumnIndex = data.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            // Extract out the value from the Cursor for the given column index
            String name = data.getString(nameColumnIndex);
            int price = data.getInt(priceColumnIndex);
            String supplier = data.getString(supplierColumnIndex);
            int quantity = data.getInt(quantityColumnIndex);
            // Update the views on the screen with the values from the database
            nameEditText.setText(name);
            priceEditText.setText(Integer.toString(price));
            supplierEditText.setText(supplier);
            quantityEditText.setText(Integer.toString(quantity));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        supplierEditText.setText("");
        quantityEditText.setText("");
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePet() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
