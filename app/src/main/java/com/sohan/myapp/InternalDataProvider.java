package com.sohan.myapp;

public class InternalDataProvider {

    private static InternalDataProvider instance;

    private String webUrl;

    private InternalDataProvider(){}

    public static InternalDataProvider getInstance(){
        if(instance == null){
            instance = new InternalDataProvider();
        }
        return instance;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
}
