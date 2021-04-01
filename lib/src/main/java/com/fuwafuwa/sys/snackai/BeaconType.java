package com.fuwafuwa.sys.snackai;

public class BeaconType {

    public static String typeof(int typeCode, int serverUUID) {
        if (serverUUID == 0xfeaa) {
            switch (typeCode) {
                case 0x20:
                    return "Eddystone_TLM";
                case 0x00:
                    return "Eddystone_UID";
                case 0x10:
                    return "Eddystone_URL";
            }
        } else if (serverUUID == 0xfed8) {
            if (typeCode == 0x0) {
                return "UriBeacon";
            }
        }
        switch (typeCode) {
            case 0x0215:
                return "iBeacon";
            case 0xbeac:
                return "AltBeacon";
        }
        return "Unknown";
    }

}
