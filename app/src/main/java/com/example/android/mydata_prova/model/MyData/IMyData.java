package com.example.android.mydata_prova.model.MyData;

/**
 * Created by Valentina on 25/08/2017.
 */


import com.example.android.mydata_prova.model.consents.InputDataConsent;
import com.example.android.mydata_prova.model.consents.OutputDataConsent;
import com.example.android.mydata_prova.model.services.IService;
import com.example.android.mydata_prova.model.users.IUser;

import java.util.Date;

public interface IMyData {

    public IUser loginUser(String email, char[] password);

    public IUser createMyDataAccount(String firstName, String lastName, Date dateOfBirth, String emailAddress,
                                     char[] password);

    public IDataSet getDataSetForOutputDataConsent(OutputDataConsent outputDataConsent);

    public void saveDataSet(IDataSet dataSet, InputDataConsent inDataConsent);

    public void issueNewServiceConsent(IService selectedService, IUser authenticatedUser);

    public void createServiceAccount(IUser user, IService service);

}

