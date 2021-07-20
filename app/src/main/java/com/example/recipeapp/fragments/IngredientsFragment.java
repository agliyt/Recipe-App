package com.example.recipeapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.FileUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.recipeapp.R;
import com.example.recipeapp.adapters.IngredientsAdapter;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class IngredientsFragment extends Fragment {

    public static final String TAG = "IngredientsFragment";

    protected List<String> ingredients;

    private Button btnAdd;
    private EditText etIngredient;
    private RecyclerView rvIngredients;
    private IngredientsAdapter ingredientsAdapter;
    protected ParseUser currentUser;

    public IngredientsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ingredients, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnAdd = view.findViewById(R.id.btnAdd);
        etIngredient = view.findViewById(R.id.etIngredient);
        rvIngredients = view.findViewById(R.id.rvIngredients);
        currentUser = ParseUser.getCurrentUser();
        initializeIngredients();
        Log.i(TAG, ingredients.toString());

        IngredientsAdapter.OnLongClickListener onLongClickListener = new IngredientsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                // Delete the item from the model
                ingredients.remove(position);
                // Notify the adaptor
                ingredientsAdapter.notifyItemRemoved(position);
                updateIngredients();
                Toast.makeText(getContext().getApplicationContext(), "Ingredient was removed", Toast.LENGTH_SHORT).show();
            }
        };
        ingredientsAdapter = new IngredientsAdapter(ingredients, onLongClickListener);
        rvIngredients.setAdapter(ingredientsAdapter);
        rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String todoItem = etIngredient.getText().toString();
                // Add item to the model
                ingredients.add(todoItem);
                // Notify the adaptor that we've inserted an item
                ingredientsAdapter.notifyItemInserted(ingredients.size() - 1);
                updateIngredients();
                etIngredient.setText("");
                Toast.makeText(getContext().getApplicationContext(), "Ingredient was added", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void initializeIngredients() {
        ingredients = (List<String>) currentUser.get("ingredientsOwned");
    }

    public void updateIngredients() {
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
    }
}