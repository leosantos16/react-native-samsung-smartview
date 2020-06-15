#import "SamsungSmartview.h"
#import "SamsungSmartview-Swift.h"

@implementation SamsungSmartview

Samsung * swift = [Samsung new];

- (void)onServiceFound(device: AnyObject)
{
    [self sendEventWithName:@"samsung_device_list" body:device];
}

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(startSearch)
{
    [swift startSearch];
}

RCT_EXPORT_METHOD(stopSearch)
{
    [swift stopSearch];
}

RCT_EXPORT_METHOD(castContent:(NSString*)appID :(NSString*)channelID, (AnyObject)attr)
{
    [swift castContent :appID :channelID :attr];
}

RCT_EXPORT_METHOD(sendMessage:(NSString*)eventID :(AnyObject)msgData)
{
    [swift sendMessage :eventID :msgData];
}

RCT_EXPORT_METHOD(disconnect)
{
    [swift disconnect];
}

@end
