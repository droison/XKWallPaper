/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 * 
 *  提示：如何获取安全校验码和合作身份者id
 *  1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *  2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 *  3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */

package com.xkwallpaper.alipay;

public final class Keys {

	// 合作身份者id，以2088开头的16位纯数字
	public static final String DEFAULT_PARTNER = "2088311377991180";

	// 收款支付宝账号
	public static final String DEFAULT_SELLER = "runmobile@qq.com";

	// 商户私钥，自助生成
	public static final String PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANCa1MXzDGZQ7zSv7+vbl8KL2YscdR75Hlf8gxfzgHnCCA98ZwBkPvQ9w69kw0icqbRLCUvltjixb6y6E8aQjaqNLq0djBG8SElDoXXY50jkXNuznA+yIXf5BvLxhLeYLU2DiDpNi6DqR4zSTRKHlv80HfQWh/gH8aTEUbcUEWapAgMBAAECgYAGnhLH4Mjw6fpcooQq6EFtM4CIMDPq8p4qzmDePqRBuI0G6Lxfv85bcyYlBz5GrGmZxZOa68OXEtHnGdQMxYMo6jG7RnPWkQWTMdumPzQmKWwHY1T8XFqFAfI9M0ykcBlRUAc1o7YytzFXqNSuBRBQ1MFdktaaHj1XAyHhwPJyLQJBAPq62tbjPkQBHHX/k1gweUwYYstXQ85jHB+/5ZUV30pHwcYbI1raD/gBneAD3LHq0ER682GkFeKiZpFDc3zd5PcCQQDU/U5/aUkVA0zjcldght2vZMHh8VkVwGhZ7Kb3+Si+eSKpiXOt4WndNbKWoYRtVcE2ifczNMoH0tgvQGT7SUlfAkA9njIt4UbqG4PNE/Q4FiGZK90Lr/SAAidlPhnD2842SRA8CWtU5oZKIsuTxB82skhgq/6oHlUqj1K5nGQDadzdAkEArNO6raKGXdmmu2zdwALPwfHVbXzE1+SgCFACkHcKe7yZxs41IuQlXg2jk50OcxXByXqcL13njf+l0nnDNzCZqwJAeY3rWzWIIjy6CEOJDX/y006YEs2QgRGaTbpxR8e7SDgbRN7sguP8b93yBO11jFj+jhy2tZiCGtCPHM/TkL+/9Q==";

	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
}
