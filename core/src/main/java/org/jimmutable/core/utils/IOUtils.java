package org.jimmutable.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jimmutable.core.exceptions.ValidationException;

public final class IOUtils
{
    private IOUtils()
    {
        // Cannot be constructed
    }
    
    /**
     * Wrap any input stream and limit the maximum number of bytes
     * that can be written to that stream. All operations pass
     * through to the wrapped stream.
     */
    static public class LimitBytesOutputStream extends OutputStream
    {
        final OutputStream out;
        final long limit;
        long count;
        
        public LimitBytesOutputStream(final OutputStream out, final long limit)
        {
            super();
            
            Validator.notNull(out);
            Validator.min(limit, 0);
            
            this.out = out;
            this.limit = limit;
        }
        
        private void ensureCapacity(long len)
        {
            if (count + len > limit)
            {
                throw new ValidationException("Exceeded maxixum write length of " + limit + " b");
            }
        }

        @Override
        public synchronized void write(int b) throws IOException
        {
            ensureCapacity(count + 1);
            out.write(b);
            count++;
        }

        @Override
        public synchronized void write(byte[] b, int off, int len) throws IOException
        {
            ensureCapacity(count + len);
            out.write(b, off, len);
            count += len;
        }

        @Override
        public void write(byte[] b) throws IOException
        {
            ensureCapacity(count + b.length);
            out.write(b);
            count += b.length;
        }

        @Override
        public void flush() throws IOException
        {
            out.flush();
        }

        @Override
        public void close() throws IOException
        {
            out.close();
        }
    }

    /**
     * Transfer all bytes from an {@link InputStream} to an {@link OutputStream}.
     * This method does not close either stream.
     * 
     * @param source
     *          The {@code InputStream} to read from. Reading continues until EOF is reached.
     * @param sink
     *          The {@code OutputStream} to write all bytes from {@code source} to.
     *          {@link OutputStream#flush() flush()} is called after EOF is reached.
     * 
     * @throws IOException
     */
    static public void transferAllBytes(final InputStream source, final OutputStream sink) throws IOException
    {
        Validator.notNull(source, sink);
        
        byte[] buf = new byte[1024 * 1024];
        
        int bytes_read = source.read(buf);
        while (bytes_read >= 0)
        {
            sink.write(buf, 0, bytes_read);
            bytes_read = source.read(buf);
        }
        
        sink.flush();
    }
}
