package com.sturdycode.catalog

import com.android.ddmlib.logcat.LogCatMessage
import com.android.utils.SparseArray

data class Records(val messages: List<LogCatMessage>, val starters: SparseArray<Starter>)