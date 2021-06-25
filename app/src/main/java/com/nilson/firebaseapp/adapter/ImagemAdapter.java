package com.nilson.firebaseapp.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nilson.firebaseapp.R;
import com.nilson.firebaseapp.model.Upload;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class ImagemAdapter extends RecyclerView.Adapter<ImagemAdapter.ImagemVH> {
    private Context context;
    private ArrayList<Upload> listaUp;
    private OnItemClickListener listener;

    public void setListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public ImagemAdapter(Context c, ArrayList<Upload> l){
        this.context = c;
        this.listaUp = l;
    }
    @NonNull
    @Override
    public ImagemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.imagem_item,parent,false);

        return new ImagemVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagemVH holder, int position) {
        Upload upload = listaUp.get(position);
        holder.textNome.setText(upload.getNomeImagem());
        //setando imagem atraves da Glide
        Glide.with(context).load(upload.getUrl()).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return listaUp.size();
    }

    public class ImagemVH extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView textNome;
        ImageView imageView;


        public ImagemVH(@NonNull View itemView) {
            super(itemView);
            textNome = itemView.findViewById(R.id.imagem_item_nome);
            imageView = itemView.findViewById(R.id.imagem_item_imagem);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Selecionar ação");

            MenuItem deletar = menu.add(0,1,1,"Deletar");
            MenuItem atualizar =  menu.add(0,1,1,"Atualizar");

            //evento de click opcao deletar
            deletar.setOnMenuItemClickListener(item -> {
                if(listener !=null){
                    int position = getAdapterPosition();
                    listener.onDeleteClick(position);
                }
                return true;
            });
            atualizar.setOnMenuItemClickListener(item -> {
               if(listener!=null){
                   int position = getAdapterPosition();
                   listener.onUpdateClick(position);
               }
               return true;
            });
        }
    }
    public interface OnItemClickListener{
        void onDeleteClick(int position);
        void onUpdateClick(int position);
    }
}
