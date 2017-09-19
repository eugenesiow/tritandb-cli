package com.tritandb.cli

import com.tritandb.cli.ClientParser.Mode
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.DefaultHelpFormatter
import mu.KLoggable
import org.zeromq.ZMQ
import com.tritandb.engine.tsc.data.EventProtos.TritanEvent.EventType.*
import com.tritandb.engine.tsc.data.buildTritanEvent
import com.xenomachina.argparser.mainBody
import kotlin.system.measureTimeMillis

/**
 * TritanDb
 * Created by eugene on 19/09/2017.
 */
class ClientCli {
    companion object: Any(), KLoggable {
        override val logger = logger()
        @JvmStatic fun main(args: Array<String>) = mainBody("tritandb-cli") {
            logger.info("starting cli...")
            val parser = ArgParser(args, ArgParser.Mode.GNU, DefaultHelpFormatter())
            val cli = ClientParser(parser)

            val context = ZMQ.context(2)
            val sender = context.socket(ZMQ.PUSH)
            sender.connect("tcp://localhost:5700")

            val receiver = context.socket(ZMQ.PULL)
            receiver.bind("tcp://localhost:5800")

            when (cli.mode) {
                Mode.LIST -> LogTime { ListTimeSeries(sender,receiver) }
            }

            receiver.close()
            sender.close()
            context.close()
        }

        private fun ListTimeSeries(sender: ZMQ.Socket,receiver: ZMQ.Socket) {
            sender.send(buildTritanEvent {
                type = INSERT_META
                name = "list"
            }.toByteArray())
            println("TIMESERIES\n==========")
            val msg = receiver.recvStr()
            println(msg)
        }

        private fun LogTime(block: () -> Unit) {
            val time = measureTimeMillis{ block() }
//            logger.info("executed block in $time ms")
            println("execution time: $time ms")
        }
    }
}