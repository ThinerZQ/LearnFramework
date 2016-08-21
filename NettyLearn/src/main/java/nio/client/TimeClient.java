package nio.client.server;

import nio.client.TimeClientHandler;

/**
 * User：ThinerZQ
 * Email：thinerzq@gmail.com
 * Date：2016/8/20 15:20
 * Project：LearnFramework
 * Package：nio
 */
public class TimeClient {
    public static void main(String[] args) {
        int port = 9999;
        if (args != null && args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                //采用默认值
            }
        }
        new Thread(new TimeClientHandler("127.0.0.1", port), "NIO-TimeClient").start();
    }
}
