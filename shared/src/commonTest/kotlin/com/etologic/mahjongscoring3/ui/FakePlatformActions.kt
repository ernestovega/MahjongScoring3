package com.etologic.mahjongscoring3.ui

import com.etologic.mahjongscoring3.domain.usecases.ExportedFile
import com.etologic.mahjongscoring3.platform.PlatformActions

/** Test double of the operating system actions: share sheets, review prompt, e-mail, browser. */
class FakePlatformActions : PlatformActions {
    val calls = mutableListOf<String>()
    override val appVersionName: String = "3.0.0"
    override fun shareText(text: String, chooserTitle: String) { calls += "shareText" }
    override fun shareFile(file: ExportedFile, chooserTitle: String) { calls += "shareFile:${file.fileName}" }
    override fun openUrl(url: String) { calls += "openUrl:$url" }
    override fun sendEmail(address: String, subject: String, chooserTitle: String): Boolean { calls += "sendEmail:$address"; return true }
    override fun requestInAppReview() { calls += "requestInAppReview" }
    override fun closeApp() { calls += "closeApp" }
}
