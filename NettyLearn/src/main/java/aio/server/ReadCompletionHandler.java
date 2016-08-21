package aio.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;

/**
 * User：ThinerZQ
 * Email：thinerzq@gmail.com
 * Date：2016/8/21 10:56
 * Project：LearnFramework
 * Package：aio
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel asynchronousSocketChannel;


    public ReadCompletionHandler(AsynchronousSocketChannel asynchronousSocketChannel) {
        if (this.asynchronousSocketChannel == null){
            this.asynchronousSocketChannel = asynchronousSocketChannel;
        }
    }

    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[]  body = new byte[attachment.remaining()];
        attachment.get(body);
        String request = null;
        try {
            request = new String(body,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("时间服务器接收到命令：" + body);
        String currentDate = "Query Time Order".equalsIgnoreCase(request) ? new Date(System.currentTimeMillis()).toString() : "Bad Order";
        doWrite( currentDate);
    }

    private void doWrite(String currentDate) {
        if (currentDate != null && currentDate.trim().length() > 0) {
            //注意处理写半包的情况
            byte[] bytes = currentDate.getBytes();
            final ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
            byteBuffer.put(bytes);
            byteBuffer.flip();

            asynchronousSocketChannel.write(byteBuffer,byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                public void completed(Integer result, ByteBuffer attachment) {
                    if (attachment.hasRemaining()){
                        asynchronousSocketChannel.write(attachment,attachment,this);
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
            this.asynchronousSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
