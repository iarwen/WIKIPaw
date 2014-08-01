import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.List;

/**
 * MyHttpServer 实现一个简单的HTTP服务器端，可以获取用户提交的内容 并给用户一个response
 * 因为时间的关系，对http头的处理显得不规范 对于上传附件，暂时只能解析只上传一个附件而且附件位置在第一个的情况
 * 转载请注明来自http://blog.csdn.net/sunxing007
 * **/
public class PawHttpServer implements Runnable {
	// 端口
	private int port;
	// 用户请求的文件的url
	private String query;

	public PawHttpServer(int port) {
		this.port = port;
		query = null;
	}

	// 处理GET请求
	private void doGet(InputStream in, OutputStream out) throws Exception {
		PaodingReader read = new PaodingReader();
		List<WIKI> wis = read.query(this.query);

		StringBuffer sb = new StringBuffer();
		sb.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");

		sb.append("<style>b{color:red}.lemmaTitleH1{font-size:1.1em;font-weight:bold;}</style>");
		sb.append("<title>" + this.query + "</title></head><body>");

		for (WIKI wiki : wis) {
			sb.append("<div  onclick=\"window.location.href='/goto/"
					+ wiki.getUuid() + "'\">");
			sb.append("<div>id:");
			sb.append(wiki.getUuid());
			sb.append("</div>");

			sb.append("<div>");
			sb.append(wiki.getTitle());
			sb.append("</div>");

			sb.append("<div>");
			sb.append(wiki.getContent().replaceAll("iframe", "-iframe-"));
			sb.append("</div>");
			sb.append("</div>");
			sb.append("<hr>");
		}

		sb.append("</body></html>");

		byte[] buf = sb.toString().getBytes();

		out.write(buf);
		out.close();
		in.close();

	}

	private void doGoto(InputStream in, OutputStream out) throws SQLException,
			IOException {

		WIKI wiki = DbUtils.getWikiDetailById(this.query);

		StringBuffer sb = new StringBuffer();
		sb.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");

		sb.append("<style>b{color:red}.lemmaTitleH1{font-size:1.1em;font-weight:bold;}</style>");
		sb.append("<title>" + this.query + "</title></head><body>");

		sb.append("<div>");
		sb.append(wiki.getTitle());
		sb.append("</div>");

		sb.append("<div>");
		sb.append(wiki.getContent().replaceAll("<img.*?>", ",")
				.replaceAll("<script[^>]*?>.*?</script>", ""));
		sb.append("</div>");
		sb.append("</div>");

		sb.append("</body></html>");

		byte[] buf = sb.toString().getBytes();

		out.write(buf);
		out.close();
		in.close();
	}

	public void run() {
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(this.port);
			System.out.println("server is ok.");
			// 开启serverSocket等待用户请求到来，然后根据请求的类别作处理
			// 在这里我只针对GET作了处理
			while (true) {
				Socket socket = serverSocket.accept();
				InputStream in = socket.getInputStream();
				BufferedReader d = new BufferedReader(new InputStreamReader(in));

				String line = d.readLine();
				if (line != null) {
					String method = line.substring(0, 4).trim();
					OutputStream out = socket.getOutputStream();
					this.query = line.split(" ")[1];
					if ("GET".equalsIgnoreCase(method)) {
						if (query.startsWith("/query/")) {
							this.query = URLDecoder.decode(query.substring(7),
									"UTF-8");
							this.doGet(in, out);
						}
						if (query.startsWith("/goto/")) {
							this.query = URLDecoder.decode(query.substring(6),
									"UTF-8");
							this.doGoto(in, out);
						}
						if (query.startsWith("/movies")) {
							this.doMovie(in, out);
						}
						if (query.startsWith("/moviet")) {
							this.doMovieT(in, out);
						}
					}
				}

				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void doMovieT(InputStream in, OutputStream out)  throws IOException {
		List<Movies> movies = MongoDBUtil.findAll(new Movies());

		StringBuffer sb = new StringBuffer();
		sb.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");

		sb.append("<style>b{color:red}.lemmaTitleH1{font-size:1.1em;font-weight:bold;}</style>");
		sb.append("<title>" + this.query + "</title></head><body>");

		for (Movies movie : movies) {
			sb.append("<div>title:");
			sb.append(movie.getTitle());
			sb.append("</div>");
		}

		sb.append("</body></html>");

		byte[] buf = sb.toString().getBytes();

		out.write(buf);
		out.flush();
		out.close();
		in.close();
	}

	private void doMovie(InputStream in, OutputStream out) throws IOException {

		List<Movies> movies = MongoDBUtil.findAll(new Movies());

		StringBuffer sb = new StringBuffer();
		sb.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");

		sb.append("<style>b{color:red}.lemmaTitleH1{font-size:1.1em;font-weight:bold;}</style>");
		sb.append("<title>" + this.query + "</title></head><body>");

		for (Movies movie : movies) {
			sb.append("<div>id:");
			sb.append(movie.getId());
			sb.append("</div>");
			sb.append("<div>title:");
			sb.append(movie.getTitle());
			sb.append("</div>");
			sb.append("<div style='padding-left:20px'>下载地址:");
			int index=1;
			for (Download dl : movie.getDownload()) {
				sb.append("<div style='padding-left:50px'>");
				sb.append(dl.getFormat());
				sb.append("<span>   </span>");
				sb.append(dl.getSize());
				sb.append("<span>   </span>");
				sb.append(dl.getName());
				sb.append("<span>   </span>");
				sb.append("<a href=\""+dl.getHref()+"\">下载地址"+index+"</a>");
				sb.append("<span>   </span>");
				index++;
				sb.append("</div>");
			}
			sb.append("</div>");
		}

		sb.append("</body></html>");

		byte[] buf = sb.toString().getBytes();

		out.write(buf);
		out.close();
		in.close();
	}

}