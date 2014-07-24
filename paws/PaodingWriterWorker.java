import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;

public class PaodingWriterWorker implements Runnable
{

    private String TITLE_NAME = "TITLE";
    private String CONTENT_NAME = "CONTENT";
    private String ID_NAME = "ID";
    
    // 将庖丁封装成符合Lucene要求的Analyzer规范
    private Analyzer analyzer;
    private Directory ramDir;

    public void run()
    {
        ramDir = LuceneConfig.getStoreDirectory();
        analyzer = LuceneConfig.getAnalyzer();
        try
        {
            int i=0;
            // 读取数据库的数据
            while(true){
                List<WIKI> wikis=DbUtils.getUnShardingWIKI(50);
                long start=System.currentTimeMillis();
                for(WIKI wiki:wikis){
                    if(null==wiki.getTitle()||"".equals(wiki.getTitle().trim())){
                        DbUtils.updateSharded(wiki.getUuid());
                        continue;
                    }
                    String content=wiki.getContent();
//                    content=content.replaceAll("<scrpit.*?/script>", "");
//                    content=content.replaceAll("<span.*>", "");
//                    content=content.replaceAll("</span>", "");
//                    content=content.replaceAll("<div.*\">", "");
//                    content=content.replaceAll("</div>", "");
//                    content=content.replaceAll("<ul.*\">", "");
//                    content=content.replaceAll("</ul>", "");
//                    content=content.replaceAll("<li.*\">", "");
//                    content=content.replaceAll("</li>", "");
//                    content=content.replaceAll("<a.*>", "");
                    content=content.replaceAll("<style.*?/style>", "");
                    content=content.replaceAll("<.*?(\\n)*>", "");
                    content=content.replaceAll("<.*?", "");
                    content=content.replaceAll(".*?>", "");
                    wiki.setContent(content);
                    sharding(wiki);
                   // System.out.println(wiki.getUuid());
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
            IndexWriter writer = new IndexWriter(ramDir, analyzer);
            ramDir.makeLock(IndexWriter.WRITE_LOCK_NAME).release();
            
            Document doc = new Document();
            Field title = new Field(TITLE_NAME, wiki.getTitle(), Field.Store.YES, Field.Index.UN_TOKENIZED, Field.TermVector.WITH_POSITIONS_OFFSETS);
            Field content = new Field(CONTENT_NAME, wiki.getContent(), Field.Store.YES, Field.Index.TOKENIZED, Field.TermVector.WITH_POSITIONS_OFFSETS);
            Field id = new Field(ID_NAME, wiki.getUuid(), Field.Store.YES, Field.Index.UN_TOKENIZED, Field.TermVector.WITH_POSITIONS_OFFSETS);
            doc.add(title);
            doc.add(content);
            doc.add(id);
            writer.addDocument(doc);
            writer.optimize();
            writer.close();
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
