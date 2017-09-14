package org.jimmutable.aws.elasticsearch;

import java.util.Objects;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jimmutable.core.objects.TransientImmutableObject;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

/**
 * A host and port for Elasticsearch
 * 
 * @author trevorbox
 *
 */
public class ElasticSearchEndpoint extends TransientImmutableObject<ElasticSearchEndpoint>
{

	private static final Logger logger = LogManager.getLogger(ElasticSearchEndpoint.class);

	private String host;
	private int port;
	public static final ElasticSearchEndpoint CURRENT;

	// set the static member to environment variable, else localhost:9300
	static {
		String endpoint = System.getProperty("elasticsearch.endpoint");

		logger.info("elasticsearch.endpoint " + endpoint);

		String tmp_host = null;
		Integer tmp_port = null;

		if (endpoint != null) {
			String[] host_port = endpoint.split(":", -1);
			if (host_port.length == 2) {
				tmp_host = host_port[0];
				try {
					tmp_port = Integer.parseInt(host_port[1]);
				} catch (NumberFormatException e) {
					logger.log(Level.FATAL, "Port is not a valid integer", e);
				}
			}
		}

		if (tmp_host == null || tmp_host.isEmpty() || tmp_port == null) {
			tmp_host = "localhost";
			tmp_port = 9300;
		}

		CURRENT = new ElasticSearchEndpoint(tmp_host, tmp_port);
	}

	public ElasticSearchEndpoint(String host, int port)
	{

		this.host = host;
		this.port = port;

		complete();
	}

	/**
	 * The host name string, like "localhost"
	 * 
	 * @return the host name string
	 */
	public String getSimpleHost()
	{
		return host;
	}

	/**
	 * The port number, like 9300
	 * 
	 * @return the port number
	 */
	public int getSimplePort()
	{
		return port;
	}

	@Override
	public int compareTo(ElasticSearchEndpoint o)
	{
		int ret = Comparison.startCompare();
		ret = Comparison.continueCompare(ret, this.getSimplePort(), o.getSimplePort());
		ret = Comparison.continueCompare(ret, this.getSimpleHost(), o.getSimpleHost());
		return ret;
	}

	@Override
	public void freeze()
	{

	}

	@Override
	public void normalize()
	{

	}

	@Override
	public void validate()
	{
		Validator.notNull(this.host);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(host, port);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof ElasticSearchEndpoint))
			return false;

		ElasticSearchEndpoint other = (ElasticSearchEndpoint) obj;

		if (!getSimpleHost().equals(other.getSimpleHost())) {
			return false;
		}
		if (getSimplePort() != other.getSimplePort()) {
			return false;
		}

		return true;
	}

	@Override
	public String toString()
	{
		return "ElasticSearchEndpoint [host=" + host + ", port=" + port + "]";
	}

}
