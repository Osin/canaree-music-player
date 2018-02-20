package dev.olog.msc.utils.k.extension

import java.util.*

fun <T> List<T>.shuffle(): List<T> {
    Collections.shuffle(this)
    return this
}

fun <T> List<T>.swap(i: Int, j: Int): List<T> {
    Collections.swap(this, i, j)
    return this
}

fun <T> List<T>.startWith(item: T): List<T> {
    val list = this.toMutableList()
    list.add(0, item)
    return list
}

fun <T> List<T>.startWith(data: List<T>): List<T> {
    val list = this.toMutableList()
    list.addAll(0, data)
    return list
}

fun <T> List<T>.startWithIfNotEmpty(item: T): List<T> {
    if (this.isNotEmpty()){
        return startWith(item)
    }
    return this
}

fun <T> List<T>.startWithIfNotEmpty(list: List<T>): List<T> {
    if (this.isNotEmpty()){
        return startWith(list)
    }
    return this
}

fun <K, V> mapOfNotNulls(vararg pairs: Pair<K, V?>): MutableMap<K, V> {
    val map = mutableMapOf<K,V>()
    for (pair in pairs) {
        pair.second?.let {
            map.put(pair.first, it)
        }
    }
    return map
}