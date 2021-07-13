/*
 * Decompiled with CFR 0.151.
 */
package com.core.shared;

import com.core.shared.UnknownPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class ConnectResponsePacket
extends UnknownPacket {
    public ConnectResponsePacket(InetAddress address, int port, long id) {
        super(address, port, ByteBuffer.allocate(16).putInt(0x10000001).putLong(id).putInt(3599).array());
    }

    public ConnectResponsePacket(UnknownPacket packet) {
        super(packet.packet);
    }

    public long getId() {
        byte[] data = this.getData();
        return ((long)data[4] & 0xFFL) << 56 | ((long)data[5] & 0xFFL) << 48 | ((long)data[6] & 0xFFL) << 40 | ((long)data[7] & 0xFFL) << 32 | ((long)data[8] & 0xFFL) << 24 | ((long)data[9] & 0xFFL) << 16 | ((long)data[10] & 0xFFL) << 8 | (long)data[11] & 0xFFL;
    }
}
