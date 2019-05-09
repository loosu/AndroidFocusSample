package com.loosu.utils.log

import android.util.Log
import com.loosu.utils.log.cache.DiskLogCache
import com.loosu.utils.log.cache.LogCache


class KLog private constructor() {
    // Util class no instance.

    companion object {
        private const val DEFAULT_TAG = "KLog"
        private const val DEFAULT_MSG = ""
        private val sCache: LogCache? = DiskLogCache()

        fun v(tag: Any? = null, msg: Any? = null, tr: Throwable? = null) {
            printLog(Level.V, tag, msg, tr)
        }

        fun d(tag: Any? = null, msg: Any? = null, tr: Throwable? = null) {
            printLog(Level.D, tag, msg, tr)
        }

        fun i(tag: Any? = null, msg: Any? = null, tr: Throwable? = null) {
            printLog(Level.I, tag, msg, tr)
        }

        fun w(tag: Any? = null, msg: Any? = null, tr: Throwable? = null) {
            printLog(Level.W, tag, msg, tr)
        }

        fun e(tag: Any? = null, msg: Any? = null, tr: Throwable? = null) {
            printLog(Level.E, tag, msg, tr)
        }

        fun printLog(level: Level, tagObj: Any? = null, msgObj: Any? = null, throwable: Throwable? = null) {

            // info about trace
            val traceElement = Thread.currentThread().stackTrace[5]
            val methodName = traceElement.methodName
            val fileName = traceElement.fileName
            val lineNumber = traceElement.lineNumber

            // tag
            //val tag = tagObj?.toString() ?: DEFAULT_TAG
            val tag = DEFAULT_TAG

            // msg
            val msg = "($fileName:$lineNumber)$methodName : ${msgObj ?: DEFAULT_MSG}"

            // real log msgObj
            when (level) {
                Level.V -> Log.v(tag, msg, throwable)
                Level.D -> Log.d(tag, msg, throwable)
                Level.I -> Log.i(tag, msg, throwable)
                Level.W -> Log.w(tag, msg, throwable)
                Level.E -> Log.e(tag, msg, throwable)
                else -> Log.wtf(tag, msg, throwable)
            }

            sCache?.save(level, tag, msg, throwable)
        }
    }
}