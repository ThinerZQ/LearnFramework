package aio.client;

/**
 * User：ThinerZQ
 * Email：thinerzq@gmail.com
 * Date：2016/8/21 11:26
 * Project：LearnFramework
 * Package：aio.client
 */
public class TimeClient {
    public static void main(String[] args) {
        int port = 9999;
        try{
            if (args!= null && args.length >0){
                port = Integer.parseInt(args[0]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        new Thread(new AsyncTimeClientHandler("127.0.0.1",port)).start();
    }
}
