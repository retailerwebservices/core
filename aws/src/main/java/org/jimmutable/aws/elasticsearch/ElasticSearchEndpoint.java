package org.jimmutable.aws.elasticsearch;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jimmutable.core.objects.TransientImmutableObject;
import org.jimmutable.core.utils.Comparison;
import org.jimmutable.core.utils.Validator;

public class ElasticSearchEndpoint extends TransientImmutableObject<ElasticSearchEndpoint>
{

	private static final Logger logger = Logger.getLogger(ElasticSearchEndpoint.class.getName());

	private String host;
	private int port;
	private String CURRENT;

	/**
	 * Sets up the localhost:9200 endpoint
	 */
	public ElasticSearchEndpoint()
	{
		CURRENT = System.getProperty("elasticsearch.endpoint");
		String tmp_host = null;
		Integer tmp_port = null;

		if (CURRENT != null)
		{
			String[] host_port = CURRENT.split(":", -1);
			if (host_port.length == 2)
			{
				tmp_host = host_port[0];
				try
				{
					tmp_port = Integer.parseInt(host_port[1]);
				} catch (NumberFormatException e)
				{
					logger.log(Level.SEVERE, "Port is not a valid integer", e);
				}
			}
		}

		if (tmp_host == null || tmp_host.isEmpty() || tmp_port == null)
		{
			tmp_host = "localhost";
			tmp_port = 9200;
		}

		this.host = tmp_host;
		this.port = tmp_port;

		complete();
	}

	public String getSimpleHost()
	{
		return this.host;
	}

	public int getSimplePort()
	{
		return this.port;
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
		Validator.notNull(this.host, this.port);
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

		if (!this.getSimpleHost().equals(other.getSimpleHost()))
		{
			return false;
		}
		if (this.getSimplePort() != other.getSimplePort())
		{
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
