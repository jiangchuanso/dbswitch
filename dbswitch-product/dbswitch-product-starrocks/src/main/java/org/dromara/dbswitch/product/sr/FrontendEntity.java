package org.dromara.dbswitch.product.sr;


import lombok.Data;

@Data
public class FrontendEntity {
    String ip;
    String httpport;
    Boolean alive;
    Boolean join;
    String role;
}
