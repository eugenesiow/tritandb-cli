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
            help = "mode of operation"
    ).default(Mode.RANGE_QUERY)
//    val dataset by parser.storing("-d", "--dataset",
//            help = "dataset") { toLowerCase() }
//    val sourceFile by parser.positional("SOURCE", help = "source filename").default("")
}