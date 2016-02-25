package com.pkmdz.chattool.service.modle;
/**
 * 接受视频用户
 * @author Administrator
 *
 */
public class VideoUser {
	/**
	 * 视频会话id
	 */
	private String vcid;
	/**
	 * 呼叫者id
	 */
	private String callerid;
	/**
	 * 被叫者id
	 */
	private String clleeid;
	/**
	 * 状态吗 
	 * 10 发出请求
	 * 11请求传递成功
	 * 12请求传递失败
	 * 20同意会话
	 * 21不同意会话
	 * 22未接听
	 * 30 会话正常结束
	 * 31会话意外结束
	 */
	private String status;
	private String closerid;
	/**
	 * 开始呼叫时间
	 */
	private String calltime;
	/**
	 * 会话建立时间
	 */
	private String receivetime;
	/**
	 * 结束时间
	 */
	private String endtime;
	/**
	 * 呼叫者最后检测会话在线时间
	 */
	private String caller_keeptime;
	/**
	 * 被叫者最后检测会话在线时间
	 */
	private String callee_keeptime;
//	private String else;
	private String caller_group;
	private String caller_name;
	private String callee_group;
	private String callee_name;
	public String getVcid() {
		return vcid;
	}
	public void setVcid(String vcid) {
		this.vcid = vcid;
	}
	public String getCallerid() {
		return callerid;
	}
	public void setCallerid(String callerid) {
		this.callerid = callerid;
	}
	public String getClleeid() {
		return clleeid;
	}
	public void setClleeid(String clleeid) {
		this.clleeid = clleeid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCloserid() {
		return closerid;
	}
	public void setCloserid(String closerid) {
		this.closerid = closerid;
	}
	public String getCalltime() {
		return calltime;
	}
	public void setCalltime(String calltime) {
		this.calltime = calltime;
	}
	public String getReceivetime() {
		return receivetime;
	}
	public void setReceivetime(String receivetime) {
		this.receivetime = receivetime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public String getCaller_keeptime() {
		return caller_keeptime;
	}
	public void setCaller_keeptime(String caller_keeptime) {
		this.caller_keeptime = caller_keeptime;
	}
	public String getCallee_keeptime() {
		return callee_keeptime;
	}
	public void setCallee_keeptime(String callee_keeptime) {
		this.callee_keeptime = callee_keeptime;
	}
	public String getCaller_group() {
		return caller_group;
	}
	public void setCaller_group(String caller_group) {
		this.caller_group = caller_group;
	}
	public String getCaller_name() {
		return caller_name;
	}
	public void setCaller_name(String caller_name) {
		this.caller_name = caller_name;
	}
	public String getCallee_group() {
		return callee_group;
	}
	public void setCallee_group(String callee_group) {
		this.callee_group = callee_group;
	}
	public String getCallee_name() {
		return callee_name;
	}
	public void setCallee_name(String callee_name) {
		this.callee_name = callee_name;
	}
	
	
	
}
