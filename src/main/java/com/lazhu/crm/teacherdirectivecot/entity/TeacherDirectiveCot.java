package com.lazhu.crm.teacherdirectivecot.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.lazhu.core.base.BaseModel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 	公司虚拟盘
 * </p>
 *
 * @author taoyang
 * @since 2018-11-16
 */
@ApiModel(value = "",description="teacherDirectiveCot")
@TableName("crm_teacher_directive_cot")
public class TeacherDirectiveCot extends BaseModel {
    private static final long serialVersionUID = 1L;
    
 // 盈利人数 
    @TableField(exist = false)
    private Integer payCount;
    // 亏损人数 
    @TableField(exist = false)
    private Integer lossCount;
    
    //customer_id 客户ID 
    @TableField(exist = false)
    private Object ids;
    //customer_name 客户姓名 
    @TableField(exist = false)
    private Object names;
    //userMoney 客户姓名 
    @TableField(exist = false)
    private Object useMoneys;
    
    //customer_adviser_record_id 客户顾问老师ID 
    @ApiModelProperty(value = "客户顾问老师ID", required = false)
    @TableField("customer_adviser_record_id")
    private Long customerAdviserRecordId;
    //servicer_id 客服id 
    @ApiModelProperty(value = "客服id", required = false)
    @TableField("servicer_id")
    private Long servicerId;
    //servicer_name 客服名称 
    @ApiModelProperty(value = "客服名称", required = false)
    @TableField("servicer_name")
    private String servicerName;
    //customer_id 客户id 
    @ApiModelProperty(value = "客户id", required = false)
    @TableField("customer_id")
    private Long customerId;
    //customer_name 客户姓名 
    @ApiModelProperty(value = "客户姓名", required = false)
    @TableField("customer_name")
    private String customerName;
    //stock_code 股票代码 
    @ApiModelProperty(value = "股票代码", required = false)
    @TableField("stock_code")
    private String stockCode;
    //stock_name 股票名称 
    @ApiModelProperty(value = "股票名称", required = false)
    @TableField("stock_name")
    private String stockName;
    //stock_source_type 股票类型 
    @ApiModelProperty(value = "股票类型", required = false)
    @TableField("stock_source_type")
    private Integer stockSourceType;
    //cot_num 持仓数量 
    @ApiModelProperty(value = "持仓数量", required = false)
    @TableField("cot_num")
    private Long cotNum;
    //cot_price 成本价格 
    @ApiModelProperty(value = "成本价格", required = false)
    @TableField("cot_price")
    private Double cotPrice;
    //cot_moeny 成本金额 
    @ApiModelProperty(value = "成本金额", required = false)
    @TableField("cot_money")
    private Double cotMoney;
    //current_price 当前价格 
    @ApiModelProperty(value = "当前价格", required = false)
    @TableField("current_price")
    private Double currentPrice;
    //current_moeny 当前金额 
    @ApiModelProperty(value = "当前金额", required = false)
    @TableField("current_money")
    private Double currentMoney;
    //loss_patio 盈亏比 
    @ApiModelProperty(value = "盈亏比", required = false)
    @TableField("loss_patio")
    private Double lossPatio;
    //loss_money 盈亏金额 
    @ApiModelProperty(value = "盈亏金额", required = false)
    @TableField("loss_money")
    private Double lossMoney;
    //position 仓位 
    @ApiModelProperty(value = "仓位", required = false)
    @TableField("position")
    private Double position;
    //index_value 买入次数 
    @ApiModelProperty(value = "买入次数", required = false)
    @TableField("index_value")
    private Integer indexValue;
    //update_date 更新日期 
    @ApiModelProperty(value = "更新日期", required = false)
    @TableField("update_date")
    private Date updateDate;
  //use_money 可用资金
    @ApiModelProperty(value = "可用资金", required = false)
    @TableField("use_money")
    private Double useMoney;
    //direction_type 操作类型 0--建仓   1---买入  2---卖出
    @ApiModelProperty(value = "操作类型", required = false)
    @TableField("direction_type")
    private Integer directionType;
    
    public Integer getPayCount() {
		return payCount;
	}

	public void setPayCount(Integer payCount) {
		this.payCount = payCount;
	}

	public Integer getLossCount() {
		return lossCount;
	}

	public void setLossCount(Integer lossCount) {
		this.lossCount = lossCount;
	}

	public Object getIds() {
		return ids;
	}

	public void setIds(Object ids) {
		this.ids = ids;
	}

	public Object getNames() {
		return names;
	}

	public void setNames(Object names) {
		this.names = names;
	}

	public Object getUseMoneys() {
		return useMoneys;
	}

	public void setUseMoneys(Object useMoneys) {
		this.useMoneys = useMoneys;
	}

	public Integer getDirectionType() {
		return directionType;
	}

	public void setDirectionType(Integer directionType) {
		this.directionType = directionType;
	}

	public void setCustomerAdviserRecordId(Long customerAdviserRecordId)
    {
        this.customerAdviserRecordId = customerAdviserRecordId;
    }

    public Long getCustomerAdviserRecordId()
    {
        return this.customerAdviserRecordId;
    }

    public void setServicerId(Long servicerId)
    {
        this.servicerId = servicerId;
    }

    public Long getServicerId()
    {
        return this.servicerId;
    }

    public void setServicerName(String servicerName)
    {
        this.servicerName = servicerName;
    }

    public String getServicerName()
    {
        return this.servicerName;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public Long getCustomerId()
    {
        return this.customerId;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    public String getCustomerName()
    {
        return this.customerName;
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

    public void setStockSourceType(Integer stockSourceType)
    {
        this.stockSourceType = stockSourceType;
    }

    public Integer getStockSourceType()
    {
        return this.stockSourceType;
    }

    public void setCotNum(Long cotNum)
    {
        this.cotNum = cotNum;
    }

    public Long getCotNum()
    {
        return this.cotNum;
    }

    public void setCotPrice(Double cotPrice)
    {
        this.cotPrice = cotPrice;
    }

    public Double getCotPrice()
    {
        return this.cotPrice;
    }


    public void setCurrentPrice(Double currentPrice)
    {
        this.currentPrice = currentPrice;
    }

    public Double getCurrentPrice()
    {
        return this.currentPrice;
    }
    
    public Double getCotMoney() {
		return cotMoney;
	}

	public void setCotMoney(Double cotMoney) {
		this.cotMoney = cotMoney;
	}

	public Double getCurrentMoney() {
		return currentMoney;
	}

	public void setCurrentMoney(Double currentMoney) {
		this.currentMoney = currentMoney;
	}

	public void setLossPatio(Double lossPatio)
    {
        this.lossPatio = lossPatio;
    }

    public Double getLossPatio()
    {
        return this.lossPatio;
    }

    public void setLossMoney(Double lossMoney)
    {
        this.lossMoney = lossMoney;
    }

    public Double getLossMoney()
    {
        return this.lossMoney;
    }

    public void setPosition(Double position)
    {
        this.position = position;
    }

    public Double getPosition()
    {
        return this.position;
    }

    public void setIndexValue(Integer indexValue)
    {
        this.indexValue = indexValue;
    }

    public Integer getIndexValue()
    {
        return this.indexValue;
    }

    public void setUpdateDate(Date updateDate)
    {
        this.updateDate = updateDate;
    }

    public Date getUpdateDate()
    {
        return this.updateDate;
    }

	public Double getUseMoney() {
		return useMoney;
	}

	public void setUseMoney(Double useMoney) {
		this.useMoney = useMoney;
	}

}
