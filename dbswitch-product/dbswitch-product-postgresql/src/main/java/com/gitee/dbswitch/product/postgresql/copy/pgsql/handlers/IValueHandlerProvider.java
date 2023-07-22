package com.gitee.dbswitch.product.postgresql.copy.pgsql.handlers;

import com.gitee.dbswitch.product.postgresql.copy.pgsql.constants.DataType;

public interface IValueHandlerProvider {

  <TTargetType> IValueHandler<TTargetType> resolve(DataType targetType);
}
