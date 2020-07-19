package com.example.gardenweather3;

public class TempData {
    private String tempStr;

    private TempData(){}
    private static class TempDataHolder {
        private static final TempData instance = new TempData();
    }
    public static TempData getInstance() {
        return TempDataHolder.instance;
    }

    public String getTempStr() {
        return tempStr;
    }

    public void setTempStr(String tempStr) {
        this.tempStr = tempStr;
    }
}
