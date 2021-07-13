/*
 * Decompiled with CFR 0.151.
 */
package com.core.client;

import javax.swing.JList;

public class OnlineUserList<T>
extends JList<T> {
    private static final long serialVersionUID = -4885209697151149099L;

    public void update(T[] users) {
        this.setListData(users);
    }
}
