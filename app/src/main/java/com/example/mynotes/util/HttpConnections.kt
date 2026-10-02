package com.example.mynotes.util

import java.net.HttpURLConnection
import java.net.URL

internal inline fun <T> URL.withHttpConnection(
    configure: HttpURLConnection.() -> Unit = {},
    block: (HttpURLConnection) -> T
): T {
    val connection = openConnection() as HttpURLConnection
    return try {
        connection.configure()
        block(connection)
    } finally {
        connection.disconnect()
    }
}
