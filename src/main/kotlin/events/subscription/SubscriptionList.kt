package moe.nea.firmod.events.subscription

import java.util.ServiceLoader
import kotlin.streams.asSequence
import moe.nea.firmod.Firmod

interface SubscriptionList {
    fun provideSubscriptions(addSubscription: (Subscription<*>) -> Unit)

    companion object {
        val allLists by lazy {
            ServiceLoader.load(SubscriptionList::class.java)
                .stream()
                .asSequence()
                .mapNotNull {
                    kotlin.runCatching { it.get() }
                        .getOrElse { ex ->
                            Firmod.logger.error("Could not load subscriptions from ${it.type()}", ex)
                            null
                        }
                }
                .toList()
        }
        init {
        	require(allLists.isNotEmpty())
        }
    }
}
