package com.riddleandcode.nfcbasic.utils;

/**
 * Created by Eyliss on 2/9/17.
 */

public enum RequestCodes {

    HASH((byte)0x01),
    SIGN((byte)0x02),
    GET_KEY((byte)0x04);

    private byte code;

    RequestCodes(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}