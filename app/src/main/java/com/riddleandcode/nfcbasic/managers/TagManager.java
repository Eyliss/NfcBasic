package com.riddleandcode.nfcbasic.managers;

import com.riddleandcode.nfcbasic.R;
import com.riddleandcode.nfcbasic.utils.Crypto;
import com.riddleandcode.nfcbasic.utils.RequestCodes;
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
import java.util.Arrays;
import java.nio.ByteBuffer;

/**
 * Created by Eyliss on 1/27/17.
 */

public class TagManager {


//    byte[] publicKey = Hex.decodeHex("049a55ad1e210cd113457ccd3465b930c9e7ade5e760ef64b63142dad43a308ed08e2d85632e8ff0322d3c7fda14409eafdc4c5b8ee0882fe885c92e3789c36a7a".toCharArray());
    //    byte[] message = Hex.decodeHex("54686973206973206a75737420736f6d6520706f696e746c6573732064756d6d7920737472696e672e205468616e6b7320616e7977617920666f722074616b696e67207468652074696d6520746f206465636f6465206974203b2d29".toCharArray());
//    byte[] sign = Hex.decodeHex("304402205fef461a4714a18a5ca6dce6d5ab8604f09f3899313a28ab430eb9860f8be9d602203c8d36446be85383af3f2e8630f40c4172543322b5e8973e03fff2309755e654".toCharArray());

    byte[] publicKey;
    byte[] message;
    byte[] signature;

    private NfcAdapter nfcAdapter;
    private NfcA nfca;

    private byte current_sec = 0;
    private int sectorSelectTimeout = 0;
    private byte[] answer;
    private byte[] command;
    private int payloadSize = 0;
    private int opCode = 0;
    private int errorCode = 0;

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

    public byte[] ntagGetVersion() throws IOException {
        command = new byte[1];
        command[0] = 96;
        answer = nfca.transceive(command);
        return answer;
    }

    public byte[] ntagGetLastCommand() {
        return command;
    }

    public byte[] ntagGetLastAnswer() {
        return answer;
    }

    // Public tag commands
    public boolean signMessage(byte[] originalMessage){
        message = originalMessage; // For external access
        return sendRequest(RequestCodes.SIGN, originalMessage);
    }

    // -------------- Private interface -----------------

    // --- Some constants for communication
    private final byte SRAM_MEMA = (byte)0xf0;
    private final byte SRAM_SIZE = 64;
    private final byte HEADER_SIZE = 2;
    private final byte HEADER_ADDRESS = SRAM_SIZE - HEADER_SIZE;

    private boolean sendRequest(RequestCodes opCode, byte[] payload) {
        // Validate data fits in memory
        assert (payload.length + HEADER_SIZE <= SRAM_SIZE);
        byte[] requestBuffer = new byte[SRAM_SIZE];
        // Copy payload to buffer
        byte payloadAddress = (byte)(HEADER_ADDRESS - (byte)payload.length);
        for(int i = 0; i < payload.length; i++) {
            requestBuffer[payloadAddress+i] = payload[i];
        }
        // Copy header data at the end
        requestBuffer[HEADER_ADDRESS] = opCode.getCode();
        requestBuffer[HEADER_ADDRESS+1] = (byte)payload.length;

        try {
            // Wait until NFC interface owns memory bus
            waitForTagOwnership();
            // Send request command
            ntagSectorSelect((byte)0);
            ntagFastWrite(requestBuffer);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void waitForTagOwnership() throws IOException, FormatException {
        while(true) {
            // Assuming pass through communication
            if(passThroughEnabled() && transferDir() == NFC_TO_I2C)
                return;
        }
    }

    private static final int NC_REG = 0;
    private static final int NS_REG = 6;

    // NC_REG
    private static final byte PTHRU_ON_OFF = 6;
    private static final byte TRANSFER_DIR = 0;
    private static final boolean NFC_TO_I2C = true;
    private static final boolean I2C_TO_NFC = false;

    // NS_REG
    private static final byte I2C_LOCKED = 6;
    private static final byte RF_LOCKED = 5;
    private static final byte SRAM_I2C_READY = 4;
    private static final byte SRAM_NFC_READY = 3;

    private boolean transferDir() throws IOException, FormatException {
        return ntagGetNsReg(NC_REG, TRANSFER_DIR);
    }

    private boolean passThroughEnabled() throws IOException, FormatException {
        return ntagGetNsReg(NC_REG, PTHRU_ON_OFF);
    }

    //special read command
    private boolean ntagGetNsReg(int regAddr, int pos) throws IOException, FormatException {
        ntagSectorSelect((byte) 0x03);
        answer = ntagRead((byte) 0xF8);

        return ((answer[regAddr] >> pos) & 1) != 0;
    }

    private void ntagSectorSelect(byte sector) throws IOException, FormatException {
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

    private void ntagFastWrite(byte[] data) throws IOException, FormatException {
        assert(data.length == SRAM_SIZE);
        answer = new byte[0];
        command = new byte[3 + data.length];
        command[0] = -90;
        command[1] = (byte)0xf0;
        command[2] = (byte)0xff;
        System.arraycopy(data, 0, command, 3, data.length);
        nfca.setTimeout(500);
        nfca.transceive(command);
        nfca.setTimeout(20);
    }

    private byte[] ntagWrite32(int data, byte blockNr) throws IOException, FormatException {
        answer = new byte[0];
        command = new byte[6];
        command[0] = -94;
        command[1] = blockNr;
        command[2] = (byte)(data>>24);
        command[3] = (byte)(data>>16);
        command[4] = (byte)(data>>8);
        command[5] = (byte)data;
        return nfca.transceive(command);
    }

    private byte[] ntagWrite(byte[] data, byte blockNr) throws IOException, FormatException {
        answer = new byte[0];
        command = new byte[6];
        command[0] = -94;
        command[1] = blockNr;
        command[2] = data[0];
        command[3] = data[1];
        command[4] = data[2];
        command[5] = data[3];
        return nfca.transceive(command);
    }

    private byte[] ntagFastRead(byte startAddr, byte endAddr) throws IOException, FormatException {
        command = new byte[3];
        command[0] = 58;
        command[1] = startAddr;
        command[2] = endAddr;
        nfca.setTimeout(500);
        answer = nfca.transceive(command);
        nfca.setTimeout(20);
        return answer;
    }

    private byte[] ntagRead(byte blockNr) throws IOException, FormatException {
        command = new byte[2];
        command[0] = 48;
        command[1] = blockNr;
        answer = nfca.transceive(command);
        return answer;
    }

    public void setTimeout(int timeout){
        nfca.setTimeout(timeout);
    }

    //Read the header block to check if the application is able to read from NFC
    public boolean ntagReadable() throws IOException, FormatException {
        ntagSectorSelect((byte) 0x01);
        answer = ntagRead((byte) 0x00);

        if(answer.length > 0 && !hasError()){
            return Util.bytesToHex(answer).charAt(answer.length - 1) == 0;
        }else{
            return false;
        }
    }

//    /*
//     * Sent the message to the tag for hashing
//     */
//    public void hashMessage(byte[] originalMessage){
//        try {
//            ntagSectorSelect((byte) 0x01);
//            byte[] hashData = {RequestCodes.HASH.getCode(), (byte) originalMessage.length};
//
//            hashData = Util.concatArray(hashData,originalMessage);
//            ntagWrite(hashData, (byte) 0x01);
//        } catch (IOException | FormatException e) {
//            e.printStackTrace();
//        }
//    }

    /*
     * Get public key from tag
     */
    public void getKey(){
        try {
            byte[] getKeyData = {RequestCodes.GET_KEY.getCode(), (byte) 0x00 ,(byte) 0x00 ,(byte) 0x00};

            ntagSectorSelect((byte) 0x01);
            ntagWrite(getKeyData, (byte) 0x01);

            //Write the header after the request
            byte[] header = {RequestCodes.GET_KEY.getCode(), (byte) message.length, (byte)0x00, (byte) 0x01};
            ntagWrite(header, (byte) 0x00);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    public void parseSignResponse(){
        try {
            ntagSectorSelect((byte) 0x01);
            byte[] sign = new byte[0];
            for(int i = 1; i < 5 ; i++){
                sign = Util.concatArray(sign,ntagRead((byte) i));
            }
            signature = sign;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    public void parseGetKeyResponse(){
        try {
            ntagSectorSelect((byte) 0x01);

            byte[] key = new byte[0];
            for(int i = 1; i < 3 ; i++){
                key = Util.concatArray(key,ntagRead((byte) i));
            }
            publicKey = key;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Controls whether a signature is the signature of the message using the public key read from the chip.
     *
     * @param message the message
     * @return true if the signature is that of the message with the expected private key
     */
    public boolean checkSign(byte[] message) throws CertificateException, CMSException, OperatorCreationException {
        return Crypto.verify(message, signature, publicKey);
    }

    public byte[] getMessage(){
        return message;
    }

    public byte[] getPublicKey(){
        return publicKey;
    }

    public byte[] getSignature(){
        return signature;
    }

    public int getErrorMessage(){
        switch (errorCode){
            case 1:
                return R.string.error_invalid_payload_size;
            case 2:
                return R.string.error_not_supported_operation;
            case 3:
                return R.string.error_operation_fail;
            default:
                return R.string.error_ocurred;
        }
    }

    private boolean hasError(){
        errorCode = Util.fromByteArray(Arrays.copyOfRange(answer,0,4));
        return errorCode != 0;
    }
}
