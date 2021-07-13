/*
 * Decompiled with CFR 0.151.
 */
package com.core.server;

import com.core.server.Server;

public class ServerMain {
    private int port;
    private boolean useUDP;
    private Server server;

    public ServerMain(int port, boolean useUDP) {
        this.port = port;
        this.useUDP = useUDP;
        this.server = new Server(port, useUDP);
    }

    public static void main(String[] args) {
        boolean useUDP;
        int port;
        if (args.length > 2) {
            System.err.println("Usage: java -jar server.jar [port] [useUDP true:false]");
            return;
        }
        if (args.length == 0) {
            port = 666;
            useUDP = true;
        } else {
            port = Integer.parseInt(args[0]);
            useUDP = Boolean.parseBoolean(args[1]);
        }
        new ServerMain(port, useUDP);
    }
}
