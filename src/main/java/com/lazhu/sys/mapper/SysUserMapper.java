package com.lazhu.sys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lazhu.core.base.BaseMapper;
import com.lazhu.sys.model.SysUser;

public interface SysUserMapper extends BaseMapper<SysUser> {

	Long queryByAccount(@Param("account") String account,@Param("userId") Long id);

	/** 查询当前用户的所有下级 **/
	List<Long> querySUBById(@Param("userId") Long userId);
	
	/** 查询所有经理及以上级别的用户 **/
	List<Long> queryAllSenior();

	/** 查询所有业务员 **/
	List<Long> queryAllMan();
	
	/** 查询所有经理 **/
	List<Long> queryAllManager();
	
	/** 查询所有总监 **/
	List<Long> queryAllDirector();
	
	/** 查询客升级户数最少的升级人员 **/
	List<Long> selectIdByClubNum();
	
	/** 查询用户组分配客户数最少的人员 **/
	List<Long> selectIdByAllotClubNum(@Param("userGroup") Integer userGroup);
	
	/** 
	 * @author ty
	 * 查询用户组升级分配客户数最少的人员 
	 * 
	 * **/
	List<Long> queryByGroupAndDept(@Param("userGroup") Integer userGroup,@Param("deptId") List<Long> deptId);
	
	/** 查询某个（升级）总监部门下分配升级客户数最少的（某个用户组）人员 **/
	List<Long> selectIdByAllotClubNumInDept(@Param("userId") Long userId,@Param("userGroup") Integer userGroup);
	
	/** 查询某个（升级）部门下分配升级客户数最少的经理 **/
	List<Long> selectUpgradeManagerIdByAllotClubNum();
	

	/** 查询所有升级业务员 **/
	List<Long> queryAllUpgradeMan();
	
	/** 查询所有升级经理 **/
	List<Long> queryAllUpgradeManager();
	
	/** 查询所有升级总监 **/
	List<Long> queryAllUpgradeDirector();
	
	/** 查询分配升级阶梯数最少的升级人员**/
	List<Long> selectIdByAllotStairNum();

	/** 查询分配次数最少的用户（经纪人）**/
	List<Long> querySalerAllotResourceMin();
	
	/** 查询所有保险经纪人和经理**/
	List<Long> queryAllManAndManager();

	/** 查询经理及所有下属**/
	List<Long> queryManagerSub(@Param("userId") Long currUser);
	/** 查询总监及所有下属**/
	List<Long> queryDirectorSub(@Param("userId") Long currUser);
	/** 查询总经理及所有下属**/
	List<Long> queryMinisterSub(@Param("userId") Long currUser);
	/** 查询市场部所有人（启用、激活）**/
	List<Long> queryAllNormalBusiness();
	/** 查询市场部待分配推广资源所有人**/
	List<Long> queryAllAllotResource();
	/** 查询所有用户**/
	List<Long> queryAllUser();
	/** 分机号查询用户 **/
	List<Long> queryAgentNo(@Param("agentNo") Integer agentNo);

	/**定时每天重置分配资源数0，最大分配数，每天抽取次数5**/
	void resetUserAllotNum(@Param("num") int num);
	void stopUserAllot();

	/** 执行每天8点-23点用户奖励资源数--、控制分配数--的消费 **/
	void recycleUserRewardCommit(@Param("num") int num);
	
	/** 每周六早上重置今日奖励资源数、今日剩余奖励资源数为零 **/
	void resetUserRewardNum();
	
	/**查询经纪人开启推广分配比例**/
	Integer queryTResourceRatio();
	
	/**
	 * 批量更新用户分配记录
	 * @param userList
	 */
	public void updateResourceAllot(@Param("list") List<Long> userList);

	/**
	 * 重置升级用户每天资源分配数
	 * 
	 */
	public void resetUserAllotClubNum();
	/**
	 * 根据升级经理id查下属分配数最少的升级人员
	 * @param manageId
	 * @return
	 */
	List<Long> querybyManageId(@Param("parentId")  long parentId);
	
	
	/** 钉钉ID查询用户 **/
	Long queryByDId(@Param("did") String did);
	
	/** 钉钉ID查询用户（多） **/
	List<Long> queryByDIds(@Param("did") String did);
	
	/** 查询总监及下属经理**/
	List<Long> queryDirectorMang(@Param("userId") Long currUser);

	List<Long> queryUpgradeUserByIds(@Param("ids") List<Long> ids);
}
