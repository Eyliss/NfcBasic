package com.riddleandcode.nfcbasic.utils;

import java.security.Security;

/**
 * Created by Eyliss on 1/31/17.
 */

public class Crypto {

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public Crypto(){

    }
}
