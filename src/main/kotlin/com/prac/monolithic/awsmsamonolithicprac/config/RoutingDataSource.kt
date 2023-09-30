package com.prac.monolithic.awsmsamonolithicprac.config

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.transaction.support.TransactionSynchronizationManager
import javax.sql.DataSource

class RoutingDataSource : AbstractRoutingDataSource() {
    override fun determineCurrentLookupKey(): Any = TransactionSynchronizationManager.isCurrentTransactionReadOnly().run {
        if (this) "reader" else "writer"
    }

    public override fun determineTargetDataSource(): DataSource {
        return super.determineTargetDataSource()
    }
}