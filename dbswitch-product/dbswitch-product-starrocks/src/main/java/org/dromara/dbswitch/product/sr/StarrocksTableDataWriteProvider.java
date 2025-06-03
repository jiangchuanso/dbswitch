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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dromara.dbswitch.common.entity.CloseableDataSource;
import org.dromara.dbswitch.core.provider.ProductFactoryProvider;
import org.dromara.dbswitch.core.provider.write.DefaultTableDataWriteProvider;

import java.util.List;

@Slf4j
public class StarrocksTableDataWriteProvider extends DefaultTableDataWriteProvider {


    private final CloseableDataSource dataSource;
    private final StarRocksUtils starRocksUtils = new StarRocksUtils();
    ;

    public StarrocksTableDataWriteProvider(ProductFactoryProvider factoryProvider) {
        super(factoryProvider);
        dataSource = (CloseableDataSource) factoryProvider.getDataSource();

    }

    @Override
    public void prepareWrite(String schemaName, String tableName, List<String> fieldNames) {
        starRocksUtils.init(schemaName, tableName, dataSource);
    }

    @Override
    public long write(List<String> fieldNames, List<Object[]> recordValues) {
        if (CollectionUtils.isEmpty(fieldNames) || CollectionUtils.isEmpty(recordValues)) {
            return 0L;
        }

        return starRocksUtils.addOrUpdateData(fieldNames, recordValues);

    }
    

}
