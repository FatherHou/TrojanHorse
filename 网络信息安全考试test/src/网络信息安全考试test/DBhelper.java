/**
 * 
 */
package 网络信息安全考试test;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * @author ����
 *
 */
public class DBhelper {

	public String url = "jdbc:mysql://localhost:3306/sec?useUnicode=true&characterEncoding=UTF-8";
	public String username = "root";
	public String password = "root";
	public static DBhelper instance = null;

	// ͨ����̬�����ע�����ݿ���������֤ע��ִֻ��һ��
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private DBhelper() {
	}

	/**
	 * �����ʵ��
	 * 
	 * @return
	 */
	public static DBhelper getInstance() {
		// �������,��ֹ�̲߳���
		synchronized (DBhelper.class) {
			if (instance == null) {
				instance = new DBhelper();
			}
		}
		return instance;
	}

	/**
	 * �������
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}

	/**
	 * �ر�����
	 * 
	 * @param conn
	 * @param st
	 * @param rs
	 */
	public static void closeConnection(Connection conn, Statement st,
			ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null) {
					st.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
