package ch.eif.intelliprolog.sdk

import ch.eif.intelliprolog.util.OSUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.projectRoots.AdditionalDataConfigurable
import com.intellij.openapi.projectRoots.Sdk
import java.io.File
import javax.swing.JComponent

class PrologSdkConfigurable : AdditionalDataConfigurable {
    private val form: PrologSdkConfigurableForm = PrologSdkConfigurableForm()

    private var mySdk: Sdk? = null

    override fun setSdk(sdk: Sdk?) {
        mySdk = sdk
    }

    override fun createComponent(): JComponent {
        return form.getContentPanel()
    }

    override fun isModified(): Boolean {
        return form.isModified
    }

    override fun apply() {
        val newData = PrologSdkAdditionalData(
                form.getGPrologPath()
        )

        val modificator = mySdk!!.sdkModificator
        modificator.sdkAdditionalData = newData
        ApplicationManager.getApplication()!!.runWriteAction(object: Runnable {
            override fun run() {
                modificator.commitChanges()
            }
        })
        form.isModified = false
    }

    override fun reset() {
        val sdk = mySdk!!
        val data = sdk.sdkAdditionalData

        if (data != null) {
            if (data !is PrologSdkAdditionalData) {
                return
            }
            val gprologData: PrologSdkAdditionalData = data
            val gprologPath = gprologData.gprologPath ?: ""

            form.init(gprologPath)
        } else {
            val file = File(sdk.homePath)
            val version = extractVersion(file.name)
            val parent = file.parent
            form.init(
                    File(parent, OSUtil.getExe("gprolog-" + version)).toString()
            )
        }

        form.isModified = false
    }

    private fun extractVersion(name: String) : String {
        val trimmedName = OSUtil.removeExtension(name)

        if (trimmedName == "gprolog") {
            return ""
        }
        if (trimmedName.startsWith("ghc-")) {
            return name.substring(4)
        }
        return ""
    }

    override fun disposeUIResources() {
    }
}