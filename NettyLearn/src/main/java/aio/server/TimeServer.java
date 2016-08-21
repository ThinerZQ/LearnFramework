package aio.server;

/**
 * User：ThinerZQ
 * Email：thinerzq@gmail.com
 * Date：2016/8/21 10:34
 * Project：LearnFramework
 * Package：aio
 */
public class TimeServer {
    public static void main(String[] args) {
        int port =9999;
        if (args != null && args.length >0){
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                //nothing
            }
        }
        AsynTimeServerHandler asynTimeServerHandler = new AsynTimeServerHandler(port);
        new Thread(asynTimeServerHandler).start();
    }
}
