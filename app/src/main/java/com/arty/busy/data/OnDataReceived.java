package com.arty.busy.data;

import com.arty.busy.ui.home.items.ItemTaskList;

import java.util.List;

public interface OnDataReceived {
    void onReceived(List<ItemTaskList> list);
}
