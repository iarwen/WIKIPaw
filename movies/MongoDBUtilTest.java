import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.mongodb.gridfs.GridFSDBFile;

public class MongoDBUtilTest {
	@Test
	public void saveOne() {
		Student t=getStudent();
		System.out.println(MongoDBUtil.save(t));
	}

	@Test
	public void saveAlist() {
		List<Student> sts = new ArrayList<Student>();
		for (int i = 0; i < 10000; i++) {
			sts.add(getStudent());
		}
		MongoDBUtil.saveList(sts);
	}

	@Test
	public void updateByDBID() {
		Student st = getStudent();
		st.setName("萨克2");
		st.set_id("52ce37a9a132cf7ebe68cdfe");
		System.out.println(MongoDBUtil.updateSingleByDBID(st));
	}
	
	@Test
	public void updateSpecifyFieldByCondition() {
		Student c = getStudent();
		c.setName("常文涛");
		c.setAge(25);
		String []cfields={"name","age"};
		
		Student n = getStudent();
		n.setName("常");
		n.setAge(10);
		n.setSex(1);
		String []nfields={"name","age","sex"};
		//update student set name="常",age=10,sex=1 where name="常文涛" and age=25
		System.out.println(MongoDBUtil.updateSpecifyFieldByCondition(cfields, c, nfields, n));
	}
	@Test
	public void findAllMovies(){
		List<Movies> list=MongoDBUtil.findAll(new Movies());
		for(Movies movie:list){
			System.out.println(movie.getTitle());
		}
	}
	@Test
	public void findByDBID() {
		Student c = getStudent();
		c.set_id("52ce37a9a132cf7ebe68cdfe");
		Student cz=MongoDBUtil.findByDBID(c);
		System.out.println(cz.getName()+"_"+cz.get_id());
	}
	@Test
	public void saveFile() throws FileNotFoundException {
		InputStream in=new FileInputStream("E:/wangyim/0/wp-admin/images/wheel.png");
		String fileName="11";
		String contentType="png";
		FileSaveEnum d=MongoDBUtil.saveFile(in, fileName,contentType);
		System.out.println(d);
	}
	@Test
	public void getFile() throws IOException {
	    String fileName="1";
		GridFSDBFile d=MongoDBUtil.getFileByName(fileName);
		if(d==null){
			System.out.println("文件不存在");
			return;
		}
		InputStream in=d.getInputStream();
		OutputStream os=new FileOutputStream("c:/"+fileName+".png");
		byte cache[]=new byte[1024];
		while(in.read(cache)!=-1){
			os.write(cache);
		}
		os.close();
		in.close();
	}
	@Test
	public void deleteFileByName() throws IOException {
		String fileName="11";
		if(MongoDBUtil.deleteFileByName(fileName)){
			System.out.println("删除成功");
		}
	}
	private Student getStudent() {
		Student st1 = new Student();
		st1.setName("常文涛");
		st1.setSex(0);
		st1.setAge(25);
		List<Book> list = new ArrayList<Book>();
		Book b1 = new Book();
		b1.setName("书1");
		b1.setAuthor("作者1");
		b1.setPage(506);
		Book b2 = new Book();
		b2.setName("书2");
		b2.setAuthor("作者2");
		b2.setPage(507);
		Book b3 = new Book();
		b3.setName("书3");
		b3.setAuthor("作者3");
		b3.setPage(508);
		list.add(b1);
		list.add(b2);
		list.add(b3);
		st1.setBooks(list);
		return st1;
	}
}
