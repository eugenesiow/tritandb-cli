package com.tritandb.cli

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

/**
 * TritanDb
 * Created by eugene on 19/09/2017.
 */
class ClientParser(parser: ArgParser) {
    enum class Mode { INGEST, RANGE_QUERY, LIST }

    val mode by parser.mapping(
            "--ingest" to Mode.INGEST,
            "--query" to Mode.RANGE_QUERY,
            "--list" to Mode.LIST,
            help = "Command/Mode of Operation"
    )
    val serverHost by parser.storing("-H", "--host",
            help = "Server Host e.g. localhost or 192.168.0.100").default("localhost")
    val serverPort by parser.storing("-P", "--port",
            help = "Server Port e.g. 5700").default("5700")
    val query by parser.storing("-Q", "--query_syn",
            help = "Query Syntax").default(null)
    val fileInput by parser.storing("-F", "--file",
            help = "Input Query or Ingestion File").default(null)
//    val sourceFile by parser.positional("SOURCE", help = "source filename").default("")
}