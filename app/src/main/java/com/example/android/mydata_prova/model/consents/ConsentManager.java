package com.example.android.mydata_prova.model.consents;

/**
 * Created by Valentina on 24/08/2017.
 */

import com.example.android.mydata_prova.model.MyData.IDataSet;
import com.example.android.mydata_prova.model.registry.ServiceRegistry;
import com.example.android.mydata_prova.model.services.IService;
import com.example.android.mydata_prova.model.users.IAccount;
import com.example.android.mydata_prova.model.users.IUser;

import java.util.Date;
import java.util.Set;

/**
 * This class provides static methods to ask for various types of consent.
 *
 * @author Giada
 *
 */

public class ConsentManager {

    public ConsentManager() {
    }

    /**
     * This method implements a mutual identification protocol between user and
     * service, by exchanging signed token that the other party can validate.
     *
     * @param user
     *            The user requesting a new ServiceConsent.
     * @param service
     *            The Service which is given consent.
     * @return a new instance of an Active ServiceConsent.
     */

    public static ServiceConsent askServiceConsent(IUser user, IService service) {

        // inizializzazione casuale in attesa di approfondimento

        byte[] tokenFromUser = "Example Token From User".getBytes(), tokenSignedUser = null;
        try {
            tokenSignedUser = user.getSecurityManager().sign(tokenFromUser);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new SecurityException("Encountered error during sign process - user side.");
        }
        if (!(service.getSecurityManager().verify(user.getSecurityManager().getPublicKey(), tokenFromUser,
                tokenSignedUser))) {
            throw new SecurityException("Encountered error during verify process - Service side.");
        } // else

        byte[] tokenFromService = "Example Token From Service".getBytes(), tokenSignedService = null;
        try {
            tokenSignedService = service.getSecurityManager().sign(tokenFromService);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SecurityException("Encountered error during sign process - Service side.");
        }
        if (!(user.getSecurityManager().verify(service.getSecurityManager().getPublicKey(), tokenFromService,
                tokenSignedService))) {
            throw new SecurityException("Encountered error during verify process - user side.");
        } // else
        ServiceConsent sc = new ServiceConsent(tokenSignedService, tokenSignedUser, new Date(), service, user);
        return sc;
    }

    /**
     * This method allows to change the Status of the consent for the specified
     * Service to the specified User. If the current status is Active or
     * Disabled, it is possible to choose any other status different from the
     * current one. Else, if the current ConsentStatus is Withdrawn, no change
     * is possible.
     *
     * @param user
     * @param service
     * @param newStatus
     *
     * @throws IllegalArgumentException
     *             If attempts to change a Withdrawn ConsentStatus are made.
     */

    public static void changeServiceConsentStatusForService(IUser user, IService service, ConsentStatus newStatus) {
        boolean changed = false;

        for (IAccount a : user.getAllAccounts())
            if (a.getService().equals(service)) {
                ServiceConsent sc = a.getActiveDisabledSC();
                if (sc != null) {
                    // il consent è attivo o disabilitato, ed è l'unico:
					// dopo averlo cambiato, esco dal ciclo
                    sc.ChangeConsentStatus(newStatus);
                    changed = true;
					break;
                }
            }

        if (!changed) {
            // non ci sono consent attivi o disabilitati
            if (newStatus != ConsentStatus.WITHDRAWN) {
                // si vuole mettere attivo o disabilitato ad un consent
                // già ritirato
                throw new IllegalStateException("Cannot change the status of a Withdrawn Service Consent.");
            }
        }

    }

    /**
     * This method issues a new Data Consent (for data flowing out of the PDV)
     * if there is an Active ServiceConsent for the user and service specified.
     * It also adds the newly created DataConsent to the corresponding IAccount.
     * Data types allowed are retrieved from Service Registry, in order to
     * prevent illegal requests from the service.
     *
     * @param user
     * @param service
     * @throws IllegalStateException
     *             If there is no Active ServiceConsent for the pair user,
     *             service.
     * @return A valid DataConsent containing the specific Set of IMetadatum for
     *         that service.
     */

    public static OutputDataConsent askOutputDataConsent(IUser user, IService service) {
        ServiceConsent consent = user.getActiveSCForService(service);
        if (consent == null)
            throw new IllegalStateException("Cannot issue Output Data Consent for service " + service.toString()
                    + " if Service Consent is not Active.");
        Set<String> metadata = ServiceRegistry.getMetadataForService(service);
        OutputDataConsent outDataConsent = new OutputDataConsent(metadata, consent);
        user.addDataConsent(outDataConsent, service);
        return outDataConsent;
    }

    /**
     * This method issues a new Data Consent (for data flowing into the PDV) if
     * there is an Active ServiceConsent for the user and service specified. It
     * also adds the newly created DataConsent to the corresponding IAccount.
     * Data types allowed are retrieved from Service Registry, in order to
     * prevent illegal requests from the service: if the dataSet specified as an
     * input parameter does not match, nor is contained in the Set specified
     * during the registration procedure, an exception is thrown.
     *
     * @param user
     * @param service
     * @param dataSet
     *            the String constants corresponding to the data types the
     *            service is going to save in the PDV.
     * @throws IllegalStateException
     *             if there is no Active ServiceConsent for the specified
     *             service.
     * @throws IllegalArgumentException
     *             if the data types specified in input are not registered in
     *             the Service Registry for this service.
     * @return
     */
    public static InputDataConsent askInputDataConsent(IUser user, IService service, IDataSet dataSet) {
        ServiceConsent consent = user.getActiveSCForService(service);
        if (consent == null)
            throw new IllegalStateException("Cannot issue Input Data Consent for service " + service.toString()
                    + " if Service Consent is not Active.");
        Set<String> metadata = ServiceRegistry.getMetadataForService(service);
        Set<String> dataSetKeys = dataSet.getKeys();
        if (!metadata.containsAll(dataSetKeys))
            throw new IllegalArgumentException("The service " + service.toString()
                    + " does not have permission to access some or all of the data types specified in the given DataSet.");
        InputDataConsent inDataConsent = new InputDataConsent(dataSet.getKeys(), consent);
        user.addDataConsent(inDataConsent, service);
        return inDataConsent;
    }

}
