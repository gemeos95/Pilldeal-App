package com.example.pilldeal5.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.pilldeal5.Login;
import com.example.pilldeal5.MainActivity;
import com.example.pilldeal5.R;


public class HomeFragment extends Fragment {

    private Context context;

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    private HomeViewModel homeViewModel;
    ImageSwitcher switchImage;
    Button nextImageButton;
    private Button take;
    private Button reset;
    private int num = 0;
    private String imageName;
    private TextView string;
    int storeImages[] = {R.drawable.pill0, R.drawable.pill1, R.drawable.pill2, R.drawable.pill3,
            R.drawable.pill4, R.drawable.pill5, R.drawable.pill6, R.drawable.pill7, R.drawable.pill7, R.drawable.pill8, R.drawable.pill9,
            R.drawable.pill10, R.drawable.pill11, R.drawable.pill12, R.drawable.pill13, R.drawable.pill14, R.drawable.pill15, R.drawable.pill16,
            R.drawable.pill17, R.drawable.pill18, R.drawable.pill19, R.drawable.pill20, R.drawable.pill21};
    int switchingImage = storeImages.length;
    int counter = 0;
    private View root;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);


        root = inflater.inflate(R.layout.fragment_home, container, false);

        switchImage = (ImageSwitcher) root.findViewById(R.id.imageSwitcher);
        nextImageButton = (Button) root.findViewById(R.id.take);

        switchImage.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageViewSwitch = new ImageView(context.getApplicationContext());
                imageViewSwitch.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //imageViewSwitch.setLayoutParams(new ImageSwitcher.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));
                imageViewSwitch.setImageResource(R.drawable.pill0);

                return imageViewSwitch;
            }
        });
        take = root.findViewById(R.id.take);
        reset= root.findViewById(R.id.reset);
        string = root.findViewById(R.id.string);
        string.setText("Day 0");
        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                num = changeImage(num);
                imageName = "Day " + num;

                string.setText(imageName);

                nextImageButton();

            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageName = "Day 0";
                string.setText(imageName);
                counter = -1;
                nextImageButton();

            }
        });
        return root;
    }

    public void nextImageButton() {
        counter++;
        if (counter == switchingImage)
            counter = 0;
        switchImage.setImageResource(storeImages[counter]);
    }


    private int changeImage(int n) {

        if (n < 22) {
            n += 1;
        } else {
            n = 0;
        }
        return n;

    }
}






