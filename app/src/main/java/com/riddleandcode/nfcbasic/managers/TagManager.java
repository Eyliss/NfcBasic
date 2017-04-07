package com.riddleandcode.nfcbasic.managers;

import com.riddleandcode.nfcbasic.utils.Crypto;
import com.riddleandcode.nfcbasic.utils.Util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.spongycastle.cms.CMSException;
import org.spongycastle.operator.OperatorCreationException;

import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;

import java.io.IOException;
import java.security.cert.CertificateException;

/**
 * Created by Eyliss on 1/27/17.
 */

public class TagManager {

//    byte[] hash = Util.convertIntToByteArray(new int[]{
//          0x98, 0x34, 0x87, 0x6d, 0xcf, 0xb0, 0x5c, 0xb1, 0x67, 0xa5, 0xc2, 0x49, 0x53, 0xeb, 0xa5, 0x8c,
//          0x4a, 0xc8, 0x9b, 0x1a, 0xdf, 0x57, 0xf2, 0x8f, 0x2f, 0x9d, 0x09, 0xaf, 0x10, 0x7e, 0xe8, 0xf0
//    });
//    byte[] signature = Util.convertIntToByteArray(new int[]{
//          0x19,0x48,0xd6,0x4c,0x60,0x3e,0x29,0x03,0x5a,0x79,0x26,0xf7,0xb3,0xcd,0x32,
//          0x35,0xae,0xcd,0x1a,0x39,0x81,0x6e,0x74,0x93,0x22,0x87,0x81,0x4e,0xea,0x52,
//          0xfe,0x27,0xe2,0xcc,0x4e,0xc4,0x17,0x1c,0x94,0xc3,0x72,0x87,0x0b,0x8d,0x4f,
//          0xf4,0x33,0x37,0xad,0x12,0xd0,0x1e,0xf7,0xcf,0xd5,0x2c,0x7b,0x43,0x28,0x69,
//          0x57,0x6c,0xad,0x97
//    });

//    byte[] publicKey = Util.convertIntToByteArray(new int[]{
//          0xaa,0x8b,0xc7,0x74,0x64,0x6a,0xdf,0x5c,0x9b,0x75,0x36,0x52,0x37,0x9d,0xe8,
//          0x77,0xc7,0x00,0x87,0xeb,0x71,0x1a,0x35,0x15,0x80,0xcc,0x72,0x61,0x73,0x8b,
//          0xb6,0x5a,0xbe,0x33,0xe1,0xe3,0x70,0x19,0x0e,0xe7,0x4f,0xd7,0x94,0x21,0xc8,
//          0xc4,0xf8,0x0f,0x93,0x75,0xce,0x2e,0x68,0x7c,0xc5,0xd1,0x55,0xc4,0x53,0xcb,
//          0x33,0x87,0x6c,0xf7
//    });

//    byte[] publicKey = Util.convertIntToByteArray(new int[]{0x81,0x04,0x54,0x6A,0xFD,0xB5,0x0B,0xED,0x7C,0x46,0x72,0x22,0x0F,0x24,0x8E,0xE9,
//          0xF1,0xB1,0xAA,0xC9,0x7D,0xC7,0xD4,0x4C,0x3F,0xAB,0x2B,0xFD,0x8B,0x29,0xBD,0x12,
//          0x1D,0x4D,0xCE,0x82,0x53,0x65,0xB1,0x13,0x1F,0x65,0xB9,0x6D,0x61,0x39,0x40,0x3D,
//          0x14,0x4C,0xDF,0x22,0x80,0xD7,0xF4,0x32,0x61,0x23,0x92,0x0B,0xE2,0xFD,0xAA,0x98});

//    byte[] publicKey = Util.hexStringToByteArray("618f75bdb193aec27743d92b710bc856445756d5e7ef54385dbcc47487516e96362264f78a9b466f3b8e3eb9570773365083225c701eff39400aa9607817a6d5");

//    byte[] publicKey = Hex.decodeHex("049a55ad1e210cd113457ccd3465b930c9e7ade5e760ef64b63142dad43a308ed08e2d85632e8ff0322d3c7fda14409eafdc4c5b8ee0882fe885c92e3789c36a7a".toCharArray());
//    byte[] sendHash = Hex.decodeHex("54686973206973206a75737420736f6d6520706f696e746c6573732064756d6d7920737472696e672e205468616e6b7320616e7977617920666f722074616b696e67207468652074696d6520746f206465636f6465206974203b2d29".toCharArray());
//    byte[] signature = Hex.decodeHex("304402205fef461a4714a18a5ca6dce6d5ab8604f09f3899313a28ab430eb9860f8be9d602203c8d36446be85383af3f2e8630f40c4172543322b5e8973e03fff2309755e654".toCharArray());

    String publicKey;
    String hashMessage;
    String signature;

    private NfcAdapter nfcAdapter;
    private NfcA nfca;

    private byte current_sec = 0;
    int sectorSelectTimeout = 0;
    private byte[] answer;
    private byte[] command;

    public TagManager(Context context) throws DecoderException {
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
    }

    public boolean isAvailable(){
        return nfcAdapter != null;
    }

    public NfcAdapter getAdapter(){
        return nfcAdapter;
    }

    /** Functions related with the tag interaction **/

    public void ntagInit(Tag tag) {
        nfca = NfcA.get(tag);
        sectorSelectTimeout = 20;
        nfca.setTimeout(20);
        current_sec = 0;
    }

    public void ntagClose() throws IOException {
        nfca.close();
        current_sec = 0;
    }

    public void ntagConnect() throws IOException {
        nfca.connect();
        current_sec = 0;
    }

    public boolean ntagIsConnected() {
        return nfca.isConnected();
    }

    public byte[] ntagGetLastCommand() {
        return command;
    }

    public byte[] ntagGetLastAnswer() {
        return answer;
    }

    public void ntagSectorSelect(byte sector) throws IOException, FormatException {
        if(current_sec != sector) {
            command = new byte[2];
            command[0] = -62;
            command[1] = -1;
            nfca.transceive(this.command);
            command = new byte[4];
            command[0] = sector;
            command[1] = 0;
            command[2] = 0;
            command[3] = 0;
            nfca.setTimeout(sectorSelectTimeout);

            try {
                nfca.transceive(this.command);
            } catch (IOException var3) {
                var3.printStackTrace();
            }

            nfca.setTimeout(20);
            current_sec = sector;
        }
    }

    public void ntagFastWrite(byte[] data, byte startAddr, byte endAddr) throws IOException, FormatException {
        answer = new byte[0];
        command = new byte[3 + data.length];
        command[0] = -90;
        command[1] = startAddr;
        command[2] = endAddr;
        System.arraycopy(data, 0, command, 3, data.length);
        nfca.setTimeout(500);
        nfca.transceive(command);
        nfca.setTimeout(20);
    }

    public void ntagWrite(byte[] data, byte blockNr) throws IOException, FormatException {
        answer = new byte[0];
        command = new byte[6];
        command[0] = -94;
        command[1] = blockNr;
        command[2] = data[0];
        command[3] = data[1];
        command[4] = data[2];
        command[5] = data[3];
        nfca.transceive(command);
    }

    public byte[] ntagFastRead(byte startAddr, byte endAddr) throws IOException, FormatException {
        command = new byte[3];
        command[0] = 58;
        command[1] = startAddr;
        command[2] = endAddr;
        nfca.setTimeout(500);
        answer = nfca.transceive(command);
        nfca.setTimeout(20);
        return answer;
    }

    public byte[] ntagRead(byte blockNr) throws IOException, FormatException {
        command = new byte[2];
        command[0] = 48;
        command[1] = blockNr;
        answer = nfca.transceive(command);
        return answer;
    }

    public byte[] ntagGetVersion() throws IOException {
        command = new byte[1];
        command[0] = 96;
        answer = nfca.transceive(command);
        return answer;
    }

    public int ntagReadBit(byte blockNr, int byteNr, int bitNr) throws IOException, FormatException {
        command = new byte[2];
        command[0] = 48;
        command[1] = blockNr;
        answer = nfca.transceive(command);
        nfca.setTimeout(20);
        return ((answer[byteNr] >> bitNr) & 0x01);
    }

    public void setNfcATimeout(int timeOut){
        nfca.setTimeout(timeOut);
    }

    /**
     * Controls whether a signature is the signature of the message using the public key read from the chip.
     *
     * @param message the message
     * @return true if the signature is that of the message with the expected private key
     */
//    public boolean checkSign(byte[] message) throws CertificateException, CMSException, OperatorCreationException {
//        return Crypto.verify(message, signature, publicKey);
//    }

    public String getHashMessage(){
        return hashMessage;
    }

    public String getPublicKey(){
        return publicKey;
    }

    public String getSignature(){
        return signature;
    }


    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public void setHashMessage(String hashMessage) {
        this.hashMessage = hashMessage;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
