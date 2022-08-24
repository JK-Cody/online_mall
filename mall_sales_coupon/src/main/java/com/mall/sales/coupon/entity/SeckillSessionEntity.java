package com.mall.sales.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 秒杀活动场次
 * 
 * @author myself
 * @email myself@gmail.com
 * @date 2022-01-12 19:07:43
 */
@Data
@TableName("sms_seckill_session")
public class SeckillSessionEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableField(exist = false)
	private List<SeckillSkuRelationEntity> seckillSkuRelationList;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 场次名称
	 */
	private String name;
	/**
	 * 每日开始时间
	 */
	private Date startTime;
	/**
	 * 每日结束时间
	 */
	private Date endTime;
	/**
	 * 启用状态
	 */
	private Integer status;
	/**
	 * 创建时间
	 */
	private Date createTime;

}
