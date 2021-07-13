/*
 * Decompiled with CFR 0.151.
 */
package com.core.shared;

import com.core.shared.UnknownConnection;
import com.core.shared.UnknownPacket;
import java.nio.ByteBuffer;

public class ConnectPacket
extends UnknownPacket {
    public ConnectPacket(UnknownConnection connect, String name) {
        super(connect, ByteBuffer.allocate(8 + name.length()).putInt(1).put(name.getBytes()).putInt(3599).array());
    }

    public ConnectPacket(UnknownPacket packet) {
        super(packet.packet);
    }

    public String getName() {
        byte[] data = this.getData();
        int offset = 4;
        int i = offset - 1;
        while (i < data.length) {
            int cur = data[i] << 24 | data[i + 1] << 16 | data[i + 2] << 8 | data[i + 3];
            if (cur == 3599) {
                return new String(data, offset, i - offset);
            }
            ++i;
        }
        return null;
    }
}
