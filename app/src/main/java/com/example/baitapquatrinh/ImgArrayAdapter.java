package com.example.baitapquatrinh;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.List;

public class ImgArrayAdapter extends ArrayAdapter<Uri> {
    private Context context;
    private List<Uri> imgList;

    public ImgArrayAdapter(@NonNull Context context,int resource, List<Uri> imgList) {
        super(context, resource, imgList);
        this.context = context;
        this.imgList = imgList;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.img_view,parent,false);
        }
        ImageView img = convertView.findViewById(R.id.imgContent);
        Uri imageUri = getItem(position);
        img.setImageURI(imageUri);
        return convertView;
    }
}
