package com.loosu.utils

import java.io.PrintWriter
import java.io.StringWriter

class ThrowableHelper private constructor() {
    companion object {
        fun getTraceStr(t: Throwable?): String? {
            if (t == null) {
                return null
            }

            val stringWrite = StringWriter()
            val printWrite = PrintWriter(stringWrite)
            t.printStackTrace(printWrite)
            return stringWrite.buffer.toString()
        }
    }
}