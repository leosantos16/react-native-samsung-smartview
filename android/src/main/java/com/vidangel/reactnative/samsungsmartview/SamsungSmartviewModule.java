package com.vidangel.reactnative.samsungsmartview;

import android.net.Uri;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.samsung.multiscreen.Result;
import com.samsung.multiscreen.Search;
import com.samsung.multiscreen.Service;
import com.samsung.multiscreen.VideoPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SamsungSmartviewModule extends ReactContextBaseJavaModule {

    private ReactApplicationContext reactContext;
    private final String LOGTAG = "SamsungSmartviewModule";
    //    private List<Service> mDeviceList = new LinkedList<>();
    private VideoPlayer mVideoPlayer;

    private Search search;

    public SamsungSmartviewModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        //log.v(LOGTAG, "SamsungSmartviewModule start");

        // Get an instance of Search
        search = Service.search(this.reactContext);

        // Add a listener for the service found event
        search.setOnServiceFoundListener(
                new Search.OnServiceFoundListener() {

                    @Override
                    public void onFound(Service service) {
                        //log.d(LOGTAG, "Search.onFound() service: " + service.toString());
                        sendEventDeviceList();
//                        updateDeviceList(service);
                    }
                }
        );

        // Add a listener for the service lost event
        search.setOnServiceLostListener(
                new Search.OnServiceLostListener() {

                    @Override
                    public void onLost(Service service) {
                        //log.d(LOGTAG, "Search.onLost() service: " + service.toString());

                        // Remove this service from the displayed list.
                    }
                }
        );
        // Start the discovery process
        search.start();
    }

    private void sendEventDeviceList() {
        //log.v(LOGTAG, "sendDevices");
        //log.v(LOGTAG, search.getServices().toString());
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
            //log.v(LOGTAG, arrOfDevices.toString());
            sendEvent("samsung_device_list", params);
        }

    }

//    private void updateDeviceList(Service device) {
//
//        //log.v(LOGTAG, "updateDeviceList");
//        if (mDeviceList.contains(device)) {
//            mDeviceList.remove(device);
//        }
//        mDeviceList.add(device);
//        sendEventDeviceList();
//    }

    private void sendEvent(String eventName, WritableMap params) {
        this.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    @Override
    public String getName() {
        return "SamsungSmartview";
    }

    @ReactMethod
    public void startSearch() {
        //log.v(LOGTAG, "startSearch");
        search.start();
        sendEventDeviceList();
    }

    @ReactMethod
    public void stopSearch() {
        //log.v(LOGTAG, "stopSearch");
        search.stop();
    }

    private Service getServiceFromUUID(String targetUuid) {
        Service target = null;
        for (Service device : search.getServices()) {
//            //log.v(LOGTAG, device.toString());
            if (device.getId().equals(targetUuid)) {
                target = device;
//                //log.v(LOGTAG, device.toString());
            }
        }
        return target;
    }

    @ReactMethod
    private void cast(final String targetUuid, final String name) {
        castContent(targetUuid, name, "App");
    }

    @ReactMethod
    private void castContent(final String targetUuid, final String name, final String appName) {
        Service service = getServiceFromUUID(targetUuid);

//        //log.v(LOGTAG, service.toString());
//        //log.v(LOGTAG, targetUuid);
//        //log.v(LOGTAG, name);

        mVideoPlayer = service.createVideoPlayer(appName);
        if (name.toLowerCase().contains(".jpg")) {
            //log.v(LOGTAG, "mplayer standby");
            mVideoPlayer.standbyConnect(
                    new Result<Boolean>() {
                        @Override
                        public void onSuccess(Boolean r) {
                            //log.v(LOGTAG, "playContent(): onSuccess.");
                        }

                        @Override
                        public void onError(com.samsung.multiscreen.Error error) {
                            //log.v(LOGTAG, "playContent(): onError: " + error.getMessage());
                        }
                    });

            // Passing through the jpg causes a cropped version on the tv. I don't know if
            // this is due to the tv settings, or DMP. Passing no jpg shows the DMP screen, which
            // will work for now. The client still needs to send through a jpg though to hit this
            // block of code. Not clean at all, but I hope to come back through with more time later

//            mVideoPlayer.standbyConnect(Uri.parse(name),
//                    new Result<Boolean>() {
//                        @Override
//                        public void onSuccess(Boolean r) {
//                            //log.v(LOGTAG, "playContent(): onSuccess.");
//                        }
//
//                        @Override
//                        public void onError(com.samsung.multiscreen.Error error) {
//                            //log.v(LOGTAG, "playContent(): onError: " + error.getMessage());
//                        }
//                    });
        } else {

            // Add service to a displayed list where your user can select one.
            // For display, we recommend that you show: service.getName()
            mVideoPlayer.playContent(Uri.parse(name),
                    appName,
                    Uri.parse("https://content.vidangel.com/chosen/cast.jpg"),
                    new Result<Boolean>() {
                        @Override
                        public void onSuccess(Boolean r) {
                            //log.v(LOGTAG, "playContent(): onSuccess.");
                        }

                        @Override
                        public void onError(com.samsung.multiscreen.Error error) {
                            //log.v(LOGTAG, "playContent(): onError: " + error.getMessage());
                        }
                    });
        }


    }

    @ReactMethod
    private void doPlay() {
        //log.i(LOGTAG, "try doPlay...");
        mVideoPlayer.play();
    }

    @ReactMethod
    private void doPause() {
        //log.i(LOGTAG, "try doPause...");
        mVideoPlayer.pause();
    }

    @ReactMethod
    private void doStop() {
        //log.i(LOGTAG, "try doStop...");
        mVideoPlayer.stop();
    }
}
