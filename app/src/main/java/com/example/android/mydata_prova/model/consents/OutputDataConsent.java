package com.example.android.mydata_prova.model.consents;

/**
 * Created by Valentina on 24/08/2017.
 */

import java.util.Set;

/**
 * This class describes DataConsent issued for data streams flowing out of the
 * Personal Data Vault.
 *
 * @author Giada
 *
 */

public class OutputDataConsent extends DataConsent {

    public OutputDataConsent(Set<String> typesConst, ServiceConsent serviceConsent) {
        super(typesConst, serviceConsent);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return super.toString() + " for data in output.";
    }

}

