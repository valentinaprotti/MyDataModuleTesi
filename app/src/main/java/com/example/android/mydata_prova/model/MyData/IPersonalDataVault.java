package com.example.android.mydata_prova.model.MyData;

/**
 * Created by Valentina on 24/08/2017.
 */

import java.util.Set;

public interface IPersonalDataVault {

    public IDataSet getData(Set<String> typesConst);

    public void saveData(IDataSet dataSet);
}

