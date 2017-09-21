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
import java.io.File
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
            logger.info("starting cli for $inetAddress...")
            val parser = ArgParser(args, ArgParser.Mode.GNU, DefaultHelpFormatter())
            val cli = ClientParser(parser)

            val context = ZMQ.context(2)
            val sender = context.socket(ZMQ.PUSH)
            sender.connect("tcp://${cli.serverHost}:${cli.serverPort}")

            val receiver = context.socket(ZMQ.PULL)
            receiver.bind("tcp://$inetAddress:5800")

            when (cli.mode) {
                Mode.LIST -> LogTime { ListTimeSeries(sender,receiver) }
                Mode.RANGE_QUERY -> {
                    var queryInput = cli.query
                    if(cli.fileInput!=null) {
                        val f = File(cli.fileInput)
                        if(f.exists())
                            queryInput = f.readText()
                    }
                    if(queryInput!=null)
                        LogTime { ExecuteQuery(sender,receiver,queryInput!!) }
                    else
                        throw(Exception("Null query input."))
                }
                else -> println("No command specified. Please specify a command e.g. --ingest or --query.")
            }

            receiver.close()
            sender.close()
            context.close()
        }

        private fun ExecuteQuery(sender: ZMQ.Socket, receiver: ZMQ.Socket, query:String) {
            val event = buildTritanEvent {
                type = EventProtos.TritanEvent.EventType.QUERY
                name = query
                address = inetAddress
            }
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