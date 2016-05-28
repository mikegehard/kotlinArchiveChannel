package io.github.mikegehard.slack

import okhttp3.HttpUrl


// Host - hostname:port, where port is optional
data class SlackHost(val hostPort: String, val secure: Boolean, val token: String) {
    private val parts: List<String>
        get() = hostPort.split(":")

    private val host: String
        get() = parts[0]

    private val port: Int?
        get() = if (parts.size > 1) {
            try {
                parts[1].toInt()
            } catch (e: NumberFormatException) {
                null
            }
        } else {
            null
        }

    private val scheme: String
        get() = if (secure) "https" else "http"

    private fun addPort(builder: HttpUrl.Builder): HttpUrl.Builder {
        val port = this.port
        if (port == null) {
            return builder
        } else {
            return builder.port(port)
        }
    }

    val apiUrl: HttpUrl
        get() {
            val builder = HttpUrl.Builder()

            builder.apply {
                scheme(scheme)
                host(host)
                addPathSegment("api")
                addQueryParameter("token", token)
            }

            return addPort(builder).build()
        }
}
