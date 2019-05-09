package com.loosu.utils

import java.io.Closeable
import java.io.IOException

class CloseUtil {
    companion object {
        fun close(closeable: Closeable?) {
            try {
                closeable?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}