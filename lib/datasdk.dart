import 'datasdk_platform_interface.dart';

class Datasdk {
  Future<String?> getPlatformVersion() {
    return DatasdkPlatform.instance.getPlatformVersion();
  }

  Future<String?> getAppData(userid, baseurl, latitude, longitude, fcmtoken) {
    return DatasdkPlatform.instance
        .getAppData(userid, baseurl, latitude, longitude, fcmtoken);
  }
}
