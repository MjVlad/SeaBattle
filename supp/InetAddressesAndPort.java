package supp;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressesAndPort {
    private InetAddress address;
    private int port;

    public InetAddressesAndPort(String addressPort) throws UnknownHostException {
        String[] addP = addressPort.split(":", 2);
        this.address = InetAddress.getByName(addP[0]);
        this.port = Integer.parseInt(addP[1]);
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
