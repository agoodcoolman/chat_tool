package org.androidpn.client.heart;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.androidpn.client.Constants;
import org.androidpn.client.XmppManager;
import org.androidpn.client.uitls.LogUtil;
import org.androidpn.client.uitls.NetUtils;
import org.androidpn.client.uitls.StateRecoder;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.ping.PingSucessListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * 
  * @ClassName: HeartManager
  * @Description: 心跳包的管理
  * @author jin
  * @Company 深圳德奥技术有限公司
  * @date 2015年12月30日 下午2:04:41
  * 
  * 长连接+心跳策略（在Galaxy S3上使用），心跳间隔WIFI下是3分20秒，手机网络是7分钟。
  *
 */
public class HeartManager implements PingFailedListener, PingSucessListener{
	
	 private static final String LOGTAG = LogUtil
	            .makeLogTag(HeartManager.class);
	
	
	private final int minFixHeart = 5000;// 最小的固定心跳包,用于在前台使用.
	private final int ATTEMPT_MIN_COUNT = 5; // 最小次数
	private int pingFaildCount = 0;
	private int pingSucessCount = 0;
	private int subType = 0;
	private int pingTaskCount = 0; // 使用ServerPingTask 进行心跳的
	private int MinHeart = 5000, MaxHeart = 0; // 心跳可选区间
	private int sucessHeart = MinHeart;// 当前成功心跳,初始化的值为MinHeart
	private final int heartStep = 5000;// 心跳增加步长 ,在测试期使用
	private final int sucessStep = 2000; // 稳定期的探测步长
	private int currentHeart ; // 当前心跳初始值为successHeart
	private final int NAT_MOIBLE = 5* 60 * 1000; // 移动NAT 老化时间
	private final int NAT_UNION = 5* 60 * 1000; // 联通NAT 老化时间
	private final int NAT_TELECOM = 28 * 60 * 1000; // 电信NAT 老化时间
	private int currentConnType = -1; // 连接类型
	private int currentSimOperate = -1;// 手机卡运营商 ,具体类型常亮在 NetUtils中,当前实际的网络类型
	private XmppManager xmppManager;

	private PingManager pingManager;
	
	private static HeartManager heartManger = null;
	private Timer calculateTimer; // 测量heart 长度使用的时间器
	
	private StateRecoder recoder = new StateRecoder();
	
	
	private HeartManager (XmppManager xmppManager) {
		this.xmppManager = xmppManager;
		pingManager = PingManager.getInstanceFor(xmppManager.getConnection());
		// 当前的网络的实际类型通过方法获取到
		currentConnType = NetUtils.networkConnectionType(xmppManager.getContext());
		currentSimOperate = NetUtils.networkOperate(xmppManager.getContext());
		switch (currentConnType) {
		// 根据网络类型进行判断,当前在sp中存贮的最大的
		case NetUtils.TYPE_WIFI: // WIFI 连接下的最大心跳间隔
			currentHeart = getSPWIFIMaxHeart();
			break;
		case NetUtils.TYPE_MOIBLE:// 手机网络
			currentHeart = getcurrentNetworkMaxHeart();
			break;
		case NetUtils.TYPE_ERROR:
			currentHeart = minFixHeart;
			break;
		}
		
		// 注册接口
		pingManager.registerPingFailedListener(this);
		pingManager.registerPingSucessListener(this);
	}

	public static HeartManager getInstance (XmppManager xmppManager) {
		
		if (heartManger == null) {
			synchronized (HeartManager.class) {
				if (heartManger == null)
					heartManger = new HeartManager(xmppManager);
			}
		}
		return heartManger;
	}

	public int getConnType() {
		return currentConnType;
	}

	public int getSimOperate() {
		return currentSimOperate;
	}


	public void setSimOperate(int simOperate) {
		this.currentSimOperate = simOperate;
	}


	// 测算最优的后台心跳包,最大步长测算,每个星期就行测算.现在环境已经正常.前面已经测试好才有这步.
	// 只有一直测试,一直加时间,一直加到失败,然后失败的上一次的成功时间就是当前的最佳的心跳时间.
	public void calculateBestHeart () {
		// MaxHeart 已经按照网络类型给定了最大的网络间隔 currentHeart 当前心跳已经在sp中取出来了
		if(recoder.isCalcSucess()) {
			// 已经测算成功了,不用测算了,直接按照sucessHeart成功心跳进行.
			fixPing(currentHeart - sucessStep);
			return;
		}
		calculateTimer = new Timer();
		final TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				Log.i(LOGTAG, "开启测算线程");
				recoder.increaseTotal1(); // 心跳包的数量
				// pingMyServer 里面的时间是表示的服务器的响应时间.
				boolean pingMyServer = pingManager.pingMyServer();
				
				if (pingMyServer) { // 成功
					// 成功认定,只要成功
					recoder.setPreSucess(true);// 只要成功了一次就一定有成功心跳,失败后根据判断是否有成功心跳,然后使用成功的心跳进行认定为
					int sucessincrenment1 = recoder.sucessincrenment1();
					Log.i(LOGTAG, "成功认定的次数" + sucessincrenment1 + ", 当前的成功的心跳时间=" + sucessHeart);
					
					
					if (sucessincrenment1 > 5) { // 5次认定法则
						// 5次延时成功认定
						MaxHeart = currentHeart;
						sucessHeart = currentHeart;
						 // 保存到sp中吧.
						saveSpMaxHeart(); // 保存当前的成功心跳时间
						// 成功次数超过5,说明当前测算的时间已经不合适了,那么一定会重新进行测算task的,当前的计算task已经没用,可以取消
						calculateTimer.cancel();
						calculateTimer.purge();
						calculateTimer = null;
						
						// 如果上一波是失败的,那么下一波就不延时测算了.当前的值就可以做保存了
						if (recoder.isPreFaild()) {
							Log.i(LOGTAG, "成功心跳 , 上次心跳失败,这里成功心跳就是当前的成功的心跳;");
							
							// 这里是进行正常的心跳运行
							// 先保存(sucessHeart)然后使用当前的成功心跳时间进行心跳 ,按照成功心跳开始心跳
//							fixPing(sucessHeart - sucessStep); // 比当前心跳小一点点的心跳作为当前的稳定心跳时间
							currentHeart = sucessHeart;
							recoder.setCalcSucess(true);
						} else {
							
							currentHeart += heartStep;
							// 上一波还没失败,还要继续测算
							recoder.clear();
							recoder.setPreSucess(true);
						    // 按照新的心跳的时间去测算.就是当前的连接还未断开,那么按照加步长的方法进行
							calculateBestHeart();
						}
						
					}
					
				} else { // 失败ping服务器失败
					// 这里是将数字加1,5次失败认定
					int andIncrement = recoder.faildIncrenment1();
					Log.i(LOGTAG, "失败认定的次数" + andIncrement + ", 当前的成功的心跳时间=" + sucessHeart);
					
					if (andIncrement > 5) {
						// 这里等于失败,失败之后要断开连接,并且重新连接,然后在进行测算.
						// 失败认定,那么就是使用上次成功的心跳包进行测试.
						calculateTimer.cancel();
						calculateTimer.purge();
						calculateTimer = null;
						
						// 上一波如果是成功的,这一波失败了,那么久按照上一波成功的时间进行心跳.
						if (recoder.isPreSucess()) { 
							// 按照上一次sp中的成功的心跳 进行(sucessHeart上一次成功心跳).下面是开启固定心跳.
							// 上次失败,这里成功,那么就是你了.
							recoder.setCalcSucess(true);// 测算成功了
//							fixPing(sucessHeart - sucessStep);
							currentHeart = sucessHeart;
						} else {
							
							// 减少心跳时间,然后进行心跳测试
							currentHeart = MaxHeart - heartStep;
							MaxHeart = MaxHeart - heartStep;
							
							recoder.clear();
							recoder.setPreFaild(true);
							// 怎么处理重启线程,然后再进来.
							xmppManager.startReconnectionThread();
						}
					}
						
				}
				
			}
		};
		calculateTimer.schedule(timerTask, currentHeart, currentHeart);
		
	}
	
	// 后台稳定心跳,动态调整测略
	private void backgroudStableHeart() {
		// 稳定心跳
		
		// 前台稳定心跳
		
		// 后台稳定心跳
		
		// 无网络、网络时好时坏、偶然失败、NAT超时变小在后台稳定期发生心跳发生失败后，我们使用延迟心跳测试法测试五次。
		// 如果有一次成功，则保持当前心跳值不变；如果五次测试全失败，重新计算合理心跳值。该过程如图4-4所示，有一点需要注意，
		// 每个新建的长连接需要先用短心跳成功维持3次后才用successHeart进行心跳。
		
	}
	
	
	
	// 前台活跃的固定的心跳包
	public void fixPing(int longTimems) {
		Log.i(LOGTAG, "fixPing.... 当前固定心跳时间" + longTimems);
		// 取范围中间的,如果超过就取边界值
//		longTimems = Math.min(Math.max(longTime, MinHeart), MaxHeart);
//		currentHeart = longTimems;
		pingManager.maybeScedulePingServerTask(longTimems/1000);
	}
	
	// 发送三次短心跳包,保证下次测试环境的正常.三次连续返回true表示三次短心跳成功, 环境稳定
	public boolean send3Ping() { // default 5s
		Log.i(LOGTAG, "开始三次短心跳包....");
		boolean pingMyServer = pingManager.pingMyServer();
			
		if (pingMyServer) {
			pingFaildCount = 0;
			pingSucessCount ++;
			
			if (pingSucessCount > 2) // 三次成功
				pingManager.maybeScedulePingServerTask(sucessHeart - sucessStep); 
			else 
				send3Ping();
			pingSucessCount = 0; // 使用完毕清空
			return true;
		} else {
			pingFaildCount ++;
			pingSucessCount = 0;
			if(pingFaildCount > 4) 
				// 连续5次失败.才算失败
				startReconnectionThread();
			else 
				send3Ping();
			pingFaildCount = 0; //使用完毕清空
			return false;
		}
		// 这里在pingManageTask中,成功了.自动继续下一个Task任务 ,利用三次连续短心跳.
		
	}
	
	// 应用程序在前台时
    public void frontTaskActivity() {
    	Log.i(LOGTAG, "HeartManager frontTaskActivity...");
    	// 进行3次短心跳前isPing3Count = true; 设置为true了.
    	boolean send3Ping = send3Ping();
    	if (send3Ping) {
    		fixPing(20000);
    	}
    	
    	
    }
    // 应用程序在后台时候
    public void backgroundTaskActivity() {
    	Log.i(LOGTAG, "HeartManager backgroundTaskActivity...");
    	boolean send3Ping = send3Ping();
    	if (send3Ping) {
    		fixPing(250000);
    	}
    }
	
    public void stopHeart() {
    	// pingmanage已经监控了连接状态,将task任务停止了.这个是否取消不重要.
    	/*pingManager.unregisterPingFailedListener(this);
    	pingManager.unregisterPingSucessListener(this);*/
    	
    }
    
    /**
     * 重新连接.需要进行心跳包时间的调整.
     */
    private void startReconnectionThread () {
    	xmppManager.startReconnectionThread();
    }
     
	// 失败才需要计数,成功不需要计数.
	@Override
	public void pingFailed() {
		Log.i(LOGTAG, "HeartManager pingFailed...");
		
		// 尝试5次,未到5次,使用上一次的当前心跳进行
		// 这里是进行的心跳的正常的工作,如果这里在正在工作的 时候出现了.失败的情况,重新动态的进行计算.
		int faildIncrenment1 = recoder.faildIncrenment1();
		
		if (faildIncrenment1 > 5) {
			xmppManager.startReconnectionThread();
		}
	}


	@Override
	public void pingSucess() {
		Log.i(LOGTAG, "HeartManager pingSucess...");
		// 成功了.
		// 成功了,将当前心跳给成功心跳
		
	}
	
	/**
	 * 保存最大的心跳时间到sp中。存到当前的网络类型下.
	 * @param key 当前的网络类型,比如:Constants.CHINA_MOIBLE
	 * @param connType
	 */
	private void saveSpMaxHeart() {
		// 保存之前要查是否有~~
		SharedPreferences sharedPreferences = xmppManager.getContext().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		if (currentConnType == ConnectivityManager.TYPE_WIFI) {
			// wifi
			sharedPreferences.edit().putInt(Constants.WIFI, sucessHeart).commit();
		} else {
			// 手机网络
			switch (currentSimOperate) {
			case NetUtils.NO_OPERATE: // 上一次没有存运营商
				//  保存当前的网络类型
				// TODO 没有运营商,显然是WIFI.就不在这里处理,下面有处理wifi的
				break;
			case NetUtils.CHINA_MOIBLE: // 移动
				sharedPreferences.edit().putInt(Constants.CHINA_MOIBLE, sucessHeart).commit();
				
				break;
			case NetUtils.CHINA_TELECOM: // 电信
				sharedPreferences.edit().putInt(Constants.CHINA_TELECOM, sucessHeart).commit();
				
				break; 
			case NetUtils.CHINA_UNICON: // 联通
				sharedPreferences.edit().putInt(Constants.CHINA_UNICON, sucessHeart).commit();
				break;
			}
		}
		
		
		// 调用成功心跳稍微小一点的心跳值,作为稳定心跳.
		
	}
	
	/**
	 * 在当前使用手机网络的情形下,获取当前网络下的最大的心跳包.
	 * @param key   eg:Constants.WIFI
	 * @param connType
	 * @return
	 * 
	 * 如果用户进行换号,换网络了,这个时间好像就不能用了.
	 */
	private int getcurrentNetworkMaxHeart () {
		
		SharedPreferences sharedPreferences = xmppManager.getContext().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
//		int netTypeMaxTime = sharedPreferences.getInt(Constants.NETWORKTYPE, NetUtils.TYPE_ERROR);// 获取当前网络类型下的最大时间.
		int operate = sharedPreferences.getInt(Constants.SIM_OPERATE, NetUtils.NO_OPERATE); // 取出上一次的网络类型,运营商
		// SP中取出(上一次的网络运营商)
		// 与
		// 当前的网络运营商
		// 当前的网络类型
		int i = -1;
		switch (currentSimOperate) {
		case NetUtils.NO_OPERATE: // 上一次没有存运营商
			//  保存当前的网络类型
			// TODO 没有运营商,显然是WIFI.就不在这里处理,下面有处理wifi的
			i = 10;
			break;
		case NetUtils.CHINA_MOIBLE: // 移动
			i = sharedPreferences.getInt(Constants.CHINA_MOIBLE, 5 * 60 * 1000); // NAT 最长的老化时间来算的
			break;
		case NetUtils.CHINA_TELECOM: // 电信
			i = sharedPreferences.getInt(Constants.CHINA_TELECOM, 5 * 60 * 1000);
			break; 
		case NetUtils.CHINA_UNICON: // 联通
			i = sharedPreferences.getInt(Constants.CHINA_UNICON, 5 * 60 * 1000);
			break;
		}
		return i;
	}
	
	/**
	 * 获取WiFi连接下的最大的心跳包
	 * @return
	 */
	private int getSPWIFIMaxHeart() {
		SharedPreferences sharedPreferences = xmppManager.getContext().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getInt(Constants.WIFI, 7 * 60 * 1000); // 后面是最大的时间
	}
	
} 
