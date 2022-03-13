//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.ximalaya.ting.android.miyataopensdk;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.webkit.WebView;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
import com.ximalaya.ting.android.b.a.c;
import com.ximalaya.ting.android.b.a.c.a;
import com.ximalaya.ting.android.miyataopensdk.framework.data.model.HomePageConfigModel;
import com.ximalaya.ting.android.miyataopensdk.framework.e.l;
import com.ximalaya.ting.android.miyataopensdk.framework.f.n;
import com.ximalaya.ting.android.opensdk.datatrasfer.AccessTokenManager;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.DeviceInfoProviderDefault;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDeviceInfoProvider;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest.ITokenStateChange;
import com.ximalaya.ting.android.opensdk.httputil.BaseCall;
import com.ximalaya.ting.android.opensdk.httputil.XimalayaException;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.util.MeteDataUtil;
import com.ximalaya.ting.android.opensdk.util.SharedPreferencesUtil;
import com.ximalaya.ting.android.opensdk.util.Utils;
import com.ximalaya.ting.android.player.MediadataCrytoUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.FormBody.Builder;
import org.json.JSONException;
import org.json.JSONObject;

public class IntentXmUISdk {
    public static String a;
    private Context b;
    private UISdkConfig c;
    private String d;
    @SuppressLint({"StaticFieldLeak"})
    private static IntentXmUISdk e;
    private static HomePageConfigModel f;
    private final Map<String, String> g = new HashMap();

    public static HomePageConfigModel getConfigModel() {
        return f;
    }

    public static void setConfigModel(HomePageConfigModel var0) {
        f = var0;
    }

    private IntentXmUISdk() {
    }

    public static IntentXmUISdk getInstance() {
        if (e == null) {
            e = new IntentXmUISdk();
        }

        return e;
    }

    public Context getAppContext() {
        return this.b;
    }

    public void init(final Context var1, UISdkConfig var2) {
        this.b = var1;
        this.c = var2;
        if (VERSION.SDK_INT >= 28) {
            try {
                WebView.setDataDirectorySuffix(com.ximalaya.ting.android.miyataopensdk.framework.f.d.f(var1));
            } catch (Throwable var6) {
                var6.printStackTrace();
            }
        }

        if (com.ximalaya.ting.android.miyataopensdk.framework.f.d.isMainProcess(var1)) {
            n.a(var1);
            this.d = SharedPreferencesUtil.getInstance(var1).getString("last_oaId");
            if (TextUtils.isEmpty(this.d)) {
                MdidSdkHelper.InitSdk(var1, true, new IIdentifierListener() {
                    public void OnSupport(boolean var1x, IdSupplier var2) {
                        if (var2 != null) {
                            IntentXmUISdk.this.d = var2.getOAID();
                            SharedPreferencesUtil.getInstance(var1).saveString("last_oaId", IntentXmUISdk.this.d);
                        }
                    }
                });
            }

            a = var2.redirectUrl;
            CommonRequest var3 = CommonRequest.getInstanse();
            var3.setAppkey(var2.appKey);
            var3.setPackid(var2.packId);
            var3.setSiteId(var2.siteId);
            var3.init(var1, var2.appSecret, this.getDeviceInfoProvider(var1));
            AccessTokenManager.getInstanse().init(var1);
            if (AccessTokenManager.getInstanse().hasLogin()) {
                registerLoginTokenChangeListener();
            }
        }

        l var7 = l.a();

        try {
            c var4 = (new a()).a(() -> {
                com.ximalaya.ting.android.b.a.a var2x = new com.ximalaya.ting.android.b.a.a();
                this.g.put("pageId", var2.siteId);
                this.g.put("appKey", var2.appKey);
                var2x.a(this.g);
                return var2x;
            }).b(CommonRequest.getInstanse().getDeviceId()).c(var7.f() != null ? var7.f().getUid() + "" : "").a("android-open").a(false).a();
            com.ximalaya.ting.android.b.a.d.a().a(var1, var4);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        com.ximalaya.ting.android.miyataopensdk.a.a(var1);
        com.ximalaya.ting.android.miyataopensdk.b.a().a(var1);
    }

    public Map<String, String> getTrackCommonExt() {
        return this.g;
    }

    public static void registerLoginTokenChangeListener() {
        CommonRequest.getInstanse().setITokenStateChange(new ITokenStateChange() {
            public boolean getTokenByRefreshSync() {
                n.d("sjc", "getTokenByRefreshSync");
                if (!TextUtils.isEmpty(AccessTokenManager.getInstanse().getRefreshToken())) {
                    try {
                        return IntentXmUISdk.refreshSync();
                    } catch (XimalayaException var2) {
                        var2.printStackTrace();
                    }
                }

                return false;
            }

            public boolean getTokenByRefreshAsync() {
                n.d("sjc", "getTokenByRefreshAsync");
                if (!TextUtils.isEmpty(AccessTokenManager.getInstanse().getRefreshToken())) {
                    try {
                        IntentXmUISdk.refresh();
                        return true;
                    } catch (XimalayaException var2) {
                        var2.printStackTrace();
                    }
                }

                return false;
            }

            public void tokenLosted() {
            }
        });
    }

    public static void refresh() throws XimalayaException {
        OkHttpClient var0 = (new OkHttpClient()).newBuilder().followRedirects(false).build();
        Builder var1 = new Builder();
        var1.add("grant_type", "refresh_token");
        var1.add("refresh_token", AccessTokenManager.getInstanse().getTokenModel().getRefreshToken());
        var1.add("client_id", CommonRequest.getInstanse().getAppKey());
        var1.add("device_id", CommonRequest.getInstanse().getDeviceId());
        var1.add("client_os_type", "2");
        var1.add("pack_id", CommonRequest.getInstanse().getPackId());
        var1.add("uid", AccessTokenManager.getInstanse().getUid());
        var1.add("redirect_uri", a);
        FormBody var2 = var1.build();
        Request var3 = (new Request.Builder()).url("http://api.ximalaya.com/oauth2/refresh_token").post(var2).build();
        var0.newCall(var3).enqueue(new Callback() {
            public void onFailure(Call var1, IOException var2) {
                n.a("refresh", "refreshToken, request failed, error message = " + var2.getMessage());
            }

            public void onResponse(Call var1, Response var2) throws IOException {
                String var3 = var2.body().string();
                JSONObject var4 = null;

                try {
                    var4 = new JSONObject(var3);
                } catch (JSONException var6) {
                    var6.printStackTrace();
                }

                if (var4 != null) {
                    AccessTokenManager.getInstanse().setAccessTokenAndUid(var4.optString("access_token"), var4.optString("refresh_token"), var4.optLong("expires_in"), var4.optString("uid"));
                }

            }
        });
    }

    public static boolean refreshSync() throws XimalayaException {
        OkHttpClient var0 = (new OkHttpClient()).newBuilder().followRedirects(false).build();
        Builder var1 = new Builder();
        var1.add("grant_type", "refresh_token");
        var1.add("refresh_token", AccessTokenManager.getInstanse().getTokenModel().getRefreshToken());
        var1.add("client_id", CommonRequest.getInstanse().getAppKey());
        var1.add("device_id", CommonRequest.getInstanse().getDeviceId());
        var1.add("client_os_type", "2");
        var1.add("pack_id", CommonRequest.getInstanse().getPackId());
        var1.add("uid", AccessTokenManager.getInstanse().getUid());
        var1.add("redirect_uri", a);
        FormBody var2 = var1.build();
        Request var3 = (new Request.Builder()).url("http://api.ximalaya.com/oauth2/refresh_token").post(var2).build();

        try {
            Response var4 = var0.newCall(var3).execute();
            if (var4.isSuccessful()) {
                try {
                    String var5 = var4.body().string();
                    JSONObject var6 = new JSONObject(var5);
                    AccessTokenManager.getInstanse().setAccessTokenAndUid(var6.optString("access_token"), var6.optString("refresh_token"), var6.optLong("expires_in"), var6.optString("uid"));
                } catch (JSONException var7) {
                    var7.printStackTrace();
                }

                return true;
            }
        } catch (IOException var8) {
            var8.printStackTrace();
        }

        return false;
    }

    public IDeviceInfoProvider getDeviceInfoProvider(Context var1) {
        return new DeviceInfoProviderDefault(var1) {
            public String oaid() {
                return IntentXmUISdk.this.d;
            }
        };
    }

    public void startMain(Context var1) {
        Intent intent = new Intent(var1, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        var1.startActivity(intent);
		// var1.startActivity(new Intent(var1, MainActivity.class));
    }

    public void startAlbum(Context var1, String var2) {
        var1.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("open_sdk://" + MeteDataUtil.getMetaDataByKey(Utils.getContext(), "MAIN_APP_ID") + "/link.album?albumId=" + var2)));
    }

    public void startTrack(Context var1, String var2) {
        var1.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("open_sdk://" + MeteDataUtil.getMetaDataByKey(Utils.getContext(), "MAIN_APP_ID") + "/link.track?trackId=" + var2)));
    }

    public UISdkConfig getSdkConfig() {
        return this.c;
    }

    public void initPlayer(Class var1) {
        BaseCall.init(getInstance().getAppContext());
        Notification var2 = XmNotificationCreater.getInstanse(this.b).initNotification(this.b, var1);
        XmPlayerManager.getInstance(getInstance().getAppContext()).init((int)System.currentTimeMillis(), var2);
    }

    public void exitPlayer() {
        XmPlayerManager.release();
        BaseCall.release();
        MediadataCrytoUtil.release();
    }

    public static class UISdkConfig {
        public String appSecret;
        public String appKey;
        public String packId;
        public String siteId;
        public String redirectUrl;
        public boolean exitPlayerWhenCloseUISDK;

        public UISdkConfig(String var1, String var2, String var3, String var4, String var5) {
            this.appSecret = var1;
            this.appKey = var2;
            this.packId = var3;
            this.siteId = var4;
            this.redirectUrl = var5;
            this.exitPlayerWhenCloseUISDK = true;
        }

        public UISdkConfig(String var1, String var2, String var3, String var4, String var5, boolean var6) {
            this.appSecret = var1;
            this.appKey = var2;
            this.packId = var3;
            this.siteId = var4;
            this.redirectUrl = var5;
            this.exitPlayerWhenCloseUISDK = var6;
        }
    }
}
