import androidx.compose.ui.window.ComposeUIViewController
import di.initKoin
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.navigationController
import platform.UIKit.setStatusBarStyle

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
        setSystemUIColor()
    }
) {
    App()
}

private fun setSystemUIColor(isDarkMode: Boolean = false) {
    val color = if (isDarkMode) greenLightUIColor else greenDarkUIColor
    setStatusBarColor(color)
    setNavigationBarColor(color)
}

private fun setStatusBarColor(color: UIColor) {
    UIApplication.sharedApplication.setStatusBarStyle(UIStatusBarStyleLightContent)
    UIApplication.sharedApplication.keyWindow?.rootViewController?.view?.backgroundColor = color
}

private fun setNavigationBarColor(color: UIColor) {
    val navigationController = UIApplication.sharedApplication.keyWindow?.rootViewController?.navigationController
    navigationController?.navigationBar?.barTintColor = color
}

private val greenLightUIColor = UIColor(
    red = 0.toDouble() / 255.0,
    green = 177.toDouble() / 255.0,
    blue = 106.toDouble() / 255.0,
    alpha = 1.0,
)

private val greenDarkUIColor = UIColor(
    red = 0.toDouble() / 255.0,
    green = 177.toDouble() / 255.0,
    blue = 106.toDouble() / 255.0,
    alpha = 1.0,
)