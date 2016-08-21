package aio.client;

import aio.server.AsynTimeServerHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;

/**
 * User：ThinerZQ
 * Email：thinerzq@gmail.com
 * Date：2016/8/21 11:28
 * Project：LearnFramework
 * Package：aio.client
 */
public class AsyncTimeClientHandler implements Runnable, CompletionHandler<Void, AsyncTimeClientHandler> {

    private AsynchronousSocketChannel asynchronousSocketChannel;
    private String host;
    private int port;
    private CountDownLatch countDownLatch;

    public AsyncTimeClientHandler(String host, int port) {
        this.host = host;
        this.port = port;
        try {
            asynchronousSocketChannel = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run() {
        countDownLatch = new CountDownLatch(1);
        asynchronousSocketChannel.connect(new InetSocketAddress(host, port), this, this);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            asynchronousSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void completed(Void result, AsyncTimeClientHandler attachment) {


        byte[] req = "Query Time Order".getBytes();
        final ByteBuffer byteBuffer = ByteBuffer.allocate(req.length);
        byteBuffer.put(req);
        byteBuffer.flip();
        asynchronousSocketChannel.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            public void completed(Integer result, ByteBuffer attachment) {
                if (attachment.hasRemaining()) {
                    asynchronousSocketChannel.write(byteBuffer, byteBuffer, this);
                } else {
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    asynchronousSocketChannel.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                        public void completed(Integer result, ByteBuffer attachment) {
                            attachment.flip();
                            byte[] bytes = new byte[attachment.remaining()];
                            attachment.get(bytes);
                            String body;
                            try {
                                body = new String(bytes, "UTF-8");
                                System.out.println("Now is :" + body);
                                countDownLatch.countDown();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        public void failed(Throwable exc, ByteBuffer attachment) {
                            try {
                                asynchronousSocketChannel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            public void failed(Throwable exc, ByteBuffer attachment) {
                try {
                    asynchronousSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
        exc.printStackTrace();
        try {
            asynchronousSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        countDownLatch.countDown();
    }
}
