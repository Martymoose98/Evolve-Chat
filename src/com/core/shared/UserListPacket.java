/*
 * Decompiled with CFR 0.151.
 */
package com.core.shared;

import com.core.shared.UnknownPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class UserListPacket
extends UnknownPacket {
    public UserListPacket(InetAddress address, int port, byte[] users) {
        super(address, port, ByteBuffer.allocate(8 + users.length).putInt(-2147483584).put(users).putInt(3599).array());
    }

    public UserListPacket(UnknownPacket packet) {
        super(packet.packet);
    }

    public String[] getUsers() {
        byte[] data = this.getData();
        int start = 4;
        int size = 0;
        String[] users = null;
        int i = start;
        while (i < data.length) {
            int cur = data[i] << 24 | data[i + 1] << 16 | data[i + 2] << 8 | data[i + 3];
            if (cur == 3599) break;
            if (data[i] == 0) {
                ++size;
            }
            ++i;
        }
        if (size > 0) {
            users = new String[size];
            users[0] = new String();
            i = start;
            int k = 0;
            while (i < data.length && k < users.length) {
                if (data[i] == 0) {
                    if (++k < users.length) {
                        users[k] = new String();
                    }
                } else {
                    int n = k;
                    users[n] = String.valueOf(users[n]) + (char)data[i];
                }
                ++i;
            }
        }
        return users;
    }
}
