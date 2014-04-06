
## 1、配置方法
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


## 2、各个包意义
### com.jeremyfeinstein.*
该包为主页侧滑menu的开源工具包，已经做了许多中文注释，本工程包为com.xkwallpaper.*
### com.xkwallpaper.alipay.*
该包为支付宝快捷支付的使用的工具包，使用入口在AlipayThread.java类，配置文件在Keys.java中
### com.xkwallpaper.baidumtj.*
该包为配置百度统计的包，本系统所有Activity Fragment FragmentActivity均集成该包中的对应的类
### com.xkwallpaper.contants.*
该包保存着系统所有的常量，包括服务器地址，系统的各个API，系统保存本地SD卡路径
### com.xkwallpaper.db.*
操作本地数据库的文件保存在该包中
### com.xkwallpaper.http.*
网络访问在该包中，其中，com.xkwallpaper.http.base.*为一些网络访问的实体文件，用于对JSON数据反序列话使用
### com.xkwallpaper.imagezoom.*
该包及其子包用于图片浏览相关操作，主要实现触摸缩放等功能。
### com.xkwallpaper.lockpaper.*
所有锁屏相关的类均放在了这个包中，包括终于后台启动锁屏服务的LockService，以及锁屏界面LockActivity，解锁的滑动组件Slidebar
### com.xkwallpaper.thread.*
保存一个用于线程管理的ThreadExecutor以及一个用于订单生成的线程
### com.xkwallpaper.ui.*
系统所有UI界面，Activity、Fragment、Adapter、以及自定义的组件都在这个包及其子包中

## 3、关于分享、社交登陆账号的相关问题
### （1）、可移交帐号 
>只在新浪微博上发现了移交应用的接口，其它开放平台未发现，均需要重新申请

新浪微博移交后，RedirectUrl值可能需要开发者自己修改。在<http://open.weibo.com>中修改完成后，需
要在应用的shareSDK.xml中的对应位置做修改
### （2）、需重新申请的帐号
微信和人人网帐号需要重新申请，申请完成后需在assets/shareSDK.xml的对应位置做修改即可
### （3）、ShareSDK帐号申请
因为本应用分享采用ShareSDK的产品，因此需要注册shareSDK的id，注册后将ID也修改到shareSDK.XML的对应位置


