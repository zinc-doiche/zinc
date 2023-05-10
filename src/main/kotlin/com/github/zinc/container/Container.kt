package com.github.zinc.container

abstract class Container<K, V> {
    val container: HashMap<K, V> = HashMap()
    open operator fun get(k: K): V? = container[k]
    fun add(k: K, v: V) { container[k] = v }
    fun remove(k: K): V? = container.remove(k)
}