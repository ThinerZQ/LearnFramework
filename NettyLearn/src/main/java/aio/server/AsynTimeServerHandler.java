package aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * User：ThinerZQ
 * Email：thinerzq@gmail.com
 * Date：2016/8/21 10:37
 * Project：LearnFramework
 * Package：aio
 */
public class AsynTimeServerHandler implements Runnable {

    private int port;
    CountDownLatch countDownLatch;
    AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    public AsynTimeServerHandler(int port) {
        this.port = port;
        try {
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("Time Server is start in port : "+port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        countDownLatch = new CountDownLatch(1);
        doAccept();
        try {
            countDownLatch.await();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void doAccept() {
        asynchronousServerSocketChannel.accept(this,new AcceptComplettionHandler());
    }
}
