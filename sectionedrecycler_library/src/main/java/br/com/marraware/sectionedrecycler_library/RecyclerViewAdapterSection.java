package br.com.marraware.sectionedrecycler_library;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by joao_gabriel on 16/08/2018.
 */
public abstract class RecyclerViewAdapterSection<R extends RecyclerView.ViewHolder, H extends RecyclerView.ViewHolder> {

    public abstract R onCreateViewHolder(@NonNull ViewGroup parent, int viewType);
    public abstract void onBindViewHolder(R viewHolder, int position);
    public abstract int getItemCount();

    public boolean hasHeader() {
        return false;
    }
    public H onCreateHeaderViewHolder() {
        return null;
    }
    public void onBindHeaderViewHolder(H header) {

    }
}
