package com.example.android.mydata_prova.model.security;

/**
 * Created by Valentina on 24/08/2017.
 */

import java.security.PublicKey;

public interface ISecurityManager {
    public byte[] sign(byte[] toSign);

    public boolean verify(PublicKey pubKey, byte[] toUpdate, byte[] toVerify);

    public PublicKey getPublicKey();
}
