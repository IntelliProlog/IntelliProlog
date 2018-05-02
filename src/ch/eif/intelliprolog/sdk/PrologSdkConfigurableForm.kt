package ch.eif.intelliprolog.sdk

import ch.eif.intelliprolog.util.gridBagConstraints
import ch.eif.intelliprolog.util.setConstraints
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.layout.LCFlags
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*
import javax.swing.event.DocumentEvent

class PrologSdkConfigurableForm {
    var isModified: Boolean = false
    private val gprologPathField: TextFieldWithBrowseButton = TextFieldWithBrowseButton()

    fun getContentPanel(): JComponent {
        val panel = JPanel(GridBagLayout())

        val listener : DocumentAdapter = object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent?) {
                isModified = true
            }
        }

        gprologPathField.textField.document.addDocumentListener(listener)

        addLine(panel, 0, "gprolog", gprologPathField)

        return panel
    }

    private fun addLine(panel: JPanel, y: Int, label: String, textField: TextFieldWithBrowseButton) {
        val base = gridBagConstraints {
            insets = Insets(2, 0, 2, 3)
            gridy = y
        }
        panel.add(JLabel(label), base.setConstraints {
            anchor = GridBagConstraints.LINE_START
            gridx = 0
        })

        panel.add(textField, base.setConstraints {
            gridx = 1
            fill = GridBagConstraints.HORIZONTAL
            weightx = 1.0
        })

        panel.add(Box.createHorizontalStrut(1), base.setConstraints {
            gridx = 2
            weightx = 0.1
        })
    }

    fun getGPrologPath(): String {
        return gprologPathField.text
    }

    fun init(gprologPath:String): Unit {
        gprologPathField.text = gprologPath
    }
}