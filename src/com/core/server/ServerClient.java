/*
 * Decompiled with CFR 0.151.
 */
package com.core.server;

import com.core.shared.DisconnectPacket;
import com.core.shared.HeartbeatPacket;
import com.core.shared.UnknownConnection;
import com.core.shared.UnknownPacket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerClient {
    public static final int MAX_HEARTBEAT_ATTEMPTS = 5;
    public String name;
    public InetAddress address;
    public int port;
    public long id;
    private AtomicInteger attempt;

    public ServerClient(String name, InetAddress address, int port, long id) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.id = id;
        this.attempt = new AtomicInteger(0);
    }

    public boolean didTimeout() {
        return this.attempt.getAndIncrement() >= 5;
    }

    public void ping(UnknownConnection connect) {
        UnknownPacket packet = this.didTimeout() ? new DisconnectPacket(connect, this.id, 2) : new HeartbeatPacket(this.address, this.port, this.id);
        connect.send(packet);
    }

    public void markActive() {
        this.attempt.set(0);
    }

    public String toString() {
        return String.valueOf(this.name) + " IPv4: " + this.address.getHostAddress() + ":" + this.port + " Id: " + this.id;
    }
}
