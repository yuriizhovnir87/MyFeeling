package com.yurazhovnir.myfeeling.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class User : RealmObject {
    @PrimaryKey
    var id: Int = 0
    var name: String? = ""

    var email: String? = ""
    var password: String? = ""
    var subscribe: String? = ""

    var startsReminderId: Int? = 0
    var dueAt: String? = null
        get() = field ?: ""
}