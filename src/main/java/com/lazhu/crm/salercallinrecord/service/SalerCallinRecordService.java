package com.lazhu.crm.salercallinrecord.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.lazhu.aladdinpbx.noanswercall.entity.Noanswercall;
import com.lazhu.aladdinpbx.noanswercall.service.NoanswercallService;
import com.lazhu.aladdinpbx.recordlogs.entity.Recordlogs;
import com.lazhu.aladdinpbx.recordlogs.service.RecordlogsService;
import com.lazhu.core.util.ComplexMD5Util;
import com.lazhu.crm.Constant;
import com.lazhu.crm.core.service.BusinessBaseService;
import com.lazhu.crm.customer.entity.Customer;
import com.lazhu.crm.customer.service.CustomerService;
import com.lazhu.crm.mobilelastlog.entity.MobileLastLog;
import com.lazhu.crm.mobilelastlog.service.MobileLastLogService;
import com.lazhu.crm.resource.entity.Resource;
import com.lazhu.crm.resource.service.ResourceService;
import com.lazhu.crm.salercallinrecord.entity.SalerCallinRecord;
import com.lazhu.crm.salercallinrecord.mapper.SalerCallinRecordMapper;
import com.lazhu.crm.serverrecordmobile.service.ServerRecordMobileService;
import com.lazhu.ecp.utils.OperationLogTool;
import com.lazhu.ecp.utils.StrUtils;
import com.lazhu.sys.model.SysUser;

/**
 * <p>
 * 市场部来电记录表 服务实现类
 * </p>
 *
 * @author hz
 * @since 2018-01-18
 */
@Service
@CacheConfig(cacheNames = "salerCallinRecord")
public class SalerCallinRecordService extends BusinessBaseService<SalerCallinRecord> {
	
	@Autowired//同步已接电话记录
	private RecordlogsService recordlogsService;
	
	@Autowired//同步未接电话记录
	private NoanswercallService noanswercallService;
	
	@Autowired//最近电话拨打记录
	private MobileLastLogService mobileLastLogService;

	@Autowired//会员
	CustomerService customerService;
	
	@Autowired//业务
	ResourceService resourceService;
	
	@Autowired//客服通话记录
	ServerRecordMobileService serverRecordMobileService;
	
	/**
	 * 通过录音文件名查询
	 * @param callFile
	 * @return
	 */
	public Long queryByCallFile(String callFile) {
		List<Long> ids = ((SalerCallinRecordMapper)mapper).queryByCallFile(callFile);
		if (null != ids && ids.size() > 0) {
			if (ids.size() == 1) {
				return ids.get(0);
			} else {
				OperationLogTool.operationLog(Constant.ERROR_LOG, "市场部来电记录表存在重复录音文件名：" + callFile);
				return null;
			}
		} else {
			return null;
		}
	}
	
	/**
	 * 录音文件名，客服通话内容，更新至市场部来电记录
	 * @param callFile
	 * @param serverRecord
	 */
	public void updateServerRecordByCallFile (String callFile,String serverRecord) {
		if (StrUtils.isNotNullOrBlank(callFile) && StrUtils.isNotNullOrBlank(serverRecord)) {
			SalerCallinRecord scr = null;
			Long id = this.queryByCallFile(callFile);
			if (null != id && id > 0) {
				scr = this.queryById(id);
				scr.setThroughRecord(serverRecord);
				this.update(scr);
			}
		}
	}

	/**
	 * 执行市场部来电记录收集
	 * 表说明：
	 * 查询表：
	 * 已接通话记录表（所有通话已接记录，id,手机号，拨打人ID）
	 * 未接通话记录表（所有通话未接记录，id,手机号，拨打人ID）
	 * 手机号最近拨打记录表（手机号，拨打人ID，拨打时间）
	 * 资源表
	 * 会员表
	 * 客服部通话记录表（来电时可以备注信息）
	 * 
	 * 目标表：
	 * 市场部我的来电表（已经电话和未接电话）（字段：已接通话ID,未接通话ID,是否接通）
	 * 
	 * 业务说明：
	 * 1.客户来电，全部转接到客服，如果接听了保存到已接通话记录表，如果没有接听保存到未接通话记录表
	 * 2.判断客户来电是不是要找市场部，保存记录到市场部我的来电表
	 * 
	 * 
	 * 例：同步市场部我的来电表已接电话
	 * 
	 * 1.查询市场部我的来电表中最大已接电话ID(answer_id)
	 * 2.查询已接通话记录表中大于answer_id的记录（recordlogs）
	 * 3.查询手机号最近拨打记录表，recordlogs的手机号最近的拨打人ID（手机号、拨打人）
	 * 
	 * 4.1如果拨打人是市场部人员，则通过手机号查询会员表信息，如果没有再查询资源表信息，如果也没有就不处理关联信息。
	 * 通过已接通话记录表录音文件名关联查询客服部通话记录表（可能有、可能无）
	 * 4.2如果拨打人是客服或者没有最近拨打记录，则停止处理。
	 * 
	 * 5.保存市场部我的来电表（已经通话记录表 + 会员表/资源表/无 + 客服部通话记录表/无）
	 * 
	 * 例：同步市场部我的来电表未接电话
	 * 
	 * 同上，区别是(无需关联查询客服部通话记录表)
	 */
	@Transactional
	public void salerCallinLog() {
		//1.查询市场部我的来电表中最大已接电话ID(answer_id)
		Long answerMaxId = queryMaxAnswerId();
		//2.查询市场部我的来电表中最大未接电话ID(noanswer_id)
		Long noanswerMaxId = queryMaxNoanswerId();
		//3.查询最新已经通话记录
		List<Long> answerList = null;
		if (null != answerMaxId && answerMaxId > 0) {
			answerList = recordlogsService.queryAnswerByMaxId(answerMaxId);
		}
		//4.查询最新未接通话记录
		List<Long> noanswerList = null;
		if (null != noanswerMaxId && noanswerMaxId > 0) {
			noanswerList = noanswercallService.queryByMaxId(noanswerMaxId);
		}
		//已接来电
		if (null != answerList && answerList.size() > 0) {
			for (Long answerId : answerList) {
				Recordlogs recordlogs = recordlogsService.queryById(answerId);
				String mobile = StrUtils.prefixDelZero(recordlogs.getRecCallederid());
				String md5Mobile = ComplexMD5Util.MD5Encode(mobile);
				Long mobileLastId = mobileLastLogService.queryLastByMd5Mobile(md5Mobile);
				MobileLastLog mobileLastLog = null;
				if (null != mobileLastId && mobileLastId> 0) {
					mobileLastLog = mobileLastLogService.queryById(mobileLastId);
				}
				if (null != mobileLastLog && mobileLastLog.getCreateBy() > 0) {
					SalerCallinRecord salerCallinRecord = new SalerCallinRecord();
					salerCallinRecord.setAnswerId(answerId);
					salerCallinRecord.setMobile(mobile);
					salerCallinRecord.setCallTime(recordlogs.getRecStarttime());
					salerCallinRecord.setRingLength(StrUtils.toLong(recordlogs.getRecRingwaittime()));
					salerCallinRecord.setCallLength(recordlogs.getRecRecordlength());
					salerCallinRecord.setIsThrough(1);
					if (StrUtils.isNotNullOrBlank(recordlogs.getRecAgentid())) {
						Long crmId = StrUtils.toLong(recordlogs.getRecAgentid());
						if (StrUtils.isNotNullOrBlank(crmId)) {
							salerCallinRecord.setThroughId(crmId);
							salerCallinRecord.setThroughName(userService.queryById(crmId).getAccount());
						}
					}
					//确认为市场部来电
					if (mobileLastLog.getType() == 1) {
						Customer customer = customerService.queryByMd5Mobile(md5Mobile);
						if (null != customer) {
							salerCallinRecord.setSalerId(customer.getSalerId());
							salerCallinRecord.setSalerName(customer.getSalerName());
							salerCallinRecord.setManagerId(customer.getManagerId());
							salerCallinRecord.setManagerName(customer.getManagerName());
							salerCallinRecord.setDirectorId(customer.getDirectorId());
							salerCallinRecord.setDirectorName(customer.getDirectorName());
							salerCallinRecord.setMinisterId(customer.getMinisterId());
							salerCallinRecord.setMinisterName(customer.getMinisterName());
							salerCallinRecord.setIsClub(1);
						} else {
							Resource resource = resourceService.queryByMd5Mobile(md5Mobile);
							if (null != resource) {
								salerCallinRecord.setSalerId(resource.getSalerId());
								salerCallinRecord.setSalerName(resource.getSalerName());
								salerCallinRecord.setManagerId(resource.getManagerId());
								salerCallinRecord.setManagerName(resource.getManagerName());
								salerCallinRecord.setDirectorId(resource.getDirectorId());
								salerCallinRecord.setDirectorName(resource.getDirectorName());
								salerCallinRecord.setMinisterId(resource.getMinisterId());
								salerCallinRecord.setMinisterName(resource.getMinisterName());
								salerCallinRecord.setIsClub(0);
							}
						}
						String callFile = recordlogs.getRecRecordpath() + recordlogs.getRecRecordfile();
						salerCallinRecord.setCallFile(callFile);
						String serverRecord = serverRecordMobileService.queryServerRecordByCallFile(callFile);
						salerCallinRecord.setThroughRecord(serverRecord);
					}
					//
					this.update(salerCallinRecord);
				}
			}
		}
		//未接来电
		if (null != noanswerList && noanswerList.size() > 0) {
			for (Long noanswerId : noanswerList) {
				Noanswercall noanswercall = noanswercallService.queryById(noanswerId);
				String mobile = StrUtils.prefixDelZero(noanswercall.getNasCallederid());
				String md5Mobile = ComplexMD5Util.MD5Encode(mobile);
				Long mobileLastId = mobileLastLogService.queryLastByMd5Mobile(md5Mobile);
				MobileLastLog mobileLastLog = null;
				if (null != mobileLastId && mobileLastId> 0) {
					mobileLastLog = mobileLastLogService.queryById(mobileLastId);
				}
				if (null != mobileLastLog && mobileLastLog.getCreateBy() > 0) {
					SalerCallinRecord salerCallinRecord = new SalerCallinRecord();
					salerCallinRecord.setNoanswerId(noanswerId);
					salerCallinRecord.setMobile(mobile);
					salerCallinRecord.setCallTime(noanswercall.getNasStarttime());
					salerCallinRecord.setRingLength(StrUtils.toLong(noanswercall.getNasRinglength()));
					salerCallinRecord.setIsThrough(0);
					Integer agentNo = StrUtils.toInteger(noanswercall.getNasExtnumber());
					if (StrUtils.isNotNullOrBlank(agentNo)) {
						SysUser user = userService.queryAgentNo(agentNo);
						if (StrUtils.isNotNullOrBlank(user)) {
							salerCallinRecord.setThroughId(user.getId());
							salerCallinRecord.setThroughName(user.getAccount());
						}
					}
					//确认为市场部来电
					if (mobileLastLog.getType() == 1) {
						Customer customer = customerService.queryByMd5Mobile(md5Mobile);
						if (null != customer) {
							salerCallinRecord.setSalerId(customer.getSalerId());
							salerCallinRecord.setSalerName(customer.getSalerName());
							salerCallinRecord.setManagerId(customer.getManagerId());
							salerCallinRecord.setManagerName(customer.getManagerName());
							salerCallinRecord.setDirectorId(customer.getDirectorId());
							salerCallinRecord.setDirectorName(customer.getDirectorName());
							salerCallinRecord.setMinisterId(customer.getMinisterId());
							salerCallinRecord.setMinisterName(customer.getMinisterName());
							salerCallinRecord.setIsClub(1);
						} else {
							Resource resource = resourceService.queryByMd5Mobile(md5Mobile);
							if (null != resource) {
								salerCallinRecord.setSalerId(resource.getSalerId());
								salerCallinRecord.setSalerName(resource.getSalerName());
								salerCallinRecord.setManagerId(resource.getManagerId());
								salerCallinRecord.setManagerName(resource.getManagerName());
								salerCallinRecord.setDirectorId(resource.getDirectorId());
								salerCallinRecord.setDirectorName(resource.getDirectorName());
								salerCallinRecord.setMinisterId(resource.getMinisterId());
								salerCallinRecord.setMinisterName(resource.getMinisterName());
								salerCallinRecord.setIsClub(0);
							}
						}
					}
					//
					this.update(salerCallinRecord);
				}
			}
		}
	}

	/**
	 * 查询市场部我的来电表中最大已接电话ID(answer_id)
	 * @return
	 */
	private Long queryMaxAnswerId() {
		return ((SalerCallinRecordMapper)mapper).queryMaxAnswerId();
	}
	
	/**
	 * 查询市场部我的来电表中最大未接电话ID(noanswer_id)
	 * @return
	 */
	private Long queryMaxNoanswerId() {
		return ((SalerCallinRecordMapper)mapper).queryMaxNoanswerId();
	}

	public Page queryList(Map<String, Object> params, Long currUser) {
		//设置市场部权限（用户组）
		super.setBusiness(params, currUser);
		//下属查询
		if (StrUtils.isNotNullOrBlank(params.get("subordinate"))) {
			super.setBusiness(params, StrUtils.toLong(params.get("subordinate")));
		}
		//删除下属key
		params.remove("subordinate");
		Page<Long> page = getPage(params);
		page.setRecords(((SalerCallinRecordMapper)mapper).selectIdPage(page, params));
		Page<SalerCallinRecord> recordPage = super.getPage(page);
		return recordPage;
	}

	public List<Long> queryServerRecordIsNull() {
		return ((SalerCallinRecordMapper)mapper).queryServerRecordIsNull();
	}
	
}
