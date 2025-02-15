package com.abdushakoor12.sawal.core

class ServiceLocator private constructor(
    private val singletons: MutableMap<Class<*>, Any>,
    private val factories: MutableMap<Class<*>, () -> Any>
) {

    fun <T : Any> get(clazz: Class<T>): T {
        return when {
            singletons.containsKey(clazz) -> singletons[clazz] as T
            factories.containsKey(clazz) -> factories[clazz]?.invoke() as T
            else -> throw IllegalArgumentException("No provider found for ${clazz.simpleName}")
        }
    }

    inline fun <reified T : Any> get(): T = get(T::class.java)

    class Builder {
        private val singletons = mutableMapOf<Class<*>, Any>()
        private val factories = mutableMapOf<Class<*>, () -> Any>()

        fun <T : Any> addSingleton(clazz: Class<T>, instance: T): Builder {
            singletons[clazz] = instance
            return this
        }

        inline fun <reified T : Any> addSingleton(instance: T): Builder {
            return addSingleton(T::class.java, instance)
        }

        fun <T : Any> addFactory(clazz: Class<T>, factory: () -> T): Builder {
            factories[clazz] = factory
            return this
        }

        inline fun <reified T : Any> addFactory(noinline factory: () -> T): Builder {
            return addFactory(T::class.java, factory)
        }

        fun build(): ServiceLocator {
            return ServiceLocator(singletons, factories)
        }
    }

    companion object {
        fun builder(): Builder = Builder()
    }
}