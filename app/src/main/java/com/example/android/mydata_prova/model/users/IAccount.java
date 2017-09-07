package com.example.android.mydata_prova.model.users;

/**
 * Created by Valentina on 24/08/2017.
 */

import com.example.android.mydata_prova.model.consents.DataConsent;
import com.example.android.mydata_prova.model.consents.ServiceConsent;
import com.example.android.mydata_prova.model.services.IService;

import java.util.List;
import java.util.Set;

public interface IAccount {

    public IService getService();

    /**
     * There can be only one ServiceConsent Active or Disabled at a time for a
     * service. If there is no such consent, this method returns null
     *
     * @return The only Active or Disabled ServiceConsent for
     *         this service, null otherwise.
     */
    public ServiceConsent getActiveDisabledSC();

    public Set<ServiceConsent> getAllServiceConsents();

    public void addDataConsent(DataConsent dataConsent);

    public void addServiceConsent(ServiceConsent serviceConsent);

    public List<DataConsent> getAllDataConsents(ServiceConsent sc);
}
