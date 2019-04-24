package org.jimmutable.cloud.servlets.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Request;
import org.jimmutable.cloud.CloudExecutionEnvironment;
import org.jimmutable.cloud.elasticsearch.Indexable;
import org.jimmutable.cloud.servlet_utils.common_objects.GeneralResponseError;
import org.jimmutable.cloud.storage.Storable;
import org.jimmutable.core.objects.StandardImmutableObject;
import org.jimmutable.core.objects.StandardObject;
import org.jimmutable.core.serialization.Format;
import org.jimmutable.core.serialization.writer.ObjectWriter;

public class ServletUtil
{
	private static final Logger logger = LogManager.getLogger(ServletUtil.class);

	public static final String APPLICATION_JSON = ContentType.APPLICATION_JSON.getMimeType();
	public static final String UTF8 = StandardCharsets.UTF_8.name();

	private static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));

	/**
	 * 
	 * Common json response writer to write standard immutable objects from the
	 * HttpResponse PrintWriter
	 * 
	 * @param response
	 *            the HttpServletResponse
	 * @param obj
	 *            the StandardImmutableObject<?>
	 * @param http_status_code
	 *            you can use the HttpServletResponse interface to get the http
	 *            status code constant. Such as:</br>
	 *            <p>
	 *            HttpServletResponse.SC_OK (200)</br>
	 *            HttpServletResponse.SC_FORBIDDEN (403)</br>
	 *            HttpServletResponse.SC_NOT_FOUND (404)</br>
	 *            HttpServletResponse.SC_INTERNAL_SERVER_ERROR (500)
	 *            </p>
	 */
	public static void writeSerializedResponse(HttpServletResponse response, StandardObject<?> obj, int http_status_code)
	{

		String json = "";
		try
		{
			json = ObjectWriter.serialize(Format.JSON_PRETTY_PRINT, obj);
		} catch (Exception e)
		{
			logger.error("Failure during serialization", e);
			http_status_code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}

		response.setContentType(APPLICATION_JSON);

		response.setCharacterEncoding(UTF8);

		response.setStatus(http_status_code);

		try
		{
			PrintWriter out = response.getWriter();
			out.write(json);
			out.flush();
		} catch (IOException e)
		{
			logger.error(e);
		}
	}

	public static void writeSerializedResponse(HttpServletResponse response, Object obj, int http_status_code)
	{

		String json = "";
		try
		{
			json = ObjectWriter.serialize(Format.JSON_PRETTY_PRINT, obj);
		} catch (Exception e)
		{
			logger.error("Failure during serialization", e);
			http_status_code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
		}

		response.setContentType(APPLICATION_JSON);

		response.setCharacterEncoding(UTF8);

		response.setStatus(http_status_code);

		try
		{
			PrintWriter out = response.getWriter();
			out.write(json);
			out.flush();
		} catch (IOException e)
		{
			logger.error(e);
		}
	}
	
	/**
	 * 
	 * Common json response writer to write JSON objects from the
	 * HttpResponse PrintWriter. This is for data that is already
	 * in JSON format and, therefore, already serialized.
	 * 
	 * @param response
	 *            the HttpServletResponse
	 * @param json
	 *            the JSON string
	 * @param http_status_code
	 *            you can use the HttpServletResponse interface to get the http
	 *            status code constant. Such as:</br>
	 *            <p>
	 *            HttpServletResponse.SC_OK (200)</br>
	 *            HttpServletResponse.SC_FORBIDDEN (403)</br>
	 *            HttpServletResponse.SC_NOT_FOUND (404)</br>
	 *            HttpServletResponse.SC_INTERNAL_SERVER_ERROR (500)
	 *            </p>
	 */
	public static void writeJSONResponse( HttpServletResponse response, String json, int http_status_code )
	{

		response.setContentType(ContentType.APPLICATION_JSON.getMimeType());

		response.setCharacterEncoding(StandardCharsets.UTF_8.name());

		response.setStatus(http_status_code);

		try
		{
			PrintWriter out = response.getWriter();
			out.write(json);
			out.flush();
		}
		catch ( IOException e )
		{
			logger.error(e);
		}
	}

	/**
	 * Parse the int from a string (http parameter). If it fails just return the
	 * default value
	 * 
	 * @param str
	 *            String
	 * @param default_int
	 *            int
	 * @return the parsed int or default_value
	 */
	public static int parseIntFromString(String str, int default_int)
	{
		try
		{
			return Integer.parseInt(str.trim());
		} catch (Exception e)
		{
		}
		return default_int;
	}

	/**
	 * Check if the request content type and charset are correctly set for UTF8
	 * JSON. If it is invalid it will write a GeneralResponseError for you, just
	 * return if false.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return true if valid, else false
	 */
	public static boolean isValidJSON(HttpServletRequest request, HttpServletResponse response)
	{

		if (request.getContentType().equals(APPLICATION_JSON) || request.getCharacterEncoding().equals(UTF8))
		{
			return true;
		}
		ServletUtil.writeSerializedResponse(response, new GeneralResponseError(String.format("Expected Content-Type=%s and charset=%s but was Content-Type=%s and charset=%s", APPLICATION_JSON, UTF8, request.getContentType(), request.getCharacterEncoding())), HttpServletResponse.SC_BAD_REQUEST);
		return false;
	}

	/**
	 * Get the JSON string from the request. Can return an empty string.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param default_value
	 *            Default string if reading fails
	 * @return the JSON as a string
	 */
	public static String getOptionalJSON(HttpServletRequest request, String default_value)
	{
		StringBuffer buffer = new StringBuffer();
		String line = null;
		try
		{
			InputStream inputStream = request.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, UTF8));
			while ((line = reader.readLine()) != null)
			{
				buffer.append(line);
			}
		} catch (Exception e)
		{
			logger.error(e);
			return default_value;
		}

		return buffer.toString();
	}

	/**
	 * Extracts the data from the request and returns it in a RequestPageData
	 * object.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param default_value
	 *            Default string if reading fails
	 * @return RequestPageData
	 */
	public static RequestPageData getPageDataFromPost(HttpServletRequest request, RequestPageData default_value)
	{
		RequestPageData page_data = new RequestPageData();

		if (ServletFileUpload.isMultipartContent(request))
		{
			request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
			page_data = getDataFromMultipartForm(request);
		} else
		{
			page_data = getJSONFromRequestBody(request);
		}

		return page_data.isEmpty() ? default_value : page_data;
	}

	private static RequestPageData getDataFromMultipartForm(HttpServletRequest request)
	{
		RequestPageData page_data = new RequestPageData();
		byte[] file_bytes = null;
		String file_name = null;
		String raw_json = "";

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload();

		// Parse the request

		InputStream stream = null;
		try
		{

			FileItemIterator iter = upload.getItemIterator(request);

			while (iter.hasNext())
			{
				FileItemStream item = iter.next();

				String field_name = item.getFieldName();
				file_name = item.getName();
				stream = item.openStream();
				if (item.isFormField())
				{
					// JSON data
					if (field_name == null || field_name == "")
					{
						field_name = RequestPageData.DEFAULT_JSON_ELEMENT;
					}
					raw_json = new String(getBytesFromInputStream(stream, new byte[0]), StandardCharsets.UTF_8);
					page_data.addElement(new PageDataElement(field_name, raw_json));
				} else
				{
					// File data
					if (field_name == null || field_name == "")
					{
						field_name = RequestPageData.DEFAULT_FILE_ELEMENT;
					}
					logger.info("File field " + field_name + " with file name " + item.getName() + " detected.");
					file_bytes = getBytesFromInputStream(stream, new byte[0]);
					page_data.addElement(new PageDataElement(field_name, null, file_bytes, file_name));
				}
				// file_name = null;
				// raw_json = "";
				// file_bytes = null;
				// field_name = null;
				stream.close();
			}
		} catch (FileUploadException | IOException e)
		{
			logger.error(e);
			// Replace page with an empty one
			page_data = new RequestPageData();
		}

		return page_data;
	}

	private static byte[] getBytesFromInputStream(InputStream is, byte[] default_value)
	{
		try
		{
			return IOUtils.toByteArray(is);
		} catch (IOException e)
		{
			logger.error(e);
			return default_value;
		}
	}

	private static RequestPageData getJSONFromRequestBody(HttpServletRequest request)
	{
		RequestPageData page_data = new RequestPageData();
		String raw_json = "";

		try
		{
			raw_json = request.getReader().lines().collect(Collectors.joining());
			page_data.addElement(new PageDataElement(RequestPageData.DEFAULT_JSON_ELEMENT, raw_json));
		} catch (Exception e)
		{
			logger.error(e);
		}

		return page_data;
	}

	/**
	 * Extracts the data from the request and returns it in a RequestPageData
	 * object.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param default_value
	 *            Default string if reading fails
	 * @return RequestPageData
	 */
	public static void handlePageDataFromPost(HttpServletRequest request, PageDataHandler handler)
	{
		if (ServletFileUpload.isMultipartContent(request))
		{
			request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
			parseDataFromMultipartFormWithHandler(request, handler);
		} else
		{
			parseJSONFromRequestBodyWithHandler(request, handler);
		}

	}

	private static void parseJSONFromRequestBodyWithHandler(HttpServletRequest request, PageDataHandler handler)
	{
		String raw_json = "";

		try
		{
			raw_json = request.getReader().lines().collect(Collectors.joining());
			handler.handle(new VisitedPageDataElement(VisitedPageDataElement.DEFAULT_JSON_ELEMENT, raw_json));
		} catch (Exception e)
		{
			logger.error(e);
			handler.onError("Default JSON not found in request");
		}
	}

	private static void parseDataFromMultipartFormWithHandler(HttpServletRequest request, PageDataHandler handler)
	{
		String file_name = null;
		String raw_json = "";

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload();

		// Parse the request

		InputStream stream = null;
		try
		{

			FileItemIterator iter = upload.getItemIterator(request);

			while (iter.hasNext())
			{
				FileItemStream item = iter.next();

				String field_name = item.getFieldName();
				file_name = item.getName();
				stream = item.openStream();
				if (item.isFormField())
				{
					// JSON data
					if (field_name == null || field_name == "")
					{
						field_name = VisitedPageDataElement.DEFAULT_JSON_ELEMENT;
					}
					raw_json = new String(getBytesFromInputStream(stream, new byte[0]), StandardCharsets.UTF_8);
					handler.handle(new VisitedPageDataElement(field_name, raw_json));
				} else
				{
					// File data
					if (field_name == null || field_name == "")
					{
						field_name = VisitedPageDataElement.DEFAULT_FILE_ELEMENT;
					}

					logger.debug(String.format("%s %s detected.", field_name, item.getName()));
					handler.handle(new VisitedPageDataElement(field_name, null, stream, file_name));
				}
				stream.close();
			}
		} catch (FileUploadException | IOException e)
		{
			logger.error(e);
		}

	}

	public static boolean upsertStorableIndexable(StandardImmutableObject<?> obj)
	{

		if (obj == null)
		{
			return false;
		}

		boolean status = false;

		if (obj instanceof Storable)
		{
			Storable storable = (Storable) obj;
			status = CloudExecutionEnvironment.getSimpleCurrent().getSimpleStorage().upsert(storable, Format.JSON_PRETTY_PRINT);
		} else
		{
			logger.error("Not a storable");
			return false;
		}

		if (obj instanceof Indexable)
		{
			Indexable indexable = (Indexable) obj;
			status = CloudExecutionEnvironment.getSimpleCurrent().getSimpleSearch().upsertDocument(indexable);
		} else
		{
			logger.error("Not an indexable");
			return false;
		}

		return status;

	}
}
