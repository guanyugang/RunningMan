package com.example.runningman.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.runningman.DynamicDemo;
import com.example.runningman.MainActivity;
import com.example.runningman.R;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class BlankFragment extends Fragment implements View.OnClickListener{

    Button dyn;
    public BlankFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        // Inflate the layout for this fragment
        dyn=view.findViewById(R.id.dyn);
        dyn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DynamicDemo.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {

    }
}
