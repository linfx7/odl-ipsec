/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.utils.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
     * @param message words to send
     * @param callback handle the return value
     */
    public void send(String message, TCPClientCallback callback) {
        new SendThread(address, port, message, callback).run();
    }

    class SendThread extends Thread {
        private InetAddress address;
        private int port;
        private String message;
        private TCPClientCallback callback;

        public SendThread(InetAddress address, int port, String message, TCPClientCallback callback) {
            this.address = address;
            this.port = port;
            this.message = message;
            this.callback = callback;
        }

        public void run() {
            Socket socket = null;
            try {
                socket = new Socket(address, port);
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.print(message);
                printWriter.flush();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                callback.deal(bufferedReader.readLine());
                bufferedReader.close();
                printWriter.close();
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
            client.send("foobar", new TCPClientCallback() {
                @Override
                public void deal(String response) {
                    System.out.println(response);
                }
            });
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
