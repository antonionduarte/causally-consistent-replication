package causality;

public enum ConfigurationProperty {

	NUMBER_CLIENTS("NUMBER_CLIENTS"),
	EXECUTION_TIME("EXECUTION_TIME"),
	WEIGHT_READS("WEIGHT_READS"),
	WEIGHT_WRITES("WEIGHT_WRITES");

	private final String parameterName;

	ConfigurationProperty(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getString() {
		return this.parameterName;
	}

}
