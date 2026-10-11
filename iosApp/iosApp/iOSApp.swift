import SwiftUI
import Shared

@main
struct iOSApp: App {
    init() {
        Koin_commonKt.doInitKoin(config: nil)
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
