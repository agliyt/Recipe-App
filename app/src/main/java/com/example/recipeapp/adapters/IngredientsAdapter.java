package com.example.recipeapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Responsible for displaying data from the model into a row in the recycler view
public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    public interface OnLongClickListener {
        void onItemLongClicked(int position);
    }

    List<String> ingredients;
    OnLongClickListener longClickListener;

    public IngredientsAdapter(List<String> ingredients, OnLongClickListener longClickListener) {
        this.ingredients = ingredients;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Use layout inflater to inflate a view
        View todoView = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);

        // Wrap it inside a View Holder and return it
        return new ViewHolder(todoView);
    }

    // Responsible for binding data to a particular View Holder
    @Override
    public void onBindViewHolder(@NonNull IngredientsAdapter.ViewHolder holder, int position) {
        // Grab the item at the position
        String ingredient = ingredients.get(position);

        // Bind the item into the specified View Holder
        holder.bind(ingredient);
    }

    // Tells the recycler view how many are in the list
    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    // Container to provide easy access to views that represent each row on the list
    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvIngredient;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredient = itemView.findViewById(android.R.id.text1);
        }

        // Update the view inside the View Holder with this data
        public void bind(String item) {
            tvIngredient.setText(item);
            tvIngredient.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Notify the listener which position was long pressed
                    longClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
        }
    }
}
