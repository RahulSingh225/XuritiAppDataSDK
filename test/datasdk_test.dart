import 'package:flutter_test/flutter_test.dart';
import 'package:datasdk/datasdk.dart';
import 'package:datasdk/datasdk_platform_interface.dart';
import 'package:datasdk/datasdk_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockDatasdkPlatform
    with MockPlatformInterfaceMixin
    implements DatasdkPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<String?> getAppData(userid, baseurl, latitude, longitude, fcmtoken) {
    // TODO: implement getAppData
    throw UnimplementedError();
  }
}

void main() {
  final DatasdkPlatform initialPlatform = DatasdkPlatform.instance;

  test('$MethodChannelDatasdk is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelDatasdk>());
  });

  test('getPlatformVersion', () async {
    Datasdk datasdkPlugin = Datasdk();
    MockDatasdkPlatform fakePlatform = MockDatasdkPlatform();
    DatasdkPlatform.instance = fakePlatform;

    expect(await datasdkPlugin.getPlatformVersion(), '42');
  });
}
