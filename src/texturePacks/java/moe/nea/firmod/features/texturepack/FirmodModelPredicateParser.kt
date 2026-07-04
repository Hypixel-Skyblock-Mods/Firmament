
package moe.nea.firmod.features.texturepack

import com.google.gson.JsonElement

interface FirmodModelPredicateParser {
    fun parse(jsonElement: JsonElement): FirmodModelPredicate?
}
