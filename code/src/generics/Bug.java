package generics;

import java.util.Date;

public class Bug {
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLabels() {
		return labels;
	}
	public void setLabels(String labels) {
		this.labels = labels;
	}
	public Date getReportDate() {
		return reportDate;
	}
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getResoveDate() {
		return resolveDate;
	}
	public void setResoveDate(Date resoveDate) {
		this.resolveDate = resoveDate;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	public Bug (String key, String type, String priority, 
			String status, String labels, 
			Date reportDate, Date resolveDate, Date updateDate, 
			String summary, String description) {
		
		this.key = key;
		this.type = type;
		this.priority = priority;
		this.status = status;
		this.labels = labels;
		this.reportDate = reportDate;
		this.resolveDate = resolveDate;
		this.updateDate = updateDate;
		this.summary = summary;
		this.description = description;
	}
	
	public void setCrashSignature(String signature) {
		this.crashSignature = signature;
	}

	public String toString() {
		String content = key + "\t" + type + "\t" + priority + "\t" + status + "\t" + reportDate.toString() + "\t" + resolveDate.toString();
		return content;
				
	}
	
	public String type;
	public String priority;
	public String status;
	public String labels;
	public Date reportDate;
	public Date resolveDate;
	public Date updateDate;
	public String summary;
	public String description;
	public String key;
	public String crashSignature = "";
}
