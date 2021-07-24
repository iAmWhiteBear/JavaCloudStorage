package Server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class NioServer {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ByteBuffer buffer;
    private Path currentPath;

    public NioServer() throws IOException {
        buffer = ByteBuffer.allocate(1024);
        selector = Selector.open();
        currentPath = Paths.get("rootDir");
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8321));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (serverSocketChannel.isOpen()) {
            selector.select();
            Set<SelectionKey> keySet = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keySet.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    hadnleAccept(key);
                }
                if (key.isReadable()) {
                    handleRead(key);
                }
                iterator.remove();
            }
        }

    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        String stringResponse;
        buffer.clear();
        StringBuilder sb = new StringBuilder();
        int read;
        while (true) {
            read = channel.read(buffer);
            if (read == -1) {
                channel.close();
                break;
            }
            if (read == 0) break;
            buffer.flip();
            while (buffer.hasRemaining()) {
                sb.append((char) buffer.get());
            }
            buffer.clear();
        }
        System.out.println("Recieved: " + sb);
        String request = sb.toString();
        /* server response for requests
         * cd
         * ls
         * cat
         * mkdir
         * touch - makefile(filename)
         */

        try {
            if (request.startsWith("cd ".toLowerCase())) {
                String dirName = sb.substring(3).trim();
                if(Files.isDirectory(currentPath.resolve(dirName))){
                    currentPath = currentPath.resolve(dirName);
                }
                else if(dirName.equals("..") && (!currentPath.equals(Paths.get("rootDir")))) currentPath = currentPath.getParent();
                else {
                    shortResponse(channel," is not directory");
                }
                currentPath = currentPath.normalize();
                showCurrentPath(channel);
            } else if (request.startsWith("ls")) {
                showCurrentPath(channel);
            } else if (request.startsWith("cat ")) {
                String filename = sb.substring(4).trim();
                Path filePath = Paths.get(currentPath.toString(), filename);
                BufferedReader reader = Files.newBufferedReader(filePath);
                longResponse(channel, reader);
            } else if (request.startsWith("mkdir ")) {
                String dirName = sb.substring(6).trim();
                Path dirPath = Paths.get(currentPath.toString(), dirName);
                Files.createDirectory(dirPath);
                shortResponse(channel, dirName + ": created");
                showCurrentPath(channel);
            } else if (request.startsWith("touch ")) {
                String fileName = sb.substring(6).trim();
                Path filePath = Paths.get(currentPath.toString(), fileName);
                Files.createFile(filePath);
                shortResponse(channel, fileName + ": created");
            } else {
                shortResponse(channel, sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            stringResponse = "command error";
            shortResponse(channel, stringResponse);
        }


    }

    private void shortResponse(SocketChannel channel, String resp) throws IOException {
        resp += "\n\r";
        ByteBuffer response = ByteBuffer.wrap(resp.getBytes(StandardCharsets.UTF_8));
        channel.write(response);
    }

    private void longResponse(SocketChannel channel, BufferedReader reader) throws IOException {
        ByteBuffer response;
        String line;
        while (reader.ready()) {
            line = reader.readLine()+"\n\r";
            response = ByteBuffer.wrap(line.getBytes(StandardCharsets.UTF_8));
            channel.write(response);
        }
    }

    private void showCurrentPath(SocketChannel channel) throws IOException {
        String stringResponse = Files.list(currentPath)
                .map(path -> {
                    StringBuilder fileB = new StringBuilder();
                    fileB.append(path.getFileName().toString());
                    fileB.append("\t\t");
                    if (Files.isDirectory(path)) fileB.append("[dir]");
                    else {
                        try {
                            fileB.append('(');
                            fileB.append(Files.size(path) / 1024);
                            fileB.append(")k");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return fileB.toString();
                })
                .collect(Collectors.joining("\n\r"))+"\n\r";
        stringResponse+=currentPath.toString()+": ";
        shortResponse(channel, stringResponse);
    }

    private void hadnleAccept(SelectionKey key) throws IOException {

        SocketChannel channel = serverSocketChannel.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
    }

    public static void main(String[] args) throws IOException {
        new NioServer();
    }


}
