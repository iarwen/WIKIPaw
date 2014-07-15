import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main
{
    public static void main(String[] args) throws SQLException, InterruptedException
    {
        LinkedBlockingQueue<Runnable> queue = new  LinkedBlockingQueue<Runnable>();
        ThreadPoolExecutor tpx= new ThreadPoolExecutor(20,50,60,TimeUnit.SECONDS, 
                queue,new ThreadPoolExecutor.DiscardOldestPolicy()); 
        for (int i = 295614; i <=295614; i++)
        {
            BaiduPawWorker worker=new BaiduPawWorker("http://baike.baidu.com/view/" + i + ".htm");
            tpx.submit(worker);
            Thread.sleep(10);  
        }
    }
}
