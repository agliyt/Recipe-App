package com.example.recipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.Recipe;

import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private Context context;
    private List<Recipe> recipes;

    public RecipesAdapter(Context context, List<Recipe> recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipesAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvTitlePreview;
        private ImageView ivImagePreview;
        private TextView tvIngredientsCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitlePreview = itemView.findViewById(R.id.tvTitlePreview);
            ivImagePreview = itemView.findViewById(R.id.ivImagePreview);
            tvIngredientsCount = itemView.findViewById(R.id.tvIngredientsCount);
            itemView.setOnClickListener(this);
        }

        public void bind(Recipe recipe) {
            // bind post data to view elements
            tvTitlePreview.setText(recipe.getTitle());
            tvIngredientsCount.setText("Missed ingredients: " + String.valueOf(recipe.getMissedIngredientsCount()) + "     Used ingredients: " + String.valueOf(recipe.getUsedIngredientsCount()));
            Glide.with(context)
                    .load(recipe.getImageUrl())
                    .into(ivImagePreview);
        }

        @Override
        public void onClick(View v) {

        }
    }
}