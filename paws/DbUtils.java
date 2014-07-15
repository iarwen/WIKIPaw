import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class DbUtils
{
    private static String INSERTWIKI="INSERT INTO T_WIKI(UUID,TILTLE,CONTENT,WIKIFROM,URLLINK,PAWTIME) VALUES(?,?,?,?,?,?)";
    private static String SELECTWIKI="SELECT 1 FROM  T_WIKI WHERE URLLINK=?";
    
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
        System.out.println(wiki.getTitle()+"----INSERTED");
        stat.close();
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
        return isLinkExist;
    }
}
