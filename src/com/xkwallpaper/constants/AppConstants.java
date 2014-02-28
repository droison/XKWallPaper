package com.xkwallpaper.constants;

import android.os.Environment;

public class AppConstants {

	public static final int fragmentAbout = 0X1001;
	
	public static final String APP_FILE_NAME = "xingku";
	public static final String APP_FILE_PATH = Environment.getExternalStorageDirectory() + "/" + APP_FILE_NAME;
	public static final String TEMP_HEAD_FILE_PATH = Environment.getExternalStorageDirectory() + "/" + APP_FILE_NAME + "/head.jpg";
	
	/**
	 * 网络访问相关的常量,以200作为头，希望HTTP code永远200！
	 */
	public static final int HANDLER_MESSAGE_NORMAL = 0X200001;
	public static final int HANDLER_MESSAGE_NULL = 0X200002;
	public static final int HANDLER_HTTPSTATUS_ERROR = 0X200003;
	public static final int HANDLER_MESSAGE_NONETWORK = 0X200004;
	public static final int HANDLER_MESSAGE_IMAGE500 = 0X200005;   //针对图片上传的500错误
	public static final int Comment_Activity_Code = 1;
	
	
	//alipay相关
	
	public static final int RQF_PAY = 30001;

	public static final int RQF_LOGIN = 30002;

	/**
	 * URL
	 */
	public interface HTTPURL{
		public static final String serverIP = "http://115.28.229.188";
		
		public static final String picPPT = serverIP+ "/app/papers/ppt"; //GET 首页PPT的三个壁纸
		public static final String picAll = serverIP + "/app/papers?page="; //GET 传入页码，从1开始
		public static final String picInfo = serverIP + "/app/papers/"; //后面直接跟ID
		
		public static final String picAllComment = serverIP + "/app/comments/paper";//GET paper_id 和 page
		public static final String picComment = serverIP + "/app/comments"; //POST paper_id（壁纸id）；parent_id（父评论id） user_id content
        public static final String picPraise = serverIP + "/app/praises"; //POST paper_id（壁纸id）；paper_id（壁纸
        
        public static final String lockPPT = serverIP +"/app/screens/ppt";
        public static final String lockAll = serverIP + "/app/screens?page=";
        public static final String lockInfo = serverIP + "/app/screens/"; //后面直接跟ID
        
        public static final String vidPPT = serverIP + "/app/movies/ppt";
        public static final String vidAll = serverIP + "/app/movies?page=";
        public static final String vidInfo = serverIP +"/app/movies/"; //后面直接跟id  REST风格
        
        public static final String commentAll = serverIP + "/app/comments/user"; //GET 参数：user_id（壁纸id）；page（页数）
        public static final String commentDelete = serverIP +"/app/comments/"; //DELETE  直接跟ID
        public static final String downloadUrl = serverIP + "/app/downloads"; //POST paper_id
        
        public static final String searchTags = serverIP + "/app/tags";
        public static final String search = serverIP + "/app/papers/search"; //参数：keyword,page,search_type（1代表关键词搜索，2代表标签搜索）；paper_type（0代表搜全部，1代表搜壁纸，2代表搜锁屏，3代表搜视频）
	
//        注册登录相关接口
        public static final String regPhoneVerify = serverIP + "/app/registrations/verify";//POST获取短信验证码 参数:phone
        public static final String reg = serverIP + "/app/registrations/create";//注册POST 参数:username,password,password_confirmation,phone,verfy 返回：private_token
	    public static final String login = serverIP + "/app/sessions/create";//登录POST 参数：phone password 返回：private_token
	    public static final String infoUpdate = serverIP + "/app/registrations/update"; //PUT 参数：user［username］（用户名）；user［password］（密码）； user［phone］（手机号）；user［face］（头像）；
	    public static final String socialBind = serverIP + "/app/registrations/bind";//POST 参数：private_token（token）；type（社交平台类型，1代表微博，2代表qq）；uid（用户id）；face（头像）；username（用户名） 
	    public static final String socialLogin = serverIP + "/app/registrations/social_create";//（POST ）参数：type（社交平台类型，1代表微博，2代表qq）；uid（用户id）；face（头像）；username（用户名）
	    public static final String lostPwdVerify = serverIP + "/app/registrations/password_verify";////POST获取短信验证码 参数:user[phone]
	    public static final String lostPwdSubmit = serverIP + "/app/registrations/password";// user［password］（新密码）；user［password_confirmation］（确认密码）；user［phone］（手机号）；user［verfy］（验证码）
	
	//付款相关
	    public static final String orderCreate = serverIP + "/app/orders"; //POST private_token（token）；order［paper_id］（壁纸id）
	    public static final String orderAsynUrl = serverIP + "/app/orders/message";//支付宝客户端异步访问的服务器地址，用来处理订单是否完成
	}
	
}
