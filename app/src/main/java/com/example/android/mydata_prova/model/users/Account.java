package com.example.android.mydata_prova.model.users;

/**
 * Created by Valentina on 24/08/2017.
 */

import com.example.android.mydata_prova.model.consents.ConsentStatus;
import com.example.android.mydata_prova.model.consents.DataConsent;
import com.example.android.mydata_prova.model.consents.ServiceConsent;
import com.example.android.mydata_prova.model.services.IService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Account implements IAccount {

    private IService service;
    private Map<ServiceConsent, List<DataConsent>> dataConsents = new HashMap<ServiceConsent, List<DataConsent>>();

    public Account(IService service, ServiceConsent firstConsent) {
        super();
        this.service = service;
        // il consent ricevuto è sicuramente attivo
        this.dataConsents.put(firstConsent, new ArrayList<DataConsent>());
    }

    @Override
    public IService getService() {
        return this.service;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((service == null) ? 0 : service.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Account other = (Account) obj;
        if (service == null) {
            if (other.service != null)
                return false;
        } else if (!service.equals(other.service))
            return false;
        return true;
    }

    @Override
    public String toString() {
        String status;
		if (this.getActiveDisabledSC() != null)
			status = this.getActiveDisabledSC().getConsentStatus().toString();
		else
			status = ConsentStatus.WITHDRAWN.toString();

        return this.service.toString() + " con stato " + status;
    }

    @Override
    public ServiceConsent getActiveDisabledSC() {
        for (ServiceConsent sc : this.dataConsents.keySet()) {
            if (sc.getConsentStatus() != ConsentStatus.WITHDRAWN) {
                return sc;
            }
        }
        return null;
    }

    @Override
    public Set<ServiceConsent> getAllServiceConsents() {
        Set<ServiceConsent> result = new HashSet<ServiceConsent>();
        for (ServiceConsent sc : this.dataConsents.keySet())
            result.add(sc);
        return result;
    }

    /**
     * This method simply adds the DataConsent to the private map of the Account
     * class. It does not check if the DataConsent is valid because this method
     * is protected, so it is invoked only by MyDataUser, which is actually in
     * charge of this matter. Moreover, the ConsentManager takes responsibility
     * for checking consents' validity before releasing any new permission.
     *
     * @param dataConsent
     *            a valid DataConsent for this service
     * @throws IllegalStateException
     *             if, by any chance, the corresponding ServiceConsent is not
     *             valid anymore
     */
    @Override
    public void addDataConsent(DataConsent dataConsent) {
        ServiceConsent key = this.getActiveDisabledSC();
        if (key == null || key.getConsentStatus() == ConsentStatus.DISABLED)
            throw new IllegalStateException("Data Consent " + dataConsent.getIdentifier().toString()
                    + " refers to a Disabled or Withdrawn Service Consent!");
        List<DataConsent> dConsents = null;
        if (this.dataConsents.get(key) == null)
            dConsents = new ArrayList<DataConsent>();
        else
            dConsents = this.dataConsents.get(key);
        dConsents.add(dataConsent);
        this.dataConsents.put(key, dConsents);
    }

    @Override
    public void addServiceConsent(ServiceConsent serviceConsent) {
        if (this.getActiveDisabledSC() != null)
            throw new IllegalStateException(
                    "Cannot issue a new ServiceConsent if there is already an instance with Active or Disabled Status.");
        this.dataConsents.put(serviceConsent, new ArrayList<DataConsent>());
    }


    @Override
    public List<DataConsent> getAllDataConsents(ServiceConsent sc) {
        if (!this.dataConsents.containsKey(sc))
            throw new IllegalArgumentException("The ServiceConsent specified does not refer to the current account.");
        return this.dataConsents.get(sc);
    }
}

