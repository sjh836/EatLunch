import java.sql.SQLException;

public class LunchApp
{
	public static void main(String[] args) throws SQLException
	{
		System.out.println("=== 서버 로그 ===");
		new DBFrame();
	}
}