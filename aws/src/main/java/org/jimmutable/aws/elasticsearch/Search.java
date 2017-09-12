package org.jimmutable.aws.elasticsearch;

//import java.net.InetAddress;
//import java.net.UnknownHostException;
//
//import org.elasticsearch.client.transport.TransportClient;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.transport.InetSocketTransportAddress;
//import org.elasticsearch.transport.client.PreBuiltTransportClient;
//
//@SuppressWarnings("resource")
public class Search
{

//	private static TransportClient;
//
//	static {
//		try {
//			client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ElasticSearchEndpoint.CURRENT.getSimpleHost()), ElasticSearchEndpoint.CURRENT.getSimplePort()));
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public boolean upsertDocumentAsync(SearchIndexDefinition index, Indexable object)
	{
		return true;
	}

//	public JSONResponse search(IndexDefinition index, StandardSearchRequest request)
//	{
//		return null;
//	}

}
