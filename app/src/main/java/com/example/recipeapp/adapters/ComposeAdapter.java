package com.example.recipeapp.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.models.Recipe;
import com.parse.ParseUser;

import java.util.List;

public class ComposeAdapter extends RecyclerView.Adapter<ComposeAdapter.ViewHolder> {

    private Context mContext;
    private List<Recipe> recipes;
    private List<String> favoriteUserRecipes;
    private ParseUser currentUser;

    public interface OnClickListener{
        void onItemClicked(int position);
        void onFavoritesClicked(int position);
    }

    OnClickListener onClickListener;

    public ComposeAdapter(Context mContext, List<Recipe> recipes, OnClickListener onClickListener) {
        this.mContext = mContext;
        this.recipes = recipes;
        this.currentUser = ParseUser.getCurrentUser();
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitlePreview;
        private ImageView ivImagePreview;
        private TextView tvIngredientsCount;
        private ImageButton btnFavorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitlePreview = itemView.findViewById(R.id.tvTitlePreview);
            ivImagePreview = itemView.findViewById(R.id.ivImagePreview);
            tvIngredientsCount = itemView.findViewById(R.id.tvIngredientsCount);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }

        public void bind(Recipe recipe) {
            // bind post data to view elements
            tvTitlePreview.setText(recipe.getTitle());
            String INGREDIENTS_STRING = "Missed ingredients: " + String.valueOf(recipe.getMissedIngredientsCount()) + "     Used ingredients: " + String.valueOf(recipe.getUsedIngredientsCount());
            tvIngredientsCount.setText(INGREDIENTS_STRING);
            Glide.with(mContext)
                    .load(recipe.getImage().getUrl())
                    .into(ivImagePreview);

            favoriteUserRecipes = (List<String>) currentUser.get("recipesFavoritedUser");
            // handle whether recipe is in favorites or not
            if (favoriteUserRecipes.contains(recipe.getObjectId())) {
                btnFavorite.setBackgroundResource(R.drawable.ic_outline_star_24);
                btnFavorite.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(android.R.color.holo_orange_light)));
            } else {
                btnFavorite.setBackgroundResource(R.drawable.ic_round_star_outline_24);
                btnFavorite.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(android.R.color.black)));
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onItemClicked(getAdapterPosition());
                }
            });

            btnFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onFavoritesClicked(getAdapterPosition());
                }
            });
        }

    }
}
