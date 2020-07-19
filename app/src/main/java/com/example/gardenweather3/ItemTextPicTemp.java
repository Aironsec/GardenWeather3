package com.example.gardenweather3;

public class ItemTextPicTemp {
    private String textTitle;
    private int pic;
    private String temp;

    public ItemTextPicTemp(String textTitle, int pic, String temp) {
        this.textTitle = textTitle;
        this.pic = pic;
        this.temp = temp;
    }

    public String getTextTitle() {
        return textTitle;
    }

    public int getPic() {
        return pic;
    }

    public String getTemp() {
        return temp;
    }
}
