package com.lazhu.crm;

import com.lazhu.ecp.utils.StrUtils;

/**
 * 常量类
 * 
 * @author hz
 * @date 2017年5月23日
 * @version 1.0.0
 */
public class Constant {

	/** 待开发资源流程 **/
	public static int FLOW_KF = 101;
	/** 未添加微信资源流程 **/
	public static int FLOW_WJ = 201;
	/** 可聊资源流程 **/
	public static int FLOW_KL = 301;
	/** 垃圾资源流程 **/
	public static int FLOW_LJ = 401;
	/** 黑名单资源流程 **/
	public static int FLOW_BLACK = 402;
	/** 已添加微信资源流程 **/
	public static int FLOW_YX = 501;
	/** 抽取资源流程 **/
	public static int FLOW_CQ = 601;
	/** 待审核资源流程 **/
	public static int FLOW_DSH = 801;
	/** 共享池资源流程 **/
	public static int FLOW_GXC = 901;
	/** 成交资源流程 **/
	public static int FLOW_CJ = 999;
	/** 成交资源流程 **/
	public static int FLOW_CJ_MORE = 1000;

	/** 会员正常流程 **/
	public static int CLUB_ZC = 501;
	/** 会员签单流程 **/
	public static int CLUB_QD = 301;

	/** 签单流程（待审核） **/
	public static int STATUS_DSH = 1;
	/** 签单流程（审核驳回） **/
	public static int STATUS_SHBH = 2;
	/** 签单流程（待合规） **/
	public static int STATUS_HG = 3;
	/** 签单流程（合规不通过） **/
	public static int STATUS_HGBH = 4;
	/** 签单流程（签单成功） **/
	public static int STATUS_SUCCESS = 5;

	/** 管理员（10000000） **/
	public static int ADMIN = 10000000;
	/** 业务员 **/
	public static int USER_GROUP_YWY = 88002001;
	/** 经理 **/
	public static int USER_GROUP_JL = 88002002;
	/** 总监 **/
	public static int USER_GROUP_ZJ = 88002003;
	/** 总经理 **/
	public static int USER_GROUP_ZJL = 88002005;

	/** 加保人员 **/
	public static int USER_GROUP_JB = 88002666;
	/** 加保经理 **/
	public static int USER_GROUP_JBJL = 88002667;
	/** 加保总监 **/
	public static int USER_GROUP_JBZJ = 88002668;
	/** 加保总经理 **/
	public static int USER_GROUP_JBZJL = 88002669;

	/** 合规客服 **/
	public static int USER_GROUP_HG = 88002006;
	/** 合规客服经理 **/
	public static int USER_GROUP_HGJL = 88002007;
	/** 客服 **/
	public static int USER_GROUP_KF = 88002008;
	/** 客服经理 **/
	public static int USER_GROUP_KFJL = 88002009;
	/** 客服总监 **/
	public static int USER_GROUP_KFZJ = 88002011;
	
	/** 指导老师 **/
	public static int USER_GROUP_ZDLS = 88002012;

	/** 人事 **/
	public static int USER_GROUP_RS = 88002020;
	/** 财务 **/
	public static int USER_GROUP_CW = 88002030;

	/** 系统管理员 **/
	public static int USER_GROUP_GLY = 88002999;

	/** 用户组为空时，提示语 **/
	public static String GROUPISNULL = "你没有权限，请重新登录试试";

	/** 错误日志路径名 **/
	public static String ERROR_LOG = "ErrorLog";

	/** 自动任务日志路径名 **/
	public static String AUTO_LOG = "AutoLog";

	/** 成交客户搜索日志路径名 **/
	public static String SEARCH_LOG = "SearchLog";

	/** 警告日志路径名 **/
	public static String WARN_LOG = "WarnLog";

	/** 资源删除日志路径名 **/
	public static String RESOURCE_DELETE = "ResourceDeleteLog";

	/** 保存通话记录日志路径名 **/
	public static String CALL_LOG = "CallLog";

	/** 核销状态 - 未核销 */
	public static Integer COMMISSION_RECORD_STATUS_CANCEL = 0;
	/** 核销状态 - 正常核销 */
	public static Integer COMMISSION_RECORD_STATUS_SUCCESS = 1;
	/** 核销状态 - 退保核销 */
	public static Integer COMMISSION_RECORD_STATUS_REFUND = 2;
	/** 核销状态 - 异常核销 */
	public static Integer COMMISSION_RECORD_STATUS_ERROR = 3;

	/**
	 * 同步资源心跳
	 */
	public static String SYNC_SOURCE_PING = "SYNC_SOURCE_PING";
	public static String SYNC_SOURCE_PRE_NOTICE = "SYNC_SOURCE_PRE_NOTICE";
	/**
	 * 分配资源心跳
	 */
	public static String ALLOT_SOURCE_PING = "ALLOT_SOURCE_PING";
	public static String ALLOT_SOURCE_PRE_NOTICE = "ALLOT_SOURCE_PRE_NOTICE";
	
	//管理员钉钉ID
	public static String ADMIN_DING_ID = "13150650531284508";
	
	//服务客户状态(正常服务)
	public static int FLOW_ZC = 701;
	
	//服务客户状态(即将到期)
	public static int FLOW_DQ = 702;
	
	//服务客户状态(暂停服务)
	public static int FLOW_ZT = 703;
	
	//服务客户状态(结束服务)
	public static int FLOW_JS = 704;
	
	
	
	/**
	 * flowId对应的流程
	 * @param flowId
	 * @return
	 */
	public static String getFlowString(Integer flowId) {
		if (flowId.intValue() == FLOW_KF) {
			return "推广资源";
		} else if (flowId.intValue() == FLOW_CQ) {
			return "抽取资源";
		} else if (flowId.intValue() == FLOW_WJ) {
			return "未添加微信资源";
		} else if (flowId.intValue() == FLOW_YX) {
			return "已添加微信资源";
		} else if (flowId.intValue() == FLOW_GXC) {
			return "共享池资源";
		} else if (flowId.intValue() == FLOW_LJ) {
			return "垃圾资源";
		} else if (flowId.intValue() == FLOW_BLACK) {
			return "黑名单资源";
		} else if (flowId.intValue() == FLOW_DSH) {
			return "待审核资源";
		} else if (flowId.intValue() == FLOW_CJ) {
			return "已成交资源";
		} else {
			return "未知流程资源";
		}
	}

	/** 是否测试手机号 **/
	public static boolean isTestMobile(String mobile) {
		if (StrUtils.isNullOrBlank(mobile)) {
			return false;
		}
		String testMobile = "13012345678,13112345678,13212345678,13312345678,13412345678,13512345678,"
				+ "13612345678,13712345678,13812345678,13912345678,15012345678,15871460442,15527491998,15623686504,"
				+ "15112345678,15212345678,15312345678,15412345678,15512345678,15612345678,15712345678,"
				+ "15812345678,15912345678,13087654321,13187654321,13287654321,13387654321,13487654321,"
				+ "13587654321,13687654321,13787654321,13887654321,13987654321,15087654321,15187654321,"
				+ "15287654321,15387654321,15487654321,15587654321,15687654321,15787654321,15887654321,"
				+ "15987654321,13023456789,13123456789,13223456789,13323456789,13423456789,13523456789,"
				+ "13623456789,13723456789,13823456789,13923456789,15023456789,15123456789,15223456789,"
				+ "15323456789,15423456789,15523456789,15623456789,15723456789,15823456789,15923456789,"
				+ "13098765432,13198765432,13298765432,13398765432,13498765432,13598765432,13698765432,"
				+ "13798765432,13898765432,13998765432,15098765432,15198765432,15298765432,15398765432,"
				+ "15498765432,15598765432,15698765432,15798765432,15898765432,15998765432,18971215956,18571816011";
		if (testMobile.indexOf(mobile) >= 0) {
			return true;
		}
		return false;
	}

}
