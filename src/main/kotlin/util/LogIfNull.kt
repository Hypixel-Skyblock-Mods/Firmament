
package moe.nea.firmod.util


fun runNull(block: () -> Unit): Nothing? {
    block()
    return null
}
