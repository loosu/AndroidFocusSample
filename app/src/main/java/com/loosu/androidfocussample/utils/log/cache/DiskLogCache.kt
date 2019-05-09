package com.loosu.utils.log.cache

import android.os.Environment
import android.text.TextUtils
import com.loosu.utils.CloseUtil
import com.loosu.utils.ThrowableHelper
import com.loosu.utils.log.Level
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

class DiskLogCache : LogCache() {

    private val DATA_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.US)
    private val LOG_DIR_PATH = Environment.getExternalStorageDirectory().absolutePath + "/com.loosu.kcamera/log/"
    private val sCaches: ArrayList<String> = ArrayList()


    override fun needSave(level: Level): Boolean {
        return when (level) {
            Level.D, Level.I, Level.W, Level.E -> true
            else -> false
        }
    }

    override fun onSave(level: Level, tag: String, msg: String?, throwable: Throwable?) {
        sCaches.add("${DATA_FORMAT.format(System.currentTimeMillis())} $level $msg ${getTraceStr(throwable)}")    // string format: "1990-01-01 00:00:00:00 D msg"

        if (sCaches.size >= 50) {
            val temp = ArrayList<String>()
            temp.addAll(sCaches)
            sCaches.clear()
            realSave(temp)
        }
    }

    private fun realSave(logs: List<String>) {
//        Observable.create<String> { emitter ->
//            for (str in logs) {
//                emitter.onNext(str)
//            }
//            emitter.onComplete()
//
//        }.observeOn(Schedulers.io())
//                .map { "$it\n" }
//                .subscribe(object : Observer<String> {
//                    var fos: FileOutputStream? = null
//                    var bos: BufferedWriter? = null
//
//                    override fun onSubscribe(d: Disposable) {
//                        val dir = File(LOG_DIR_PATH)
//                        if (!dir.exists()) {
//                            dir.mkdirs()
//                        }
//                        val logFile = File("${LOG_DIR_PATH}log${System.currentTimeMillis()}.txt")
//                        fos = FileOutputStream(logFile, true)
//                        bos = BufferedWriter(OutputStreamWriter(fos))
//                    }
//
//                    override fun onNext(text: String) {
//                        bos!!.write(text)
//                    }
//
//                    override fun onComplete() {
//                        bos!!.flush()
//                        CloseUtil.close(bos)
//                        CloseUtil.close(fos)
//                    }
//
//                    override fun onError(e: Throwable) {
//                        CloseUtil.close(bos)
//                        CloseUtil.close(fos)
//                    }
//                })
    }

    private fun getTraceStr(throwable: Throwable?): String? {
        val traceStr = ThrowableHelper.getTraceStr(throwable)
        return if (TextUtils.isEmpty(traceStr)) {
            null
        } else {
            "\n\t$traceStr"
        }
    }
}