package com.fuwafuwa.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.net.RouteInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class NetSuit {


    public static class NetInfo {
        public String netType;
        public String apn;
        public String localIp;
        public String netMask;
        public String gateWay;
        public String serverAddr;
        public String dns1;
        public String dns2;
        public String mac;
        public String isp;
        public String ispAddr;
        public String routerMac;
        public String vpn;
        public String proxy;
        public List<String> localIpv6s;
        public String cellarName;

        public String getIpv6() {
            if (localIpv6s != null) {
                String[] ipv6Arr = localIpv6s.toArray(new String[localIpv6s.size()]);
                return Arrays.toString(ipv6Arr);
            }
            return "";
        }
    }

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean checkEnable(Context context) {
        ConnectivityManager connecty = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        if (connecty != null) {
            //connecty.getActiveNetworkInfo().getSubtypeName() LTE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] allInfo = connecty.getAllNetworks();
                for (int i = 0; allInfo != null && i < allInfo.length; i++) {
                    NetworkInfo inf = connecty.getNetworkInfo(allInfo[i]);
                    if (connecty.getActiveNetworkInfo() != null) {
                        if (inf.isConnected() && connecty.getActiveNetworkInfo().getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                            return true;
                        }
                    } else if (inf.isConnected()) {
                        return true;
                    }
                }
                return false;
            }
            NetworkInfo info = connecty.getActiveNetworkInfo();
            return info != null && info.isConnected();
        }
        return false;
    }


    /**
     * 0~4 signal merely~
     *
     * @param context
     * @return
     */
    public static int[] checkWifiSignalInt(@NonNull Context context) {
        WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager == null) return new int[]{-1, 0};
        Boolean isWifi = isWifiOrMobile(context);
        if (isWifi == null) return new int[]{-2, 0};
        if (isWifi) {
            WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
            int wifi = mWifiInfo.getRssi();
            if (wifi > -50 && wifi < 0) {//最强
                return new int[]{4, wifi};
            } else if (wifi > -70 && wifi < -50) {//较强
                return new int[]{3, wifi};
            } else if (wifi > -80 && wifi < -70) {//较弱
                return new int[]{2, wifi};
            } else if (wifi > -100 && wifi < -80) {//微弱
                return new int[]{1, wifi};
            } else {
                return new int[]{0, wifi};
            }
        }
        return new int[]{-3, 0};
    }

    public static List<ScanResult> getWifiResults(@NonNull Context context) {
        WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null) {
            return mWifiManager.getScanResults();
        }
        return null;
    }

    /**
     * WIFI或移动网
     *
     * @param context
     * @return
     */
    public static Boolean isWifiOrMobile(Context context) {
        ConnectivityManager connecty = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        if (connecty != null) {
            NetworkInfo info = connecty.getActiveNetworkInfo();
            if (info != null) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI) return true;
                if (info.getType() == ConnectivityManager.TYPE_MOBILE) return false;
            }
        }
        return null;
    }


    /**
     * @param context
     * @return
     */
    public static NetInfo netInfo(Context context, NetInfo netInfo) {
        ConnectivityManager connecty = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        if (netInfo == null) netInfo = new NetInfo();
        if (connecty != null) {
            NetworkInfo info = connecty.getActiveNetworkInfo();
            if (info != null) {
                netInfo.apn = info.getExtraInfo();
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    netInfo.netType = "WIFI";

                } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    netInfo.netType = "MOBILE " + info.getSubtypeName();
                    switch (info.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                            //"4G";
                            netInfo.cellarName = "4G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN://api<8 : replace by 11
                            //2G
                            netInfo.cellarName = "2G";
                            break;
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                        case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                        case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                            //"3G";
                            netInfo.cellarName = "3G";
                            break;
                        default:
                            // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                            if ("TD-SCDMA".equalsIgnoreCase(info.getSubtypeName()) || "WCDMA".equalsIgnoreCase(info.getSubtypeName()) ||
                                    "CDMA2000".equalsIgnoreCase(info.getSubtypeName())) {
                                //"3G";
                                netInfo.cellarName = "3G";
                            }
                            break;

                    }
                } else {
                    netInfo.netType = "UNKNOWN";
                }
            }
        }
        return netInfo;
    }

    public static boolean isWifi5G(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) return false;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int freq = 0;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            freq = wifiInfo.getFrequency();
        } else {
            String ssid = wifiInfo.getSSID();
            if (ssid != null && ssid.length() > 2) {
                String ssidTemp = ssid.substring(1, ssid.length() - 1);
                List<ScanResult> scanResults = wifiManager.getScanResults();
                for (ScanResult scanResult : scanResults) {
                    if (scanResult.SSID.equals(ssidTemp)) {
                        freq = scanResult.frequency;
                        break;
                    }
                }
            }
        }
        return freq > 4900 && freq < 5900;
    }

    @SuppressLint("HardwareIds")
    public static NetInfo getNetInfo(Context context, NetInfo netInfo) {
        ConnectivityManager connecty = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE));
        if (connecty == null) return netInfo;
        NetworkInfo networkInfo = connecty.getActiveNetworkInfo();
        if (networkInfo == null) return netInfo;
        if (netInfo == null) netInfo = new NetInfo();
        netInfo.proxy = getProxy(context);
        netInfo.vpn = getVpnName();
        netInfo.localIpv6s = getIpv6Addr();
        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null) return netInfo;
            boolean isWifi5G = isWifi5G(context);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            DhcpInfo dhcp = wifiManager.getDhcpInfo();
            netInfo.netType = "WIFI" + (isWifi5G ? " (5G) " : "");
            netInfo.localIp = ipStringify(dhcp.ipAddress);
            netInfo.netMask = ipStringify(dhcp.netmask);
            netInfo.gateWay = ipStringify(dhcp.gateway);
            netInfo.dns1 = ipStringify(dhcp.dns1);
            netInfo.dns2 = ipStringify(dhcp.dns2);
            netInfo.apn = networkInfo.getExtraInfo();//wifiInfo.getSSID();
            netInfo.mac = getMac(wifiInfo);
            netInfo.routerMac = wifiInfo.getBSSID();
            netInfo.serverAddr = ipStringify(wifiInfo.getIpAddress());
            netInfo.serverAddr = ipStringify(dhcp.serverAddress);
        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            netInfo.netType = "MOBILE " + networkInfo.getSubtypeName();
            netInfo.apn = networkInfo.getExtraInfo();
            netInfo.mac = getMac(null);
        }
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        netInfo.localIp = inetAddress.getHostAddress();
                        netInfo.mac = bytesToString(intf.getHardwareAddress());
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                LinkProperties prot = connecty.getLinkProperties(connecty.getActiveNetwork());
                List<InetAddress> dnses = prot.getDnsServers();
                for (int i = 0; dnses != null && i < dnses.size(); i++) {
                    if (i == 0)
                        netInfo.dns1 = dnses.get(0).getHostAddress();
                    else if (i == 1)
                        netInfo.dns2 = dnses.get(1).getHostAddress();
                    else break;
                }
                List<RouteInfo> r = prot.getRoutes();
                if (r != null && r.size() > 0) {
                    for (int i = 0; i < r.size(); i++) {
                        RouteInfo g = r.get(i);
                        if (g.isDefaultRoute()) {
                            netInfo.gateWay = g.getGateway().getHostAddress();
                        }
                    }
                }
            }
        }
        return netInfo;//得到IPV4地址
    }


    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }


    public static String getProxy(Context context) {
        String proxyAddress;
        int proxyPort;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            proxyAddress = System.getProperty("http.proxyHost");
            String portstr = System.getProperty("http.proxyPort");
            proxyPort = Integer.parseInt((portstr != null ? portstr : "-1"));
        } else {
            proxyAddress = Proxy.getHost(context);
            proxyPort = Proxy.getPort(context);
        }
        if (TextUtils.isEmpty(proxyAddress)) {
            return null;
        }
        return proxyAddress + ":" + proxyPort;
    }

    public static String getVpnName() {
        try {
            Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
            if (niList != null) {
                for (NetworkInterface intf : Collections.list(niList)) {
                    if (!intf.isUp() || intf.getInterfaceAddresses().size() == 0) {
                        continue;
                    }
                    if ("tun0".equals(intf.getName()) || "ppp0".equals(intf.getName())) {
                        return intf.getName(); // The VPN is up
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     *  * 将得到的int类型的IP转换为String类型
     *  *
     *  * @param ip
     *  * @return
     *  
     */
    public static String ipStringify(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }


    private static List<String> getIpv6Addr() {
        List<String> ipv6s = new ArrayList<>();
        String command = " /system/bin/ip -6 addr show";
        String ipv6addrTemp = "";
        final int IPV6LEN_LEFT_BIT = 6; // 默认，截取的Ipv6字符串左起始位
        final int IPV6LEN_RIGHT_BIT = 1; // 默认，截取的Ipv6字符串右结束位
        Process p = shell(command);
        if (p != null) {
            try (InputStream is = p.getInputStream();) {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    if ((line.contains("inet6")) && (line.contains("scope"))) {
                        ipv6addrTemp = line.substring(line.indexOf("inet6") + IPV6LEN_LEFT_BIT, line.lastIndexOf("scope") - IPV6LEN_RIGHT_BIT);
                        ipv6s.add(ipv6addrTemp);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ipv6s;
    }

    @SuppressLint("HardwareIds")
    private static String getMac(WifiInfo wifiInfo) {
        String mac = null;
        if (wifiInfo != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mac = wifiInfo.getMacAddress();
        }
        if (!"02:00:00:00:00:00".equals(mac)) return mac;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                byte[] macbytes = intf.getHardwareAddress();
                if (macbytes != null) {
                    mac = bytesToString(macbytes);
                    if (mac != null) {
                        return mac;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return mac;
    }

    public static Process shell(String command) {
        Runtime rt = Runtime.getRuntime();
        Process p = null;
        try {
            p = rt.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
            p = null;
        }
        return p;
    }

    public static void watchStdout(Process process, StdoutCallback callback) {
        if (process != null) {
            try (InputStream is = process.getInputStream()) {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                boolean wait = true;
//                while (wait) {
//
//                    if (callback.exit()) {
//                        wait = false;
//                        break;
//                    }
//                }
                while ((line = br.readLine()) != null) {
                    if (callback != null) {
                        callback.out(line);
                    }
                }
                if (callback != null) {
                    callback.complete("");
                }
            } catch (IOException e) {
                if (callback != null) {
                    callback.err(e.getMessage());
                }
            }
        }
    }

    public interface StdoutCallback {
        void out(String line);

        void err(String message);

        void complete(String message);

        boolean exit();
    }
}
