package com.svetikov.bot20222

import com.github.s7connector.api.DaveArea
import com.github.s7connector.api.S7Connector
import com.github.s7connector.api.factory.S7ConnectorFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okio.utf8Size
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
@EnableScheduling
class PLCLaunch(private val service: BotService) {

    //todo new
    val listPLC = listOf<PLCData>(

        PLCData("PLC_N", "172.20.4.2", 0, 2, null),
        PLCData("PU_10", "172.20.1.2", 0, 3, null),
        PLCData("PU_40", "172.20.2.5", 0, 3, null),
        PLCData("PLC_P", "172.20.4.3", 0, 3, null),
        PLCData("PLC_M", "172.20.4.4", 0, 2, null),
    )

    //todo new
    val listStatus = mutableListOf(false, false, false, false, false, false, false, false, false, false, false, false, false,)


    //todo new
    init {
GlobalScope.launch {
    service.sendMessToGroup("Reload program")
}


        initPLCConnector(listPLC)
    }

    //todo new
    private fun initPLCConnector(listPLC: List<PLCData>) {
        for (plc in listPLC) {
            plc.connector = S7ConnectorFactory
                .buildTCPConnector()
                .withHost(plc.host)
                .withRack(plc.rack)
                .withSlot(plc.slot)
                .build()
        }
    }

    //todo new
    private fun getBit(connector: S7Connector, db: Int, byte: Int, bit: Int): Boolean {
        val maxBit = 7
        var calculateBit = 0
        var calculateByte = 0
        if (byte >= 0) {
            calculateByte = byte
        } else throw Exception("Byte can't be less then 0")
        if (bit in 0..7) {
            calculateBit = maxBit - bit
        } else throw Exception("Bit must be more then 0 and less than 8")
        var testByte: ByteArray = byteArrayOf(0xf)
        if (connector != null)
            testByte = connector.read(DaveArea.DB, db, 1, calculateByte)

        var bitStatus = testByte[0].plus(0xff00)
            .toUShort()
            .toString(2)
            .drop(8)[calculateBit]
      //  println("test ${bitStatus.javaClass}: $bitStatus")
        return bitStatus == '1'
    }



    @Scheduled(fixedDelay = 5000L)
    fun scannerData() {

        //todo new

        GlobalScope.launch {
            messageSend(
                !listPLC[0].connector?.let { getBit(it, 100, 0, 0) }!!,
                "Відкрився ніс на формлінії",
                "Закрився ніс на формлінії",
                0
            ) //todo PLC_N nose
            messageSend(
                listPLC[1].connector?.let { getBit(it, 100, 0, 0) }!!,
                "Запустився головний двигун -> ОКОРОВКИ",
                "Зупинився головний двигун -> ОКОРОВКИ",
                1
            )//todo PU_10 gen motor
            messageSend(
                listPLC[2].connector?.let { getBit(it, 100, 0, 0) }!!,
                "Запустився головний двигун -> ДЕФІБРАТОР",
                "Зупинився головний двигун -> ДЕФІБРАТОР",
                2
            )//todo PU_40 gen motor
            messageSend(
                listPLC[2].connector?.let { getBit(it, 100, 0, 1) }!!,
                "Запустився розвантажувальний шнек парова колона -> ДЕФІБРАТОР",
                "Зупинився розвантажувальний шнек парова колона -> ДЕФІБРАТОР",
                3
            )//todo PU_40 screew
            messageSend(
                listPLC[2].connector?.let { getBit(it, 100, 0, 2) }!!,
                "Дівертор в позиції виробництво -> ДЕФІБРАТОР",
                "Дівертор в позиції циклон -> ДЕФІБРАТОР",
                4
            )//todo PU_40 divertor
            messageSend(
                listPLC[3].connector?.let { getBit(it, 99, 0, 0) }!!,
                "Прес старт",
                "Прес стоп",
                5
            )//todo PLC_P Press
            messageSend(
                listPLC[3].connector?.let { getBit(it, 99, 0, 1) }!!,
                "Зона пил cтарт",
                "Зона пил стоп",
                6
            )//todo PLC_P Saw DDS
            messageSend(
                listPLC[3].connector?.let { getBit(it, 99, 0, 2) }!!,
                "Зона охолоджування старт",
                "Зона охолоджування стоп",
                7
            )//todo PLC_P Cooling  CS
            messageSend(
                listPLC[4].connector?.let { getBit(it, 100, 0, 0) }!!,
                "Головний мотор старт -> СУШКА ВОЛОКНА",
                "Головний мотор стоп -> СУШКА ВОЛОКНА",
                8
            )//todo PLC_E gen motor run
            messageSend(
                listPLC[4].connector?.let { getBit(it, 100, 0, 1) }!!,
                "Продукція ON -> СУШКА ВОЛОКНА",
                "Продукція OFF -> СУШКА ВОЛОКНА",
                9
            )//todo PLC_E production

        }//todo end launch courutine

    }

    private suspend fun messageSend(
        plcBitStatus: Boolean,
        messageON: String,
        messageOFF: String,
        index: Int
    ) {

        if (plcBitStatus && !listStatus[index]) {
            println(messageON)
            service.sendMessToGroup(messageON)
            listStatus[index] = true
        }
        if (!plcBitStatus && listStatus[index]) {
            println(messageOFF)
            service.sendMessToGroup(messageOFF)
            listStatus[index] = false
        }

    }


}