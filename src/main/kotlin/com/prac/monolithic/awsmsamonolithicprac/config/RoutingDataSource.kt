package com.prac.monolithic.awsmsamonolithicprac.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.transaction.support.TransactionSynchronizationManager
import javax.sql.DataSource

class RoutingDataSource : AbstractRoutingDataSource() {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun determineCurrentLookupKey(): Any = TransactionSynchronizationManager.isCurrentTransactionReadOnly().run {
        if (this) "reader" else "writer"
    }

    public override fun determineTargetDataSource(): DataSource {
        return super.determineTargetDataSource()
    }
}