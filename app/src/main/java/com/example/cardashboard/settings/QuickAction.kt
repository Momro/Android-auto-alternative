package com.example.cardashboard.settings

import org.json.JSONArray
import org.json.JSONObject

data class QuickAction(
    val id: String,
    val label: String,
    val icon: String,
    val domain: String,
    val service: String,
    val entityId: String
)

fun defaultQuickActions() = listOf(
    QuickAction("garage", "Garage", "Garage", "cover", "toggle", "cover.garage_door"),
    QuickAction("preheat", "Vorheizen", "ElectricCar", "switch", "toggle", "switch.preheating"),
    QuickAction("scene_drive", "Szene Fahren", "Scene", "scene", "turn_on", "scene.drive"),
    QuickAction("light", "Licht", "Lightbulb", "light", "toggle", "light.garage")
)

fun List<QuickAction>.toJson(): String {
    val arr = JSONArray()
    forEach { a ->
        arr.put(
            JSONObject()
                .put("id", a.id)
                .put("label", a.label)
                .put("icon", a.icon)
                .put("domain", a.domain)
                .put("service", a.service)
                .put("entityId", a.entityId)
        )
    }
    return arr.toString()
}

fun parseQuickActions(json: String): List<QuickAction> {
    if (json.isBlank()) return defaultQuickActions()
    return try {
        val arr = JSONArray(json)
        List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            QuickAction(
                id = o.getString("id"),
                label = o.getString("label"),
                icon = o.getString("icon"),
                domain = o.getString("domain"),
                service = o.getString("service"),
                entityId = o.getString("entityId")
            )
        }
    } catch (e: Exception) {
        defaultQuickActions()
    }
}
