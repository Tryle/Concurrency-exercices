package network.td01;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class StoreWithEncoding {
    public static void main(String[] args) throws IOException {
        Path pOut = FileSystems.getDefault().getPath(args[1]);
        Scanner in = new Scanner(System.in);
        Charset charset = Charset.forName(args[0]);
        List<String> list = new LinkedList<>();
        while(in.hasNextLine()) {
            String next = in.nextLine();
            list.add(new String(next.getBytes(Charset.defaultCharset()), charset));
        }
        Files.write(pOut, list, charset, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        in.close();
    }
}
