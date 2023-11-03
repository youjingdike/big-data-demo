package com.thread.synchr;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilModule {
	/** 用于生成交易码流水号的静态成员变量 */
    public static int i = 0;
	public static Integer ii = 0;

	
	public static synchronized String createTaxDealCode() {
		StringBuilder sb = new StringBuilder();

		// 6.添加三位随机的流水号（3位）
		i++;
		if (i < 10) {
			sb.append("00" + i);
		} else if (i < 100) {
			sb.append("0" + i);
		} else if (i >= 100) {
			sb.append(i);
		}
		if (i == 999) {
			i = 0;
		}
		return sb.toString();
	}
	
	public static String createTaxDealCode2() {
        StringBuilder sb = new StringBuilder();

        // 6.添加三位随机的流水号（3位）
        synchronized(UtilModule.class){
            i++;
            if (i < 10) {
                sb.append("00" + i);
            } else if (i < 100) {
                sb.append("0" + i);
            } else if (i >= 100) {
                sb.append(i);
            }
            if (i == 999) {
                i = 0;
            }
        }
        return sb.toString();
    }
	
	/**
     * 交易码生成模块
     * 
     * @param flag
     *            所属标识代码
     * @param companyCode
     *            公司代码
     * @param areaCode
     *            国标区域代码
     * @param serviceCode
     *            服务编码
     * @return taxDealCode 交易码
     */
    public static synchronized String createTaxDealCode(String flag,
            String companyCode, String areaCode, String serviceCode) {
        // 所属标识代码（1位）+公司代码（4位）+国标区域代码（6位）+服务编码（1位）+日期（8位）+时间（9位）+流水号（3位）
        StringBuilder sb = new StringBuilder();

        // 1.添加平台的所属标识代码(1位)
        sb.append(flag);

        // 2.添加公司代码（4位）
        companyCode = companyCode.toUpperCase();
        sb.append(companyCode);

        // 3.增加国标区域代码（6位）
        serviceCode = serviceCode.toUpperCase();
        sb.append(areaCode);

        // 4.添加服务标志代码（1位）
        sb.append(serviceCode);

        // 5.增加8位日期和9位时间
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyyMMddHHmmssSSS");
        String da = simpleDateFormat.format(date);
        sb.append(da.substring(0, da.length()-2));

        // 6.添加三位随机的流水号（3位）
        i++;
        if (i < 10) {
            sb.append("00" + i);
        } else if (i < 100) {
            sb.append("0" + i);
        } else if (i >= 100) {
            sb.append(i);
        }
        if (i == 999) {
            i = 0;
        }
        return sb.toString();
    }
	
    public static String createTaxDealCode1() {
        StringBuilder sb = new StringBuilder();

        // 6.添加三位随机的流水号（3位）
        synchronized(ii) {
            ii++;
            if (ii < 10) {
                sb.append("00" + ii);
            } else if (ii < 100) {
                sb.append("0" + ii);
            } else if (ii >= 100) {
                sb.append(ii);
            }
            if (ii == 999) {
                ii = 0;
            }
        }
        return sb.toString();
    }
    
	public static void main(String[] args) {
	    T1 t1 = new T1();
//	    T2 t2 = new T2();
	    Thread r1 = new Thread(t1,"r1");
	    Thread r2 = new Thread(t1,"r2");
	    
//	    Thread r2 = new Thread(t2);
	    r1.start();
	    r2.start();
	}
}

class T1 implements Runnable{

    public void run() {
//        UtilModule module = new UtilModule();
        for (int i=0; i<500; i++) {
            System.out.println(Thread.currentThread().getName() + "：" + UtilModule.createTaxDealCode1() + ",i=" + i);
//            System.out.println("T1：" + UtilModule.createTaxDealCode1());
//            System.out.println("T1：" + module.createTaxDealCode1());
        }
        
    }
    
}
class T2 implements Runnable{

    public void run() {
//        UtilModule module = new UtilModule();
        for (int i=0; i<100; i++) {
//            System.out.println("T2：" + module.createTaxDealCode1());
            System.out.println("T2：" + UtilModule.createTaxDealCode1());
        }
        
    }
    
}