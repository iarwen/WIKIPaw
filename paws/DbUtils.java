import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class DbUtils
{
    private static String INSERTWIKI="INSERT INTO T_WIKI(UUID,TITLE,CONTENT,WIKIFROM,URLLINK,PAWTIME) VALUES(?,?,?,?,?,?)";
    private static String SELECTWIKI="SELECT 1 FROM  T_WIKI WHERE URLLINK=?";
    private static String SELECTWIKIBYID="SELECT UUID,TITLE,CONTENT,WIKIFROM,URLLINK,PAWTIME FROM  T_WIKI WHERE UUID=?";
    private static String SELECTUNSHARDINGWIKI="SELECT UUID,TITLE,CONTENT FROM T_WIKI WHERE ISSHARDING=0 LIMIT ?";
    private static String UPDATESHARDED="UPDATE T_WIKI SET ISSHARDING=1 WHERE UUID = ?";
    
    public static int addAWiki(WIKI wiki) throws SQLException{
        Connection con= DBConnectPool.getConnection();
        PreparedStatement stat = con.prepareStatement(INSERTWIKI);
        int index=0;
        stat.setString(++index, UUID.randomUUID().toString());
        stat.setString(++index, wiki.getTitle());
        stat.setString(++index, wiki.getContent());
        stat.setString(++index, wiki.getWikifrom());
        stat.setString(++index, wiki.getUrl());
        stat.setDate(++index, wiki.getPawTime());
        int result=stat.executeUpdate();
        //System.out.println(wiki.getTitle()+"----INSERTED");
        stat.close();
        con.close();
        return result;
    }
    public static boolean isLinkExist(String link) throws SQLException{
        Connection con= DBConnectPool.getConnection();
        PreparedStatement stat = con.prepareStatement(SELECTWIKI);
        
        stat.setString(1, link);
       
        ResultSet result=stat.executeQuery();
        boolean isLinkExist=result.next();
        result.close();
        stat.close();
        con.close();
        return isLinkExist;
    }
    public static List<WIKI> getUnShardingWIKI(int limit) throws SQLException{
        Connection con= DBConnectPool.getConnection();
        PreparedStatement stat = con.prepareStatement(SELECTUNSHARDINGWIKI);
        stat.setInt(1, limit);
        ResultSet result=stat.executeQuery();
        List<WIKI> lw=new ArrayList<WIKI>();
        while(result.next()){
            WIKI wiki=new WIKI();
            wiki.setUuid(result.getString("UUID"));
            wiki.setTitle(result.getString("TITLE"));
            wiki.setContent(result.getString("CONTENT"));
            lw.add(wiki);
        }
        result.close();
        stat.close();
        con.close();
        return lw;
    }
    public static void updateSharded(String id)throws SQLException{
        Connection con= DBConnectPool.getConnection();
        PreparedStatement stat = con.prepareStatement(UPDATESHARDED);
        stat.setString(1, id);
        stat.executeUpdate();
        stat.close();
        con.close();
    }
	public static WIKI getWikiDetailById(String query) throws SQLException {
		 Connection con= DBConnectPool.getConnection();
	        PreparedStatement stat = con.prepareStatement(SELECTWIKIBYID);
	        stat.setString(1, query);
	        ResultSet result=stat.executeQuery();
	        WIKI wiki=new WIKI();
	        while(result.next()){
	            wiki.setUuid(result.getString("UUID"));
	            wiki.setTitle(result.getString("TITLE"));
	            wiki.setContent(result.getString("CONTENT"));
	            wiki.setUrl(result.getString("URLLINK"));
	            wiki.setPawTime(result.getDate("PAWTIME"));
	        }
	        result.close();
	        stat.close();
	        con.close();
	        return wiki;
	}
}
