package com.example.recipeapp.helpers;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.recipeapp.BuildConfig;
import com.example.recipeapp.R;
import com.example.recipeapp.fragments.IngredientsFragment;
import com.example.recipeapp.models.Receipt;
import com.example.recipeapp.models.Recipe;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiptProcessor extends AppCompatActivity {

    public static final String API_KEY = BuildConfig.VERYFI_KEY;
    public static final String CLIENT_ID = BuildConfig.VERYFI_CLIENT_ID;
    public static final String TAG = "ReceiptProcessor";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static String photoFileName = "receipt.jpg";

    private ParseUser currentUser;
    private Receipt receipt;
    private static File photoFile;

    public void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(this, "com.example.recipeapp.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                // query to API
                uploadReceiptToParse();
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    private void uploadReceiptToParse() {
        ParseQuery<Receipt> query = ParseQuery.getQuery(Receipt.class);
        query.include("user");
        query.findInBackground(new FindCallback<Receipt>() {
            @Override
            public void done(List<Receipt> receipts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting receipt", e);
                    return;
                }
                // should only be one receipt per user
                if (receipts.size() != 1) {
                    Log.e(TAG, "Only 1 receipt allowed per user!");
                }
                receipt = receipts.get(0);
                receipt.setImage(new ParseFile(photoFile));

                // Saves the object.
                receipt.saveInBackground(error -> {
                    if (error == null) {
                        //Save successful
                        Log.i(TAG, "Save successful");
                        queryToApi();
                    } else {
                        // Something went wrong while saving
                        Log.e(TAG, "Save unsuccessful", error);
                    }
                });
            }
        });
    }

    private void queryToApi() {
        // Using Volley for POST endpoints: https://stackoverflow.com/a/33578202
        String URL = "https://api.veryfi.com/api/v7/partner/documents/";

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("file_name", photoFileName);
            jsonObject.put("file_url", receipt.getImage().getUrl());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i("VOLLEY", response.toString());
                JSONArray line_items = null;
                try {
                    line_items = response.getJSONArray("line_items");
                    List<String> newIngredients = new ArrayList<>();
                    for (int i = 0; i < line_items.length(); i++) {
                        String ingredient = line_items.getJSONObject(i).getString("description");
                        if (!ingredient.equals("SPECIAL")) {
                            newIngredients.add(ingredient);
                        }
                    }
                    Log.i(TAG, newIngredients.toString());

                    parseIngredients(newIngredients);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY", "VolleyError", error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("CLIENT-ID", CLIENT_ID);
                headers.put("AUTHORIZATION", API_KEY);
                Log.i(TAG, headers.toString());
                return headers;
            }
        };

        // in case of timeout error
        volleyRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }
            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }
            @Override
            public void retry(VolleyError error) throws VolleyError {
            }
        });

        requestQueue.add(volleyRequest);
    }

    private void parseIngredients(List<String> ingredients) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ingredients.size() - 1; i++) {
            sb.append(ingredients.get(i));
            sb.append("\n");
        }
        sb.append(ingredients.get(ingredients.size() - 1));
        String ingredientsString = sb.toString();

        String URL = ApiUrlHelper.getIngredientsParseUrl();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JSONArray jsonArray = new JSONArray();

        JsonArrayRequest volleyRequest = new JsonArrayRequest(Request.Method.POST, URL, jsonArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i("VOLLEY", response.toString());
                List<String> ingredientsParsed = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        ingredientsParsed.add(response.getJSONObject(i).getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                saveNewIngredients(ingredientsParsed);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY", "VolleyError", error);
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    String s = "ingredientList=\"" + ingredientsString + "\"";
                    return s.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of request body using %s", "utf-8");
                    return null;
                }
            }
        };
        requestQueue.add(volleyRequest);
    }

    private void saveNewIngredients(List<String> newIngredients) {
        currentUser = ParseUser.getCurrentUser();
        List<String> ingredients = (List<String>) currentUser.get("ingredientsOwned");

        ingredients.addAll(newIngredients);
        if (currentUser != null) {
            // Other attributes than "ingredientsOwned" will remain unchanged!
            currentUser.put("ingredientsOwned", ingredients);

            // Saves the object.
            currentUser.saveInBackground(e -> {
                if(e==null){
                    //Save successfull
                    Log.i(TAG, "Save successful: " + ingredients.toString());
                }else{
                    // Something went wrong while saving
                    Log.e(TAG, "Save unsuccessful", e);
                }
            });
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.ingredientsTab);

        FragmentTransaction ft =  getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        IngredientsFragment ingredientsFragment = new IngredientsFragment();

        ft.replace(R.id.flRecipesContainer, ingredientsFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

}
