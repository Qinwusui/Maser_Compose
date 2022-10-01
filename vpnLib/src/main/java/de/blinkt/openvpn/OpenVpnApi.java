package de.blinkt.openvpn;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Build;
import android.os.RemoteException;
import android.text.TextUtils;

import androidx.activity.result.contract.ActivityResultContracts;

import com.blankj.utilcode.util.ToastUtils;

import java.io.IOException;
import java.io.StringReader;

import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VPNLaunchHelper;

public class OpenVpnApi {

    public static void startVpn(Context context, String inlineConfig, String sCountry, String userName, String pw) throws RemoteException {
        Intent i= VpnService.prepare(context);
        if (i!=null){
            context.startService(i);
        }
        if (TextUtils.isEmpty(inlineConfig)) {
            ToastUtils.showShort("配置为空！");
        }
        startVpnInternal(context, inlineConfig, sCountry, userName, pw);
    }

    static void startVpnInternal(Context context, String inlineConfig, String sCountry, String userName, String pw) throws RemoteException {
        ConfigParser cp = new ConfigParser();
        try {
            cp.parseConfig(new StringReader(inlineConfig));
            VpnProfile vp = cp.convertProfile();
            // Analysis.ovpn
            vp.mName = sCountry;
            vp.mProfileCreator = context.getPackageName();
            vp.mUsername = userName;
            vp.mPassword = pw;
            ProfileManager.setTemporaryProfile(context, vp);
            VPNLaunchHelper.startOpenVpn(vp, context);
        } catch (IOException | ConfigParser.ConfigParseError e) {
            throw new RemoteException(e.getMessage());
        }
    }
}
