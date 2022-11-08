package com.svetikov.bot20222

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class BotController(val service: BotService) {


    @GetMapping("/bot/{text}")
    suspend fun start1(@PathVariable text: String): String {
        return service.dispatcherApi(text)
    }


}