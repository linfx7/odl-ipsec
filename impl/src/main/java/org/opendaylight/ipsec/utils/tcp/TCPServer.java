/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.utils.tcp;

import org.opendaylight.ipsec.utils.ByteTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static org.opendaylight.ipsec.utils.Flags.ACK;

public class TCPServer extends Thread {

    private int serverPort;
    private TCPServerCallback callback;
    private boolean running;
    private ServerSocket serverSocket = null;
    private int threadPoolSize;

    /**
     * Construct a TCP service.
     * @param serverPort port the service listens on
     * @param threadPoolSize size of thread pool
     * @param callback callback interface
     */
    public TCPServer(int serverPort, int threadPoolSize, TCPServerCallback callback) {
        this.serverPort = serverPort;
        this.threadPoolSize = threadPoolSize;
        this.callback = callback;
    }

    /**
     * Construct a TCP service. Thread pool size is set to 100 by default
     * @param serverPort port the service listens on
     * @param callback callback interface
     */
    public TCPServer(int serverPort, TCPServerCallback callback) {
        this.serverPort = serverPort;
        this.threadPoolSize = 200;
        this.callback = callback;
    }

    /**
     * Start the service.
     */
    public void run() {
        try {
            serverSocket = new ServerSocket(serverPort);
            running = true;
            for (int i = 0; i < threadPoolSize; ++i) {
                new ListenThread().start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void safeStop() {
        running = false;
        try {
            // close the serversocket
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Thread listening for request.
     */
    private class ListenThread extends Thread {

        public ListenThread() {
        }

        public void run() {
            Socket socket;
            while (running) {
                try {
                    socket = serverSocket.accept();
                    System.out.println("Client connected.");
                    handleSocket(socket);
                    System.out.println("Client disconnected.");
                } catch (Exception e) {
                    // server closed
                    break;
                }
            }
        }

        private void handleSocket(Socket socket) {
            try {
                // get request bytes
                InetAddress remote = socket.getInetAddress();
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                while (!socket.isClosed()) {
                    try {
                        byte[] request = ByteTools.readStream(inputStream);
                        // send response bytes
                        ByteTools.writeStream(outputStream, new byte[]{request[0], request[1], ACK, '\n'});
                        outputStream.flush();
                        // call the callback interface
                        callback.respond(remote, request);
                    } catch (Exception e) {
//                        e.printStackTrace();
                        break;
                    }
                }
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
