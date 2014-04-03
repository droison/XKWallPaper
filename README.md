
## 配置方法
### 配置共享、社交登录的appkey。
因为应用需要社交登录和社交分享，需要到对应的社交开放平台申请ID，所有社交ID的配置文件均在“/assets/shareSDK.xml”中完成，其中，qq和weibo两个需要有第三方登录权限（微博默认有，腾讯的QQ空间需要单独申请）
### 软件更新地址配置。 
APP请求新版本的地址参数为“com.xkwallpaper.constants.AppContants.HTTPURL”的checkVersion参数，该参数为一个HTTP网络地址，返回为一组JSON格式数据：


    { version: 17, url: "http://****/XKWallPaper.apk",info: "提示信息",result: "true"}。
 
 其中，若version大于当前app的versionCode，则提示更新，更新信息为info参数，新版本下载地址为url参数。若管理人员不希望应用继续被用户使用，只需将result置为false
### 系统关于页面的内容修改。 
所有数据均在"res.values.strings.xml"中
### 统计信息配置。  
APP添加了百度移动统计工具，如果需要修改，请修改Manifest.xml中对应位置的value值。

<meta-data android:name="BaiduMobAd_STAT_ID" android:value="98261f89c8" />
