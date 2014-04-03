/**
 * 和网络有关的操作都在这里
 * 1、DownloadTask.java :所有下载图片、视频的功能均调用该类
 * 2、SetPicOrLock.java :所有设置壁纸、锁屏的功能均调用该类
 * 3、PreviewTask.java :用于壁纸、锁屏预览页网络访问功能
 * 4、ViewVideo.java,SaveVideo.java :用于视频观看时网络访问使用
 * 5、ShareTask.java :所有分享均调用该类，该类继续调用OneKeyShare-library
 * 6、Delete**.java,Get***.java,Post***.java,Put***.java :均用于和服务端交互时对JSON数据的上传和获取使用
 * 7、HTTP.java :所有http访问采用的基类，该处定义http访问的具体参数
 * 8、AsyncImageLoader.java :用于图片异步加载的工具，所有图片访问均调用该方法
 */
/**
 * @author song
 *
 */
package com.xkwallpaper.http;