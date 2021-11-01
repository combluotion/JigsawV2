package com.uocp8.jigsawv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.uocp8.jigsawv2.model.PictureModel;

import java.util.List;

public class RecyclerViewAdaptador extends RecyclerView.Adapter<RecyclerViewAdaptador.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView picture;
        ImageView imgPicture;


        public ViewHolder(View itemView) {
            super(itemView);
            picture=(TextView)itemView.findViewById(R.id.textPicture);
            imgPicture=(ImageView) itemView.findViewById(R.id.imgPicture);

        }
    }

    public List<PictureModel> pictureLista;
    private ItemClickListener mItemListener;

    public RecyclerViewAdaptador(List<PictureModel> pictureLista, ItemClickListener itemClickListener) {
        this.pictureLista = pictureLista;
        this.mItemListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_picture, parent, false);
        ViewHolder viewHolder= new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.picture.setText(pictureLista.get(position).getPicture());
        holder.imgPicture.setImageResource(pictureLista.get(position).getImgPicture());

        holder.itemView.setOnClickListener(view -> {
            mItemListener.onItemClick(pictureLista.get(position));
        });
    }


    @Override
    public int getItemCount() {
        return pictureLista.size();
    }

    public interface ItemClickListener {
        void onItemClick(PictureModel picture);
    }
}
