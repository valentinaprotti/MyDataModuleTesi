package com.example.android.mydata_prova.model.services;

/**
 * Created by Valentina on 25/08/2017.
 */

import com.example.android.mydata_prova.model.MyData.IDataSet;
import com.example.android.mydata_prova.model.MyData.IMyData;
import com.example.android.mydata_prova.model.MyData.MyData;
import com.example.android.mydata_prova.model.consents.ConsentManager;
import com.example.android.mydata_prova.model.consents.InputDataConsent;
import com.example.android.mydata_prova.model.consents.OutputDataConsent;
import com.example.android.mydata_prova.model.consents.ServiceConsent;
import com.example.android.mydata_prova.model.security.ISecurityManager;
import com.example.android.mydata_prova.model.users.IUser;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Any new service should extend this class to be invokable within the
 * infrastructure. This does not comprehend service registration, which should
 * be done by the extending class.
 *
 * @author Giada
 *
 */

public abstract class AbstractService implements IService {

    private IMyData myDataInstance;
    private ISecurityManager securityManager;

    protected AbstractService() {
        this.myDataInstance = MyData.getInstance();
        this.securityManager = new com.example.android.mydata_prova.model.security.SecurityManager();
    }

    /**
     * This method implements that part of the business logic which is common to
     * each service: it asks the ConsentManager for a OutputDataConsent and it
     * receives the DataSet associated with that consent. After that, an
     * abstract method is called, which will contain the specific implementation
     * of the service considered.
     */
    @Override
    public Object provideService(IUser user) throws FileNotFoundException, IOException {
        OutputDataConsent outDataConsent = ConsentManager.askOutputDataConsent(user, this);
        IDataSet dataSet = myDataInstance.getDataSetForOutputDataConsent(outDataConsent);
        return this.concreteService(dataSet);
    }

    @Override
    public void gatherData(IUser user, IDataSet dataSet) {
        if (!user.hasAccountAtService(this))
            throw new IllegalArgumentException(
                    "User " + user.toString() + " doesn't have an account to this service " + this.toString() + ".");
        ServiceConsent sConsent = user.getActiveSCForService(this);
        if (sConsent == null)
            throw new IllegalStateException(
                    "The Service Consent associated with " + this.toString() + " is not Active.");
        InputDataConsent inDataConsent = ConsentManager.askInputDataConsent(user, this, dataSet);
        this.myDataInstance.saveDataSet(dataSet, inDataConsent);
    }

    @Override
    public ISecurityManager getSecurityManager() {
        return this.securityManager;
    }

    protected abstract Object concreteService(IDataSet dataSet) throws FileNotFoundException, IOException;

    /**
     * This method is called at the instantiation of the implementing object,
     * which will be the concrete service. Its function is to register the
     * service to the Service Registry, as specified in the MyData Architecture
     * Specification. In particular, the concrete service has to declare which
     * data types it needs to work.
     */
    protected abstract void registerService();

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract String toString();

}
