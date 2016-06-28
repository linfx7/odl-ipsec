/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.utils.tcp;

import org.opendaylight.ipsec.utils.ByteTools;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {
    private InetAddress address;
    private int port;

    /**
     * Build tcp client.
     * @param address ip address of service
     * @param port port of service
     */
    public TCPClient(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    /**
     * Send messages.
     * @param request words to send
     * @param callback handleMessage the return value
     */
    public void send(byte[] request, TCPClientCallback callback) {
        new SendThread(address, port, request, callback).run();
    }

    class SendThread extends Thread {
        private InetAddress address;
        private int port;
        private byte[] request;
        private TCPClientCallback callback;

        public SendThread(InetAddress address, int port, byte[] request, TCPClientCallback callback) {
            this.address = address;
            this.port = port;
            this.request = request;
            this.callback = callback;
        }

        public void run() {
            Socket socket = null;
            try {
                socket = new Socket(address, port);
                // send request bytes
                OutputStream outputStream = socket.getOutputStream();
                ByteTools.writeStream(outputStream, request);
                outputStream.flush();
                // get response bytes
                InputStream inputStream = socket.getInputStream();
                byte[] response = ByteTools.readStream(inputStream);
                inputStream.close();
                outputStream.close();
                // call the callback interface
                callback.deal(address, response);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            TCPClient client = new TCPClient(InetAddress.getByName("127.0.0.1"), 1919);
            client.send("foo\nbar".getBytes(), new TCPClientCallback() {
                @Override
                public void deal(InetAddress address, byte[] response) {
                    System.out.println(new String(response));
                }
            });
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
