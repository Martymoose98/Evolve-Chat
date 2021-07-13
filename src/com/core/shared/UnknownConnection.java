/*
 * Decompiled with CFR 0.151.
 */
package com.core.shared;

import com.core.shared.UnknownPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

public abstract class UnknownConnection {
    protected InetAddress ip;
    protected int port;

    public UnknownConnection(String address, int port) {
        this(new InetSocketAddress(address, port));
    }

    protected UnknownConnection(InetSocketAddress address) {
        this.port = address.getPort();
        this.ip = address.getAddress();
    }

    public abstract boolean isValid();

    public abstract boolean send(UnknownPacket var1);

    public abstract UnknownPacket recv();

    public abstract void close();

    public String toString() {
        return String.valueOf(this.ip.getHostAddress()) + ":" + this.port;
    }

    public String getAddress() {
        return this.ip.getHostAddress();
    }

    public int getPort() {
        return this.port;
    }
}
