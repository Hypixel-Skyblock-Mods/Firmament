
package moe.nea.firmod.events.subscription

import moe.nea.firmod.events.FirmodEvent
import moe.nea.firmod.events.FirmodEventBus


data class Subscription<T : FirmodEvent>(
    val owner: Any,
    val invoke: (T) -> Unit,
    val eventBus: FirmodEventBus<T>,
    val methodName: String,
)
