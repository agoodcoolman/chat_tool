package com.pkmdz.chattool.service.modle;
/**
 * 获得在线用户信息
 * @author Administrator
 *
 */
public class User {
	/**
	 * 会员id
	 */
	private String uid;
	/**
	 * 会员姓名
	 */
	private String username;
	private String password;
	/**
	 * 是否在线状态 
	 * 0 离线
	 * 1 在线
	 */
	private String status;
	/**
	 * group 
	 * 0 超级管理员
	 * 1 专家组
	 * 2 前台手机
	 */
	private String group;
	/**
	 * 推流url地址，仅对前台组有效
	 */
	private String tlurl;
	/**
	 * 专家组swf地址
	 */
	private String swfurl;
	private String lastchecktime;
	private String createtime;
	private String updatetime;
	/**
	 * 上一次登录ip
	 */
	private String lastloginip;
	/**
	 * 上一次登录时间
	 */
	private String lastlogintime;
	private String elses;
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getTlurl() {
		return tlurl;
	}
	public void setTlurl(String tlurl) {
		this.tlurl = tlurl;
	}
	public String getSwfurl() {
		return swfurl;
	}
	public void setSwfurl(String swfurl) {
		this.swfurl = swfurl;
	}
	public String getLastchecktime() {
		return lastchecktime;
	}
	public void setLastchecktime(String lastchecktime) {
		this.lastchecktime = lastchecktime;
	}
	public String getCreatetime() {
		return createtime;
	}
	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	public String getLastloginip() {
		return lastloginip;
	}
	public void setLastloginip(String lastloginip) {
		this.lastloginip = lastloginip;
	}
	public String getLastlogintime() {
		return lastlogintime;
	}
	public void setLastlogintime(String lastlogintime) {
		this.lastlogintime = lastlogintime;
	}
	public String getElses() {
		return elses;
	}
	public void setElses(String elses) {
		this.elses = elses;
	}
	
	
}
