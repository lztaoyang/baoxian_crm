package com.lazhu.crm.customercotoperationlog.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 	客户实际盘操作记录
 * </p>
 *
 * @author taoyang
 * @since 2018-11-16
 */
@ApiModel(value = "",description="customerCotOperationLog")
@TableName("crm_customer_cot_operation_log")
public class CustomerCotOperationLog extends BaseModel {
    private static final long serialVersionUID = 1L;
    //customer_actual_cot_id 客户实际操作ID 
    @ApiModelProperty(value = "客户实际操作ID", required = false)
    @TableField("customer_actual_cot_id")
    private Long customerActualCotId;
    //stock_code 股票代码 
    @ApiModelProperty(value = "股票代码", required = false)
    @TableField("stock_code")
    private String stockCode;
    //stock_name 股票名称 
    @ApiModelProperty(value = "股票名称", required = false)
    @TableField("stock_name")
    private String stockName;
    //direction_type 操作类型 0--建仓   1---买入  2---卖出
    @ApiModelProperty(value = "操作类型", required = false)
    @TableField("direction_type")
    private Integer directionType;
    //trade_num 交易数量 
    @ApiModelProperty(value = "交易数量", required = false)
    @TableField("trade_num")
    private Long tradeNum;
    //trade_price 交易价格 
    @ApiModelProperty(value = "交易价格", required = false)
    @TableField("trade_price")
    private Double tradePrice;
    //trade_money 交易金额 
    @ApiModelProperty(value = "交易金额", required = false)
    @TableField("trade_money")
    private Double tradeMoney;
    //deal_price 成交价格 
    @ApiModelProperty(value = "成本价格", required = false)
    @TableField("cot_price")
    private Double cotPrice;
    //deal_money 成交金额 
    @ApiModelProperty(value = "成本金额", required = false)
    @TableField("cot_money")
    private Double cotMoney;
    //then_loss_ratio 盈亏% 
    @ApiModelProperty(value = "盈亏%", required = false)
    @TableField("then_loss_ratio")
    private Double thenLossRatio;
    //then_loss_money 盈亏金额 
    @ApiModelProperty(value = "盈亏金额", required = false)
    @TableField("then_loss_money")
    private Double thenLossMoney;
    //update_date 更新日期 
    @ApiModelProperty(value = "更新日期", required = false)
    @TableField("update_date")
    private Date updateDate;

    public void setCustomerActualCotId(Long customerActualCotId)
    {
        this.customerActualCotId = customerActualCotId;
    }

    public Long getCustomerActualCotId()
    {
        return this.customerActualCotId;
    }

    public void setStockCode(String stockCode)
    {
        this.stockCode = stockCode;
    }

    public String getStockCode()
    {
        return this.stockCode;
    }

    public void setStockName(String stockName)
    {
        this.stockName = stockName;
    }

    public String getStockName()
    {
        return this.stockName;
    }

    public void setDirectionType(Integer directionType)
    {
        this.directionType = directionType;
    }

    public Integer getDirectionType()
    {
        return this.directionType;
    }

    public void setTradeNum(Long tradeNum)
    {
        this.tradeNum = tradeNum;
    }

    public Long getTradeNum()
    {
        return this.tradeNum;
    }

    public void setTradePrice(Double tradePrice)
    {
        this.tradePrice = tradePrice;
    }

    public Double getTradePrice()
    {
        return this.tradePrice;
    }

    public void setTradeMoney(Double tradeMoney)
    {
        this.tradeMoney = tradeMoney;
    }

    public Double getTradeMoney()
    {
        return this.tradeMoney;
    }
    
    public Double getCotPrice() {
		return cotPrice;
	}

	public void setCotPrice(Double cotPrice) {
		this.cotPrice = cotPrice;
	}

	public Double getCotMoney() {
		return cotMoney;
	}

	public void setCotMoney(Double cotMoney) {
		this.cotMoney = cotMoney;
	}

	public void setThenLossRatio(Double thenLossRatio)
    {
        this.thenLossRatio = thenLossRatio;
    }

    public Double getThenLossRatio()
    {
        return this.thenLossRatio;
    }

    public void setThenLossMoney(Double thenLossMoney)
    {
        this.thenLossMoney = thenLossMoney;
    }

    public Double getThenLossMoney()
    {
        return this.thenLossMoney;
    }

    public void setUpdateDate(Date updateDate)
    {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate()
    {
        return this.updateDate;
    }
}
