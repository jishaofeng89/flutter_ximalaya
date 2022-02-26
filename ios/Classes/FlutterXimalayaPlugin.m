#import "FlutterXimalayaPlugin.h"
#if __has_include(<flutter_ximalaya/flutter_ximalaya-Swift.h>)
#import <flutter_ximalaya/flutter_ximalaya-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_ximalaya-Swift.h"
#endif

@implementation FlutterXimalayaPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterXimalayaPlugin registerWithRegistrar:registrar];
}
@end
