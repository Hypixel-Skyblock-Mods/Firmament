package moe.nea.firmod.features

import moe.nea.firmod.events.FirmodEvent
import moe.nea.firmod.events.subscription.Subscription
import moe.nea.firmod.events.subscription.SubscriptionList
import moe.nea.firmod.util.ErrorUtil
import moe.nea.firmod.util.compatloader.ICompatMeta

object FeatureManager {

	fun subscribeEvents() {
		SubscriptionList.allLists.forEach { list ->
			if (ICompatMeta.shouldLoad(list.javaClass.name))
				ErrorUtil.catch("Error while loading events from $list") {
					list.provideSubscriptions {
						subscribeSingleEvent(it)
					}
				}
		}
	}

	private fun <T : FirmodEvent> subscribeSingleEvent(it: Subscription<T>) {
		it.eventBus.subscribe(false, "${it.owner.javaClass.simpleName}:${it.methodName}", it.invoke)
	}
}
