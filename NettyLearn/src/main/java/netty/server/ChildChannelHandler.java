package netty.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * User：ThinerZQ
 * Email：thinerzq@gmail.com
 * Date：2016/8/21 12:48
 * Project：LearnFramework
 * Package：netty.server
 */
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(new TimeServerHandler());
    }
}
