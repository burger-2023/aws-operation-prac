package com.prac.monolithic.awsmsamonolithicprac.config

import com.zaxxer.hikari.HikariDataSource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import java.sql.SQLException
import javax.sql.DataSource

@Configuration
class DataSourceUtils(
    private val dataSource: DataSource
) {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun getDatabaseUrl() {
        if (dataSource is RoutingDataSource) {
            val currentDataSource: DataSource = dataSource.determineTargetDataSource()
            try {
                val url = (currentDataSource as HikariDataSource).jdbcUrl
                log.info("[databaseURL = $url]")
            } catch (e: SQLException) {
                log.error("databaseURL unknown")
            }
        } else log.error("databaseURL unknown")
    }
}