import java.io.IOException;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;

public class LuceneConfig
{
    private  LuceneConfig(){
        
    }
    private static Directory ramDir=null;
    private static Analyzer analyzer=null;
    public synchronized static Directory getStoreDirectory()
    {
        if(ramDir==null){
            
            try
            {
                ramDir = FSDirectory.getDirectory("D:/lucene_index/");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
           
        }
        return ramDir;
    }

    public static Analyzer getAnalyzer()
    {
        if(analyzer==null){
            analyzer = new PaodingAnalyzer();
        }
        return analyzer;
    }
}
