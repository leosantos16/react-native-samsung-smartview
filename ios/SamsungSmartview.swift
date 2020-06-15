import Foundation

@objc class Samsung : NSObject {
    let serviceSearch = Service.search();
    let msApplication: Application?
    var objcsmartview = SamsungSmartview();
    
    override init () {
        serviceSearch.delegate = self
    }

    // MARK: - ServiceSearchDelegate -
    @objc func onServiceFound(service: Service) {
        objcsmartview.onServiceFound(service)
    }

    @objc func onServiceLost(service: Service) {
        objcsmartview.onServiceFound(service)
    }
    
    @objc func startSearch() {
        serviceSearch.start()
    }
    
    @objc func stopSearch() {
        serviceSearch.stop()
    }
    
    @objc func castContent(appID: String, channelID: String, attr: AnyObject) {
        msApplication = service.createApplication(appID, channelURI: channelID, args: nil);
        msApplication.connect(attr);
        func onClientConnect() {
            serviceSearch.stop();
            print("Application.start Success");
        }
        func onError(error: NSError) {
            msApplication.install({ (success, error) -> Void in
                if success == true {
                    print("Application.install Success");
                    serviceSearch.stop();
                } else {
                    print("Application.install Error : \(error)");
                }
            })
        }
    }
    
    @objc func sendMessage(eventID: String, msgData: AnyObject) {
        msApplication?.publish(event: eventID, message: msgData as AnyObject);
    }
    
    @objc func disconnect() {
        msApplication.disconnect();
    }
}
