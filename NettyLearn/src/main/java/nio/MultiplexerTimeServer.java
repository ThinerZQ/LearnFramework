package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * User：ThinerZQ
 * Email：thinerzq@gmail.com
 * Date：2016/8/20 14:13
 * Project：LearnFramework
 * Package：nio
 */
public class MultiplexerTimeServer implements Runnable {
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private volatile Boolean stop = false;

    public MultiplexerTimeServer(int prot) {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            //设置端口，和blacklog队列大小：tcp已经三次握手+正在握手的连接数。
            serverSocketChannel.socket().bind(new InetSocketAddress(prot), 1024);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("服务器已启动");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void setStop(Boolean stop) {
        this.stop = stop;
    }

    public void run() {
        while (!stop) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeySet.iterator();
                SelectionKey key = null;
                while (iterator.hasNext()) {
                    key = iterator.next();
                    iterator.remove();
                    try {
                        handleInput(key);
                    } catch (IOException e) {
                        //抛异常了，关掉key
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //服务器停止了，关闭
        //sleector关闭了，所有注册在selector上的Channel pipe都会自动注册关闭
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleInput(SelectionKey key) throws IOException {
        if (key.isValid()) {
            if (key.isAcceptable()) {
                //服务器连接事件，相当于TCP三次握手已经完成
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
            } else if (key.isReadable()) {
                //Socket准备就绪，可读了
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int count = sc.read(byteBuffer);
                if (count > 0) {
                    //将缓冲区从写模式转换成读模式
                    byteBuffer.flip();
                    byte[] bytes = new byte[byteBuffer.remaining()];
                    byteBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("时间服务器接收到命令：" + body);
                    String currentDate = "Query Time Order".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "Bad Order";
                    doWrite(sc, currentDate);
                } else if (count < 0) {
                    //对端链路关闭
                    key.cancel();
                    sc.close();
                } else {
                    //读到0字节，忽略
                }
            }
        }
    }

    private void doWrite(SocketChannel sc, String response) throws IOException {
        if (response != null && response.trim().length() > 0) {
            //注意处理写半包的情况
            byte[] bytes = response.getBytes();
            ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
            byteBuffer.put(bytes);
            byteBuffer.flip();
            sc.write(byteBuffer);
        }
    }
}
