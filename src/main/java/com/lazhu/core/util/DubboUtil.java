package com.lazhu.core.util;

/**
 * @author naxj
 * 
 */
public class DubboUtil {
    private DubboUtil() {
    }

   // private static final ConcurrentMap<String, ReferenceBean<?>> referenceConfigs = new ConcurrentHashMap<String, ReferenceBean<?>>();

    /** 获取Dubbo服务 */
  /*  public static Object refer(ApplicationContext applicationContext, String interfaceName) {
        String key = "/" + interfaceName + ":";
        ReferenceBean<?> referenceConfig = referenceConfigs.get(key);
        if (referenceConfig == null) {
            referenceConfig = new ReferenceBean<Object>();
            referenceConfig.setInterface(interfaceName);
            if (applicationContext != null) {
                referenceConfig.setApplicationContext(applicationContext);
                try {
                    referenceConfig.afterPropertiesSet();
                } catch (RuntimeException e) {
                    throw (RuntimeException)e;
                } catch (Exception e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }
            }
            referenceConfigs.putIfAbsent(key, referenceConfig);
            referenceConfig = referenceConfigs.get(key);
        }
        return referenceConfig.get();
    }*/
}
