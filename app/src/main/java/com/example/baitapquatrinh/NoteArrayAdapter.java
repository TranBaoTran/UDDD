package com.example.baitapquatrinh;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class NoteArrayAdapter extends ArrayAdapter<note> {
    private Context context;
    List<note> notes;

    public NoteArrayAdapter(@NonNull Context context, int resource, List<note> notes) {
        super(context, resource, notes);
        this.context = context;
        this.notes = notes;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.note_view, parent, false);
        }
        TextView title = convertView.findViewById(R.id.titleView);
        TextView time = convertView.findViewById(R.id.timeView);
        TextView content = convertView.findViewById(R.id.contentView);

        note n = getItem(position);

        title.setText(n.getTitle());
        time.setText(n.getSaveDate());
        content.setText(n.getContent());

        return convertView;
    }
}
