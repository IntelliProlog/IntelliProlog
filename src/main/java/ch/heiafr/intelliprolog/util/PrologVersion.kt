package ch.heiafr.intelliprolog.util

class PrologVersion constructor(private val version: List<Int>) : Comparable<PrologVersion> {

    companion object {
        fun fromString(version: String) = PrologVersion(version.split(".").map { it.toInt() }.toList())
    }

    override fun equals(other: Any?): Boolean {
        if (other is PrologVersion) {
            val v1 = this.version
            val v2 = other.version
            if (v1.size != v2.size)
                return false
            for (i in v1.indices) {
                if (v1[i] != v2[i])
                    return false
            }
            return true
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        var result = 1
        for (v in version) {
            result = 31 * result + v.toInt()
        }
        return result
    }

    override fun toString(): String {
        val buf = StringBuilder()
        for (v in version) {
            if (buf.length > 0) {
                buf.append('.')
            }
            buf.append(v)
        }
        return buf.toString()
    }


    override fun compareTo(other: PrologVersion): Int {
        val v1 = this.version
        val v2 = other.version
        val minSize = Math.min(v1.size, v2.size)
        for (i in 0..minSize - 1) {
            val compare = v1[i].compareTo(v2[i])
            if (compare != 0)
                return compare
        }
        return v1.size - v2.size
    }

}