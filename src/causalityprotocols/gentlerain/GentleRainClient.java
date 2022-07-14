package causalityprotocols.gentlerain;

public class GentleRainClient {

	private String clientId;
	private long clientTime;
	private long globalStableTime;
	private boolean occupied;

	public GentleRainClient(String clientId, long clientTime, long globalStableTime) {
		this.clientId = clientId;
		this.clientTime = clientTime;
		this.globalStableTime = globalStableTime;
		this.occupied = false;
	}

	public String getClientId() {
		return clientId;
	}

	public long getClientTime() {
		return clientTime;
	}

	public long getGlobalStableTime() {
		return globalStableTime;
	}

	public boolean isOccupied() {
		return this.occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}
}
