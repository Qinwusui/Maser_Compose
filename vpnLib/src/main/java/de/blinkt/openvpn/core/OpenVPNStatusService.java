/*
 * Copyright (c) 2012-2016 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package de.blinkt.openvpn.core;

import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ThreadUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by arne on 08.11.16.
 */

public class OpenVPNStatusService extends Service implements  VpnStatus.StateListener {
    static final RemoteCallbackList<IStatusCallbacks> M_CALLBACKS =
            new RemoteCallbackList<>();
    private static final OpenVPNStatusHandler M_HANDLER = new OpenVPNStatusHandler();
    private static final int SEND_NEW_LOGITEM = 100;
    private static final int SEND_NEW_STATE = 101;
    private static final int SEND_NEW_BYTECOUNT = 102;
    private static final int SEND_NEW_CONNECTED_VPN = 103;
    static UpdateMessage mLastUpdateMessage;
    private static final IServiceStatus.Stub M_BINDER = new IServiceStatus.Stub() {

        @Override
        public ParcelFileDescriptor registerStatusCallback(IStatusCallbacks cb) throws RemoteException {
            if (mLastUpdateMessage != null) {
                sendUpdate(cb, mLastUpdateMessage);
            }

            M_CALLBACKS.register(cb);
            try {
                final ParcelFileDescriptor[] pipe = ParcelFileDescriptor.createPipe();
                ThreadUtils.getFixedPool(2).submit(()->{
                    DataOutputStream fd = new DataOutputStream(new ParcelFileDescriptor.AutoCloseOutputStream(pipe[1]));
                    try {
                        synchronized (VpnStatus.READ_FILE_LOCK) {
                            if (!VpnStatus.readFileLog) {
                                VpnStatus.READ_FILE_LOCK.wait();
                            }
                        }
                    } catch (InterruptedException e) {
                    }
                    try {

                        // Mark end
                        fd.writeShort(0x7fff);
                        fd.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                return pipe[0];
            } catch (IOException e) {
                e.printStackTrace();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    throw new RemoteException(e.getMessage());
                }
                return null;
            }
        }

        @Override
        public void unregisterStatusCallback(IStatusCallbacks cb) {
            M_CALLBACKS.unregister(cb);
        }

        @Override
        public String getLastConnectedVPN() {
            return VpnStatus.getLastConnectedVPNProfile();
        }

        @Override
        public void setCachedPassword(String uuid, int type, String password) {
            PasswordCache.setCachedPassword(uuid, type, password);
        }

        @Override
        public TrafficHistory getTrafficHistory() {
            return VpnStatus.trafficHistory;
        }

    };

    private static void sendUpdate(IStatusCallbacks broadcastItem,
                                   UpdateMessage um) throws RemoteException {
        broadcastItem.updateStateString(um.state, um.logmessage, um.resId, um.level, um.intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return M_BINDER;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        VpnStatus.addStateListener(this);
        M_HANDLER.setService(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VpnStatus.removeStateListener(this);
        M_CALLBACKS.kill();

    }

    @Override
    public void updateState(String state, String logmessage, int localizedResId, ConnectionStatus level, Intent intent) {

        mLastUpdateMessage = new UpdateMessage(state, logmessage, localizedResId, level, intent);
        Message msg = M_HANDLER.obtainMessage(SEND_NEW_STATE, mLastUpdateMessage);
        msg.sendToTarget();
    }

    @Override
    public void setConnectedVPN(String uuid) {
        Message msg = M_HANDLER.obtainMessage(SEND_NEW_CONNECTED_VPN, uuid);
        msg.sendToTarget();
    }

    static class UpdateMessage {
        public String state;
        public String logmessage;
        public ConnectionStatus level;
        public Intent intent;
        int resId;

        UpdateMessage(String state, String logmessage, int resId, ConnectionStatus level, Intent intent) {
            this.state = state;
            this.resId = resId;
            this.logmessage = logmessage;
            this.level = level;
            this.intent = intent;
        }
    }

    private static class OpenVPNStatusHandler extends Handler {
        WeakReference<OpenVPNStatusService> service = null;

        private void setService(OpenVPNStatusService statusService) {
            service = new WeakReference<>(statusService);
        }

        @Override
        public void handleMessage(Message msg) {

            RemoteCallbackList<IStatusCallbacks> callbacks;
            if (service == null || service.get() == null) {
                return;
            }
            callbacks = M_CALLBACKS;
            // Broadcast to all clients the new value.
            final int N = callbacks.beginBroadcast();
            for (int i = 0; i < N; i++) {

                try {
                    IStatusCallbacks broadcastItem = callbacks.getBroadcastItem(i);

                    switch (msg.what) {
                        case SEND_NEW_LOGITEM:
                            break;
                        case SEND_NEW_BYTECOUNT:
                            break;
                        case SEND_NEW_STATE:
                            sendUpdate(broadcastItem, (UpdateMessage) msg.obj);
                            break;

                        case SEND_NEW_CONNECTED_VPN:
                            broadcastItem.connectedVPN((String) msg.obj);
                            break;
                        default:
                            break;
                    }
                } catch (RemoteException e) {
                    // The RemoteCallbackList will take care of removing
                    // the dead object for us.
                }
            }
            callbacks.finishBroadcast();
        }
    }
}