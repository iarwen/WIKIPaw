import java.sql.Date;

public class WIKI
{
    private Date pawTime;
    private String title;
    private String content;
    private String wikifrom;
    private String url;

    public Date getPawTime()
    {
        return pawTime;
    }

    public void setPawTime(Date pawTime)
    {
        this.pawTime = pawTime;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getWikifrom()
    {
        return wikifrom;
    }

    public void setWikifrom(String wikifrom)
    {
        this.wikifrom = wikifrom;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

}
enum WIKIFROM{
    BAIDU,WIKIPEDIA
}
