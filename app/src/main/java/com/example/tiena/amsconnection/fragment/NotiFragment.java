package com.example.tiena.amsconnection.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.tiena.amsconnection.R;
import com.example.tiena.amsconnection.item.Task;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.tiena.amsconnection.viewholder.NotiHolder;

/**
 * Created by tiena on 22/08/2017.
 */

public class NotiFragment extends Fragment {
    FirebaseDatabase mDatabase;
    FirebaseRecyclerAdapter mAdapter;
    LinearLayout rootLayout;
    RecyclerView notiList;
    Activity activity;
    DatabaseReference refToTasksInClasses;
    DatabaseReference refToTasks;
    int mCurCheckPosition;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity=getActivity();


        View notiFragmentLayout=inflater.inflate(R.layout.noti_fragment,container,false);
        notiList=notiFragmentLayout.findViewById(R.id.notiList);
        mDatabase=FirebaseDatabase.getInstance();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user==null)return null;
        refToTasks=mDatabase.getReference("tasks");
        mDatabase.getReference("students/"+user.getUid()+"/class_id").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String user_class_id=dataSnapshot.getValue(String.class);
                refToTasksInClasses=mDatabase.getReference("classes/"+user_class_id+"/tasks");
                setRecyclerAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return notiFragmentLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
        //Save the fragment's state here
    }

    void setRecyclerAdapter(){

        LinearLayoutManager manager = new LinearLayoutManager(activity);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        notiList.setLayoutManager(manager);

        mAdapter = new FirebaseRecyclerAdapter<Boolean, NotiHolder>(
                Boolean.class,
                R.layout.notification,
                NotiHolder.class,
                refToTasksInClasses) {
            @Override
            public void populateViewHolder(final NotiHolder holder, Boolean value, int position) {
                String key=mAdapter.getRef(position).getKey();
                holder.init(key);
            }
        };
        //notiList.setHasFixedSize(true);
        notiList.setItemViewCacheSize(30);
        notiList.setDrawingCacheEnabled(true);
        notiList.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        notiList.setAdapter(mAdapter);
    }


}
