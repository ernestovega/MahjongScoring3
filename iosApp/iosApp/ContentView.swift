import SwiftUI
import UIKit
import Shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            // Status bar area painted with MS2 colorPrimaryDark; Compose handles the rest of the insets
            .background(Color(red: 0, green: 0x7B / 255.0, blue: 0x49 / 255.0))
            .ignoresSafeArea(.all)
    }
}
