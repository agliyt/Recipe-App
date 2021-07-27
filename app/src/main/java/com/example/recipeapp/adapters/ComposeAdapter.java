package com.example.recipeapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.MainActivity;
import com.example.recipeapp.R;
import com.example.recipeapp.helpers.FavoritesHelper;
import com.example.recipeapp.helpers.ItemTapHandler;
import com.example.recipeapp.models.ParseRecipe;
import com.example.recipeapp.models.Recipe;
import com.google.android.material.snackbar.Snackbar;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ComposeAdapter extends RecyclerView.Adapter<ComposeAdapter.ViewHolder> {

    private Context mContext;
    private List<Recipe> recipes;
    private List<String> favoriteUserRecipes;
    private ParseUser currentUser;
    private Recipe recentlyDeletedRecipe;
    private int recentlyDeletedRecipePosition;

    public void deleteItem(int position) {
        recentlyDeletedRecipe = recipes.get(position);
        recentlyDeletedRecipePosition = position;
        recipes.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();

        // after undo snackbar passes, actually delete recipe from Parse
        ParseQuery<ParseRecipe> query = ParseQuery.getQuery(ParseRecipe.class);
        query.getInBackground(recentlyDeletedRecipe.getObjectId(), (object, e) -> {
            if (e == null) {
                // Deletes the fetched ParseObject from the database
                object.deleteInBackground(e2 -> {
                    if(e2==null){
                        Log.i("ComposeAdapter", "Success deleting recipe: " + recentlyDeletedRecipe.getTitle());
                    }else{
                        //Something went wrong while deleting the Object
                        Log.e("ComposeAdapter", "Issue with deleting recipe: " + recentlyDeletedRecipe.getTitle(), e);
                    }
                });
            }else{
                //Something went wrong while retrieving the Object
                Log.e("ComposeAdapter", "Issue with getting recipe: " + recentlyDeletedRecipe.getTitle(), e);
            }
        });
    }

    private void showUndoSnackbar() {
        View view = ((MainActivity) mContext).findViewById(R.id.rvUserRecipes);
        Snackbar snackbar = Snackbar.make(view, "Deleted", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete() {
        // add recipe back to parse
        ParseRecipe parseRecipe = new ParseRecipe();
        parseRecipe.setTitle(recentlyDeletedRecipe.getTitle());
        parseRecipe.setImage(recentlyDeletedRecipe.getImage());
        parseRecipe.setAuthor(currentUser);
        parseRecipe.setServings(recentlyDeletedRecipe.getServings());
        parseRecipe.setReadyInMinutes(recentlyDeletedRecipe.getReadyInMinutes());
        parseRecipe.setIngredients(recentlyDeletedRecipe.getIngredients());
        parseRecipe.setInstructions(recentlyDeletedRecipe.getInstructions());
        parseRecipe.setIngredientsParsed(recentlyDeletedRecipe.getIngredientsParsed());

        parseRecipe.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("ComposeAdapter", "Error while saving", e);
                    return;
                }
                Log.i("ComposeAdapter", "Post save was successful");
                List<ParseRecipe> recipeList = new ArrayList<>();
                recipeList.add(parseRecipe);
                try {
                    recipes.add(recentlyDeletedRecipePosition, Recipe.fromParseRecipeArray(recipeList).get(0));
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                notifyItemInserted(recentlyDeletedRecipePosition);
            }
        });
    }

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

    public Context getmContext() {
        return mContext;
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
            Glide.with(mContext)
                    .load(recipe.getImage().getUrl())
                    .into(ivImagePreview);

            favoriteUserRecipes = (List<String>) currentUser.get("recipesFavoritedUser");
            // handle whether recipe is in favorites or not
            if (favoriteUserRecipes.contains(recipe.getObjectId())) {
                btnFavorite.setLiked(true);
            } else {
                btnFavorite.setLiked(false);
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

                public void unLiked(LikeButton likeButton) {
                    onClickListener.onFavoritesClicked(getAdapterPosition());
                }
            });
        }

    }
}
