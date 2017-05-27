/*
 * Copyright (C) 2016 Alberto Pires de Oliveira Neto
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
package io.github.albertopires.mjc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import br.com.movbr.jvm.dao.JvmStatsDAO;
import br.com.movbr.jvm.model.JdkVersion;
import br.com.movbr.jvm.model.JvmStats;

/**
 *
 * @author Alberto Pires de Oliveira Neto
 */
public class JConsoleM {
	private MBeanServerConnection mbsc;
	private int ncpu = 1;
	private long sample;
	private static ApplicationContext applicationContext;

	public static void main(String[] args) throws Exception {
		applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		new Thread(new LogInvoker(obterConfiguracaoWildfly(System.getProperty("jvm.host")))).start();
	}

	private static ServerConfiguration obterConfiguracaoWildfly(String host) {
		ServerConfiguration serverConfiguration = new ServerConfiguration();
		serverConfiguration.setHost(host);
		serverConfiguration.setPort("9995");
		serverConfiguration.setUser("jconsole");
		serverConfiguration.setPassword("*100SvnGit");
		serverConfiguration.setAuthenticate(Boolean.TRUE);
		serverConfiguration.setJdkVersion(JdkVersion.JDK8);
		return serverConfiguration;
	}
	
	private static ServerConfiguration obterConfiguracaoJboss(String host) {
		ServerConfiguration serverConfiguration = new ServerConfiguration();
		serverConfiguration.setHost(host);
		serverConfiguration.setPort("7779");
		serverConfiguration.setAuthenticate(Boolean.FALSE);
		serverConfiguration.setJdkVersion(JdkVersion.JDK6);
		return serverConfiguration;
	}

	public static void jvmLog(ServerConfiguration serverConfiguration) throws Exception {
		JConsoleM jc;

		try {
			jc = getInstance(serverConfiguration);
			jc.setSample(4000);
			for (;;) {
				try {
					JvmStats jvmStats = jc.runStats(serverConfiguration);
					JvmStatsDAO jvmStatsDAO = (JvmStatsDAO) applicationContext.getBean("jvmStatsDAO");
					jvmStatsDAO.save(jvmStats);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Loop Exception : " + e.getMessage());
					jc = getInstance(serverConfiguration);
					jc.setSample(4000);
				}
			}
		} catch (Exception e) {
			System.err.println("Exception : " + e.getMessage());
		}
	}

	private JConsoleM(ServerConfiguration serverConfiguration) throws Exception {
		JMXConnector jmxc = null;
		if (serverConfiguration.getAuthenticate()) {
			String urlStr = "service:jmx:http-remoting-jmx://" + serverConfiguration.getHost() + ":" + serverConfiguration.getPort();
			Map<String, Object> env = new HashMap<String, Object>();
			String[] creds = {serverConfiguration.getUser(), serverConfiguration.getPassword()};
			env.put(JMXConnector.CREDENTIALS, creds);
			jmxc = JMXConnectorFactory.connect(new JMXServiceURL(urlStr), env);
		} else {
			String urlStr = "service:jmx:rmi:///jndi/rmi://" + serverConfiguration.getHost() + ":" + serverConfiguration.getPort() + "/jmxrmi";
//			String urlStr = "service:jmx:remote+http://" + serverConfiguration.getHost() + ":" + serverConfiguration.getPort();
			JMXServiceURL url = new JMXServiceURL(urlStr);
			jmxc = JMXConnectorFactory.connect(url, null);
		}
		mbsc = jmxc.getMBeanServerConnection();
		ncpu = getAvailableProcessors();
	}

	public static JConsoleM getInstance(ServerConfiguration serverConfiguration) {
		JConsoleM jc = null;
		
		while (jc == null) {
			try {
				jc = new JConsoleM(serverConfiguration);
			} catch (Exception ex) {
				jc = null;
			}

			try {
				Thread.sleep(10000);
			} catch (InterruptedException ex) {
				System.err.println("InterruptedException : " + ex.getMessage());
			}
		}
		return jc;
	}

	public JvmStats runStats(ServerConfiguration serverConfiguration) throws Exception {
		JvmStats jvmStats = new JvmStats();
		jvmStats.setCpuUsage(new BigDecimal(String.format("%.2f", getCpuUsage())));
		jvmStats.setHeapUsage(getHeapUsage());
		jvmStats.setLoadedClassCount(getLoadedClassCount());
		jvmStats.setThreadCount(getThreadCount());
		jvmStats.setCmsUsage(getCMSUsage(serverConfiguration));
		jvmStats.setEdenUsage(getEdenUsage(serverConfiguration));
		jvmStats.setNonHeapUsage(getNonHeapUsage());
		jvmStats.setCmsUsageThresholdCount(getCMSUsageThresholdCount(serverConfiguration));
		jvmStats.setDate(new Date());
		jvmStats.setHost(serverConfiguration.getHost());
		jvmStats.setPort(serverConfiguration.getPort());
		getDeadLockedThreads(serverConfiguration);
		return jvmStats;
	}

	public double getCpuUsage() throws Exception {
		long c, u;
		double ec, eu;

		c = getOSProcessCpuTime();
		u = getUpTime();
		Thread.sleep(sample);
		ec = (getOSProcessCpuTime() - c) / 1000000;
		eu = (getUpTime() - u);
		// System.err.println("C :" + ec + " U : " + eu);
		return (ec / (eu * ncpu)) * 100;
	}

	public final int getAvailableProcessors() throws Exception {
		ObjectName mbeanName;
		mbeanName = new ObjectName("java.lang:type=OperatingSystem");
		Integer ut;
		ut = (Integer) mbsc.getAttribute(mbeanName, "AvailableProcessors");
		return ut.intValue();
	}

	public long getUpTime() throws Exception {
		ObjectName mbeanName;
		mbeanName = new ObjectName("java.lang:type=Runtime");
		Long ut;
		ut = (Long) mbsc.getAttribute(mbeanName, "Uptime");
		return ut.longValue();
	}

	public long getOSProcessCpuTime() throws Exception {
		ObjectName mbeanName;
		mbeanName = new ObjectName("java.lang:type=OperatingSystem");
		Long ut;
		ut = (Long) mbsc.getAttribute(mbeanName, "ProcessCpuTime");
		return ut.longValue();
	}

	public long getHeapUsage() throws Exception {
		ObjectName mbeanName;
		mbeanName = new ObjectName("java.lang:type=Memory");
		CompositeDataSupport o;
		o = (CompositeDataSupport) mbsc.getAttribute(mbeanName,
				"HeapMemoryUsage");
		return ((Long) o.get("used")).longValue();
	}

	public long getNonHeapUsage() throws Exception {
		ObjectName mbeanName;
		mbeanName = new ObjectName("java.lang:type=Memory");
		CompositeDataSupport o;
		o = (CompositeDataSupport) mbsc.getAttribute(mbeanName,
				"NonHeapMemoryUsage");
		return ((Long) o.get("used")).longValue();
	}

	public int getThreadCount() throws Exception {
		ObjectName mbeanName;
		mbeanName = new ObjectName("java.lang:type=Threading");
		Integer ut;
		ut = (Integer) mbsc.getAttribute(mbeanName, "ThreadCount");
		return ut.intValue();
	}

	public int getLoadedClassCount() throws Exception {
		ObjectName mbeanName;
		mbeanName = new ObjectName("java.lang:type=ClassLoading");
		Integer ut;
		ut = (Integer) mbsc.getAttribute(mbeanName, "LoadedClassCount");
		return ut.intValue();
	}

	public long getCMSUsageThresholdCount(ServerConfiguration serverConfiguration) throws Exception {
		ObjectName mbeanName = null;
		if (serverConfiguration.getJdkVersion().equals(JdkVersion.JDK6)) {
			mbeanName = new ObjectName("java.lang:type=MemoryPool,name=CMS Old Gen");
		} else if (serverConfiguration.getJdkVersion().equals(JdkVersion.JDK8)) {
			mbeanName = new ObjectName("java.lang:type=MemoryPool,name=G1 Old Gen");
		}
		Long ut;
		ut = (Long) mbsc.getAttribute(mbeanName, "UsageThresholdCount");
		return ut.longValue();
	}

	public long getCMSUsage(ServerConfiguration serverConfiguration) throws Exception {
		ObjectName mbeanName = null;
		if (serverConfiguration.getJdkVersion().equals(JdkVersion.JDK6)) {
			mbeanName = new ObjectName("java.lang:type=MemoryPool,name=CMS Old Gen");			
			CompositeDataSupport o;
			o = (CompositeDataSupport) mbsc.getAttribute(mbeanName, "Usage");
			return ((Long) o.get("used")).longValue();
		} else {
			return 0L;
		}
	}

	public long getEdenUsage(ServerConfiguration serverConfiguration) throws Exception {
		ObjectName mbeanName = null;
		if (serverConfiguration.getJdkVersion().equals(JdkVersion.JDK6)) {
			mbeanName = new ObjectName("java.lang:type=MemoryPool,name=Par Eden Space");
		} else if (serverConfiguration.getJdkVersion().equals(JdkVersion.JDK8)) {
			mbeanName = new ObjectName("java.lang:type=MemoryPool,name=G1 Eden Space");
		}
		CompositeDataSupport o;
		o = (CompositeDataSupport) mbsc.getAttribute(mbeanName, "Usage");
		return ((Long) o.get("used")).longValue();
	}

	public final void getDeadLockedThreads(ServerConfiguration serverConfiguration) throws Exception {
		ObjectName mbeanName;
		mbeanName = new ObjectName("java.lang:type=Threading");
		long[] dl = (long[]) mbsc.invoke(mbeanName, "findDeadlockedThreads",
				null, null);
		StringBuilder sb;
		if (dl != null) {
			sb = new StringBuilder();
			sb.append("Dead Lock Detected - Host:");
			sb.append(serverConfiguration.getHost());
			sb.append("\n");
			for (int i = 0; i < dl.length; i++) {
				sb.append("Thread " + dl[i] + "\n");
			}
		}
	}

	public String[] getRcptList(Properties conf) {
		int i = 0;
		String addr;
		ArrayList<String> addrList = new ArrayList<String>();
		while (true) {
			addr = conf.getProperty("mail.rcpto." + i);
			i++;
			if (addr != null)
				addrList.add(addr);
			if (addr == null)
				break;
		}
		return addrList.toArray(new String[0]);
	}

	public void showInfo() throws Exception {
		String domains[] = mbsc.getDomains();
		Arrays.sort(domains);
		for (String domain : domains) {
			echo("\tDomain = " + domain);
		}
		echo("\nMBeanServer default domain = " + mbsc.getDefaultDomain());
		echo("\nMBean count = " + mbsc.getMBeanCount());

		echo("\nQuery MBeanServer MBeans:");
		Set<ObjectName> names = new TreeSet<ObjectName>(mbsc.queryNames(null,
				null));
		for (ObjectName name : names) {
			echo("\tObjectName = " + name);
		}
	}

	private static void echo(String msg) {
		System.out.println(msg);
	}

	public long getSample() {
		return sample;
	}

	public void setSample(long sample) {
		this.sample = sample;
	}
}

class LogInvoker implements Runnable {
	private ServerConfiguration serverConfiguration;

	public LogInvoker(ServerConfiguration serverConfiguration) {
		this.serverConfiguration = serverConfiguration;
	}

	@Override
	public void run() {
		try {
			JConsoleM.jvmLog(serverConfiguration);
		} catch (Exception ex) {
			System.err.println("Thread Exception " + ex.getMessage());
		}
	}

	public ServerConfiguration getServerConfiguration() {
		return serverConfiguration;
	}

	public void setServerConfiguration(ServerConfiguration serverConfiguration) {
		this.serverConfiguration = serverConfiguration;
	}
}
