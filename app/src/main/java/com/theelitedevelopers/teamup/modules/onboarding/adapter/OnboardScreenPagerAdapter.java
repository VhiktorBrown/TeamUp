package com.theelitedevelopers.teamup.modules.onboarding.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.theelitedevelopers.teamup.R;


public class OnboardScreenPagerAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;

    public OnboardScreenPagerAdapter(Context context) {
        this.context = context;
    }

    public int[] slide_images = {
        R.drawable.team_up_task,
        R.drawable.team_up_group,
        R.drawable.team_up_chat,
    };

    public String[] slide_titles = {
            "Your tasks in one place",
            "Group chat with Colleagues",
            "Private Messaging"
    };

    public String[] slide_description = {
            "Get assigned tasks, carry them out and turn them in by marking them as completed",
            "Spark up conversations surrounding work related stuff with your colleagues in General group chats",
            "Ask questions from your fellow colleagues by chatting them up privately."
    };

    @Override
    public int getCount() {
        return slide_description.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (ConstraintLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.slide_layout, container, false);

        ImageView imageView = view.findViewById(R.id.image);
        TextView title = view.findViewById(R.id.title);
        TextView description = view.findViewById(R.id.description);

        imageView.setImageResource(slide_images[position]);
        title.setText(slide_titles[position]);
        description.setText(slide_description[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
