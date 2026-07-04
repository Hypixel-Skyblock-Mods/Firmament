package moe.nea.firmod.events

import java.util.concurrent.CopyOnWriteArrayList
import org.apache.commons.lang3.reflect.TypeUtils
import moe.nea.firmod.Firmod
import moe.nea.firmod.util.ErrorUtil
import moe.nea.firmod.util.MC

/**
 * A pubsub event bus.
 *
 * [subscribe] to events [publish]ed on this event bus.
 * Subscriptions may not necessarily be delivered in the order of registering.
 */
open class FirmodEventBus<T : FirmodEvent> {
    companion object {
        val allEventBuses = mutableListOf<FirmodEventBus<*>>()
    }

    val eventType = TypeUtils.getTypeArguments(javaClass, FirmodEventBus::class.java)!!.values.single()

    init {
        allEventBuses.add(this)
    }

    data class Handler<T>(
        val invocation: (T) -> Unit, val receivesCancelled: Boolean,
        var knownErrors: MutableSet<Class<*>> = mutableSetOf(),
        val label: String,
    )

    private val toHandle: MutableList<Handler<T>> = CopyOnWriteArrayList()
    val handlers: List<Handler<T>> get() = toHandle

    fun subscribe(label: String, handle: (T) -> Unit) {
        subscribe(false, label, handle)
    }

    fun subscribe(receivesCancelled: Boolean, label: String, handle: (T) -> Unit) {
        toHandle.add(Handler(handle, receivesCancelled, label = label))
    }

    fun publish(event: T): T {
        for (function in toHandle) {
            if (function.receivesCancelled || event !is FirmodEvent.Cancellable || !event.cancelled) {
                try {
                    function.invocation(event)
                } catch (e: Exception) {
                    val klass = e.javaClass
                    if (!function.knownErrors.contains(klass) || Firmod.DEBUG) {
                        function.knownErrors.add(klass)
                        ErrorUtil.softError("Caught exception during processing event $event by $function", e)
                    }
                }
            }
        }
        return event
    }

    fun publishSync(event: T) {
        MC.onMainThread {
            publish(event)
        }
    }
}
