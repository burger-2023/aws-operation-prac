package com.prac.monolithic.awsmsamonolithicprac.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = ["com.prac.monolithic.awsmsamonolithicprac.repository"])
class DataSourceConfig {

    @Bean("writeDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.write")
    fun writeDataSource(): DataSource = DataSourceBuilder.create().type(HikariDataSource::class.java).build()

    @Bean("readDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.read")
    fun readDataSource(): DataSource = DataSourceBuilder.create().type(HikariDataSource::class.java).build()

    @Bean
    @Primary
    @DependsOn("writeDataSource", "readDataSource")
    fun routingDataSource(
        @Qualifier("writeDataSource") masterDataSource: DataSource,
        @Qualifier("readDataSource") slaveDataSource: DataSource
    ): DataSource {
        val routingDataSource = RoutingDataSource()
        val datasourceMap: Map<Any, Any> = object : HashMap<Any, Any>() {
            init {
                put("writer", masterDataSource)
                put("reader", slaveDataSource)
            }
        }
        routingDataSource.setTargetDataSources(datasourceMap)
        routingDataSource.setDefaultTargetDataSource(masterDataSource)

        return routingDataSource
    }

    @Bean
    @DependsOn("routingDataSource")
    fun dataSource(routingDataSource: DataSource): LazyConnectionDataSourceProxy {
        return LazyConnectionDataSourceProxy(routingDataSource)
    }
}