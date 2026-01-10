package com.desynatuser.ui.home.chat.response

data class ChatMessage(
    var by: String? = null,
    var id: String? = null,
    var message: String? = null,
    var times: String? = null,
    var type: String? = null,
    var unique_id: String? = null,
    var user_id: Long? = null,
    var read_status: Int? = 0
)