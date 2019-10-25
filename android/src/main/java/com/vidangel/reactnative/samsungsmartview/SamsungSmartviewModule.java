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

import java.util.LinkedList;
import java.util.List;

public class SamsungSmartviewModule extends ReactContextBaseJavaModule {

    private ReactApplicationContext reactContext;
    private final String LOGTAG = "SamsungSmartviewModule";
//    private List<Service> mDeviceList = new LinkedList<>();
    private VideoPlayer mVideoPlayer;

    private Search search;

    public SamsungSmartviewModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        Log.v(LOGTAG, "SamsungSmartviewModule start");

        // Get an instance of Search
        search = Service.search(this.reactContext);

        // Add a listener for the service found event
        search.setOnServiceFoundListener(
                new Search.OnServiceFoundListener() {

                    @Override
                    public void onFound(Service service) {
                        Log.d(LOGTAG, "Search.onFound() service: " + service.toString());
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
                        Log.d(LOGTAG, "Search.onLost() service: " + service.toString());

                        // Remove this service from the displayed list.
                    }
                }
        );
        // Start the discovery process
        search.start();
    }

    private void sendEventDeviceList() {
        Log.v(LOGTAG, "sendDevices");
        Log.v(LOGTAG, search.getServices().toString());
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
            Log.v(LOGTAG, arrOfDevices.toString());
            sendEvent("samsung_device_list", params);
        }

    }

//    private void updateDeviceList(Service device) {
//
//        Log.v(LOGTAG, "updateDeviceList");
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
        Log.v(LOGTAG, "startSearch");
        search.start();
        sendEventDeviceList();
    }

    @ReactMethod
    public void stopSearch() {
        Log.v(LOGTAG, "stopSearch");
        search.stop();
    }

    private Service getServiceFromUUID(String targetUuid) {
        Service target = null;
        for (Service device : search.getServices()) {
            Log.v(LOGTAG, device.toString());
            if (device.getId().equals(targetUuid)) {
                target = device;
                Log.v(LOGTAG, device.toString());
            }
        }
        return target;
    }

    @ReactMethod
    private void cast(final String targetUuid, final String name) {
        Service service = getServiceFromUUID(targetUuid);

        Log.v(LOGTAG, service.toString());

        mVideoPlayer = service.createVideoPlayer("The Chosen");
        // Add service to a displayed list where your user can select one.
        // For display, we recommend that you show: service.getName()
        mVideoPlayer.playContent(Uri.parse(name),
                new Result<Boolean>() {
                    @Override
                    public void onSuccess(Boolean r) {
                        Log.v(LOGTAG, "playContent(): onSuccess.");
                    }

                    @Override
                    public void onError(com.samsung.multiscreen.Error error) {
                        Log.v(LOGTAG, "playContent(): onError: " + error.getMessage());
                    }
                });

    }

    @ReactMethod
    private void doPlay() {
        Log.i(LOGTAG, "try doPlay...");
        mVideoPlayer.play();
    }

    @ReactMethod
    private void doPause() {
        Log.i(LOGTAG, "try doPause...");
        mVideoPlayer.pause();
    }

    @ReactMethod
    private void doStop() {
        Log.i(LOGTAG, "try doStop...");
        mVideoPlayer.stop();
    }
}
