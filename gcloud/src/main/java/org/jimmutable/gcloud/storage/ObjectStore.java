package org.jimmutable.gcloud.storage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.google.api.gax.paging.Page;
import com.google.cloud.RestorableState;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
//Imports the Google Cloud client library
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Bucket.BlobWriteOption;
import com.google.cloud.storage.Storage.BlobGetOption;
import com.google.cloud.storage.Storage.BlobListOption;
import com.google.cloud.storage.Storage.BlobSourceOption;
import com.google.cloud.storage.Storage.BucketField;
import com.google.cloud.storage.Storage.BucketGetOption;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;

public class ObjectStore
{

	Bucket bucket;
	Storage storage = StorageOptions.getDefaultInstance().getService();

	public ObjectStore(String bucket_name) throws Exception
	{
		try
		{
			bucket = storage.get(bucket_name);
			if (bucket != null)
			{
				System.out.println("Bucket " + bucket_name + " found. Create not needed");
			} else
			{
				System.out.println("Bucket " + bucket_name + "not found. Create needed");
			}
		} catch (StorageException se)
		{
			createBucket(bucket_name);
		} catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	public void createBucket(String bucket_name) throws Exception
	{
		// Instantiates a client
		bucket = storage.create(BucketInfo.newBuilder(bucket_name)
				// See here for possible values: http://g.co/cloud/storage/docs/storage-classes
				.setStorageClass(StorageClass.REGIONAL)
				// Possible values: http://g.co/cloud/storage/docs/bucket-locations#location-mr
				.setLocation("us-east1").setVersioningEnabled(true).build());

		System.out.printf("Bucket %s created.%n", bucket.getName());
	}

	// public void updateVersioning()
	// {
	// BucketInfo bucketInfo =
	// BucketInfo.newBuilder(bucket.getName()).setVersioningEnabled(true).build();
	// bucket = storage.update(bucketInfo);
	// }

	public void listBlobsInBucket(boolean versions)
	{
		Page<Blob> blobs;
		if (versions)
		{
			blobs = storage.list(bucket.getName(), BlobListOption.versions(true));
		} else
		{
			blobs = storage.list(bucket.getName());
		}

		for (Blob blob : blobs.iterateAll())
		{
			printBlobInfo(blob);
		}
	}

	public void listBlobsAllVersions(String blob_name_prefix)
	{
		Page<Blob> blobs;
		blobs = storage.list(bucket.getName(), BlobListOption.prefix(blob_name_prefix), BlobListOption.versions(true));
		for (Blob blob : blobs.iterateAll())
		{
			printBlobInfo(blob);
		}
	}

	public boolean deleteBucket()
	{
		System.out.println("\nAttempting to delete bucket " + bucket.getName());
		return bucket.delete();
	}

	public Bucket getBucket()
	{
		return bucket;
	}

	public void printBucketInfo()
	{
		System.out.println("Location is " + bucket.getLocation());
		System.out.println("Storage Class is " + bucket.getStorageClass().toString());
	}

	public void createBlobWithText(String blob_name, String blob_data)
	{
		BlobId blobId = BlobId.of(bucket.getName(), blob_name);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
		Blob blob = storage.create(blobInfo, blob_data.getBytes(UTF_8));
	}

	public Blob getBlob(String blob_name)
	{
		BlobId blobId = BlobId.of(bucket.getName(), blob_name);
		Blob blob = storage.get(blobId);
		return blob;
	}

	public Page<Blob> getBlobs(String blob_name)
	{
		Page<Blob> blobs;
		blobs = storage.list(bucket.getName(), BlobListOption.versions(true));
		return blobs;
	}

	public Blob getBlobWithGeneration(String blob_name, long blob_generation) throws StorageException
	{
		// Blob blob = storage.get(bucket.getName(), blob_name,
		// BlobGetOption.generationMatch(blob_generation)); // This doesn't work
		System.out.println("\nAttempting to read with name " + blob_name + " and generation " + blob_generation);
		BlobId blobId = BlobId.of(bucket.getName(), blob_name, blob_generation);
		Blob blob = storage.get(blobId);
		if (blob != null)
			System.out.println("Read of object with generation " + blob_generation + " successful");
		return blob;
	}

	public void printBlobInfo(Blob blob)
	{
		System.out.println("\nObject Name is " + blob.getName());
		System.out.println("Object Storage Class is " + blob.getStorageClass().toString());
		System.out.println("Content Type is " + blob.getContentType());
		System.out.println("Deleted at: " + blob.getDeleteTime());
		System.out.println("Generation is " + blob.getGeneration());
		System.out.println("MetaGeneration is " + blob.getMetageneration());
	}

	public void printBlobInfo(String blob_name)
	{

		BlobId blobId = BlobId.of(bucket.getName(), blob_name);
		BlobInfo blobInfo = storage.get(blobId);
		System.out.println("Object Name is " + blobInfo.getName());
		System.out.println("Object Id is " + blobInfo.getBlobId());
		System.out.println("Object Storage Class is " + blobInfo.getStorageClass().toString());
		System.out.println("Content Type is " + blobInfo.getContentType());
		System.out.println("Generation is " + blobInfo.getGeneration());

	}

	public void deleteBlob(String blob_name)
	{
		BlobId blobId = BlobId.of(bucket.getName(), blob_name);
		boolean deleted = storage.delete(blobId);
	}

	public boolean objectExists(String blob_name)
	{
		BlobId blobId = BlobId.of(bucket.getName(), blob_name);
		Blob blob = storage.get(blobId);
		if (blob == null)
		{
			System.out.println("No such object");
			return false;
		} else
		{
			System.out.println("Object " + blobId + " found");
			return true;
		}
	}

	public void makeObjectPublic(String blob_name)
	{
		BlobId blobId = BlobId.of(bucket.getName(), blob_name);
		Acl acl = storage.createAcl(blobId, Acl.of(User.ofAllUsers(), Role.READER));
	}

//	public void upsertBlobWithChecksum(String blob_name, String content, int content_length)
//	{
//		Crc32c crc32c = new Crc32c();
//		crc32c.update(content.getBytes(UTF_8), 0, content_length);
//		String checksum = Long.toString(crc32c.getValue());		
//		
//		BlobId blobId = BlobId.of(bucket.getName(), blob_name);
//		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setCrc32c(checksum).setContentType("text/plain").build();
//		Blob blob = storage.create(blobInfo, content.getBytes(UTF_8));
//	}
	
	private String calculateChecksum(byte[] content)
	{

		Crc32c crc32c = new Crc32c();
		crc32c.update(content, 0, content.length);
		return Long.toString(crc32c.getValue());
	}
	
	private String calculateChecksumFromFile(String filename)
	{
		InputStream fis = null;
		try
		{
			fis = new FileInputStream(filename);
		} catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
	    Crc32c crc32c = new Crc32c();
		
	    try 
		{
		 byte[] buffer = new byte[1024];
	     int numRead;
	     do {
	      numRead = fis.read(buffer);
	      if (numRead > 0) {
	        
	    	  crc32c.update(buffer, 0, numRead);
	        }
	      } while (numRead != -1);
	     fis.close();
		}
		catch (IOException e)
		{
			
		}
	     return Long.toString(crc32c.getValue());
	}
	
	public void uploadFileToBlob(String filename, String blob_name, String content_type)
			throws FileNotFoundException, IOException
	{
		File file = new File(filename);

		FileInputStream fis = new FileInputStream(file);
		// System.out.println(file.exists() + "!!");
		// InputStream in = resource.openStream();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		try
		{
			for (int readNum; (readNum = fis.read(buf)) != -1;)
			{
				bos.write(buf, 0, readNum); // no doubt here is 0
				// Writes len bytes from the specified byte array starting at offset off to this
				// byte array output stream.
				// System.out.println("read " + readNum + " bytes,");
			}
			System.out.println("File " + filename + " read");
		} catch (IOException ex)
		{
			System.out.println("IO Exception on file");
			return;
		} finally
		{
			fis.close();
		}
		
		byte[] bytes = bos.toByteArray();
		
		BlobId blobId = BlobId.of(bucket.getName(), blob_name);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(content_type).build();
		Blob blob = storage.create(blobInfo, bytes);
		System.out.println("File " + filename + " uploaded to bucket " + bucket.getName());
	}

	public void uploadBigFileToBlob(String filename, String blob_name, String content_type)
	{
		Path path = Paths.get(filename);

		String checksum = calculateChecksumFromFile(filename);
		Long file_size = 0L;	
		BlobId blobId = BlobId.of(bucket.getName(), blob_name);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
				.setCrc32c(checksum)
				.setContentType(content_type)
				.build();
		// RestorableState<WriteChannel> data_state;

	
		try (WriteChannel writer = storage.writer(blobInfo, Storage.BlobWriteOption.crc32cMatch()))
		{
			file_size = Files.size(path);
			writer.setChunkSize(100000);
			byte[] buffer = new byte[1024];
			try (InputStream input = Files.newInputStream(path))
			{
				int limit;
				while ((limit = input.read(buffer)) >= 0)
				{
					try
					{
						writer.write(ByteBuffer.wrap(buffer, 0, limit));
					} catch (Exception ex)
					{
						// data_state = writer.capture();
						System.out.println("IO Exception on file");
//						writer.close();
//						validateBlobSize(blobId, file_size);
						break;
					}
				}
			}
		} 
		catch (NoSuchFileException nsf)
		{
			System.out.println("No Such File" + filename);
			return;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("IO Exception on file");
			return;
		}
//		System.out.println("Blob was created");
//		validateBlobSize(blobId, file_size);
	}

	public void validateBlobSize(BlobId blobId, long expected_size /*, String expected_crc32c */)
	{
		Blob blob = storage.get(blobId);
		if(blob.getSize() != expected_size)
		{
			System.out.println("File is incomplete");
			// TODO: delete the incomplete file
		}
		
	}
	
	public void uploadBigFileToBlobWithCRC32C(String filename, String blob_name, String content_type)
	{
		Path path = Paths.get(filename);
		BlobId blobId = BlobId.of(bucket.getName(), blob_name);
		
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(content_type).build();
		// RestorableState<WriteChannel> data_state;

		Long file_size = 0L;
		try (WriteChannel writer = storage.writer(blobInfo, Storage.BlobWriteOption.crc32cMatch()))
		{
			file_size = Files.size(path);
			writer.setChunkSize(100000);
			byte[] buffer = new byte[1024];
			try (InputStream input = Files.newInputStream(path))
			{
				int limit;
				while ((limit = input.read(buffer)) >= 0)
				{
					try
					{
						writer.write(ByteBuffer.wrap(buffer, 0, limit));
					} catch (Exception ex)
					{
						// data_state = writer.capture();
						System.out.println("IO Exception on file");
//						writer.close();
//						validateBlobSize(blobId, file_size);
						break;
					}
				}
			}
		} 
		catch (NoSuchFileException nsf)
		{
			System.out.println("No Such File" + filename);
			return;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.out.println("IO Exception on file");
			return;
		}
//		System.out.println("Blob was created");
		validateBlobSize(blobId, file_size);
	}
	
	public void uploadStringToBlob(String blob_name, String content) throws IOException
	{
		InputStream in = new ByteArrayInputStream(content.getBytes(UTF_8));
		BlobId blobId = BlobId.of(bucket.getName(), blob_name);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
		Blob blob = storage.create(blobInfo, in);
	}

	public void uploadJSONToBlob(String blob_name, String content) throws IOException
	{
		if (content.length() < 500)
		{
			uploadStringToBlob(blob_name, "application/json", content);
		} else
		{
			uploadAndCompressStringToBlob(blob_name, "application/json", content);
		}

	}

	public void uploadXMLToBlob(String blob_name, String content) throws IOException
	{
		if (content.length() < 500)
		{
			uploadStringToBlob(blob_name, "application/xml", content);
		} else
		{
			uploadAndCompressStringToBlob(blob_name, "application/xml", content);
		}
	}

	public void uploadHTMLToBlob(String blob_name, String content) throws IOException
	{
		if (content.length() < 500)
		{
			uploadStringToBlob(blob_name, "text/html", content);
		} else
		{
			uploadAndCompressStringToBlob(blob_name, "text/html", content);
		}
	}

	public void uploadStringToBlob(String blob_name, String content_type, String content)
	{

		InputStream in = new ByteArrayInputStream(content.getBytes(UTF_8));
		BlobId blobId = BlobId.of(bucket.getName(), blob_name);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(content_type).build();
		
		
		BlobInfo.Builder b = BlobInfo.newBuilder(blobId);
	
		
		Blob blob = storage.create(blobInfo, in);
	}

	public void uploadAndCompressStringToBlob(String blob_name, String content_type, String content) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(bos);
		OutputStreamWriter osw = new OutputStreamWriter(gzip, StandardCharsets.UTF_8);
		osw.write(content);
		osw.close();

		byte[] bytes = bos.toByteArray();
		bos.close();

		BlobId blobId = BlobId.of(bucket.getName(), blob_name);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(content_type).setContentEncoding("gzip").build();
		Blob blob = storage.create(blobInfo, bytes);
		System.out.println("Compressed string data uploaded to bucket " + bucket.getName());
	}

	public void uploadAndCompressFileToBlob(String filename, String blob_name, String content_type)
			throws FileNotFoundException, IOException
	{
		File file = new File(filename);

		FileInputStream fis = new FileInputStream(file);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(bos);

		byte[] buf = new byte[1024];
		try
		{
			for (int readNum; (readNum = fis.read(buf)) != -1;)
			{
				gzip.write(buf, 0, readNum); // no doubt here is 0
				// Writes len bytes from the specified byte array starting at offset off to this
				// byte array output stream.
				// System.out.println("read " + readNum + " bytes,");
			}
			gzip.close();
			System.out.println("File " + filename + " read");
		} catch (IOException ex)
		{
			System.out.println("IO Exception on file");
			return;
		}

		byte[] bytes = bos.toByteArray();
		bos.close();
		fis.close();

		BlobId blobId = BlobId.of(bucket.getName(), blob_name);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(content_type).setContentEncoding("gzip").build();
		Blob blob = storage.create(blobInfo, bytes);
		System.out.println("File " + filename + " uploaded to bucket " + bucket.getName());
	}

	public void downloadFileFromBlob(String blob_name, String path) throws Exception
	{
		BlobId blobId = BlobId.of(bucket.getName(), blob_name);
		Blob blob = storage.get(blobId);

		File someFile = new File(path + blob_name + ".pdf"); // Use blob name for the file name
		FileOutputStream fos = new FileOutputStream(someFile);
		fos.write(blob.getContent());
		fos.flush();
		fos.close();
	}
}
