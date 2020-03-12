#import "React/RCTBridgeModule.h"
#import "React/RCTEventEmitter.h"

@interface RCT_EXTERN_MODULE(SamsungSmartview, RCTEventEmitter)
RCT_EXTERN_METHOD(startSearch)
RCT_EXTERN_METHOD(stopSearch)
RCT_EXTERN_METHOD(
  cast:(NSDictionary *)options
  resolver:(RCTPromiseResolveBlock)resolve
  rejecter:(RCTPromiseRejectBlock)reject
)
RCT_EXTERN_METHOD(play)
RCT_EXTERN_METHOD(pause)
RCT_EXTERN_METHOD(stop)
RCT_EXTERN_METHOD(disconnect)
RCT_EXTERN_METHOD(seek:(double *)time)
// RCT_EXTERN_METHOD(increment)
// RCT_EXTERN_METHOD(getCount: (RCTResponseSenderBlock)callback)
// RCT_EXTERN_METHOD(decrement: (RCTPromiseResolveBlock)resolve rejecter: (RCTPromiseRejectBlock)reject)
@end
