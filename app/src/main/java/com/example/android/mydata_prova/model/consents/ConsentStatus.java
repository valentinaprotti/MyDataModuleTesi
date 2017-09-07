package com.example.android.mydata_prova.model.consents;

/**
 * Created by Valentina on 24/08/2017.
 */

public enum ConsentStatus {
    ACTIVE ("Active"),
    WITHDRAWN ("Withdrawn"),
    DISABLED ("Disabled");

    private String status;

    private ConsentStatus(String status) {
        this.status = status;
    }

    public String toString() {
        return this.status;
    }
}

