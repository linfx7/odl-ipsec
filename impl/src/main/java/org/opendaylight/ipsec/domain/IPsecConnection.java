/*
 * Copyright © 2015 Copyright(c) linfx7, inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.ipsec.domain;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class IPsecConnection {

    private String keyexchange, ike, ah, esp, authby;

    private String left, right;
    private String leftid, rightid;
    private String leftcert, rightcert;
    private String leftsubnet, rightsubnet;
    private String leftfirewall, rightfirewall;
    private String auto;

    public IPsecConnection(String keyexchange, String ike, String ah, String esp, String authby,
                           String left, String right, String leftid, String rightid,
                           String leftcert, String rightcert, String leftsubnet, String rightsubnet,
                           String leftfirewall, String rightfirewall, String auto) {
        this.keyexchange = keyexchange;
        this.ike = ike;
        this.ah = ah;
        this.esp = esp;
        this.authby = authby;
        this.left = left;
        this.right = right;
        this.leftid = leftid;
        this.rightid = rightid;
        this.leftcert = leftcert;
        this.rightcert = rightcert;
        this.leftsubnet = leftsubnet;
        this.rightsubnet = rightsubnet;
        this.leftfirewall = leftfirewall;
        this.rightfirewall = rightfirewall;
        this.auto = auto;
    }

    public byte[] toByteArray(String name) {
        StringBuffer result = new StringBuffer();
        result.append("conn " + name + "\n");
        Field[] fields = IPsecConnection.class.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = null, fieldValue = null;
            try {
                fieldName = field.getName();
                Method method = IPsecConnection.class.getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
                fieldValue = (String) method.invoke(this);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (fieldValue != null && !fieldValue.equals("")) {
                result.append("\t" + fieldName + "=" + fieldValue + "\n");
            }
        }
        return result.toString().getBytes();
    }

    public static void main(String[] args) {
        System.out.println("start");
        Field[] fields = IPsecConnection.class.getDeclaredFields();
        for (Field f : fields) {
            System.out.println(f.getName());
        }
    }

    public String getKeyexchange() {
        return keyexchange;
    }

    public void setKeyexchange(String keyexchange) {
        this.keyexchange = keyexchange;
    }

    public String getIke() {
        return ike;
    }

    public void setIke(String ike) {
        this.ike = ike;
    }

    public String getAh() {
        return ah;
    }

    public void setAh(String ah) {
        this.ah = ah;
    }

    public String getEsp() {
        return esp;
    }

    public void setEsp(String esp) {
        this.esp = esp;
    }

    public String getAuthby() {
        return authby;
    }

    public void setAuthby(String authby) {
        this.authby = authby;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getLeftid() {
        return leftid;
    }

    public void setLeftid(String leftid) {
        this.leftid = leftid;
    }

    public String getRightid() {
        return rightid;
    }

    public void setRightid(String rightid) {
        this.rightid = rightid;
    }

    public String getLeftcert() {
        return leftcert;
    }

    public void setLeftcert(String leftcert) {
        this.leftcert = leftcert;
    }

    public String getRightcert() {
        return rightcert;
    }

    public void setRightcert(String rightcert) {
        this.rightcert = rightcert;
    }

    public String getLeftsubnet() {
        return leftsubnet;
    }

    public void setLeftsubnet(String leftsubnet) {
        this.leftsubnet = leftsubnet;
    }

    public String getRightsubnet() {
        return rightsubnet;
    }

    public void setRightsubnet(String rightsubnet) {
        this.rightsubnet = rightsubnet;
    }

    public String getLeftfirewall() {
        return leftfirewall;
    }

    public void setLeftfirewall(String leftfirewall) {
        this.leftfirewall = leftfirewall;
    }

    public String getRightfirewall() {
        return rightfirewall;
    }

    public void setRightfirewall(String rightfirewall) {
        this.rightfirewall = rightfirewall;
    }

    public String getAuto() {
        return auto;
    }

    public void setAuto(String auto) {
        this.auto = auto;
    }

    @Override
    public String toString() {
        return "IPsecConnection{" +
                "keyexchange='" + keyexchange + '\'' +
                ", ike='" + ike + '\'' +
                ", ah='" + ah + '\'' +
                ", esp='" + esp + '\'' +
                ", authby='" + authby + '\'' +
                ", left='" + left + '\'' +
                ", right='" + right + '\'' +
                ", leftid='" + leftid + '\'' +
                ", rightid='" + rightid + '\'' +
                ", leftcert='" + leftcert + '\'' +
                ", rightcert='" + rightcert + '\'' +
                ", leftsubnet='" + leftsubnet + '\'' +
                ", rightsubnet='" + rightsubnet + '\'' +
                ", leftfirewall='" + leftfirewall + '\'' +
                ", rightfirewall='" + rightfirewall + '\'' +
                ", auto='" + auto + '\'' +
                '}';
    }
}
