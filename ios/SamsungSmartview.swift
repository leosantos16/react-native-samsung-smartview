//
//  SamsungSmartview.swift
//  SamsungSmartview
//
//  Created by Jason Bodily on 3/10/20.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Foundation
import SmartView

@objc(SamsungSmartview)
class SamsungSmartview: RCTEventEmitter, ServiceSearchDelegate {

  private var services: [Service] = []
  private var serviceSearch = Service.search()
  private var videoPlayer: VideoPlayer?

  override init() {
    super.init()
    serviceSearch.delegate = self
  }

  @objc func startSearch() -> Void {
    serviceSearch.start()
  }

  @objc func stopSearch() -> Void {
    serviceSearch.stop()
  }

  // MARK: - ServiceSearchDelegate -
  // Update your UI by using the serviceDiscovery.services array
  @objc func onServiceFound(_ service: Service) {
    if (!services.contains(where: { $0.id == service.id })) {
      services.append(service)
    }
    self.emitServices()
  }

  @objc func onServiceLost(_ service: Service) {
    services.removeAll(where: { $0.id == service.id })
    self.emitServices()
  }

  @objc func onStart() {
    self.emitServices()
  }

  @objc func emitServices() {
    let mapped_services = services.map {
      return [
        "id": $0.id,
        "uri": $0.uri,
        "name": $0.name,
        "type": $0.type,
        "version": $0.version,
      ]
    }
    var body: Data? = nil
    do {
      body = try JSONSerialization.data(withJSONObject: mapped_services, options: [])
    } catch {
      print("Failed to serialize")
    }
    if let body = body {
      sendEvent(withName: "services", body: String(data: body, encoding: .utf8) ?? "")
    }
  }

  @objc func cast(_ options: NSDictionary, resolver resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) -> Void {
    enum CastError: Error {
      case missingKey
      case missingService
    }
    guard
      let id = options["id"] as? String,
      let url = options["url"] as? String,
      let title = options["title"] as? String,
      let imageUrl = options["image_url"] as? String
    else {
      reject("CastError", "A required key (id, url, title, image_url) is missing", CastError.missingKey)
      return
    }
    guard
      let service = getServiceById(id:id)
    else {
      reject("CastError", "Service with id '\(id)' not found", CastError.missingService)
      return
    }
    videoPlayer = service.createVideoPlayer("SmartView")
    videoPlayer?.playContent(
      URL(string: url),
      title: title,
      thumbnailURL: URL(string: imageUrl),
      completionHandler: {
        (error:NSError?) -> Void in
        if (error != nil) {
          reject("CastError", error?.description, error)
        } else {
          resolve(nil)
        }
      })
  }

  @objc func play() {
    videoPlayer?.play()
  }

  @objc func pause() {
    videoPlayer?.pause()
  }

  @objc func stop() {
    videoPlayer?.stop()
  }

  @objc func seek(_ time: Double) {
    let time_interval = TimeInterval(time)
    videoPlayer?.seek(time_interval)
    videoPlayer?.play()
  }

  @objc func getServiceById(id: String) -> Service? {
    return services.first(where: { $0.id == id })
  }

  // we need to override this method and
  // return an array of event names that we can listen to
  override func supportedEvents() -> [String]! {
    return ["services"]
  }

  override static func requiresMainQueueSetup() -> Bool {
    return true
  }

}
