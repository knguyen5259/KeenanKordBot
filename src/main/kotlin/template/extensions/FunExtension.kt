package template.extensions

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescingString
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.utils.respond

class FunExtension : Extension() {
    override val name: String = "Fun"

    override suspend fun setup() {
        chatCommand(::EchoArgs) {
            name = "echo"
            description = "Echos a message"

            action {
                message.respond {
                    content = arguments.message
                }
            }
        }

    }

    inner class EchoArgs : Arguments() {
        val message by coalescingString {
            name = "message"
            description = "The string you want to repeat"
        }
    }
}