/**
 * 
 */
package 网络信息安全考试test;

/**
 * @author hou
 *
 */
public class User {
	/**
	 * 
	 */
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	private String name;
	private String password;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @param name
	 * @param password
	 */
	public User(String name, String password) {
		super();
		this.name = name;
		this.password = password;
	}
	
}
