package com.svetikov.bot20222

import com.github.s7connector.api.S7Connector

data class PLCData(
    val name:String,
    val host:String,
    val rack:Int,
    val slot:Int,
    var connector: S7Connector? =null
) {
}