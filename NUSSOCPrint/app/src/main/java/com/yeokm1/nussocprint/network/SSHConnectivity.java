package com.yeokm1.nussocprint.network;

import android.content.Context;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.yeokm1.nussocprint.R;

import java.io.InputStream;

/**
 * Created by yeokm1 on 6/10/2014.
 */
public class SSHConnectivity {

    private static final int portNumber = 22;
    private static final int timeout = 60000;
    private static final String TAG = "SSHConnectivity";

    private JSch jschSSHChannel;
    private Session session;
    private String hostname;
    private String username;
    private String password;
    private Context context;

    public SSHConnectivity(String hostname, String username, String password, Context context) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.context = context;
        jschSSHChannel = new JSch();
    }


    public void connect() throws Exception {
        try {
            session = jschSSHChannel.getSession(username, hostname, portNumber);
            session.setPassword(password);
            // To make things easier for user, I skip key check
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(timeout);

        } catch (JSchException e) {
            if (e.getMessage().equals("Auth fail")) {
                String wrongCredentials = context.getString(R.string.misc_wrong_credentials);
                throw new Exception(wrongCredentials);
            } else {
                throw e;
            }
        }
    }


    public void disconnect() {
        if (session != null) {
            session.disconnect();
        }
    }


    public String runCommand(String command) throws Exception {
        Log.i(TAG + " runCommand", command);

        StringBuilder outputBuffer = new StringBuilder();

        Channel channel = session.openChannel("exec");
        ((ChannelExec) channel).setCommand(command);
        channel.connect();
        InputStream commandOutput = channel.getInputStream();
        int readByte = commandOutput.read();

        while (readByte != 0xffffffff) {
            outputBuffer.append((char) readByte);
            readByte = commandOutput.read();
        }

        channel.disconnect();

        String output = outputBuffer.toString();
        return output;
    }


}
