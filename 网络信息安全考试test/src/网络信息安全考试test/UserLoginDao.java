/**
 * 
 */
package 网络信息安全考试test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Lenovo
 *
 */
public class UserLoginDao {
	
	public static User login(User user) throws Exception{
		
		Connection con = DBhelper.getInstance().getConnection();

		User resultUser = null;

		String sql = "select * from user where name=? and password=?";

		PreparedStatement pstmt = con.prepareStatement(sql);

		pstmt.setString(1, user.getName());

		pstmt.setString(2, user.getPassword());

		ResultSet rs =  pstmt.executeQuery();

		if (rs.next()) {

			resultUser = new User();

			resultUser.setName(rs.getString("name"));

			resultUser.setPassword(rs.getString("password"));
			
			
		}

		return resultUser;

	}
	/*
	 * author hou
	 */

	public static void register(User user) {



		try {

			Connection c = DBhelper.getInstance().getConnection();

			String sql = "insert into user(name,password) values(?,?)";

			PreparedStatement ps = c.prepareStatement(sql);

			ps.setString(1, user.getName());
			
			ps.setString(2, user.getPassword());

			ps.execute();

			DBhelper.closeConnection(c, ps, null);


		} catch (Exception e) {

			e.printStackTrace();

		}

	}
	
	/*

	 * @author Hou 11.18

	 */

	public static List<User> list(int start, int count) {
		List<User> users = new ArrayList<User>();



		try {

			Connection c = DBhelper.getInstance().getConnection();

			String sql = "select * from user limit ?,?";

			PreparedStatement ps = c.prepareStatement(sql);

			ps.setInt(1, start);

			ps.setInt(2, count);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {

				User user = new User();
				
				user.setName(rs.getString("name"));
				
				user.setPassword(rs.getString("password"));

				users.add(user);

			}

			DBhelper.closeConnection(c, ps, rs);

		} catch (Exception e) {

			e.printStackTrace();

		}

		return users;

	}


}
