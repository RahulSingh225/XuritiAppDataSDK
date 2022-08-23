import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'datasdk_platform_interface.dart';

/// An implementation of [DatasdkPlatform] that uses method channels.
class MethodChannelDatasdk extends DatasdkPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('datasdk');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  Future<String?> getAppData(
      userid, baseurl, latitude, longitude, fcmtoken) async {
    final version = await methodChannel
        .invokeMethod<String>('getAppData', <String, dynamic>{
      'userid': userid,
      'baseurl': baseurl,
      'lat': latitude,
      'long': longitude,
      'fcmtoken': fcmtoken
    });
    return version;
  }
}
