package com.yurazhovnir.myfeeling.model

open class Filing(
    var id: Int = 0,
    var doneAt: String? = null,
    var comment: String? = null,
    var emoji: String? = null,
    var startsAt: String? = null,
    var dueAt: String? = null,
    var hydration: Double? = null,
    var steps: Int? = null,
    var sleep: Int? = null,
    var userId: Int? = null
)