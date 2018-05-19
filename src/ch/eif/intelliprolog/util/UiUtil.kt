package ch.eif.intelliprolog.util

import java.awt.GridBagConstraints

inline fun gridBagConstraints(init: GridBagConstraints.() -> Unit): GridBagConstraints {
    val result = GridBagConstraints()
    result.init()
    return result
}

inline fun GridBagConstraints.setConstraints(init: GridBagConstraints.() -> Unit): GridBagConstraints {
    this.init()
    return this
}