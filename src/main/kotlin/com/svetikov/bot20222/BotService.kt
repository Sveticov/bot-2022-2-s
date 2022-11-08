package com.svetikov.bot20222

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.ivmg.telegram.bot
import me.ivmg.telegram.dispatch
import me.ivmg.telegram.dispatcher.command
import me.ivmg.telegram.dispatcher.text
import me.ivmg.telegram.network.fold
import org.springframework.beans.factory.BeanCreationException


import org.springframework.stereotype.Service
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

@Service
class BotService {

    var bot = bot {
        token = "5786686686:AAGy4DHunxKpqim0CV9IbB6bONr2hevoW4o"
    }

    init {
     bot.startPolling()
    }

    suspend fun launchBot() {
        val text = "Hello"
        val bot = bot {


            token = "5786686686:AAGy4DHunxKpqim0CV9IbB6bONr2hevoW4o"
            dispatch {

                text { bot, u ->
                    bot.sendMessage(422698441, text)
                    println(u.message)
                }

            }
        }
        bot.startPolling()
    }

    suspend fun dispatcherApi(text: String): String {
        bot.sendMessage(422698441, text)

        return text

    }


    suspend fun sendMessToGroup(text:String){
        var urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s"
        val apiToken = "5786686686:AAGy4DHunxKpqim0CV9IbB6bONr2hevoW4o"
        val chatId = "-816026794"
        val text = text
        urlString = String.format(urlString, apiToken, chatId, text)
        val url = URL(urlString)
        val conn = url.openConnection()
        val inputStream = BufferedInputStream(conn.getInputStream())
        val br = BufferedReader(InputStreamReader(inputStream))
        val response = br.readText()
    }
}