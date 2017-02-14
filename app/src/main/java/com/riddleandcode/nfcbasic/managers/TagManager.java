package com.riddleandcode.nfcbasic.managers;

import com.riddleandcode.nfcbasic.utils.RequestCodes;
import com.riddleandcode.nfcbasic.utils.Util;

import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Eyliss on 1/27/17.
 */

public class TagManager {

    private NfcAdapter nfcAdapter;
    private NfcA nfca;

    private byte current_sec = 0;
    private int sector_select_timeout = 0;
    private final int timeout = 20;
    private byte[] answer;
    private byte[] command;

    public TagManager(Context context){
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
    }

    public boolean isAvailable(){
        return nfcAdapter != null;
    }

    public NfcAdapter getAdapter(){
        return nfcAdapter;
    }

    /** Functions related with the tag interaction **/

    //special read command
    public int ntagGetNsReg(int regAddr, int pos) throws IOException, FormatException {
        ntagSectorSelect((byte) 0x03);
        answer = ntagRead((byte) 0xF8);

        return (int) (answer[regAddr] >> pos) & 1;
    }

    public void ntagInit(Tag tag) {
        nfca = NfcA.get(tag);
        sector_select_timeout = 20;
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
            nfca.setTimeout(sector_select_timeout);

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

    public void setTimeout(int timeout){
        nfca.setTimeout(timeout);
    }

    /*
     * Get
     * 4 bytes -> opcode, like the previous version
     * 4 bytes -> payload size, same old
     * 4 bytes -> reserved for future use, undefined content, can just leave it as 0s
     * 4 bytes -> comm dir. 0 means NFC can read/write, 1 means i2c can read/write
     */
    public void getHeader(){
        try {

            ntagSectorSelect((byte) 0x01);
            byte commDir;
            do{
                byte[] response = ntagRead((byte) 0xF0);
                byte[] opcode = Arrays.copyOfRange(response,0,4);
                byte[] payloadSize = Arrays.copyOfRange(response,4,8);
                commDir = response[15];
            }while (commDir !=  1);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
    }

    public String hashMessage(byte[] message){
        try {

            ntagSectorSelect((byte) 0x01);
            byte[] hashData = {RequestCodes.HASH.getCode(), (byte) message.length};
            hashData = Util.concatArray(hashData,message);
            ntagWrite(hashData, (byte) 0xF0);
            ntagRead((byte) 0xF0);


//            4 bytes -> opcode, like the previous version
//            4 bytes -> payload size, same old
//            4 bytes -> reserved for future use, undefined content, can just leave it as 0s
//            4 bytes -> comm dir. 0 means NFC can read/write, 1 means i2c can read/write
            return Util.bytesToHex(answer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void signMessage(byte[] message){
        try {
            byte[] signData = {RequestCodes.SIGN.getCode(), (byte) 0x21 ,(byte) 0x31 ,(byte) 0x41};
            ntagWrite(signData, (byte) 0xF0);

            ntagSectorSelect((byte) 0x01);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

    }

    public String getKey(){
        try {
            byte[] getKeyData = {RequestCodes.GET_KEY.getCode(), (byte) 0x21 ,(byte) 0x31 ,(byte) 0x41};

            ntagSectorSelect((byte) 0x01);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        return "";
    }
}
