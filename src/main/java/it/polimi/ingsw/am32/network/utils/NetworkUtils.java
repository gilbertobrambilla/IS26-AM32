package it.polimi.ingsw.am32.network.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Network utilities are implemented to handle interface discovery.
 */
public class NetworkUtils {

    /**
     * Available local ipv4 addresses are retrieved and mapped to their interface names.
     *
     * @return a map containing the ip addresses as keys and the interface names as values
     */
    public static Map<String, String> getAvailableIpv4Addresses() {
        // map is instantiated
        Map<String, String> ipMap = new LinkedHashMap<>();

        try {
            // network interfaces are retrieved
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                // loopback and inactive interfaces are skipped
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                // addresses are enumerated
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();

                    // only ipv4 addresses are processed
                    if (addr instanceof Inet4Address) {
                        ipMap.put(addr.getHostAddress(), networkInterface.getDisplayName());
                    }
                }
            }
        } catch (Exception e) {
            // exception is ignored to allow fallback
        }

        // map is returned
        return ipMap;
    }
}
