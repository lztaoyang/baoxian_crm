package com.lazhu.core.interceptor;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.method.HandlerMethod;

import com.alibaba.fastjson.JSON;
import com.lazhu.core.Constants;
import com.lazhu.core.util.DateUtil;
import com.lazhu.core.util.ExceptionUtil;
import com.lazhu.core.util.WebUtil;
import com.lazhu.sys.model.SysEvent;
import com.lazhu.sys.service.SysEventService;

import cz.mallat.uasparser.OnlineUpdater;
import cz.mallat.uasparser.UASparser;
import cz.mallat.uasparser.UserAgentInfo;
import io.swagger.annotations.ApiOperation;

/**
 * 日志拦截器
 * 
 * @author naxj
 * 
 */
public class EventInterceptor extends BaseInterceptor {
	protected static Logger logger = LogManager.getLogger();

	private final ThreadLocal<Long> startTimeThreadLocal = new NamedThreadLocal<Long>("ThreadLocal StartTime");
	private ExecutorService executorService = Executors.newCachedThreadPool();

	@Autowired
	protected SysEventService sysEventService;
	
	static UASparser uasParser = null;
	
	public String defultLogin="/sys/login.html";//默认登录页面 

	// 初始化uasParser对象
	static {
		try {
			uasParser = new UASparser(OnlineUpdater.getVendoredInputStream());
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 开始时间（该数据只有当前请求的线程可见）
		startTimeThreadLocal.set(System.currentTimeMillis());
		
		request.setCharacterEncoding("UTF-8");  
        response.setCharacterEncoding("UTF-8");  
        response.setContentType("text/html;charset=UTF-8");  
  
        String uri = request.getRequestURI();  
       //设置不拦截的对象
        String[] noFilters = new String[] {"login","index","wutongshu","lazhu"};
        //String[] noFilters = new String[] {};
        boolean beFilter = true;
        if (noFilters.length > 0) {
        	for (String s : noFilters) {
        		if (uri.indexOf(s) != -1) {
        			beFilter = false;
        			break; 
        		}
        	}
		}
		if (beFilter) {
			try {
				HandlerMethod handlerMethod = (HandlerMethod) handler;
				ApiOperation apiOperation = handlerMethod.getMethod().getAnnotation(ApiOperation.class);
				request.setAttribute(Constants.OPERATION_NAME, apiOperation.value());
			} catch (Exception e) {
				logger.error("", e);
			}
		}
		/*if (!beFilter) {
			//重定向到登录页
			//response.sendRedirect(request.getContextPath()+defultLogin);
			response.sendRedirect(defultLogin);
		}*/
		return super.preHandle(request, response, handler);
	}

	public void afterCompletion(final HttpServletRequest request, HttpServletResponse response, Object handler,
			final Exception ex) throws Exception {
		final Long startTime = startTimeThreadLocal.get();
		final Long endTime = System.currentTimeMillis();
		// 保存日志

		String userAgent = null;
		try {
			UserAgentInfo userAgentInfo = uasParser.parse(request.getHeader("user-agent"));
			userAgent = userAgentInfo.getOsName() + " " + userAgentInfo.getType() + " " + userAgentInfo.getUaName();
		} catch (IOException e) {
			logger.error("", e);
		}
		String path = request.getRequestURI();//getServletPath();
		if (!path.contains("/read/") && !path.contains("/unauthorized") && !path.contains("/forbidden")) {
			final SysEvent record = new SysEvent();
			Long uid = WebUtil.getCurrentUser();
			record.setMethod(request.getMethod());
			record.setRequestUri(request.getRequestURI());//.getServletPath()
			record.setClientHost(WebUtil.getHost(request));
			record.setUserAgent(userAgent);
			if (path.contains("/upload/")) {
				record.setParameters("");
			} else {
				record.setParameters(JSON.toJSONString(request.getParameterMap()));
			}
			record.setStatus(response.getStatus());
			record.setCreateBy(uid);
			record.setUpdateBy(uid);
			final String msg = (String) request.getAttribute("msg");

			executorService.submit(new Runnable() {
				public void run() {
					try { // 保存操作
						record.setTitle((String) request.getAttribute(Constants.OPERATION_NAME));
						if (StringUtils.isNotBlank(msg)) {
							record.setRemark(msg);
						} else {
							record.setRemark(ExceptionUtil.getStackTraceAsString(ex));
						}

						sysEventService.update(record);

						// 内存信息
						if (logger.isDebugEnabled()) {
							String message = "开始时间: {}; 结束时间: {}; 耗时: {}s; URI: {}; ";
							// 最大内存: {}M; 已分配内存: {}M; 已分配内存中的剩余空间: {}M; 最大可用内存:
							// {}M.
							// long total = Runtime.getRuntime().totalMemory() /
							// 1024 / 1024;
							// long max = Runtime.getRuntime().maxMemory() /
							// 1024 / 1024;
							// long free = Runtime.getRuntime().freeMemory() /
							// 1024 / 1024;
							// , max, total, free, max - total + free
							logger.debug(message, DateUtil.format(startTime, "HH:mm:ss.SSS"),
									DateUtil.format(endTime, "HH:mm:ss.SSS"), (endTime - startTime) / 1000.00,
									record.getRequestUri());
						}
					} catch (Exception e) {
						logger.error("Save event log cause error :", e);
					}
				}
			});
		} else if (path.contains("/unauthorized")) {
			logger.warn("用户[{}]没有登录", WebUtil.getHost(request) + "@" + userAgent);
		} else if (path.contains("/forbidden")) {
			logger.warn("用户[{}]没有权限", WebUtil.getCurrentUser() + "@" + WebUtil.getHost(request) + "@" + userAgent);
		}
		super.afterCompletion(request, response, handler, ex);
	}
}
