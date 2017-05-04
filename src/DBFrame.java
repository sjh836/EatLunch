import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class DBFrame extends JFrame
{
	private JPanel search, display, button;
	private JComboBox<String> shoplist, snlist, gradelist, gdlist;
	private JTable menulist;
	private JTextField menuname, price, grade;
	private JButton cheap3000, cheap5000, best;
	private Connection con;
	private Statement stmt;
	private ResultSet rs;
	private DefaultTableModel model;
	private Object[][] data;
	private Image BGimg, Iconimg;
	private Color BGcolor;

	public DBFrame() throws SQLException
	{
		super("점심 뭐먹을래?");

		init();
		event();

		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	private void init() //컴포넌트 초기화용
	{
		Iconimg = new ImageIcon("logo.jpg").getImage();
		setIconImage(Iconimg); //아이콘 변경
		BGcolor=new Color(178,235,244,80); //바탕색 설정

		/* 위쪽 판넬(Search) 구간 시작 */
		search=new JPanel() {
			public void paintComponent(Graphics g) //바탕색 설정
			{
				g.setColor(BGcolor);
				g.fillRect(0,0,2000,100);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		search.setLayout(new GridLayout(0,4));

		search.add(new JLabel("점포검색",JLabel.CENTER));
		String[] shoplistItem1 = {"전체보기","파인하우스","프랜들리","하늘지기","모아밀터","라이스볼","토마토","밥풀","봉구스밥버거","마녀즉석떡볶이","중국관","최고집칼국수"}; 
		shoplist = new JComboBox<String>(shoplistItem1);
		search.add(shoplist);

		search.add(new JLabel("메뉴검색",JLabel.CENTER));
		search.add(menuname=new JTextField());

		search.add(new JLabel("가격검색(이하)",JLabel.CENTER));
		search.add(price=new JTextField());

		search.add(new JLabel("평점검색",JLabel.CENTER));
		String[] gradelistItem1 = {"전체보기","★","★★","★★★","★★★★", "★★★★★"}; 
		gradelist = new JComboBox<String>(gradelistItem1);
		search.add(gradelist);

		add(search, BorderLayout.NORTH);
		/* 위쪽 판넬(Search) 구간 끝 */

		/* 중앙쪽 판넬(display) 구간 시작 */
		display=new JPanel() {
			public void paintComponent(Graphics g)
			{
				g.setColor(BGcolor);
				g.fillRect(0,0,2000,1000);
				setOpaque(false);
				super.paintComponent(g);
			}
		};;

		String colName[] = {"점포명", "메뉴", "가격", "평점"};
		model = new DefaultTableModel(colName, 0); //Table에 들어갈 데이터 목록들 (헤더정보, 추가 될 row 개수)
		menulist = new JTable(model);

		TableColumn shopedit = menulist.getColumnModel().getColumn(0); //점포를 수정할 때 지정된 곳으로만 편집가능 설정
		String[] shoplistItem2 = {"파인하우스","프랜들리","하늘지기","모아밀터","라이스볼","토마토","밥풀","봉구스밥버거","마녀즉석떡볶이","중국관","최고집칼국수"};
		snlist = new JComboBox<String>(shoplistItem2);
		shopedit.setCellEditor(new DefaultCellEditor(snlist));

		TableColumn gradeedit = menulist.getColumnModel().getColumn(3); //평점을 수정할 때 지정된 별 갯수로만 편집가능 설정
		String[] gradelistItem2 = {"★","★★","★★★","★★★★", "★★★★★"}; 
		gdlist = new JComboBox<String>(gradelistItem2);
		gradeedit.setCellEditor(new DefaultCellEditor(gdlist));
		
		menulist.setEnabled(true); //값을 수정가능하게 함
		menulist.getTableHeader().setResizingAllowed(false); //테이블 칼럼너비를 수정불가능하게 함

		menulist.setRowHeight(18); //칼럼높이 설정
		menulist.getColumnModel().getColumn(0).setPreferredWidth(60); //칼럼너비 설정
		menulist.getColumnModel().getColumn(2).setPreferredWidth(20);
		menulist.getColumnModel().getColumn(3).setPreferredWidth(30);

		menulist.getTableHeader().setFont(new Font("맑은고딕",Font.BOLD,20)); //테이블 헤더폰트 설정
		//menulist.setFont(new Font("맑은고딕",Font.BOLD,10)); //테이블 내 폰트 설정

		DefaultTableCellRenderer center = new DefaultTableCellRenderer(); //텍스트 중앙 정렬
		center.setHorizontalAlignment(SwingConstants.CENTER);  
		menulist.getColumnModel().getColumn(0).setCellRenderer(center);
		menulist.getColumnModel().getColumn(1).setCellRenderer(center);
		menulist.getColumnModel().getColumn(2).setCellRenderer(center);
		menulist.getColumnModel().getColumn(3).setCellRenderer(center);

		data = new Object[318][4]; //추가 할 데이터 생성(열), 현재 DB에 318개들어가있음.
		TabelDisplay("SELECT * FROM food"); //처음엔 싹다 출력

		menulist.setAutoCreateRowSorter(true); // 컬럼 헤더를 클릭하면 자동 정렬
		menulist.setFillsViewportHeight(true);
		
		display.add(new JScrollPane(menulist));
		add(display, BorderLayout.CENTER);
		/* 중앙쪽 판넬(display) 구간 끝 */

		/* 아래쪽 판넬(Button) 구간 시작 */
		button=new JPanel() {
			public void paintComponent(Graphics g)
			{
				g.setColor(BGcolor);
				g.fillRect(0,0,2000,100);
				setOpaque(false);
				super.paintComponent(g);
			}
		};;
		
		button.add(cheap3000=new JButton("한끼삼천"));
		//cheap3000.setFont(new Font("굴림", Font.BOLD,15)); 버튼폰트준비중
		button.add(cheap5000=new JButton("한끼오천"));
		button.add(best=new JButton("Best메뉴"));

		add(button,BorderLayout.SOUTH);
		/* 아래쪽 판넬(Button) 구간 끝 */
	}

	private void event() //각종 이벤트 무명클래스
	{
		/* 위쪽 판넬(Search) 이벤트 시작 */
		shoplist.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) //콤보박스에서 뭐누르면 해당 가게 메뉴 출력 이벤트
			{
				String shopS=(String) shoplist.getSelectedItem();
				String gradeS = (String) gradelist.getSelectedItem();
				menuname.setText("");
				price.setText("");

				if(gradeS=="전체보기")
				{
					if(shopS=="전체보기")
					{
						TabelDisplay("SELECT * FROM food");
					}
					else
					{
						TabelDisplay("SELECT * FROM food WHERE shopname='"+shopS+"'");
					}
				}
				else //평점고른 후 검색 지원
				{
					if(shopS=="전체보기")
					{
						TabelDisplay("SELECT * FROM food WHERE grade='"+gradeS+"'");
					}
					else
					{
						TabelDisplay("SELECT * FROM food WHERE shopname='"+shopS+"' and grade='"+gradeS+"'");
					}
				}
			}
		});
		menuname.addActionListener(new ActionListener() //음식메뉴 검색 이벤트
		{
			public void actionPerformed(ActionEvent event)
			{
				String shopS=(String) shoplist.getSelectedItem();
				String menuS=menuname.getText();
				price.setText("");
				gradelist.setSelectedIndex(0);

				if(shopS=="전체보기")
				{
					TabelDisplay("SELECT * FROM food WHERE menu Like '%"+menuS+"%'");
				}
				else //점포고른 후 검색 지원
				{
					TabelDisplay("SELECT * FROM food WHERE shopname='"+shopS+"' and menu Like '%"+menuS+"%'");
				}
			}
		});
		price.addActionListener(new ActionListener() //음식가격 검색 이벤트
		{
			public void actionPerformed(ActionEvent event)
			{
				String shopS=(String) shoplist.getSelectedItem();
				String priceS=price.getText();
				menuname.setText("");
				gradelist.setSelectedIndex(0);

				if(shopS=="전체보기")
				{
					TabelDisplay("SELECT * FROM food WHERE price<="+priceS);
				}
				else //점포고른 후 검색 지원
				{
					TabelDisplay("SELECT * FROM food WHERE shopname='"+shopS+"' and price<="+priceS);
				}
			}
		});
		gradelist.addActionListener(new ActionListener() //음식평점 검색 이벤트
		{
			public void actionPerformed(ActionEvent e)
			{
				String shopS=(String) shoplist.getSelectedItem();
				String gradeS = (String) gradelist.getSelectedItem();
				menuname.setText("");
				price.setText("");

				if(shopS=="전체보기")
				{
					if(gradeS=="전체보기")
					{
						TabelDisplay("SELECT * FROM food");
					}
					else
					{
						TabelDisplay("SELECT * FROM food WHERE grade='"+gradeS+"'");
					}
				}
				else //점포고른 후 검색 지원
				{
					if(gradeS=="전체보기")
					{
						TabelDisplay("SELECT * FROM food WHERE shopname='"+shopS+"'");
					}
					else
					{
						TabelDisplay("SELECT * FROM food WHERE shopname='"+shopS+"' and grade='"+gradeS+"'");
					}
				}
			}
		});
		/* 위쪽 판넬(Search) 이벤트 끝 */

		/* 중앙쪽 판넬(display) 이벤트 시작 */
		menulist.getModel().addTableModelListener(new TableModelListener() //음식평점 수정 이벤트
		{
			public void tableChanged(TableModelEvent e)
			{
				int row = e.getFirstRow();
				int column = e.getColumn();
				if(column==3)
				{
					String shopS=(String)model.getValueAt(row, column-3);
					String menuS=(String)model.getValueAt(row, column-2);
					String regrade=(String)model.getValueAt(row, column);
					String q="UPDATE food SET grade='"+regrade+"' WHERE shopname='"+shopS+"' and menu='"+menuS+"'";
					ReGrade(q);
				}
			}
		});
		/* 중앙쪽 판넬(display) 이벤트 끝 */

		/* 아래쪽 판넬(Button) 이벤트 시작 */
		cheap3000.addActionListener(new ActionListener() //삼천원대 메뉴만 출력
		{
			public void actionPerformed(ActionEvent event)
			{
				shoplist.setSelectedIndex(0);
				gradelist.setSelectedIndex(0);
				JOptionPane.showMessageDialog(null, "싸고맛도잇으면 1석2조 개이득~~", " 삼천원냠냠",JOptionPane.INFORMATION_MESSAGE);
				TabelDisplay("SELECT * FROM food WHERE price>=2500 and price<4000");
			}
		});
		cheap5000.addActionListener(new ActionListener() //오천원대 메뉴만 출력
		{
			public void actionPerformed(ActionEvent event)
			{
				shoplist.setSelectedIndex(0);
				gradelist.setSelectedIndex(0);
				JOptionPane.showMessageDialog(null, "오천원근처 메뉴를 꼭집어 알려드릴게요~!", " 오천원뚝딱",JOptionPane.INFORMATION_MESSAGE);
				TabelDisplay("SELECT * FROM food WHERE price>=4500 and price<6000");
			}
		});
		best.addActionListener(new ActionListener() //평점 별5개 메뉴만 출력
		{
			public void actionPerformed(ActionEvent event)
			{
				shoplist.setSelectedIndex(0);
				gradelist.setSelectedIndex(0);
				JOptionPane.showMessageDialog(null, "별다섯개 강추메뉴만 모아모아~!!!", " 맛있겠지?",JOptionPane.INFORMATION_MESSAGE);
				TabelDisplay("SELECT * FROM food WHERE grade='★★★★★'");
			}
		});
		/* 아래쪽 판넬(Button) 이벤트 끝 */
	}
	private void TabelDisplay(String query) //테이블 출력 화면
	{
		for(int i= model.getRowCount()-1; i>=0; i--) 
			model.removeRow(i);
		try
		{
			con=makeConnection();
			stmt=con.createStatement();
			rs=stmt.executeQuery(query);

			for(int i=0; rs.next(); i++)
			{
				data[i][0] = rs.getString("shopname");
				data[i][1] = rs.getString("menu");
				data[i][2] = rs.getInt("price");
				data[i][3] = rs.getString("grade");
				model.addRow(data[i]);
			}
			System.out.println("데이터 로딩 성공");
			stmt.close();
			con.close();
			System.out.println("데이터베이스 연결 해제\n");
		}
		catch (Exception err)
		{
			System.out.println("데이터 로딩 오류");
			err.printStackTrace();
		}
	}
	private void ReGrade(String query) //평점 수정 메소드
	{
		try
		{
			con=makeConnection();
			stmt=con.createStatement();
			int j=stmt.executeUpdate(query);
			if(j==1)
			{
				System.out.println("평점 수정 성공");
				JOptionPane.showMessageDialog(this, "평점이 수정되었습니다.", " 알림",JOptionPane.INFORMATION_MESSAGE);
			}
			else
				System.out.println("평점 수정 실패"); 
			stmt.close();
			con.close();
			System.out.println("데이터베이스 연결 해제\n"); 
		}
		catch (SQLException err)
		{
			System.out.println("평점 수정 오류");
			err.printStackTrace();
		}
	}
	public static Connection makeConnection() //DB연결 메소드
	{
		String url="jdbc:mysql://localhost/YOUR_DATABASE_NAME";
		String id="YOUR_ID";
		String password="YOUR_PASSWORD";
		Connection con=null;

		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("드라이버 적재 성공");
			con=DriverManager.getConnection(url, id, password);
			System.out.println("데이터베이스 연결 성공");
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("드라이버를 찾을 수 없습니다.");
		}
		catch (SQLException e)
		{
			System.out.println("드라이버를 찾을 수 없습니다.");
		}
		return con;
	}
}