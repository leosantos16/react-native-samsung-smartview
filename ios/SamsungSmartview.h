#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>
#import "SamsungSmartview-Bridging-Header.h"

@interface SamsungSmartview : RCTEventEmitter <RCTBridgeModule>
- (void)onServiceFound(device: AnyObject);
- (void)onServiceLost(device: AnyObject);
@end
