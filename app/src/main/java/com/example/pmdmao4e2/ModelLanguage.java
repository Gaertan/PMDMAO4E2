package com.example.pmdmao4e2;

import java.util.Objects;

public class ModelLanguage {


    String languageCode;
    String languageTitle;


    public ModelLanguage(String languageCode, String languageTitle) {
        this.languageCode = languageCode;
        this.languageTitle = languageTitle;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageTitle() {
        return languageTitle;
    }

    public void setLanguageTitle(String languageTitle) {
        this.languageTitle = languageTitle;
    }

    @Override
    public String toString() {
        return  languageTitle ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModelLanguage that = (ModelLanguage) o;
        return getLanguageCode().equals(that.getLanguageCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLanguageCode());
    }
}
