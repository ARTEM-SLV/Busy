package com.arty.busy.ui.home.items;

import java.util.Date;
import java.util.List;

public class ItemListOfDays {
    private Date date;
    private List<String> titlesService;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<String> getTitlesService() {
        return titlesService;
    }

    public void setTitlesService(List<String> titlesService) {
        this.titlesService = titlesService;
    }
}
