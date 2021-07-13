/*
 * Decompiled with CFR 0.151.
 */
package com.core.server;

import com.core.server.ServerClient;
import com.core.server.UniqueIdentifier;
import com.core.shared.BroadcastPacket;
import com.core.shared.ConnectPacket;
import com.core.shared.ConnectResponsePacket;
import com.core.shared.DisconnectPacket;
import com.core.shared.HeartbeatPacket;
import com.core.shared.TCPConnection;
import com.core.shared.UDPConnection;
import com.core.shared.UnknownConnection;
import com.core.shared.UnknownPacket;
import com.core.shared.UserListPacket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server
implements Runnable {
    private List<ServerClient> clients;
    private UnknownConnection connect;
    private Thread server;
    private Thread manage;
    private Thread send;
    private Thread receive;
    private Scanner scanner = new Scanner(System.in);
    private AtomicBoolean isRunning;
    private AtomicBoolean isRaw;

    public Server(int port, boolean useUDP) {
        this.clients = Collections.synchronizedList(new ArrayList());
        this.isRaw = new AtomicBoolean(false);
        this.isRunning = new AtomicBoolean(false);
        UnknownConnection unknownConnection = this.connect = useUDP ? new UDPConnection(port) : new TCPConnection(port);
        if (this.connect.isValid()) {
            this.server = new Thread((Runnable)this, "Server");
            this.server.start();
        }
    }

    @Override
    public void run() {
        this.isRunning.set(true);
        System.out.println("[SERVER]: " + LocalDateTime.now() + " Server initalized on port " + this.connect.getPort());
        this.heartbeat();
        this.receive();
        while (this.isRunning.get()) {
            String text = this.scanner.nextLine();
            if (text.startsWith("$")) {
                if (text.equals("$raw")) {
                    this.toggleRawMode();
                    continue;
                }
                if (text.equals("$queryclients")) {
                    this.queryClients();
                    continue;
                }
                if (text.equals("$stop")) {
                    this.stop();
                    continue;
                }
                if (text.equals("$help")) {
                    this.printHelp();
                    continue;
                }
                if (text.startsWith("$kick")) {
                    ServerClient client;
                    String name = text.split(" ")[1];
                    int id = -1;
                    boolean isId = true;
                    try {
                        id = Integer.parseInt(name);
                    }
                    catch (NumberFormatException e) {
                        isId = false;
                    }
                    ServerClient serverClient = client = isId ? this.getClientById(id) : this.getClientByName(name);
                    if (client != null) {
                        this.disconnect(client, new DisconnectPacket(client.address, client.port, client.id, 1));
                        continue;
                    }
                    System.out.println(isId ? "Client with id: " + id + " does not exist! Check client id." : "Client with name: " + name + " does not exist! Check client name.");
                    continue;
                }
                System.out.println("Unknown command.");
                this.printHelp();
                continue;
            }
            this.broadcast(new BroadcastPacket(this.connect, -1L, String.format("Server: %s", text)));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void stop() {
        List<ServerClient> list = this.clients;
        synchronized (list) {
            for (ServerClient client : this.clients) {
                this.send(new DisconnectPacket(client.address, client.port, client.id, 1));
            }
        }
        this.isRunning.set(false);
        this.close();
    }

    public void close() {
        new Thread("Close"){

            @Override
            public void run() {
                Server.this.connect.close();
                Server.this.scanner.close();
            }
        }.start();
    }

    private void heartbeat() {
        this.manage = new Thread("Manage"){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                while (Server.this.isRunning.get()) {
                    byte[] names = Server.this.queryClientNames();
                    List list = Server.this.clients;
                    synchronized (list) {
                        for (ServerClient client : Server.this.clients) {
                            Server.this.send(new UserListPacket(client.address, client.port, names));
                            client.ping(Server.this.connect);
                        }
                    }
                    try {
                        Thread.sleep(2000L);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        this.manage.start();
    }

    private void receive() {
        this.receive = new Thread("Inbound Packets"){

            @Override
            public void run() {
                while (Server.this.isRunning.get()) {
                    Server.this.process(Server.this.connect.recv());
                }
            }
        };
        this.receive.start();
    }

    private void process(UnknownPacket packet) {
        if (packet == null) {
            return;
        }
        if (this.isRaw.get()) {
            System.out.println(packet);
        }
        switch (packet.getType()) {
            case 1: {
                this.connect(new ConnectPacket(packet));
                break;
            }
            case 2: {
                this.disconnect(new DisconnectPacket(packet));
                break;
            }
            case -2147483616: {
                this.broadcast(new BroadcastPacket(packet));
                break;
            }
            case 0x10000010: {
                this.markClientActive(new HeartbeatPacket(packet).getId());
            }
        }
    }

    private void connect(ConnectPacket packet) {
        ServerClient client = new ServerClient(packet.getName(), packet.getAddress(), packet.getPort(), UniqueIdentifier.getIndentifier());
        this.clients.add(client);
        this.send(new ConnectResponsePacket(client.address, client.port, client.id));
        System.out.println(String.valueOf(client.name) + " connected from " + client.address.getHostAddress() + ":" + client.port + " Id: " + client.id);
    }

    private void disconnect(DisconnectPacket packet) {
        ServerClient client = this.getClientById(packet.getId());
        this.disconnect(client, packet);
    }

    private void disconnect(ServerClient client, DisconnectPacket packet) {
        if (client == null) {
            return;
        }
        this.clients.remove(client);
        this.send(packet);
        String sReason = packet.getReason() == 2 ? " timed out from " : " disconnected from ";
        System.out.println(String.valueOf(client.name) + sReason + client.address.getHostAddress() + ":" + client.port + " Id: " + client.id);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void broadcast(BroadcastPacket packet) {
        ServerClient sender = this.getClientById(packet.getId());
        if (sender == null) {
            return;
        }
        List<ServerClient> list = this.clients;
        synchronized (list) {
            for (ServerClient client : this.clients) {
                this.send(new BroadcastPacket(client.address, client.port, client.id, packet.getMessage()));
            }
        }
    }

    private void send(final UnknownPacket packet) {
        this.send = new Thread("Outbound Packets"){

            @Override
            public void run() {
                Server.this.connect.send(packet);
            }
        };
        this.send.start();
    }

    private void markClientActive(long id) {
        this.getClientById(id).markActive();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ServerClient getClientByName(String name) {
        List<ServerClient> list = this.clients;
        synchronized (list) {
            for (ServerClient client : this.clients) {
                if (!client.name.equals(name)) continue;
                return client;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ServerClient getClientById(long id) {
        List<ServerClient> list = this.clients;
        synchronized (list) {
            for (ServerClient client : this.clients) {
                if (client.id != id) continue;
                return client;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void queryClients() {
        System.out.println("===< Clients >===");
        List<ServerClient> list = this.clients;
        synchronized (list) {
            for (ServerClient client : this.clients) {
                System.out.println(client);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private byte[] queryClientNames() {
        String users = new String();
        List<ServerClient> list = this.clients;
        synchronized (list) {
            for (ServerClient client : this.clients) {
                users = String.valueOf(users) + client.name + "\u0000";
            }
        }
        return users.getBytes();
    }

    private void toggleRawMode() {
        boolean raw;
        boolean bl = raw = !this.isRaw.get();
        if (raw) {
            System.out.println("Raw mode on.");
        } else {
            System.out.println("Raw mode off.");
        }
        this.isRaw.set(raw);
    }

    private void printHelp() {
        System.out.println("Here is a list of all available commands:");
        System.out.println("=========================================");
        System.out.println("$raw - Enables raw mode.");
        System.out.println("$queryclients - Shows all connected clients.");
        System.out.println("$kick [id | username] - Kicks a client.");
        System.out.println("$help - Shows this help dialog.");
        System.out.println("$stop - Shuts down the server.");
    }
}
