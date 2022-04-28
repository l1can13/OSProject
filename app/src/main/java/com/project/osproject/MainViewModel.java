package com.project.osproject;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    MediatorLiveData<String> mutableLiveDara = new MediatorLiveData<>();

    public void setTextt(String s){
        mutableLiveDara.setValue(s);
    }

    public MutableLiveData<String> getTextt(){
        return mutableLiveDara;
    }
}
