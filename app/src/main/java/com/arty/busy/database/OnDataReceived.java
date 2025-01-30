package com.arty.busy.database;

import com.arty.busy.ui.home.items.ItemTaskList;

import java.util.List;

public interface OnDataReceived {
    void onReceived(List<ItemTaskList> list);
}
