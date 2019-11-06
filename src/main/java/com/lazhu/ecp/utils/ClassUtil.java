package com.lazhu.ecp.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

public class ClassUtil {
	
	/**打印类的集合（List）**/
	public static void ListPrint(List<?> list) {
		if(list != null && list.size() > 0){
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			for (int i = 0; i < list.size(); i++) {
				 try {
					reflect(list.get(i));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		} else {
			System.out.println("打印类时，当前集合为空。");
		}
	}
	
	/**反射：类的属性名和值**/
	public static void reflect(Object o) throws Exception{
        Class cls = o.getClass();
        Field[] fields = cls.getDeclaredFields();
        for(int i=0; i<fields.length; i++){
            Field f = fields[i];
            f.setAccessible(true);
            System.out.println("属性名:" + f.getName() + "\t属性值:" + f.get(o));
        }
    }
	
	/**给一个对象赋值另一个对象里面相同的属性**/
	public static void setField(Object m,Object n) throws Exception{
		Class classM = m.getClass();
		Field[] fieldsM = classM.getDeclaredFields();
		
        Class classN = n.getClass();
        Field[] fieldsN = classN.getDeclaredFields();
        ClassUtil cu = new ClassUtil();
        for(Field mf: fieldsM){
        	if(mf.getName().equals("id")){
        		continue;
        	}
        	
        	for(Field mn : fieldsN){
        		Object value = cu.invokeGetMethod(classM, mf.getName(), null);
        		if(mf.getName().equals(mn.getName())){
        			Object[] obj = new Object[1];
        			obj[0] = value;
        			cu.invokeSetMethod(classN, mn.getName(), obj);
        		}
        	}
        }
        
        System.out.println("---");
        /*for(int i=0; i<fieldsN.length; i++){
            Field fN = fieldsN[i];
            fN.setAccessible(true);
            for (int j = 0; j < fieldsM.length; j++) {
            	Field fM = fieldsM[j];
            	fM.setAccessible(true);
            	System.out.println(fN.getName()+" | "+fM.getName());
            	if (fN.getName().equals(fM.getName())) {
            		System.out.println(j);
            		fM.set(fM.getName(), fN.get(n));
            	}
			}
        }*/
    }
	
	/** 
     *  
     * 执行某个Field的getField方法 
     *  
     * @param clazz 类 
     * @param fieldName 类的属性名称 
     * @param args 参数，默认为null 
     * @return  
     */  
    private Object invokeGetMethod(Object clazz, String fieldName, Object[] args)  
    {  
        String methodName = fieldName.substring(0, 1).toUpperCase()+ fieldName.substring(1);  
        Method method = null;  
        try   
        {  
            method = Class.forName(clazz.getClass().getName()).getDeclaredMethod("get" + methodName);  
            return method.invoke(clazz);  
        }   
        catch (Exception e)  
        {  
            e.printStackTrace();  
            return "";  
        }  
    }  
      
    /** 
     *  
     * 执行某个Field的setField方法 
     *  
     * @param clazz 类 
     * @param fieldName 类的属性名称 
     * @param args 参数，默认为null 
     * @return  
     */  
    private Object invokeSetMethod(Object clazz, String fieldName, Object[] args)  
    {         
        String methodName = fieldName.substring(0, 1).toUpperCase()+ fieldName.substring(1);  
        Method method = null;  
        try   
        {  
            Class[] parameterTypes = new Class[1];  
            Class c = Class.forName(clazz.getClass().getName());  
            Field field = c.getDeclaredField(fieldName);  
            Method[] methods = c.getDeclaredMethods();
            field.setAccessible(true);
            parameterTypes[0] = field.getType();  
            method = c.getDeclaredMethod("set" + methodName,parameterTypes);  
            return method.invoke(clazz,args);  
        }   
        catch (Exception e)  
        {  
            e.printStackTrace();  
            return "";  
        }  
    }  
	
	/**测试**/
    public static void main(String[] args) throws Exception{
        NEntity n = new NEntity();
        n.setName("E类");
        n.setAge(100);
        n.setBrithday(new Date());
        reflect(n);
        
        MEntity m = new MEntity();
        setField(n, m);
        System.out.println("\n---------------\n");
        reflect(m);
    }

}
