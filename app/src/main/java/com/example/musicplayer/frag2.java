package com.example.musicplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

//专辑界面
public class frag2 extends Fragment {
    private View zj;
    //显示布局
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        zj=inflater.inflate(R.layout.frag2_layout,null);
        return zj;
    }
}
