package com.example.android.mydata_prova.model.MyData;

/**
 * Created by Valentina on 24/08/2017.
 */

import java.util.Set;

public interface IDataSet {

    public void put(String typeConst, Object obj);

    public Object getObject(String typeConst);

    public Set<String> getKeys();
}

