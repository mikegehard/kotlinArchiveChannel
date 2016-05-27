package io.github.mikegehard.slack


// Host - hostname:port, where port is optional
data class SlackHost(val hostPort: String, val secure: Boolean, val token: String) {
    val host: String
        get() = hostPort.split(":")[0]

    val scheme: String
        get() = if (secure) "https" else "http"

    val port: Int
        get() {
            val parts = hostPort.split(":")

            if (parts.size == 1) {
                return if (secure) 443 else 80
            }

            try {
                return parts[1].toInt()
            } catch(e: NumberFormatException) {
                return if (secure) 443 else 80
            }
        }
}
