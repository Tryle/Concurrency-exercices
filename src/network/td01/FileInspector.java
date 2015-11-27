package network.td01;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileInspector {
    public final static int BUFFER_SIZE = 10;
	public static void main(String[] args) throws IOException {
		Path pIn = FileSystems.getDefault().getPath(args[0]);
		ByteChannel in = Files.newByteChannel(pIn, StandardOpenOption.READ);
		
		ByteBuffer bb = ByteBuffer.allocate(BUFFER_SIZE);
		int nb = 0;
		while ((nb=in.read(bb)) != -1) {
			System.out.println(nb + " bytes read");
			bb.flip();
			while (bb.hasRemaining()) {
				byte b = bb.get();
				System.out.println("octet :" + b + " (char : " + (char)b + ")");
			}
			bb.clear();
		}
	    in.close();
	}
}