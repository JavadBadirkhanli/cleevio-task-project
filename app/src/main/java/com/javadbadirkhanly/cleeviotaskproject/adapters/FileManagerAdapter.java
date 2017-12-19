package com.javadbadirkhanly.cleeviotaskproject.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.javadbadirkhanly.cleeviotaskproject.R;
import com.javadbadirkhanly.cleeviotaskproject.models.FileModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by javadbadirkhanly on 12/18/17.
 */

public class FileManagerAdapter extends RecyclerView.Adapter<FileManagerAdapter.ViewHolder> {

    private static final String TAG = FileManagerAdapter.class.getSimpleName();

    private Context context;

    private List<FileModel> fileModelList;

    public interface Listener {
        void ClickListener(int position);

        void LongClickListener(int position);
    }

    private Listener listener;

    public FileManagerAdapter(Context context, List<FileModel> fileModelList, Listener listener) {
        this.context = context;
        this.fileModelList = fileModelList;
        this.listener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_file_manager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FileModel fileModel = fileModelList.get(position);

        holder.tvName.setText(fileModel.getName());
        if (fileModel.isDirectory())
            holder.ivIconFile.setImageResource(R.drawable.folder_icon);
        else
            holder.ivIconFile.setImageResource(R.drawable.file_icon);
    }

    @Override
    public int getItemCount() {
        return fileModelList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        @BindView(R.id.ivIconFileManagerAdapter)
        ImageView ivIconFile;

        @BindView(R.id.tvNameFileManagerAdapter)
        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.ClickListener(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            listener.LongClickListener(getAdapterPosition());
            return true;
        }
    }
}
