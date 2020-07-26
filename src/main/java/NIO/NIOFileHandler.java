package NIO;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class NIOFileHandler implements Runnable {

    private String serverFilePath = "./src/main/resources/serverFiles";
    private SocketChannel channel;
    private boolean isRunning = true;
    //private static int cnt = 1;

    public NIOFileHandler(SocketChannel channel) throws IOException {
        this.channel = channel;
        //String userName = "user" + cnt;
        //cnt++;
        //serverFilePath += "/" + userName;
        //File dir = new File(serverFilePath);
        //if (!dir.exists()) {
        //    dir.mkdir();
        //}
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(80);
                int count = channel.read(buffer);
                if (count == -1) {
                    channel.close();
                    break;
                }
                buffer.flip();
                StringBuilder command = new StringBuilder();
                while (buffer.hasRemaining()) {
                    command.append((char)buffer.get());
                }
                String [] op = command.toString().split(" ");

                if (op[0].equals("./download")) {
                    String fileName = op[1];
                    File file = new File(serverFilePath + "/" + fileName);
                    if (file.exists()) {
                        channel.write(ByteBuffer.wrap("OK\n".getBytes()));
                        RandomAccessFile aFile = new RandomAccessFile(serverFilePath + "/" + fileName, "rw");
                        FileChannel inChannel = aFile.getChannel();

                        ByteBuffer buf = ByteBuffer.allocate(1024);
                        int bytesRead = inChannel.read(buf);
                        while (bytesRead != -1) {
                            buf.flip();
                            while(buf.hasRemaining()){
                                channel.write(buf);
                            }
                            buf.clear();
                            bytesRead = inChannel.read(buf);
                        }
                        aFile.close();

                    } else {
                        channel.write(ByteBuffer.wrap("File not exists\n".getBytes()));
                    }
                } else if (op[0].equals("./upload")) {
                    // TODO: 7/23/2020 upload
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
