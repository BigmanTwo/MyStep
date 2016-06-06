package com.example.asus.mystep;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wearable.view.CircledImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class UpFragment extends Fragment{
    private CircledImageView imageView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_up_fragment,null);
        imageView=(CircledImageView) view.findViewById(R.id.img);
        imageView.setImageResource(R.mipmap.ph1);
        return view;
    }
}
