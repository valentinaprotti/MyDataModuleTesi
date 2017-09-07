package com.example.android.mydata_prova.model.users;

/**
 * Created by Valentina on 24/08/2017.
 */

import com.example.android.mydata_prova.model.MyData.IPersonalDataVault;
import com.example.android.mydata_prova.model.consents.DataConsent;
import com.example.android.mydata_prova.model.consents.ServiceConsent;
import com.example.android.mydata_prova.model.security.ISecurityManager;
import com.example.android.mydata_prova.model.services.IService;

import java.util.Date;
import java.util.Set;

public interface IUser {

    public String getFirstName();

    public void setFirstName(String firstName);

    public String getLastName();

    public void setLastName(String lastName);

    public Date getDateOfBirth();

    public void setDateOfBirth(Date dateOfBirth);

    public String getEmailAddress();

    public void setEmailAddress(String emailAddress);

    public ISecurityManager getSecurityManager();

    public int hashCode();

    /**
     * This function compares two users on their emailAddress. As a design
     * choice, the emailAddress field must be unique.
     *
     * @param other
     * @return true if the email is the same, false otherwise.
     */
    public boolean equals(Object other);

    /**
     * This function creates a new Account for the service specified only if the
     * user does not already have one. In order to do this, it asks the Consent
     * Manager to issue a new Service Consent, which is necessary to create a
     * new account.
     *
     * @param service
     *            The service the user wants to register at.
     * @throws IllegalArgumentException
     *             if the account already exists.
     */
    public void newAccountAtService(IService service);

    public Set<IAccount> getAllAccounts();

    public boolean checkIfPasswordEqual(char[] password);

    public boolean hasAccountAtService(IService service);

    /**
     * This method returns the instance of ServiceConsent which has Active
     * Status and is registered for the specified service.
     *
     * @param service
     * @throws IllegalArgumentException
     *             if this user does not have an account at the specified
     *             service
     * @return the corresponding ServiceConsent if there is one with Active
     *         status, null otherwise.
     */
    public ServiceConsent getActiveSCForService(IService service);

    public void addDataConsent(DataConsent dataConsent, IService service);

    public void addServiceConsent(IService selectedService);

    public IPersonalDataVault getPersonalDataVault();

}
