package aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * User：ThinerZQ
 * Email：thinerzq@gmail.com
 * Date：2016/8/21 10:44
 * Project：LearnFramework
 * Package：aio
 */
public class AcceptComplettionHandler implements CompletionHandler<AsynchronousSocketChannel,AsynTimeServerHandler> {
    public void completed(AsynchronousSocketChannel result, AsynTimeServerHandler attachment) {
        attachment.asynchronousServerSocketChannel.accept(attachment,this);
        ByteBuffer buffer =ByteBuffer.allocate(1024);
        result.read(buffer,buffer,new ReadCompletionHandler(result));
    }

    public void failed(Throwable exc, AsynTimeServerHandler attachment) {
exc.printStackTrace();
        attachment.countDownLatch.countDown();
    }
}
