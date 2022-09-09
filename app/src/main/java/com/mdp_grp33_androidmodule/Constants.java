package com.mdp_grp33_androidmodule;

public interface Constants {
    static final int MESSAGE_STATE_CHANGE = 1;
    static final int MESSAGE_READ = 2;
    static final int MESSAGE_WRITE = 3;
    static final int MESSAGE_DEVICE_NAME = 4;
    static final int MESSAGE_TOAST = 5;
    static final String DEVICE_NAME = "device_name";
    static final String TOAST = "toast";

    static final int STATE_NONE = 0;       // we're doing nothing
    static final int STATE_LISTEN = 1;     // now listening for incoming connections
    static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    static final int STATE_CONNECTED = 3;  // now connected to a remote device
}
