package com.bihnerdranch.example.messenger.data

import android.provider.BaseColumns

class TableForChat private constructor() {
    object Chat : BaseColumns {
        const val NAME = "tableOFKey"
        const val ID = BaseColumns._ID
    }
}