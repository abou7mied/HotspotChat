package hotspotchat.abou7mied.me.hotspotchat.net;

import android.content.Context;
import android.content.pm.LauncherApps;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;

import hotspotchat.abou7mied.me.hotspotchat.BR;
import hotspotchat.abou7mied.me.hotspotchat.core.App;
import hotspotchat.abou7mied.me.hotspotchat.core.Preferences;

/**
 * Created by abou7mied on 12/12/16.
 */

public class NetWorkManager extends BaseObservable {


    private boolean hotspotEnabled;
    private String currentNetworkName = "";
    private String apPassword;

    public void prepare(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    setCurrentNetworkName(wifiInfo.getSSID());
                }
            }
        } else
            setCurrentNetworkName("");


        setHotspotEnabled(isApOn(context));

    }

    @Bindable
    public String getCurrentNetworkName() {
        if (hotspotEnabled) {
            String s = "Network started, invite your friends to join your network!\nNetwork name: " + currentNetworkName;
            if (apPassword != null)
                s += "\nPassword: " + apPassword;
            return s;
        }
        if (currentNetworkName.equals(""))
            return "Not connected to any network";
        else
            return "Current network: " + currentNetworkName;
    }

    public void setCurrentNetworkName(String currentNetworkName) {
        this.currentNetworkName = currentNetworkName != null ? currentNetworkName : "";
        notifyPropertyChanged(BR.currentNetworkName);
    }


    public void setHotspotEnabled(boolean hotspotEnabled) {
        this.hotspotEnabled = hotspotEnabled;
        notifyPropertyChanged(BR.hotspotEnabled);
    }

    @Bindable
    public boolean getHotspotEnabled() {
        return hotspotEnabled;
    }

    public boolean isApOn(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        Method method = null;
        try {
            method = manager.getClass().getDeclaredMethod("getWifiApState");
            method.setAccessible(true);
            int actualState = (Integer) method.invoke(manager, (Object[]) null);

            if (actualState == 13) {

                Method getWifiApConfigurationMethod = manager.getClass().getMethod("getWifiApConfiguration");
                WifiConfiguration netConfig = (WifiConfiguration) getWifiApConfigurationMethod.invoke(manager);
                apPassword = netConfig.preSharedKey;
                setCurrentNetworkName(netConfig.SSID);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void startHotspot(final Context context, final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Log.i("startHotspot", "startHotspot");

                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

                if (wifiManager.isWifiEnabled()) {
//            wifiManager.setWifiEnabled(false);
                    stopHotspot(context);

                }


                WifiConfiguration netConfig = new WifiConfiguration();

                Preferences preferences = App.getInstance().getPreferences();
                netConfig.SSID = preferences.getNetworkName();
                netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

                String networkPassword = preferences.getNetworkPassword();
                if (networkPassword != null && !networkPassword.equals("")) {
                    netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    netConfig.preSharedKey = networkPassword;
                } else
                    netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                try {
                    Method setWifiApMethod = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                    boolean apstatus = (Boolean) setWifiApMethod.invoke(wifiManager, netConfig, true);

                    Method isWifiApEnabledmethod = wifiManager.getClass().getMethod("isWifiApEnabled");
                    while (!(Boolean) isWifiApEnabledmethod.invoke(wifiManager)) {
                    }
                    Method getWifiApStateMethod = wifiManager.getClass().getMethod("getWifiApState");
                    int apstate = (Integer) getWifiApStateMethod.invoke(wifiManager);
                    Method getWifiApConfigurationMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
                    netConfig = (WifiConfiguration) getWifiApConfigurationMethod.invoke(wifiManager);
                    Log.e("CLIENT", "\nSSID:" + netConfig.SSID + "\nPassword:" + netConfig.preSharedKey + "\n");

                } catch (Exception e) {
                    e.printStackTrace();
                }

                callback.done();

            }
        }).start();
    }

    public static void stopHotspot(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        try {
            Method setWifiApMethod = wm.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            setWifiApMethod.invoke(wm, null, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface Callback {
        void done();
    }

}
