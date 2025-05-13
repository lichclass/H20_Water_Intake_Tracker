package com.shysoftware.h20tracker.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.model.Notification;
import com.shysoftware.h20tracker.utils.NotificationAdapter;
import com.shysoftware.h20tracker.viewmodel.NotificationViewModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    RecyclerView notificationRecycler;

    NotificationViewModel notificationViewModel;

    NotificationAdapter notificationAdapter;

    List<Notification> notifications = new ArrayList<>();

    public NotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* ------------------ Place All View Logic Here ------------------ */
        initViews(view);
        initVM();

        notificationViewModel.getNotifications().observe(getViewLifecycleOwner(), list -> {
            notifications.clear();
            notifications.addAll(list);
            notificationAdapter.notifyDataSetChanged();
        });


    }

    private void initViews(View view){
        notificationRecycler = view.findViewById(R.id.recyclerViewNotifications);
        notificationRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        notificationAdapter = new NotificationAdapter(requireContext(), notifications);
        notificationRecycler.setAdapter(notificationAdapter);
    }

    private void initVM(){
        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);
    }


}