package ch.eif.intelliprolog.sdk

import com.intellij.openapi.projectRoots.SdkAdditionalData
import org.jdom.Element

class PrologSdkAdditionalData(gprologPath: String?) : SdkAdditionalData {

    companion object {
        private val GPROLOG_PATH = "gprolog_path"

        fun load(element: Element): PrologSdkAdditionalData {
            val data = PrologSdkAdditionalData(null)
            data.gprologPath = element.getAttributeValue(GPROLOG_PATH)

            return data
        }
    }

    var gprologPath: String? = gprologPath

    override fun clone(): Any {
        throw CloneNotSupportedException()
    }

    fun save(element: Element): Unit {
        if (gprologPath != null) {
            element.setAttribute(GPROLOG_PATH, gprologPath)
        }
    }
}