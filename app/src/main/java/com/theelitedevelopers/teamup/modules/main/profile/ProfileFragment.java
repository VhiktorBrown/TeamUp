package com.theelitedevelopers.teamup.modules.main.profile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.theelitedevelopers.teamup.R;
import com.theelitedevelopers.teamup.core.data.local.SharedPref;
import com.theelitedevelopers.teamup.core.utils.AppUtils;
import com.theelitedevelopers.teamup.core.utils.Constants;
import com.theelitedevelopers.teamup.databinding.FragmentProfileBinding;
import com.theelitedevelopers.teamup.modules.authentication.LoginActivity;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        binding.profileName.setText(SharedPref.getInstance(requireActivity()).getString(Constants.NAME));
        binding.role.setText(SharedPref.getInstance(requireActivity()).getString(Constants.ROLE));
        binding.email.setText(SharedPref.getInstance(requireActivity()).getString(Constants.EMAIL));
        binding.phoneNumber.setText(SharedPref.getInstance(requireActivity()).getString(Constants.PHONE));
        binding.address.setText(SharedPref.getInstance(requireActivity()).getString(Constants.ADDRESS));

        binding.logOut.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            AppUtils.Companion.removeDataToSharedPref(requireActivity());
            startActivity(new Intent(requireActivity(), LoginActivity.class));
            ((Activity) v.getContext()).finishAffinity();
        });

        return binding.getRoot();
    }
}