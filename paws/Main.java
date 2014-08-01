import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        
        LinkedBlockingQueue<Runnable> queue = new  LinkedBlockingQueue<Runnable>();
        ThreadPoolExecutor tpx= new ThreadPoolExecutor(50,100,60,TimeUnit.SECONDS, 
                queue,new ThreadPoolExecutor.DiscardOldestPolicy()); 
        
        //分析器
        //PaodingWriterWorker pww=new PaodingWriterWorker();
        // tpx.submit( pww );
        //启动查询服务
        PawHttpServer server = new PawHttpServer(8080);
        tpx.submit( server );
        
//        for (int i = 105000; i <=1000000; i++)
//        {
//            BaiduPawWorker worker=new BaiduPawWorker("http://baike.baidu.com/view/" + i + ".htm");
//            tpx.submit(worker);
//            Thread.sleep(2);  
//        }
        System.out.println("全部初始化结束");
        
    }
}
