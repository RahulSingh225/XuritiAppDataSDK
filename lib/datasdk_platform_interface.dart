import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'datasdk_method_channel.dart';

abstract class DatasdkPlatform extends PlatformInterface {
  /// Constructs a DatasdkPlatform.
  DatasdkPlatform() : super(token: _token);

  static final Object _token = Object();

  static DatasdkPlatform _instance = MethodChannelDatasdk();

  /// The default instance of [DatasdkPlatform] to use.
  ///
  /// Defaults to [MethodChannelDatasdk].
  static DatasdkPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [DatasdkPlatform] when
  /// they register themselves.
  static set instance(DatasdkPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> getAppData(userid, baseurl, latitude, longitude, fcmtoken) {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
