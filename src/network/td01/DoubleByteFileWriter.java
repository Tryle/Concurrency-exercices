package network.td01;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DoubleByteFileWriter {
    private static final char CONST = 'A';
    public final static int BUFFER_SIZE = 10;

	public static void main(String[] args) throws IOException {
		Path pIn = FileSystems.getDefault().getPath(args[0]);
        Path pOut = FileSystems.getDefault().getPath(args[1]);
		ByteChannel in = Files.newByteChannel(pIn, StandardOpenOption.READ);
		ByteChannel out = Files.newByteChannel(pOut, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		ByteBuffer bb = ByteBuffer.allocate(BUFFER_SIZE);
		int nb = 0;
		while ((nb=in.read(bb)) != -1) {
			bb.flip();
			while(bb.hasRemaining()) {
				byte b = bb.get();
                ByteBuffer w;
                if(b == CONST) {
                    byte[] arr = {b, b};
                    w = ByteBuffer.wrap(arr);
                } else {
                    byte[] arr = {b};
                    w = ByteBuffer.wrap(arr);
                }
                out.write(w);
			}
			bb.clear();
		}
	    in.close();
        out.close();
	}
}