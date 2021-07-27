package com.example.recipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.helpers.FavoritesHelper;
import com.example.recipeapp.helpers.ItemTapHandler;
import com.example.recipeapp.models.Recipe;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.parse.ParseUser;

import java.util.List;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.ViewHolder> {

    private Context mContext;
    private List<Recipe> recipes;
    private ParseUser currentUser;
    private List<String> favoriteApiRecipes;
    private List<String> favoriteUserRecipes;

    public interface OnClickListener{
        void onItemClicked(int position);
        void onFavoritesClicked(int position);
    }

    OnClickListener onClickListener;

    public RecipesAdapter(Context mContext, List<Recipe> recipes, OnClickListener onClickListener) {
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
    public void onBindViewHolder(@NonNull RecipesAdapter.ViewHolder holder, int position) {
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
        private LikeButton btnFavorite;

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
            if (recipe.isFromApi()) {
                Glide.with(mContext)
                        .load(recipe.getImageUrl())
                        .into(ivImagePreview);
            } else {
                Glide.with(mContext)
                        .load(recipe.getImage().getUrl())
                        .into(ivImagePreview);
            }

            favoriteApiRecipes = (List<String>) currentUser.get("recipesFavoritedAPI");
            favoriteUserRecipes = (List<String>) currentUser.get("recipesFavoritedUser");
            // handle whether recipe is in favorites or not
            if (recipe.isFromApi()) {
                if (favoriteApiRecipes.contains(String.valueOf(recipe.getId()))) {
                    btnFavorite.setLiked(true);
                } else {
                    btnFavorite.setLiked(false);
                }
            } else {
                if (favoriteUserRecipes.contains(recipe.getObjectId())) {
                    btnFavorite.setLiked(true);
                } else {
                    btnFavorite.setLiked(false);
                }
            }

            itemView.setOnTouchListener(new ItemTapHandler(mContext, new ItemTapHandler.Listener() {
                @Override
                public void onTap() {
                    onClickListener.onItemClicked(getAdapterPosition());
                }

                @Override
                public void onDoubleTap() {
                    FavoritesHelper.favoriteRecipe(recipe);
                    notifyItemChanged(getAdapterPosition());
                }
            }));

            btnFavorite.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    onClickListener.onFavoritesClicked(getAdapterPosition());
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    onClickListener.onFavoritesClicked(getAdapterPosition());
                }
            });

        }

    }
}