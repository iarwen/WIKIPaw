import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.LockObtainFailedException;

public class PaodingWriterWorker implements Runnable
{

    private String TITLE_NAME = "TITLE";
    private String CONTENT_NAME = "CONTENT";
    private String ID_NAME = "ID";

    public void run()
    {
        try
        {
            int i=0;
            // 读取数据库的数据
            while(true){
                List<WIKI> wikis=DbUtils.getUnShardingWIKI(100);
                long start=System.currentTimeMillis();
                if(wikis.size()==0){Thread.sleep(20000);}
                for(WIKI wiki:wikis){
                    if(null==wiki.getTitle()||"".equals(wiki.getTitle().trim())){
                        DbUtils.updateSharded(wiki.getUuid());
                        continue;
                    }
                    String content=wiki.getContent();
                    content=content.replaceAll("<script[^>]*?>.*?</script>", "");
                    content=content.replaceAll("<style[^>]*?>.*?</style>", "");
                    content=content.replaceAll("<.*?(\\n)*>", "");
                    content=content.replaceAll("<a.*?20px\"", "");
                    content=content.replaceAll("data-lemmaid.*?>", "");
                    content=content.replaceAll("style=\".*?>", "");
                    content=content.replaceAll("&nbsp;", "");
                    content=content.replaceAll("<a.*?0px;\"","");
                    content=content.replaceAll("iframe","-iframe-");
                    content=content.replaceAll("收藏 查看.*?赶紧来编辑吧！","");
                    content=content.replaceAll("收藏 查看.*?编辑","");
                    content=content.replaceAll("收藏 查看.*?有用\\+1","");
                    wiki.setContent(content);
                    sharding(wiki);
                    DbUtils.updateSharded(wiki.getUuid());
                }
                i+=wikis.size();
                System.err.println("||||||||"+(i)+"个已被处理||||||||"+(System.currentTimeMillis()-start));
                Thread.sleep(10);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private void sharding(WIKI wiki)
    {
        try
        {
        	IndexWriter writer=LuceneConfig.getIndexWriter();
            Document doc = new Document();
            Field title = new Field(TITLE_NAME, wiki.getTitle(), Field.Store.YES, Field.Index.UN_TOKENIZED, Field.TermVector.WITH_POSITIONS_OFFSETS);
            Field content = new Field(CONTENT_NAME, wiki.getContent(), Field.Store.YES, Field.Index.TOKENIZED, Field.TermVector.WITH_POSITIONS_OFFSETS);
            Field id = new Field(ID_NAME, wiki.getUuid(), Field.Store.YES, Field.Index.UN_TOKENIZED, Field.TermVector.WITH_POSITIONS_OFFSETS);
            doc.add(title);
            doc.add(content);
            doc.add(id);
            writer.addDocument(doc);
           // writer.optimize();
        }
        catch (CorruptIndexException e)
        {
            e.printStackTrace();
        }
        catch (LockObtainFailedException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
       
    }
}
