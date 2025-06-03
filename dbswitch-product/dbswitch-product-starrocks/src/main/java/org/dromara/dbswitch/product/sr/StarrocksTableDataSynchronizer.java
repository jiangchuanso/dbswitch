// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package org.dromara.dbswitch.product.sr;


import org.apache.commons.collections4.CollectionUtils;
import org.dromara.dbswitch.common.entity.CloseableDataSource;
import org.dromara.dbswitch.core.provider.ProductFactoryProvider;
import org.dromara.dbswitch.core.provider.sync.DefaultTableDataSynchronizeProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StarrocksTableDataSynchronizer extends DefaultTableDataSynchronizeProvider {

    private volatile List<String> fieldNames;
    private final CloseableDataSource dataSource;

    private final StarRocksUtils starRocksUtils = new StarRocksUtils();


    public StarrocksTableDataSynchronizer(ProductFactoryProvider factoryProvider) {
        super(factoryProvider);
        dataSource = (CloseableDataSource) factoryProvider.getDataSource();
    }

    @Override
    public void prepare(String schemaName, String tableName, List<String> fieldNames, List<String> pks) {
        starRocksUtils.init(schemaName, tableName, dataSource);

        this.fieldNames = fieldNames;


        if (fieldNames.isEmpty() || pks.isEmpty() || fieldNames.size() < pks.size()) {
            throw new IllegalArgumentException("字段列表和主键列表不能为空，或者字段总个数应不小于主键总个数");
        }
        if (!fieldNames.containsAll(pks)) {
            throw new IllegalArgumentException("字段列表必须包含主键列表");
        }

        Map<String, Integer> columnType = getTableColumnMetaData(schemaName, tableName, fieldNames);
        this.fieldOrders = new ArrayList<>(fieldNames);
        this.pksOrders = new ArrayList<>(pks);

        this.insertStatementSql = getInsertPrepareStatementSql(schemaName, tableName, fieldNames);
        this.updateStatementSql = getUpdatePrepareStatementSql(schemaName, tableName, fieldNames, pks);
        this.deleteStatementSql = getDeletePrepareStatementSql(schemaName, tableName, pks);

        insertArgsType = new int[fieldNames.size()];
        for (int k = 0; k < fieldNames.size(); ++k) {
            String field = fieldNames.get(k);
            insertArgsType[k] = columnType.get(field);
        }

        updateArgsType = new int[fieldNames.size()];
        int idx = 0;
        for (int i = 0; i < fieldNames.size(); ++i) {
            String field = fieldNames.get(i);
            if (!pks.contains(field)) {
                updateArgsType[idx++] = columnType.get(field);
            }
        }
        for (String pk : pks) {
            updateArgsType[idx++] = columnType.get(pk);
        }

        deleteArgsType = new int[pks.size()];
        for (int j = 0; j < pks.size(); ++j) {
            String pk = pks.get(j);
            deleteArgsType[j] = columnType.get(pk);
        }
    }


    @Override
    public long executeInsert(List<Object[]> recordValues) {
        if (CollectionUtils.isEmpty(fieldNames) || CollectionUtils.isEmpty(recordValues)) {
            return 0L;
        }
        if (CollectionUtils.isEmpty(fieldNames) || CollectionUtils.isEmpty(recordValues)) {
            return 0L;
        }

        return starRocksUtils.addOrUpdateData(fieldNames, recordValues);
    }

    @Override
    public long executeUpdate(List<Object[]> recordValues) {
        if (CollectionUtils.isEmpty(fieldNames) || CollectionUtils.isEmpty(recordValues)) {
            return 0L;
        }
        if (CollectionUtils.isEmpty(fieldNames) || CollectionUtils.isEmpty(recordValues)) {
            return 0L;
        }

        return starRocksUtils.addOrUpdateData(fieldNames, recordValues);
    }


}
