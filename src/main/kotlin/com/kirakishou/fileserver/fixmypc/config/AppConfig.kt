package com.kirakishou.fileserver.fixmypc.config

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.kirakishou.fileserver.fixmypc.log.FileLog
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class AppConfig {

    @Bean
    open fun kotlinModule() = KotlinModule()

    @Bean
    @Throws(Exception::class)
    fun containerCustomizer(): EmbeddedServletContainerCustomizer {
        return EmbeddedServletContainerCustomizer { container ->
            if (container is TomcatEmbeddedServletContainerFactory) {
                container.addConnectorCustomizers(TomcatConnectorCustomizer { connector ->
                    connector.maxPostSize = 10000000
                })
            }
        }
    }

    @Bean
    fun provideFileLog(): FileLog {
        return FileLog()
    }
}