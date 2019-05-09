package com.loosu.utils.log.cache

import com.loosu.utils.log.Level

abstract class LogCache {

    /**
     * 是否缓存该log
     * @param level log 等级
     * @return true: 缓存; false：不缓存
     */
    protected open fun needSave(level: Level): Boolean = true

    fun save(level: Level, tag: String, msg: String? = null, throwable: Throwable? = null) {
        if (needSave(level)) {
            onSave(level, tag, msg, throwable)
        }
    }

    abstract fun onSave(level: Level, tag: String, msg: String? = null, throwable: Throwable? = null)
}