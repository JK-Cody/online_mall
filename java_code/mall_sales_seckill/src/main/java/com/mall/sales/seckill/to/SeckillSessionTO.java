package com.mall.sales.seckill.to;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * SeckillSessionEntity的TO
 */
@Data
public class SeckillSessionTO {

    private List<SeckillSkuRelationTO> seckillSkuRelationList;
    private Long id;
    private String name;
    private Date startTime;
    private Date endTime;
    private Integer status;
    private Date createTime;

}
