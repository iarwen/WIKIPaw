import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main
{
    public static void main(String[] args) throws SQLException, InterruptedException
    {
        LinkedBlockingQueue<Runnable> queue = new  LinkedBlockingQueue<Runnable>();
        ThreadPoolExecutor tpx= new ThreadPoolExecutor(50,100,60,TimeUnit.SECONDS, 
                queue,new ThreadPoolExecutor.DiscardOldestPolicy()); 
        for (int i = 1; i <=1000000; i++)
        {
            BaiduPawWorker worker=new BaiduPawWorker("http://baike.baidu.com/view/" + i + ".htm");
            tpx.submit(worker);
            Thread.sleep(2);  
        }
    }
}
