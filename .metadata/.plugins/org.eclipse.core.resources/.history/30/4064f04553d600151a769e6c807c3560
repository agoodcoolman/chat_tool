/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidpn.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import org.androidpn.client.heart.HeartManager;
import org.androidpn.client.packetlistener.MessagePacketListener;
import org.androidpn.client.packetlistener.NotificationPacketListener;
import org.androidpn.client.threadpool.ExecutorThreadPool;
import org.androidpn.client.uitls.DateUtils;
import org.androidpn.client.uitls.LogUtil;
import org.androidpn.client.uitls.NetUtils;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.ping.PingManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.Log;

/**
 * This class is to manage the XMPP connection between client and server.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public class XmppManager {

    private static final String LOGTAG = LogUtil.makeLogTag(XmppManager.class);

    private static final String XMPP_RESOURCE_NAME = "AndroidpnClient";

    private Context context;

    private NotificationService.TaskSubmitter taskSubmitter;

    private NotificationService.TaskTracker taskTracker;

    private SharedPreferences sharedPrefs;

    private String xmppHost;

    private int xmppPort;

    private XMPPConnection connection;

    private String username;

    private String password;

    private int networkType ;// 网络类型(WIFI/手机网络)
    
//    private int simOperate ;// 手机卡运营商 ,具体类型常亮在 NetUtils中
    
    private ConnectionListener connectionListener;

    private PacketListener notificationPacketListener;

    private Handler handler;

    private List<Runnable> taskList;

    private boolean running = false;

    private Future<?> futureTask;

    private Thread reconnection;
    // 心跳包的广播接收者
//    private HeartbeatReceiver heartbeatReceiver;
    // 启动手机定时闹钟的手机意图
    private PendingIntent operation;
    // 闹钟管理对象
    private AlarmManager alarmManager;

    public XmppManager(NotificationService notificationService) {
        context = notificationService;
        taskSubmitter = notificationService.getTaskSubmitter();
        taskTracker = notificationService.getTaskTracker();
        sharedPrefs = notificationService.getSharedPreferences();

        xmppHost = sharedPrefs.getString(Constants.XMPP_HOST, "localhost");
        xmppPort = sharedPrefs.getInt(Constants.XMPP_PORT, 5222);
        username = sharedPrefs.getString(Constants.XMPP_USERNAME, "");
        password = sharedPrefs.getString(Constants.XMPP_PASSWORD, "");
        
        
        connectionListener = new PersistentConnectionListener(this);
        notificationPacketListener = new NotificationPacketListener(this);
//        messagePacketListener = new MessagePacketListener(this);
        
        handler = new Handler();
        taskList = new ArrayList<Runnable>();
        reconnection = new ReconnectionThread(this);
        
        
    }

    public Context getContext() {
        return context;
    }
    // 这个方法在NotificationService的onCreate调用一次
    // 然后在广播中也调用了一次,网络连接畅通也要接收一个广播,启动一个Connect连接
    public void connect() {
        Log.i(LOGTAG, "connect() 当前线程名字  ...."+Thread.currentThread().getName());
        submitLoginTask();
    }

    public void disconnect() {
        Log.i(LOGTAG, "disconnect()...");
        // 取消闹钟广播什么的
//        unregisterHeartBeatReceiver();

        terminatePersistentConnection();
    }

    // 终止持久链接
    public void terminatePersistentConnection() {
        Log.i(LOGTAG, "terminatePersistentConnection()...");
        Runnable runnable = new Runnable() {

            final XmppManager xmppManager = XmppManager.this;

            public void run() {
                if (xmppManager.isConnected()) {
                    Log.i(LOGTAG, "terminatePersistentConnection()... run()");
                    xmppManager.getConnection().removePacketListener(
                            xmppManager.getNotificationPacketListener());
//                    xmppManager.getConnection().removePacketListener(getMessagePacketListener());
                   
                    xmppManager.getConnection().disconnect();
                }
                xmppManager.runTask();
            }

        };
        addTask(runnable);
    }

    public XMPPConnection getConnection() {
        return connection;
    }

    public void setConnection(XMPPConnection connection) {
        this.connection = connection;
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

    public ConnectionListener getConnectionListener() {
        return connectionListener;
    }

    public PacketListener getNotificationPacketListener() {
        return notificationPacketListener;
    }

//    public MessagePacketListener getMessagePacketListener() {
//		return messagePacketListener;
//	}
    
	public void startReconnectionThread() {
        synchronized (taskList) {
            // FIXME 这里需要修复,如果连接时成功的,但是register环节,或者是登录环节出现问题
            // 那么是成功连接的, 这是进入重连线程,无法启动重新连接
        	terminatePersistentConnection(); // 先关闭其他的连接
            if ((reconnection == null || !reconnection.isAlive()) || 
            		( (getConnection() == null) 
            				|| !getConnection().isAuthenticated())) {
                reconnection  = new ReconnectionThread(this);
                reconnection.setName("Xmpp Reconnection Thread");
                reconnection.start();
            }
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void reregisterAccount() {
        removeAccount();
        submitLoginTask();
        runTask();
    }

    public List<Runnable> getTaskList() {
        return taskList;
    }

    public Future<?> getFutureTask() {
        return futureTask;
    }
    // 启动任务栈
    public void runTask() {
        Log.i(LOGTAG, "runTask()...");
        synchronized (taskList) {
            running = false;
            futureTask = null;
            if (!taskList.isEmpty()) {
                Runnable runnable = (Runnable) taskList.get(0);
                Log.i(LOGTAG, "runTask()..."+runnable.getClass().getSimpleName()+taskList.size());
                taskList.remove(0);
                running = true;
                futureTask = taskSubmitter.submit(runnable);
                if (futureTask == null) {
                    taskTracker.decrease();
                }
            }
        }
        taskTracker.decrease();
        Log.i(LOGTAG, "runTask()...done");
    }

    private String newRandomUUID() {
        String uuidRaw = UUID.randomUUID().toString();
        return uuidRaw.replaceAll("-", "");
    }

    private boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    private boolean isAuthenticated() {
        return connection != null && connection.isConnected()
                && connection.isAuthenticated();
    }

    private boolean isRegistered() {
        return sharedPrefs.contains(Constants.XMPP_USERNAME)
                && sharedPrefs.contains(Constants.XMPP_PASSWORD);
    }

    private void submitConnectTask() {
        Log.i(LOGTAG, "submitConnectTask()...");
        addTask(new ConnectTask());
    }

    private void submitRegisterTask() {
        Log.i(LOGTAG, "submitRegisterTask()...");
        submitConnectTask();
        addTask(new RegisterTask());
    }

    private void submitLoginTask() {
        Log.i(LOGTAG, "submitLoginTask()...");
        submitRegisterTask();
        addTask(new LoginTask());
    }

    private void addTask(Runnable runnable) {
        Log.i(LOGTAG, "addTask(runnable)..."+ runnable.getClass().getSimpleName());
        taskTracker.increase();
        synchronized (taskList) {
            if (taskList.isEmpty() && !running) {
                //空或者是 非执行状态
                running = true;
                futureTask = taskSubmitter.submit(runnable);
                if (futureTask == null) {
                    taskTracker.decrease();
                }
            } else {
                taskList.add(runnable);
            }
        }
        Log.i(LOGTAG, "addTask(runnable)... done");
    }
    /**
     * 移除列表中的线程
     * @param dropNum
     */
    private void dropTask(int dropNum) {
        synchronized (taskList) {
            if (taskList.size() >= dropNum) {
                for (int i = 0; i < dropNum; i++) {
                    Log.i(LOGTAG, "dropTask(runnable)... done");
                    taskList.remove(0);
                    taskTracker.decrease();
                }
            }
        }
    }

    public void cleanTask() {
    	dropTask(taskList.size());
    }
    
    private void removeAccount() {
        Editor editor = sharedPrefs.edit();
        editor.remove(Constants.XMPP_USERNAME);
        editor.remove(Constants.XMPP_PASSWORD);
        editor.commit();
    }

    /**
     * A runnable task to connect the server. 
     */
    private class ConnectTask implements Runnable {

        final XmppManager xmppManager;

        private ConnectTask() {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
            this.xmppManager = XmppManager.this;
        }

        public void run() {
            Log.i(LOGTAG, "ConnectTask.run()..."+"taskList's number"+taskList.size());

            if (!xmppManager.isConnected()) {
                // Create the configuration for this new connection
                ConnectionConfiguration connConfig = new ConnectionConfiguration(
                        xmppHost, xmppPort);
                // connConfig.setSecurityMode(SecurityMode.disabled);
                connConfig.setSecurityMode(SecurityMode.required);
                connConfig.setSASLAuthenticationEnabled(false);
                connConfig.setCompressionEnabled(false);

                XMPPConnection connection = new XMPPConnection(connConfig);
                xmppManager.setConnection(connection);

                try {
                    // Connect to the server
                    connection.connect();
                    Log.i(LOGTAG, "XMPP connected successfully"+"taskList"+taskList.size());

                    // packet provider
                    ProviderManager.getInstance().addIQProvider("notification",
                            "androidpn:iq:notification",
                            new NotificationIQProvider());
                    
                    xmppManager.runTask();
                } catch (XMPPException e) {
                    Log.e(LOGTAG, "XMPP connection failed", e);
                    dropTask(2);
                    xmppManager.startReconnectionThread();
                    xmppManager.runTask();
                }

            } else {
                Log.i(LOGTAG, "XMPP connected already");
                xmppManager.runTask();
            }
        }
    }

    /**
     * A runnable task to register a new user onto the server. 
     */
    private class RegisterTask implements Runnable {

        final XmppManager xmppManager;

        private RegisterTask() {
            xmppManager = XmppManager.this;
        }
        // 已经成功注册了
        private boolean isSuccessRegister = false;
        // 是否已经连接超时
        private boolean isTimeOut = false;
        public void run() {
            Log.i(LOGTAG, "RegisterTask.run()..."+"taskList"+taskList.size());

            if (!xmppManager.isRegistered()) {
            	/*
            	 *  SharedPreferences spConfig = LoginActivity.this.getSharedPreferences("config", Context.MODE_PRIVATE);
			        Editor edit = spConfig.edit();
			        edit.putString("user_no", "police001");
			        edit.putString("Password", "000000");
            	 */
            	/* final String newUsername = getContext().getSharedPreferences("config", Context.MODE_PRIVATE)
            	.getString("user_no", "");
            	 final String newPassword = getContext().getSharedPreferences("config", Context.MODE_PRIVATE)
            			 .getString("Password", "");*/

//                final String newUsername = newRandomUUID();
//                final String newPassword = newRandomUUID();
                final String newUsername = XmppPush.newUsername;
                final String newPassword = XmppPush.newPassword;

                Registration registration = new Registration();

                PacketFilter packetFilter = new AndFilter(new PacketIDFilter(
                        registration.getPacketID()), new PacketTypeFilter(
                        IQ.class));

                PacketListener packetListener = new PacketListener() {

                    public void processPacket(Packet packet) {
                        synchronized (RegisterTask.class) {
                            Log.i("RegisterTask.PacketListener", "processPacket().....");
                            Log.i("RegisterTask.PacketListener", "packet=" + packet.toXML());

                            if (packet instanceof IQ) {
                                IQ response = (IQ) packet;
                                if (response.getType() == IQ.Type.ERROR) {
                                    if (!response.getError().toString().contains(
                                            "409")) {
                                        Log.e(LOGTAG,
                                                "Unknown error while registering XMPP account! "
                                                        + response.getError()
                                                        .getCondition());
                                    }
                                } else if (response.getType() == IQ.Type.RESULT) {
                                    xmppManager.setUsername(newUsername);
                                    xmppManager.setPassword(newPassword);
                                    Log.i(LOGTAG, "username=" + newUsername);
                                    Log.i(LOGTAG, "password=" + newPassword);

                                    Editor editor = sharedPrefs.edit();
                                    editor.putString(Constants.XMPP_USERNAME,
                                            newUsername);
                                    editor.putString(Constants.XMPP_PASSWORD,
                                            newPassword);
                                    editor.commit();

                                    // 执行连接成功 并且没有超时,可以runTask 执行下一个任务 ,
                                    // 如果已经超时了,就已经重连了,这里不需要执行下一步
                                    if (!isSuccessRegister && !isTimeOut) {
                                        Log.i(LOGTAG,"Account registered successfully");
                                        isSuccessRegister = true;
                                        xmppManager.runTask();
                                    }
                                }
                            } 
                        }
                    }
                };

                connection.addPacketListener(packetListener, packetFilter);

                registration.setType(IQ.Type.SET);
                // registration.setTo(xmppHost);
                // Map<String, String> attributes = new HashMap<String, String>();
                // attributes.put("username", rUsername);
                // attributes.put("password", rPassword);
                // registration.setAttributes(attributes);
                registration.addAttribute("username", newUsername);
                registration.addAttribute("password", newPassword);
                // 这里要进行等待,等待是否执行发送成功,如果失败重新连接
                connection.sendPacket(registration);
                try {
                    Thread.sleep(1000*10);
                    // 加同步
                    synchronized (RegisterTask.class) {
                        // 如果没有注册上,应该是超时了.重新连接
                        // 重连,要给超时标志
                        if (!isSuccessRegister) {
                            isTimeOut = true;
                            Log.i(LOGTAG, "Account registered faild time out, reconnting...");
                            dropTask(1);
                            xmppManager.startReconnectionThread();
                            xmppManager.runTask();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                Log.i(LOGTAG, "Account registered already");
                xmppManager.runTask();
            }
        }
    }

    /**
     * A runnable task to log into the server. 
     */
    private class LoginTask implements Runnable {

        final XmppManager xmppManager;

        private LoginTask() {
            this.xmppManager = XmppManager.this;
        }

        public void run() {
            Log.i(LOGTAG, "LoginTask.run()..."+",taskList"+taskList.size());

            if (!xmppManager.isAuthenticated()) {
                Log.i(LOGTAG, "username=" + username);
                Log.i(LOGTAG, "password=" + password);

                try {
                    xmppManager.getConnection().login(
                            xmppManager.getUsername(),
                            xmppManager.getPassword(), XMPP_RESOURCE_NAME);
                    Log.i(LOGTAG, "Loggedn in successfully");

                    // connection listener
                    if (xmppManager.getConnectionListener() != null) {
                        xmppManager.getConnection().addConnectionListener(
                                xmppManager.getConnectionListener());
                        Log.i(LOGTAG, "register connection listener in successfully");
                    }

                    // packet filter
                    PacketFilter packetFilter = new PacketTypeFilter(NotificationIQ.class);
                    // packet listener
                    PacketListener packetListener = xmppManager.getNotificationPacketListener();
                    connection.addPacketListener(packetListener, packetFilter);

                    // 注册用户之前的消息 接收
//                    PacketTypeFilter messageTypeFilter = new PacketTypeFilter(Message.class);
//                    MessagePacketListener messagePacketListener = xmppManager.getMessagePacketListener();
//                    connection.addPacketListener(messagePacketListener, messageTypeFilter);

                    // 开启心跳包
                    startHeartManager();
                    Log.i(LOGTAG, "start heart beat in successfully");
                    if (!xmppManager.isConnected()) {
                        dropTask(taskList.size());
                    }
                } catch (XMPPException e) {
                    // 执行xmppManager.setConnection(null);将判断是否已经连接服务器置空。这样程序就会自动重新去连接服务器，成功建立新的会话。
                    xmppManager.setConnection(null);
                    Log.e(LOGTAG, "LoginTask.run()... xmpp error");
                    Log.e(LOGTAG, "Failed to login to xmpp server. Caused by: "
                            + e.getMessage());
                    String INVALID_CREDENTIALS_ERROR_CODE = "401";
                    String errorMessage = e.getMessage();
                    
                    xmppManager.startReconnectionThread();
                    
                    if (errorMessage != null
                            && errorMessage.contains(INVALID_CREDENTIALS_ERROR_CODE)) {
                        xmppManager.reregisterAccount();
                        return;
                    }

                } catch (Exception e) {
                    Log.e(LOGTAG, "LoginTask.run()... other error");
                    Log.e(LOGTAG, "Failed to login to xmpp server. Caused by: "
                            + e.getMessage());
                    xmppManager.startReconnectionThread();
                } finally {
                    xmppManager.runTask();
                }
            } else {
                Log.i(LOGTAG, "Logged in already");
                xmppManager.runTask();
            }

        }
    }
    /**
     * 这里代表连接成功,开始定时闹钟.注册广播接收.发送定时的心跳
     */
    public void startHeartManager() {
        
    	HeartManager instance = HeartManager.getInstance(XmppManager.this);
    	// 连接成功,三次心跳
    	boolean send3Ping = instance.send3Ping();
    	// 是否要进行测算,这里是即使重新开了线程,这里照样是.
    	if (send3Ping && NetUtils.TYPE_MOIBLE == networkType && DateUtils.getDay_OF_WEEK() == 4) {
    		// 测算要求是星期三,并且是手机网络. 这里是每个星期四进行测算
    		instance.calculateBestHeart();
    	}
    	instance.frontTaskActivity();
    }
    // 切换到前台
    public void frontTask() {
    	Log.i(LOGTAG, "xmppManager frontTask ...");
    	if (getConnection() != null && getConnection().isAuthenticated()) {
    		HeartManager instance = HeartManager.getInstance(XmppManager.this);
    		instance.frontTaskActivity();
    	}
	}

	// 切换到后台
	public void backgroundTask() {
		Log.i(LOGTAG, "xmppManager backgroundTask ...");
		if (getConnection() != null && getConnection().isAuthenticated()) {
			HeartManager instance = HeartManager.getInstance(XmppManager.this);
			instance.backgroundTaskActivity();
    	}
	}
   
	
	
  /*  *//**
     * 开启闹钟,定时发送
     *//*
    public static int ALARM_REQUESTCODE = 100;

    *//**
     * 聊天消息的监听
     *//*
	private MessagePacketListener messagePacketListener;
	
	

    *//**
     * 开启手机的定时闹钟功能
     *//*
    public void startAlram() {
        Intent intent = new Intent("com.deao.alarm.heartbeat");
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        operation = PendingIntent.getBroadcast(context, ALARM_REQUESTCODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(alarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, operation);
    }
    *//**
     * 注销接收手机定时心跳包的广播接收者
     *//*
    public void unregisterHeartBeatReceiver() {
        synchronized (HeartbeatReceiver.class) {
            if (heartbeatReceiver != null) {
                try {
                    // 如果已经注册了,取消注册防止异常
                    context.unregisterReceiver(heartbeatReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                heartbeatReceiver = null;
                if (operation != null) {
                    alarmManager.cancel(operation);
                    operation = null;
                }
            }
        }
    }
    *//**
     * 这是注册一个广播界首者,接收来自闹钟管家的定时广播,发送心跳包
     *//*
    public void registerHeartBeat() {
        synchronized (HeartbeatReceiver.class) {
            heartbeatReceiver = new HeartbeatReceiver();
            IntentFilter intentFilter = new IntentFilter("com.deao.alarm.heartbeat");
            context.registerReceiver(heartbeatReceiver, intentFilter);
        }

    }
    *//**
     * 注册一个接收定时广播的广播接受者,主要为了提供保持连接不挂掉的功能
     * @author Administrator
     *
     *//*
    public class HeartbeatReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(LOGTAG, "send heart beat ...");
            // 发送数据,这个数据是空哒
             
            PingManager instanceFor = PingManager.getInstanceFor(getConnection());
            SmackConfiguration.setPacketReplyTimeout(6000);
            boolean pingMyServer = instanceFor.pingMyServer();
            if (pingMyServer) {
            	Log.i(LOGTAG, "心跳包");
            }
//            connection.sendPacket(heart);
        }

    }*/
    
    public void sendIMMessage(String messageBody, String receiptUser) {
    	Log.i(LOGTAG, "send message packet ...");
    	 // 发送数据 聊天的发送数据
        Message message = new Message(receiptUser + "@deao.com/android");
        message.setBody("朕的发送消息就指着你啦!");
        message.setType(Message.Type.chat);
        message.setDefaultXmlns("jabber:message:send");
        connection.sendPacket(message);
    }

}
