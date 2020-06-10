package com.vidangel.reactnative.samsungsmartview;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.samsung.multiscreen.Application;
import com.samsung.multiscreen.Client;
import com.samsung.multiscreen.Error;
import com.samsung.multiscreen.Message;
import com.samsung.multiscreen.Result;
import com.samsung.multiscreen.Search;
import com.samsung.multiscreen.Service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SamsungSmartviewModule extends ReactContextBaseJavaModule {

    private ReactApplicationContext reactContext;
    private final String LOGTAG = "SamsungSmartviewModule";
    private Search search;
    private Application application = null;

    public SamsungSmartviewModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        search = Service.search(this.reactContext);

        search.setOnServiceFoundListener(
                new Search.OnServiceFoundListener() {

                    @Override
                    public void onFound(Service service) {
                        sendEventDeviceList();
                    }
                }
        );

        search.setOnServiceLostListener(
                new Search.OnServiceLostListener() {

                    @Override
                    public void onLost(Service service) {
                        sendEventDeviceList();
                    }
                }
        );
        search.start();
    }

    private void sendEventDeviceList() {
        if (search.getServices() != null && search.getServices().size() > 0) {
            JSONArray arrOfDevices = new JSONArray();
            for (Service dev : search.getServices()) {
                JSONObject json = new JSONObject();
                try {
                    json.put("name", dev.getName());
                    json.put("uuid", dev.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                arrOfDevices.put(json);
            }

            WritableMap params = Arguments.createMap();
            params.putString("devices", arrOfDevices.toString());
            sendEvent("samsung_device_list", params);
        }

    }

    private void sendEvent(String eventName, WritableMap params) {
        this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    @Override
    public String getName() {
        return "SamsungSmartview";
    }

    @ReactMethod
    public void startSearch() {
        search.start();
        sendEventDeviceList();
    }

    @ReactMethod
    public void stopSearch() {
        search.stop();
    }

    @ReactMethod
    public void disconnect() {
        application.disconnect(true, new Result<Client>() {
      
            @Override
            public void onSuccess(Client client) {
                Log.d(LOGTAG, "Application.disconnect onSuccess()");           
            }

            @Override
            public void onError(Error error) {
                Log.d(LOGTAG, "Application.disconnect onError() " + error.toString());
            }
         });
    }

    private Service getServiceFromUUID(String targetUuid) {
        Service target = null;
        for (Service device : search.getServices()) {
            if (device.getId().equals(targetUuid)) {
                target = device;
            }
        }
        return target;
    }

    @ReactMethod
    private void castContent(final String targetUuid, final String channelId, final String program, final String name) {
        Service service = getServiceFromUUID(targetUuid);
        application = service.createApplication(targetUuid, channelId);
        application.connect(new Result<Client>() {
            @Override
            public void onSuccess(Client client) {
                Log.d(LOGTAG, "Application.connect onSuccess()");
                application.publish(program, name, Message.TARGET_HOST);
            }
        
            @Override
            public void onError(Error error) {
                Log.d(LOGTAG, "Application.connect onError() " + error.toString());

                if (error.getCode() == 404) {
                    application.install(new Result<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            Log.d(LOGTAG, "Application.install onSuccess() " + result.toString());
                    
                        }
                        @Override
                        public void onError(Error error) {
                            Log.d(LOGTAG, "Application.install onError() " + error.toString());
                        }
                    });
                }
            }
        });
    }

    @ReactMethod
    public void sendMessage(String message, String data) {
        application.publish(message, data, Message.TARGET_HOST);
    }
}
