

package moe.nea.firmod.events

/**
 * An event that can be fired by a [FirmodEventBus].
 *
 * Typically, that event bus is implemented as a companion object
 *
 * ```
 * class SomeEvent : FirmodEvent() {
 *     companion object : FirmodEventBus<SomeEvent>()
 * }
 * ```
 */
abstract class FirmodEvent {
    /**
     * A [FirmodEvent] that can be [cancelled]
     */
    abstract class Cancellable : FirmodEvent() {
        /**
         * Cancels this is event.
         *
         * @see cancelled
         */
        fun cancel() {
            cancelled = true
        }

        /**
         * Whether this event is cancelled.
         *
         * Cancelled events will bypass handlers unless otherwise specified and will prevent the action that this
         * event was originally fired for.
         */
        var cancelled: Boolean = false
    }
}
