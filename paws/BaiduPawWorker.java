import java.sql.SQLException;

public class BaiduPawWorker implements Runnable
{
    BaiduPaw baiduPaw;

    BaiduPawWorker(String link)
    {
        baiduPaw = new BaiduPaw(link);
    }

    public void run()
    {
        WIKI wiki=null;
        try
        {
            if (!DbUtils.isLinkExist(baiduPaw.url))
            {
                wiki= baiduPaw.getWIKI();
                //if(!"".equals(wiki.getTitle())){
                    DbUtils.addAWiki(wiki);
                //}
            }
            Thread.sleep(10);
        }
        catch (SQLException e)
        {
            System.err.println(wiki.getUrl());
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }
}
