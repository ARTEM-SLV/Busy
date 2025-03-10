package com.arty.busy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.arty.busy.consts.Constants;
import com.arty.busy.ui.home.tasks.TaskFragment;

public class ActivityForFragments extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_fragments);

        int idTask = getIntent().getIntExtra(Constants.ID_TASK, -1);
        setTaskFragment(idTask);
    }

    private void setTaskFragment(int id){
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ID_TASK, id);

        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.container_for_fragments, TaskFragment.class, bundle)
//                .addToBackStack(null)
                .commit();
    }
}