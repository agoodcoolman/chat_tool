package com.pkmdz.chattool.utils;
/**
 * 接口定义类
 * @author Administrator
 *
 */
public class InterfaceUtils {
	public static final String HOST = "http://120.27.118.186/index.php";
	// 回话同意与否回复
	public static final String st1 = "http://120.27.118.186/index.php?s=/admin/video/viAnswer&vcid=101&answer=yes";
	// 获取当前在线人数数据
	public static final  String roster = "http://120.27.118.186/index.php?s=/admin/video/ajaxGetOnliner.html";
	// 回话维护
	public static final  String st3 = "http://120.27.118.186/index.php?s=/admin/video/vcMaintain&t=14563653830068";
	// 轮询是否有找自己的回话
	public static final  String query = "http://120.27.118.186/index.php?s=/admin/video/voCallmePoll.html";
	//登录
	public static final  String LOGIN_URL = "http://120.27.118.186/index.php?s=/Admin/Login/appLogin.html";
	
	// 更新在线时间
	public static final  String UPDATE_LOGIN_TIME = "http://120.27.118.186/index.php?s=/admin/index/updateLastCheckTime.html&";
}
