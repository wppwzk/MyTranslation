package com.ghj.translation.mytranslation.adapter;

/**
 * Created by YBC on 2018/3/31.
 */

public class GlossaryItem {
    private String firstLanguage;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GlossaryItem(String id, String firstLanguage, String endLanguage) {
        this.endLanguage = endLanguage;
        this.firstLanguage = firstLanguage;
        this.id = id;
    }

    public String getFirstLanguage() {
        return firstLanguage;
    }

    public void setFirstLanguage(String firstLanguage) {
        this.firstLanguage = firstLanguage;
    }

    public String getEndLanguage() {
        return endLanguage;
    }

    public void setEndLanguage(String endLanguage) {
        this.endLanguage = endLanguage;
    }

    private String endLanguage;
}
