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
import java.net.ServerSocket;
import java.net.Socket;

import static org.opendaylight.ipsec.utils.Flags.*;

public class TCPServer {

    private int serverPort;
    private TCPServerCallback callback;
    private boolean running;

    /**
     * Construct a TCP service.
     * @param serverPort port the service listens on
     * @param callback callback interface
     */
    public TCPServer(int serverPort, TCPServerCallback callback) {
        this.serverPort = serverPort;
        this.callback = callback;
    }

    /**
     * Start the service.
     */
    public void start() {
        ServerSocket serverSocket = null;
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(serverPort);
            running = true;
            while (running) {
                socket = serverSocket.accept();
                ChildThread childThread = new ChildThread(socket, callback);
                childThread.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // close the serversocket, the accepted socket will be closed by the chile thread
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
    }

    /**
     * Thread for request.
     */
    private class ChildThread extends Thread {
        Socket socket;
        private TCPServerCallback callback;

        public ChildThread(Socket socket, TCPServerCallback callback) {
            this.socket = socket;
            this.callback = callback;
        }

        public void run() {
            try {
                // get request bytes
                InetAddress remote = socket.getInetAddress();
                InputStream inputStream = socket.getInputStream();
                byte[] request = ByteTools.readStream(inputStream);
                // send response bytes
                OutputStream outputStream = socket.getOutputStream();
                ByteTools.writeStream(outputStream, new byte[] {request[0], request[1], ACK});
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                // call the callback interface
                callback.respond(remote, request);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    // socket must be closed
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
        TCPServer server = new TCPServer(1919, new TCPServerCallback() {
            @Override
            public void respond(InetAddress from, byte[] request) {
                String res = "Hello "+new String(request)+".";
                System.out.println(res);
            }
        });
        server.start();
    }
}
