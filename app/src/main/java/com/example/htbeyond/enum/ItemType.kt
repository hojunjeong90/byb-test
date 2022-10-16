package com.example.htbeyond.enum

enum class ItemType(val typeId: Int) {

    BOOK_SEARCH_RESULT_HEADER(1000),
    BOOK_VERTICAL_NORMAL(1001);

    companion object {
        fun from(typeId: Int) : ItemType {
            return values().firstOrNull { it.typeId == typeId } ?: BOOK_VERTICAL_NORMAL
        }
    }
}