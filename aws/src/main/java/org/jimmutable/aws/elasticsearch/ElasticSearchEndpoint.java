package org.jimmutable.aws.elasticsearch;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	private static final Logger logger = Logger.getLogger(ElasticSearchEndpoint.class.getName());

	private String host;
	private int port;
	private String CURRENT; // CODE REVEIW: You need to set the current endpoint here, not in the constructor.  If you do it in the constructor, CURRENT is unset until you construct and endpoint, and it is reset everytime you make an endpoint

	/**
	 * defaults to localhost:9300 if environment variable is unset or invalid
	 */
	public ElasticSearchEndpoint()
	{
		CURRENT = System.getProperty("elasticsearch.endpoint");
		String tmp_host = null;
		Integer tmp_port = null;

		if (CURRENT != null) {
			String[] host_port = CURRENT.split(":", -1);
			if (host_port.length == 2) {
				tmp_host = host_port[0];
				try {
					tmp_port = Integer.parseInt(host_port[1]);
				} catch (NumberFormatException e) {
					logger.log(Level.SEVERE, "Port is not a valid integer", e);
				}
			}
		}

		if (tmp_host == null || tmp_host.isEmpty() || tmp_port == null) {
			tmp_host = "localhost";
			tmp_port = 9300;
		}

		this.host = tmp_host;
		this.port = tmp_port;

		complete();
	}

	public String getSimpleHost()
	{
		return this.host;  // CODE REVEIW: just return host, not this.host
	}

	public int getSimplePort()
	{
		return this.port; // CODE REVEIW: Just return port
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
		Validator.notNull(this.host, this.port); // CODE REVEIW: port is an integer, can't be null
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

		// CODE REVEIW: Don't use this.
		if (!this.getSimpleHost().equals(other.getSimpleHost())) {
			return false;
		}
		if (this.getSimplePort() != other.getSimplePort()) {
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
