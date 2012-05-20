package ua.org.dector.lwsge.common

import collection.mutable.HashMap

/**
 * @author dector (dector9@gmail.com)
 */

object Config {
    private val params = HashMap.empty[String, Any]

    def apply(name: String): Any = {
        if (params contains name) params(name)
        else null
    }

    def remove(name: String) { params.remove(name) }

    def s(name: String): String                 = { this(name).toString }
    def bool(name: String): Boolean             = { this(name).asInstanceOf[Boolean] }
    def i(name: String): Int                    = { this(name).asInstanceOf[Int] }
    def l(name: String): Long                   = { this(name).asInstanceOf[Long] }
    def f(name: String): Float                  = { this(name).asInstanceOf[Float] }

    def update(name: String, value: Any) { params(name) = value }

    def contains(name: String) = params.contains(name)
}
