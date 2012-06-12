package de.akuz.android.smsalarm.data;

import java.util.List;

public class AlarmResponseConfiguration {

	private long id;

	private List<AlarmResponse> availableResponses;
	private boolean respondtoReceivedNumber;
	private String responseNumber;
	private AlarmResponseTypes responseType;
	private long parentId;

	public List<AlarmResponse> getAvailableResponses() {
		return availableResponses;
	}

	public void setAvailableResponses(List<AlarmResponse> availableResponses) {
		this.availableResponses = availableResponses;
	}

	public boolean isRespondtoReceivedNumber() {
		return respondtoReceivedNumber;
	}

	public void setRespondtoReceivedNumber(boolean respondtoReceivedNumber) {
		this.respondtoReceivedNumber = respondtoReceivedNumber;
	}

	public String getResponseNumber() {
		return responseNumber;
	}

	public void setResponseNumber(String responseNumber) {
		this.responseNumber = responseNumber;
	}

	public AlarmResponseTypes getResponseType() {
		return responseType;
	}

	public void setResponseType(AlarmResponseTypes responseType) {
		this.responseType = responseType;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

}
