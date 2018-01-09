package org.jimmutable.core.utils;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.jimmutable.core.exceptions.ValidationException;
import org.junit.Test;

public class IOUtilsTest
{
    @Test
    public void testTransferAllBytesBasic() throws IOException
    {
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        ByteArrayInputStream source = new ByteArrayInputStream("Look at me, ma!".getBytes());
        
        IOUtils.transferAllBytes(source, sink);
        assertTrue(Arrays.equals("Look at me, ma!".getBytes(), sink.toByteArray()));
    }
    
    @Test(expected = ValidationException.class)
    public void testTransferAllBytesNullSource() throws IOException
    {
        ByteArrayOutputStream sink = new ByteArrayOutputStream();
        IOUtils.transferAllBytes(null, sink);
    }
    
    @Test(expected = ValidationException.class)
    public void testTransferAllBytesNullSink() throws IOException
    {
        ByteArrayInputStream source = new ByteArrayInputStream(new byte[0]);
        IOUtils.transferAllBytes(source, null);
    }
}
