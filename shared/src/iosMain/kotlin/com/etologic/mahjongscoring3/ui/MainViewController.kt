package com.etologic.mahjongscoring3.ui

import androidx.compose.ui.window.ComposeUIViewController
import com.etologic.mahjongscoring3.ui.main.App
import platform.UIKit.UIViewController

/** Entry point used by the iOS app: `MainViewControllerKt.MainViewController()`. */
@Suppress("FunctionName", "unused")
fun MainViewController(): UIViewController = ComposeUIViewController { App() }
