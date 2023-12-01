package pns.project.pz.connector;

public interface ConnectionListener {
    void onConnected();

    void onDisconnected();

    void onConnectionFailed();
}
