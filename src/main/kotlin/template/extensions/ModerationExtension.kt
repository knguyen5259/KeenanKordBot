package template.extensions

import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescingDefaultingString
import com.kotlindiscord.kord.extensions.commands.converters.impl.duration
import com.kotlindiscord.kord.extensions.commands.converters.impl.member
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.common.entity.Permission
import dev.kord.core.behavior.ban
import dev.kord.core.behavior.edit
import dev.kord.rest.builder.message.create.embed
import dev.kord.rest.request.RestRequestException
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

class ModerationExtension : Extension() {
    override val name: String = "Moderation"

    override suspend fun setup() {

        chatCommand(::PunishmentArgs) {
            name = "ban"
            description = "Ban a member"

            check {
                hasPermission(Permission.BanMembers)
            }

            action {
                try {
                    arguments.member.ban {
                        reason = if (arguments.reason == "null") null else arguments.reason
                    }

                    message.respond {
                        embed {
                            title = "Banned"
                            description = "${arguments.member.mention} has been banned."
                            if (arguments.reason != "null") {
                                field {
                                    name = "Reason"
                                    value = arguments.reason
                                }
                            }
                        }
                    }
                } catch (_: RestRequestException) {
                    message.respond {
                        embed {
                            title = "Error"
                            description = "Failed to ban ${arguments.member.mention}."
                            field {
                                name = "Reason"
                                value = "Missing permissions."
                            }
                        }
                    }
                }
            }
        }

        chatCommand(::PunishmentArgs) {
            name = "kick"
            description = "Kick a member"

            check {
                hasPermission(Permission.KickMembers)
            }

            action {
                try {
                    arguments.member.kick(if (arguments.reason == "null") null else arguments.reason)

                    message.respond {
                        embed {
                            title = "Kicked"
                            description = "${arguments.member.mention} has been kicked."
                            if (arguments.reason != "null") {
                                field {
                                    name = "Reason"
                                    value = arguments.reason
                                }
                            }
                        }
                    }
                } catch (_: RestRequestException) {
                    message.respond {
                        embed {
                            title = "Error"
                            description = "Failed to kick ${arguments.member.mention}."
                            field {
                                name = "Reason"
                                value = "Missing permissions."
                            }
                        }
                    }
                }
            }
        }

        chatCommand(::MuteArgs) {
            name = "mute"
            description = "Mute a member"

            check {
                hasPermission(Permission.MuteMembers)
            }

            action {
                try {
                    arguments.member.edit {
                        communicationDisabledUntil = Clock.System.now()
                            .plus(arguments.duration.nanoseconds, DateTimeUnit.NANOSECOND, TimeZone.UTC)
                            .plus(arguments.duration.seconds, DateTimeUnit.SECOND, TimeZone.UTC)
                            .plus(arguments.duration.minutes, DateTimeUnit.MINUTE, TimeZone.UTC)
                            .plus(arguments.duration.hours, DateTimeUnit.HOUR, TimeZone.UTC)
                            .plus(arguments.duration.days, DateTimeUnit.DAY, TimeZone.UTC)
                            .plus(arguments.duration.months, DateTimeUnit.MONTH, TimeZone.UTC)
                            .plus(arguments.duration.years, DateTimeUnit.YEAR, TimeZone.UTC)
                        reason = if (arguments.reason == "null") null else arguments.reason
                    }

                    message.respond {
                        embed {
                            title = "Muted"
                            description = "${arguments.member.mention} has been muted."
                            if (arguments.reason != "null") {
                                field {
                                    name = "Reason"
                                    value = arguments.reason
                                }
                            }
                        }
                    }
                } catch (_: RestRequestException) {
                    message.respond {
                        embed {
                            title = "Error"
                            description = "Failed to mute ${arguments.member.mention}."
                            field {
                                name = "Reason"
                                value = "Missing permissions."
                            }
                        }
                    }
                }
            }
        }
    }

    inner class PunishmentArgs: Arguments() {
        val member by member {
            name = "member"
            description = "The member you want to punish"
        }

        val reason by coalescingDefaultingString {
            name = "reason"
            defaultValue = "null"
            description = "The reason for the punishment"
        }
    }

    inner class MuteArgs: Arguments() {
        val member by member {
            name = "member"
            description = "The member you want to punish"
        }

        val duration by duration {
            name = "duration"
            description = "The length of the mute"
        }

        val reason by coalescingDefaultingString {
            name = "reason"
            defaultValue = "null"
            description = "The reason for the punishment"
        }
    }
}