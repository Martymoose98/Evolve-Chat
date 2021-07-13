/*
 * Decompiled with CFR 0.151.
 */
package com.core.client;

import com.core.shared.TCPConnection;
import com.core.shared.UDPConnection;
import com.core.shared.UnknownConnection;
import com.core.shared.UnknownPacket;
import java.util.concurrent.atomic.AtomicLong;

public class Client {
    private UnknownConnection connect;
    private Thread send;
    private String name;
    private AtomicLong id;
    private final boolean useUDP = true;

    public Client(String name) {
        this.name = name;
        this.id = new AtomicLong(-1L);
    }

    public boolean openConnection(String address, int port, boolean useUDP) {
        this.connect = useUDP ? new UDPConnection(address, port) : new TCPConnection(address, port);
        return this.connect.isValid();
    }

    public void send(final UnknownPacket packet) {
        this.send = new Thread("Send"){

            @Override
            public void run() {
                Client.this.connect.send(packet);
            }
        };
        this.send.start();
    }

    public UnknownPacket receive() {
        return this.connect.recv();
    }

    public void close() {
        new Thread("Close"){

            @Override
            public void run() {
                Client.this.connect.close();
            }
        }.start();
    }

    public <T> long sizeof(T type) {
        if (type instanceof Integer) {
            return 4L;
        }
        if (type instanceof Long) {
            return 8L;
        }
        if (type instanceof Short) {
            return 2L;
        }
        if (type instanceof Boolean) {
            return 1L;
        }
        if (type instanceof int[]) {
            return ((int[])type).length * 4;
        }
        return 0L;
    }

    public final String getName() {
        return this.name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final UnknownConnection getConnection() {
        return this.connect;
    }

    public final boolean isUsingUDP() {
        return true;
    }

    public long getId() {
        return this.id.get();
    }

    public void setId(long id) {
        this.id.getAndSet(id);
    }
}
