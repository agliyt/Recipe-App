package com.example.recipeapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.recipeapp.R;
import com.example.recipeapp.models.Recipe;

import java.util.List;

public class ComposeAdapter extends RecyclerView.Adapter<ComposeAdapter.ViewHolder> {

    private Context mContext;
    private List<Recipe> recipes;

    public interface OnClickListener{
        void onItemClicked(int position);
    }

    OnClickListener onClickListener;

    public ComposeAdapter(Context mContext, List<Recipe> recipes, OnClickListener onClickListener) {
        this.mContext = mContext;
        this.recipes = recipes;
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

        private Button btnCompose;
        private TextView tvTitlePreview;
        private ImageView ivImagePreview;
        private TextView tvIngredientsCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitlePreview = itemView.findViewById(R.id.tvTitlePreview);
            ivImagePreview = itemView.findViewById(R.id.ivImagePreview);
            tvIngredientsCount = itemView.findViewById(R.id.tvIngredientsCount);
            btnCompose = itemView.findViewById(R.id.btnCompose);
        }

        public void bind(Recipe recipe) {
            // bind post data to view elements
            tvTitlePreview.setText(recipe.getTitle());
            String INGREDIENTS_STRING = "Missed ingredients: " + String.valueOf(recipe.getMissedIngredientsCount()) + "     Used ingredients: " + String.valueOf(recipe.getUsedIngredientsCount());
            tvIngredientsCount.setText(INGREDIENTS_STRING);
            Glide.with(mContext)
                    .load(recipe.getImageUrl())
                    .into(ivImagePreview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onItemClicked(getAdapterPosition());
                }
            });
        }

    }
}
