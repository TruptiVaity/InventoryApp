package com.example.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ProductProvider extends ContentProvider {

    private ProductsDbHelper mDbHelper;
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PATH_PRODUCTS,PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PATH_PRODUCTS+"/#",PRODUCT_ID);
    }


    @Override
    public boolean onCreate() {
        // TODO: Create and initialize a DBhelper object to gain access to the products database.
        mDbHelper = new ProductsDbHelper(getContext());
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match){
            case PRODUCTS:
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unkknown URI" + uri + "with match" + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values){
        //Check that the name is not null
        String name = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Product requires a name");
        }
        // TODO: Finish sanity checking the rest of the attributes in ContentValues
        Integer price = values.getAsInteger((ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE));
        if(price != null && price < 0){
            throw new IllegalArgumentException("Please update a valid price of the product");
        }

/**        Integer quantity = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }
 **/
        String supplierName = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER);
        if (supplierName == null || supplierName.isEmpty()) {
            throw new IllegalArgumentException("Product requires a name");
        }
        // TODO: Insert a new pet into ProductProviderthe pets database table with the given ContentValues
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Once we know the ID of the new row in the table,
        long id = database.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        //Notify all listeners that the data has been changed for the pet content URI
        //uri:content://com.example.android.pets/pets
        getContext().getContentResolver().notifyChange(uri,null);
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // TODO: Update the selected products in the products database table with the given ContentValues
        // TODO: Return the number of rows that were affected
        if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if(values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE)){
            Integer price = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price != null && price < 0){
            throw new IllegalArgumentException("Please update a valid price of the product");
            }
        }
/**       if (values.containsKey(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity != null || quantity < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }
**/
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ProductContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }
}
