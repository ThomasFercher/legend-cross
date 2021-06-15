#import "LegendInterfacePlugin.h"
#if __has_include(<legend_interface/legend_interface-Swift.h>)
#import <legend_interface/legend_interface-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "legend_interface-Swift.h"
#endif

@implementation LegendInterfacePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftLegendInterfacePlugin registerWithRegistrar:registrar];
}
@end
