/*
 * Decompiled with CFR 0.151.
 */
package com.core.shared;

import com.core.shared.UnknownConnection;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class UnknownPacket {
    public static final int MAX_PACKET_BYTES = 1024;
    public static final int TERMINATION_SEQ = 3599;
    public static final int INVALID = 0;
    public static final int CONNECT = 1;
    public static final int DISCONNECT = 2;
    public static final int KICK = 4;
    public static final int BAN = 8;
    public static final int QUERY = 16;
    public static final int MESSAGE = 32;
    public static final int USERS = 64;
    public static final int RESPONSE = 0x10000000;
    public static final int PRIVATE = 0x20000000;
    public static final int BROADCAST = Integer.MIN_VALUE;
    public static final int CONNECT_RESPONSE = 0x10000001;
    public static final int DISCONNECT_RESPONSE = 0x10000002;
    public static final int QUERY_RESPONSE = 0x10000010;
    public static final int QUERY_ALL = -2147483632;
    public static final int QUERY_PRIVATE = 0x20000010;
    public static final int MESSAGE_ALL = -2147483616;
    public static final int MESSAGE_PRIVATE = 0x20000020;
    public static final int USERS_ALL = -2147483584;
    public static final int MESSAGE_MASK = -1610612704;
    public static final int MANAGE_MASK = -1610612708;
    protected int type;
    protected DatagramPacket packet;

    public UnknownPacket(UnknownConnection connect) {
        this(connect, new byte[1024]);
    }

    protected UnknownPacket(UnknownConnection connect, byte[] data) {
        this(connect.ip, connect.port, data);
    }

    protected UnknownPacket(InetAddress address, int port, byte[] data) {
        this(new DatagramPacket(data, data.length, address, port));
    }

    protected UnknownPacket(DatagramPacket packet) {
        this.packet = packet;
        this.parse();
    }

    protected void parse() {
        byte[] data = this.getData();
        this.type = data.length + 1 < 4 ? 0 : data[0] << 24 | data[1] << 16 | data[2] << 8 | data[3];
    }

    public InetAddress getAddress() {
        return this.packet.getAddress();
    }

    public int getPort() {
        return this.packet.getPort();
    }

    public byte[] getData() {
        return this.packet.getData();
    }

    public int getType() {
        return this.type;
    }

    public String getTypeString() {
        String sType = new String();
        boolean or = false;
        if (this.type == 0) {
            sType = "INVALID";
        } else {
            if ((this.type & 1) != 0) {
                sType = String.valueOf(sType) + "CONNECT";
                or = true;
            }
            if ((this.type & 2) != 0) {
                sType = String.valueOf(sType) + (or ? " | DISCONNECT" : "DISCONNECT");
                or = true;
            }
            if ((this.type & 4) != 0) {
                sType = String.valueOf(sType) + (or ? " | KICK" : "KICK");
                or = true;
            }
            if ((this.type & 8) != 0) {
                sType = String.valueOf(sType) + (or ? " | BAN" : "BAN");
                or = true;
            }
            if ((this.type & 0x10) != 0) {
                sType = String.valueOf(sType) + (or ? " | QUERY" : "QUERY");
                or = true;
            }
            if ((this.type & 0x20) != 0) {
                sType = String.valueOf(sType) + (or ? " | MESSAGE" : "MESSAGE");
                or = true;
            }
            if ((this.type & 0x10000000) != 0) {
                sType = String.valueOf(sType) + (or ? " | RESPONSE" : "RESPONSE");
                or = true;
            }
            if ((this.type & 0x20000000) != 0) {
                sType = String.valueOf(sType) + (or ? " | PRIVATE" : "PRIVATE");
                or = true;
            }
            if ((this.type & Integer.MIN_VALUE) != 0) {
                sType = String.valueOf(sType) + (or ? " | BROADCAST" : "BROADCAST");
                or = true;
            }
        }
        return sType;
    }

    public String toString() {
        String result = new String();
        byte[] data = this.getData();
        result = String.valueOf(result) + String.format("Type: %s (0x%08X)\n", this.getTypeString(), this.type);
        int i = 0;
        while (i < data.length) {
            if (i % 64 == 0) {
                result = String.valueOf(result) + "\n";
            }
            result = String.valueOf(result) + String.format("%02X ", data[i]);
            ++i;
        }
        return result;
    }
}
