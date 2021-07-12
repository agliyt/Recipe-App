package com.example.recipeapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.FileUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.recipeapp.R;
import com.example.recipeapp.adapters.IngredientsAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class IngredientsFragment extends Fragment {

    List<String> ingredients = new ArrayList<>();

    Button btnAdd;
    EditText etIngredient;
    RecyclerView rvIngredients;
    IngredientsAdapter ingredientsAdapter;

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

        IngredientsAdapter.OnLongClickListener onLongClickListener = new IngredientsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                // Delete the item from the model
                ingredients.remove(position);
                // Notify the adaptor
                ingredientsAdapter.notifyItemRemoved(position);
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
                etIngredient.setText("");
                Toast.makeText(getContext().getApplicationContext(), "Ingredient was added", Toast.LENGTH_SHORT).show();
            }
        });
    }
}