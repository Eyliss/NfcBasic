package com.riddleandcode.nfcbasic.utils;

import org.spongycastle.cms.CMSException;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.ECPointUtil;
import org.spongycastle.jce.interfaces.ECPublicKey;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECNamedCurveParameterSpec;
import org.spongycastle.jce.spec.ECNamedCurveSpec;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.jce.spec.ECPrivateKeySpec;
import org.spongycastle.jce.spec.ECPublicKeySpec;
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

            boolean success = signature.verify(sign);
            Log.d("Crypto","Signature with real signature: " + success);

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

    private static PublicKey getPublicKeyFromBytes(byte[] pubKeyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec("prime256v1");
        KeyFactory kf = KeyFactory.getInstance("ECDSA", new BouncyCastleProvider());
        ECNamedCurveSpec params = new ECNamedCurveSpec("prime256v1", spec.getCurve(), spec.getG(), spec.getN());
        java.security.spec.ECPoint point =  ECPointUtil.decodePoint(params.getCurve(), pubKeyBytes);
        java.security.spec.ECPublicKeySpec pubKeySpec = new java.security.spec.ECPublicKeySpec(point, params);
        ECPublicKey pk = (ECPublicKey) kf.generatePublic(pubKeySpec);
        return pk;
    }
}
