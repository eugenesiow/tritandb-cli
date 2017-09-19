package com.tritandb.cli

import com.tritandb.cli.ClientParser.Mode
import com.tritandb.engine.tsc.data.EventProtos
import com.tritandb.engine.tsc.data.EventProtos.TritanEvent.EventType.INSERT_META
import com.tritandb.engine.tsc.data.buildRow
import com.tritandb.engine.tsc.data.buildRows
import com.tritandb.engine.tsc.data.buildTritanEvent
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.DefaultHelpFormatter
import com.xenomachina.argparser.mainBody
import mu.KLoggable
import org.zeromq.ZMQ
import java.net.InetAddress
import java.util.*
import kotlin.system.measureTimeMillis

/**
 * TritanDb
 * Created by eugene on 19/09/2017.
 */
class ClientCli {
    companion object: Any(), KLoggable {
        override val logger = logger()
        private val inetAddress = InetAddress.getLocalHost().hostAddress!!
        @JvmStatic fun main(args: Array<String>) = mainBody("tritandb-cli") {
            logger.info("starting cli for ${inetAddress}...")
            val parser = ArgParser(args, ArgParser.Mode.GNU, DefaultHelpFormatter())
            val cli = ClientParser(parser)

            val context = ZMQ.context(2)
            val sender = context.socket(ZMQ.PUSH)
            sender.connect("tcp://${cli.serverHost}:${cli.serverPort}")

            val receiver = context.socket(ZMQ.PULL)
            receiver.bind("tcp://${inetAddress}:5800")

            when (cli.mode) {
                Mode.LIST -> LogTime { ListTimeSeries(sender,receiver) }
                Mode.RANGE_QUERY -> LogTime { ExecuteQuery(sender,receiver,cli.query) }
                else -> println("No command specified. Please specify a command e.g. --ingest or --query.")
            }

            receiver.close()
            sender.close()
            context.close()
        }

        private fun ExecuteQuery(sender: ZMQ.Socket, receiver: ZMQ.Socket, query:String?) {
            val fixedSeed = 100L
            val rand = Random(fixedSeed)
            val max = 1406141325958
            val min = 1271692742104
            val range = ((max + 1 - min )/100).toInt()

            val a = (rand.nextInt(range))*100L + min
            val b = (rand.nextInt(range))*100L + min
            var start = a
            var end = b
            if(a>b) {
                start = b
                end = a
            }
            val event = buildTritanEvent {
                type = EventProtos.TritanEvent.EventType.QUERY
                name = "shelburne"
                address = inetAddress
                rows = buildRows {
                    addRow(buildRow {
                        timestamp = start
                        addValue(end)
                    })
                }
            }
            //SELECT * WHERE { FILTER(?time>="2010-06-27T02:20:08.704" && ?time<"2011-08-17T06:45:29.504) }"
//            println("$start $end")
            sender.send(event.toByteArray())
            while (!Thread.currentThread().isInterrupted) {
                val msg = receiver.recvStr()
                if(msg=="end") {
                    break
                }
                println(msg)
            }
        }

        private fun ListTimeSeries(sender: ZMQ.Socket,receiver: ZMQ.Socket) {
            sender.send(buildTritanEvent {
                type = INSERT_META
                address = inetAddress
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