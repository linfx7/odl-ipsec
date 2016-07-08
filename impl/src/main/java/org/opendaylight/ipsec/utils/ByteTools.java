/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.utils;

import java.io.*;

public class ByteTools {

    /**
     * Get input byte stream.
     * @param inputStream input steam
     * @return bytes got
     * @throws IOException
     */
    public static byte[] readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1400];
        int len = inputStream.read(buffer);
        result.write(buffer, 0, len);
        result.close();
        return result.toByteArray();
    }

    /**
     * Send output byte stream.
     * @param outputStream output steam
     * @param words bytes to send
     * @throws IOException
     */
    public static void writeStream(OutputStream outputStream, byte[] words) throws IOException {
        PrintStream printStream = new PrintStream(outputStream);
        printStream.write(words);
    }

    /**
     * Get subarray of a byte array.
     * @param array source array
     * @param start position of start
     * @param end position of end (not included)
     * @return new subarray
     */
    public static byte[] subByteArray(byte[] array, int start, int end) {
        int len = end - start;
        byte[] result = new byte[len];
        System.arraycopy(array, start, result, 0, len);
        return result;
    }

    public static byte[] addByteArrays(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
