// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.common.type;

/**
 * 处理大小写转换的枚举类
 *
 * @author tang
 */
public enum CaseConvertEnum {
  /**
   * 不做任何处理
   */
  NONE(s -> s),
  /**
   * 全大写
   */
  UPPER(String::toUpperCase),
  /**
   * 全小写
   */
  LOWER(String::toLowerCase),
  /**
   * 驼峰转下划线
   */
  SNAKE(s -> {
    StringBuilder snakeCaseString = new StringBuilder(s);
    for (int i = 1; i < snakeCaseString.length(); i++) {
      if (Character.isUpperCase(snakeCaseString.charAt(i))) {
        snakeCaseString.insert(i, '_');
        i++;
      }
    }
    return snakeCaseString.toString().toLowerCase();
  }),
  /**
   * 下划线转驼峰
   */
  CAMEL(s -> {
    String[] parts = s.split("_");
    StringBuilder camelCaseString = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      String part = parts[i];
      if (i == 0) {
        camelCaseString.append(part.toLowerCase());  // 第一个单词保持全小写
      } else {
        camelCaseString.append(Character.toUpperCase(part.charAt(0)));  // 其他单词首字母大写
        camelCaseString.append(part.substring(1).toLowerCase());  // 其他部分小写
      }
    }
    return camelCaseString.toString();
  });

  private Converter function;

  CaseConvertEnum(Converter function) {
    this.function = function;
  }

  public String convert(String name) {
    return function.convert(name);
  }

  interface Converter {

    String convert(String s);
  }

}
