package ua.org.dector.lwsge.common

import collection.mutable.HashMap

/**
 * @author dector (dector9@gmail.com)
 */

object Config {
    private val params = HashMap.empty[String, String]

    def apply(name: String): String = {
        if (params contains name) params(name)
        else ""
    }

    def update(name: String, value: String)     { params(name) = value }
    def update(name: String, value: Boolean)    { this(name) = value.toString }
    def update(name: String, value: Byte)       { this(name) = value.toString }
    def update(name: String, value: Char)       { this(name) = value.toString }
    def update(name: String, value: Short)      { this(name) = value.toString }
    def update(name: String, value: Int)        { this(name) = value.toString }
    def update(name: String, value: Long)       { this(name) = value.toString }
    def update(name: String, value: Float)      { this(name) = value.toString }
    def update(name: String, value: Double)     { this(name) = value.toString }
}
