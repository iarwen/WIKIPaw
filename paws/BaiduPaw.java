import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;

/**
 * 百度
 * @author RD_wentao_chang
 *
 */
public class BaiduPaw extends Paw
{
    
    public BaiduPaw(String url)
    {
        super.url=url;
    }
    protected WIKI getWIKI(){
        WIKI wiki=new WIKI();
        try{
            Parser parser = new Parser( (HttpURLConnection) (new URL(url)).openConnection() );
            
            NodeFilter filter = new HasAttributeFilter ("ID","sec-content0");
            NodeFilter filtert = new HasAttributeFilter("class","lemmaTitleH1");
            OrFilter orFilter = new OrFilter();  
            orFilter.setPredicates(new NodeFilter[] { filter , filtert});  
            
            NodeList nodes = parser.extractAllNodesThatMatch(orFilter); 
            
            StringBuffer content=new StringBuffer();
            StringBuffer title=new StringBuffer();
            for (NodeIterator i = nodes.elements (); i.hasMoreNodes(); ) {
                Node node = i.nextNode();
                if(node.toHtml().startsWith("<div class=\"lemmaTitleH1\"")){title.append(node.toHtml());}
                if(node.toHtml().startsWith("<div class=\"text lazyload\"")){content.append(node.toHtml());}
            }
            wiki.setWikifrom(WIKIFROM.BAIDU.name());
            wiki.setPawTime(new Date(System.currentTimeMillis()));
            wiki.setTitle(title.toString());
            wiki.setContent(content.toString());
            wiki.setUrl(url);
        }
        catch( Exception e ) {     
            System.out.println( "Exception:"+e );
        }
        return wiki;
    }
}
