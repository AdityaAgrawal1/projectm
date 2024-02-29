package com.example.projectm.utils.constants

enum class DropdownType( val displayName: String) {
    MANUFACTURER("Manufacturer"),
    COUNTRY("Country"),
    STATE("State"),
    CATEGORY("Category"),
    ITEM("Item");

    override fun toString(): String = displayName
}
