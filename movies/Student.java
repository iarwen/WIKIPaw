import java.util.List;

public class Student  extends AbstractDBObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8523348687707759436L;
	private String name;
	private int age;
	private int sex;
	private List<Book> books;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}

}
