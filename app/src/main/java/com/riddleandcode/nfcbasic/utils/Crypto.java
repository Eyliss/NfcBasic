package com.riddleandcode.nfcbasic.utils;

import org.spongycastle.cms.CMSException;
import org.spongycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.ECPointUtil;
import org.spongycastle.jce.interfaces.ECPublicKey;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.jce.spec.ECNamedCurveSpec;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.jce.spec.ECPrivateKeySpec;
import org.spongycastle.jce.spec.ECPublicKeySpec;
import org.spongycastle.math.ec.ECCurve;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Properties;
import org.spongycastle.util.encoders.Base64;
import org.spongycastle.util.encoders.Hex;

import android.util.Log;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Eyliss on 1/31/17.
 */

public class Crypto {

    public static final String CURVE_NAME = "secp256r1";

    /**
     * verify the message depending of the signature and the key.
     *
     * @param message the message
     * @param sign the signature
     * @param key the public key.
     * @return true, if successful
     */
    public static boolean verify(byte[] message, byte[] sign, byte[] key) throws CertificateException, CMSException, OperatorCreationException {
        Security.addProvider(new BouncyCastleProvider());

        try {

            PublicKey testKey = getPublicKeyFromBytes(key);
            Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
            signature.initVerify(testKey);
            signature.update(message);

//            boolean success = signature.verify(message);
//            Log.d("Crypto","Verification success: " + success);

            return false;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static KeyPair generate() throws InvalidAlgorithmParameterException, NoSuchProviderException, NoSuchAlgorithmException {
        ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(CURVE_NAME);
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", "SC");
        generator.initialize(ecSpec, new SecureRandom());
        KeyPair keyPair = generator.generateKeyPair();
        return keyPair;
    }

    public static PublicKey getPublicKeyFromEncodedBytes(byte[] encodedBytes) throws GeneralSecurityException {

        X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedBytes);
        KeyFactory fact = KeyFactory.getInstance("EC");
        return fact.generatePublic(spec);
    }

//    private static PublicKey getPublicKeyFromBytes(byte[] pubKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
//        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("prime256v1");
//        KeyFactory kf = KeyFactory.getInstance("ECDSA", new BouncyCastleProvider());
//        ECNamedCurveSpec params = new ECNamedCurveSpec("prime256v1", spec.getCurve(), spec.getG(), spec.getN());
//        java.security.spec.ECPoint point =  ECPointUtil.decodePoint(params.getCurve(), pubKeyBytes);
//        java.security.spec.ECPublicKeySpec pubKeySpec = new java.security.spec.ECPublicKeySpec(point, params);
//        ECPublicKey pk = (ECPublicKey) kf.generatePublic(pubKeySpec);
//
//        return pk;
//    }

//    private static PublicKey getPublicKeyFromBytes(byte[] pubKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException {
//
//        byte[] rowbyte = new byte[64];
//
//        for (int i = 0; i <64 ; i++) {
//            if (i<32)
//                rowbyte[i] = (byte) pubKey[31-i];
//            else
//                rowbyte[i] = (byte) pubKey[95-i];
//        }
//        ECGenParameterSpec ecParamSpec = new ECGenParameterSpec("secp256r1");//select curve
//        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
//        kpg.initialize(ecParamSpec);
//        KeyPair kpA = kpg.generateKeyPair();
//        ECPublicKey apk= (ECPublicKey) kpA.getPublic();//get a publickey in secp256r1 format
//        byte[] android_pk_encode = apk.getEncoded();
//        System.arraycopy(rowbyte,0,android_pk_encode,android_pk_encode.length-rowbyte.length,rowbyte.length);
//        //keep the head remained while replace the ECPoint data by the row byte array from 10040
//        X509EncodedKeySpec ecpks = new X509EncodedKeySpec(android_pk_encode);
//        KeyFactory keyFactory = KeyFactory.getInstance("EC");
//        ECPublicKey ECDHpk  = null;
//        try {
//            ECDHpk = (ECPublicKey) keyFactory.generatePublic(ecpks);
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();//failed in generating publickey!!!
//        }
//        return ECDHpk;//show the information by textview in an activity
//
//    }

    private static PublicKey getPublicKeyFromBytes(byte[] pubKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("prime256v1");
        KeyFactory kf = KeyFactory.getInstance("ECDSA", new BouncyCastleProvider());
        ECNamedCurveSpec params = new ECNamedCurveSpec("prime256v1", spec.getCurve(), spec.getG(), spec.getN());
        java.security.spec.ECPoint point =  ECPointUtil.decodePoint(params.getCurve(), pubKey);
        java.security.spec.ECPublicKeySpec pubKeySpec = new java.security.spec.ECPublicKeySpec(point, params);
        ECPublicKey pk = (ECPublicKey) kf.generatePublic(pubKeySpec);
        Log.d("Crypto","Public key "+pk);

        return pk;
    }

    /**
     * This method converts the uncompressed raw EC public key into java.security.interfaces.ECPublicKey
     * @return java.security.interfaces.ECPublicKey
     */
//    public static ECPublicKey getPublicKeyFromBytes(byte[] rawPublicKey) {
//        ECPublicKey ecPublicKey = null;
//        KeyFactory kf = null;
//
//        ECNamedCurveParameterSpec ecNamedCurveParameterSpec = ECNamedCurveTable.getParameterSpec("P-256");
//        ECCurve curve = ecNamedCurveParameterSpec.getCurve();
//        EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, ecNamedCurveParameterSpec.getSeed());
//        java.security.spec.ECPoint ecPoint = ECPointUtil.decodePoint(ellipticCurve, rawPublicKey);
//        java.security.spec.ECParameterSpec ecParameterSpec = EC5Util.convertSpec(ellipticCurve, ecNamedCurveParameterSpec);
//        java.security.spec.ECPublicKeySpec publicKeySpec = new java.security.spec.ECPublicKeySpec(ecPoint, ecParameterSpec);
//
//        try {
//            kf = KeyFactory.getInstance("EC");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            ecPublicKey = (ECPublicKey) kf.generatePublic(publicKeySpec);
//        } catch (Exception e) {
//            System.out.println("Caught Exception public key: " + e.toString());
//        }
//
//        return ecPublicKey;
//    }

}
