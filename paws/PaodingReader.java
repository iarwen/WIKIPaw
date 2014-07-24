import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.paoding.analysis.examples.gettingstarted.BoldFormatter;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.TokenSources;

public class PaodingReader
{
    private static String TITLE_NAME = "TITLE";
    private static String CONTENT_NAME = "CONTENT";
    private static String ID_NAME = "ID";
    
   
    public List<WIKI> query(String queryString) throws CorruptIndexException, IOException, ParseException
    {
        IndexReader reader = LuceneConfig.getReader();
        QueryParser parser = new QueryParser(CONTENT_NAME, LuceneConfig.getAnalyzer());
        Query query = parser.parse(queryString);
        Searcher searcher = new IndexSearcher(reader);
        query = query.rewrite(reader);
        System.out.println("Searching for: " + query.toString(CONTENT_NAME));
        Hits hits = searcher.search(query);

        BoldFormatter formatter = new BoldFormatter();
        Highlighter highlighter = new Highlighter(formatter, new QueryScorer(query));
        highlighter.setTextFragmenter(new SimpleFragmenter(50));
        List<WIKI> lw=new ArrayList<WIKI>();
        for (int i = 0; i < hits.length(); i++)
        {
            String qtext = hits.doc(i).get(CONTENT_NAME);
            String qid = hits.doc(i).get(ID_NAME);
            String qtitle = hits.doc(i).get(TITLE_NAME);
            int maxNumFragmentsRequired = 5;
            String fragmentSeparator = "...";
            TermPositionVector tpv = (TermPositionVector) reader.getTermFreqVector(hits.id(i), CONTENT_NAME);
            TokenStream tokenStream = TokenSources.getTokenStream(tpv);
            String result_content=qtext;
			try {
				result_content = highlighter.getBestFragments(tokenStream, qtext, maxNumFragmentsRequired, fragmentSeparator);
			} catch (StringIndexOutOfBoundsException e) {
				System.err.println("遇了个错误");
				e.printStackTrace();
			}
            WIKI wiki=new WIKI();
            wiki.setUuid(qid);
            wiki.setTitle(qtitle);
            wiki.setContent(result_content);
            lw.add(wiki);
        }
        return lw;
    }
}
