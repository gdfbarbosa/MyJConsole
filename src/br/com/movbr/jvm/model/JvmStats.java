package br.com.movbr.jvm.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "jvmstats")
public class JvmStats extends BaseEntidade<Integer> {
	private static final long serialVersionUID = 7247021877000982173L;
	
	private Integer id;
	private BigDecimal cpuUsage;
	private Long heapUsage;
	private Integer loadedClassCount;
	private Integer threadCount;
	private Long cmsUsage;
	private Long edenUsage;
	private Long nonHeapUsage;
	private Long cmsUsageThresholdCount;
	private Date date;
	private String host;
	private String port;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", insertable = true, updatable = false, nullable = false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name = "cpu_usage")
	public BigDecimal getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(BigDecimal cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	@Column(name = "heap_usage")
	public Long getHeapUsage() {
		return heapUsage;
	}

	public void setHeapUsage(Long heapUsage) {
		this.heapUsage = heapUsage;
	}

	@Column(name = "loaded_class_count")
	public Integer getLoadedClassCount() {
		return loadedClassCount;
	}

	public void setLoadedClassCount(Integer loadedClassCount) {
		this.loadedClassCount = loadedClassCount;
	}

	@Column(name = "thread_count")
	public Integer getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(Integer threadCount) {
		this.threadCount = threadCount;
	}

	@Column(name = "cms_usage")
	public Long getCmsUsage() {
		return cmsUsage;
	}

	public void setCmsUsage(Long cmsUsage) {
		this.cmsUsage = cmsUsage;
	}

	@Column(name = "eden_usage")
	public Long getEdenUsage() {
		return edenUsage;
	}

	public void setEdenUsage(Long edenUsage) {
		this.edenUsage = edenUsage;
	}

	@Column(name = "non_heap_usage")
	public Long getNonHeapUsage() {
		return nonHeapUsage;
	}

	public void setNonHeapUsage(Long nonHeapUsage) {
		this.nonHeapUsage = nonHeapUsage;
	}

	@Column(name = "cms_usage_threshold_count")
	public Long getCmsUsageThresholdCount() {
		return cmsUsageThresholdCount;
	}

	public void setCmsUsageThresholdCount(Long cmsUsageThresholdCount) {
		this.cmsUsageThresholdCount = cmsUsageThresholdCount;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Column(name = "host")
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Column(name = "port")
	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
}