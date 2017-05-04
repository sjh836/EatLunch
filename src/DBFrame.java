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
		super("���� ��������?");

		init();
		event();

		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	private void init() //������Ʈ �ʱ�ȭ��
	{
		Iconimg = new ImageIcon("logo.jpg").getImage();
		setIconImage(Iconimg); //������ ����
		BGcolor=new Color(178,235,244,80); //������ ����

		/* ���� �ǳ�(Search) ���� ���� */
		search=new JPanel() {
			public void paintComponent(Graphics g) //������ ����
			{
				g.setColor(BGcolor);
				g.fillRect(0,0,2000,100);
				setOpaque(false);
				super.paintComponent(g);
			}
		};
		search.setLayout(new GridLayout(0,4));

		search.add(new JLabel("�����˻�",JLabel.CENTER));
		String[] shoplistItem1 = {"��ü����","�����Ͽ콺","�����鸮","�ϴ�����","��ƹ���","���̽���","�丶��","��Ǯ","�����������","�����Ｎ������","�߱���","�ְ���Į����"}; 
		shoplist = new JComboBox<String>(shoplistItem1);
		search.add(shoplist);

		search.add(new JLabel("�޴��˻�",JLabel.CENTER));
		search.add(menuname=new JTextField());

		search.add(new JLabel("���ݰ˻�(����)",JLabel.CENTER));
		search.add(price=new JTextField());

		search.add(new JLabel("�����˻�",JLabel.CENTER));
		String[] gradelistItem1 = {"��ü����","��","�ڡ�","�ڡڡ�","�ڡڡڡ�", "�ڡڡڡڡ�"}; 
		gradelist = new JComboBox<String>(gradelistItem1);
		search.add(gradelist);

		add(search, BorderLayout.NORTH);
		/* ���� �ǳ�(Search) ���� �� */

		/* �߾��� �ǳ�(display) ���� ���� */
		display=new JPanel() {
			public void paintComponent(Graphics g)
			{
				g.setColor(BGcolor);
				g.fillRect(0,0,2000,1000);
				setOpaque(false);
				super.paintComponent(g);
			}
		};;

		String colName[] = {"������", "�޴�", "����", "����"};
		model = new DefaultTableModel(colName, 0); //Table�� �� ������ ��ϵ� (�������, �߰� �� row ����)
		menulist = new JTable(model);

		TableColumn shopedit = menulist.getColumnModel().getColumn(0); //������ ������ �� ������ �����θ� �������� ����
		String[] shoplistItem2 = {"�����Ͽ콺","�����鸮","�ϴ�����","��ƹ���","���̽���","�丶��","��Ǯ","�����������","�����Ｎ������","�߱���","�ְ���Į����"};
		snlist = new JComboBox<String>(shoplistItem2);
		shopedit.setCellEditor(new DefaultCellEditor(snlist));

		TableColumn gradeedit = menulist.getColumnModel().getColumn(3); //������ ������ �� ������ �� �����θ� �������� ����
		String[] gradelistItem2 = {"��","�ڡ�","�ڡڡ�","�ڡڡڡ�", "�ڡڡڡڡ�"}; 
		gdlist = new JComboBox<String>(gradelistItem2);
		gradeedit.setCellEditor(new DefaultCellEditor(gdlist));
		
		menulist.setEnabled(true); //���� ���������ϰ� ��
		menulist.getTableHeader().setResizingAllowed(false); //���̺� Į���ʺ� �����Ұ����ϰ� ��

		menulist.setRowHeight(18); //Į������ ����
		menulist.getColumnModel().getColumn(0).setPreferredWidth(60); //Į���ʺ� ����
		menulist.getColumnModel().getColumn(2).setPreferredWidth(20);
		menulist.getColumnModel().getColumn(3).setPreferredWidth(30);

		menulist.getTableHeader().setFont(new Font("�������",Font.BOLD,20)); //���̺� �����Ʈ ����
		//menulist.setFont(new Font("�������",Font.BOLD,10)); //���̺� �� ��Ʈ ����

		DefaultTableCellRenderer center = new DefaultTableCellRenderer(); //�ؽ�Ʈ �߾� ����
		center.setHorizontalAlignment(SwingConstants.CENTER);  
		menulist.getColumnModel().getColumn(0).setCellRenderer(center);
		menulist.getColumnModel().getColumn(1).setCellRenderer(center);
		menulist.getColumnModel().getColumn(2).setCellRenderer(center);
		menulist.getColumnModel().getColumn(3).setCellRenderer(center);

		data = new Object[318][4]; //�߰� �� ������ ����(��), ���� DB�� 318��������.
		TabelDisplay("SELECT * FROM food"); //ó���� �ϴ� ���

		menulist.setAutoCreateRowSorter(true); // �÷� ����� Ŭ���ϸ� �ڵ� ����
		menulist.setFillsViewportHeight(true);
		
		display.add(new JScrollPane(menulist));
		add(display, BorderLayout.CENTER);
		/* �߾��� �ǳ�(display) ���� �� */

		/* �Ʒ��� �ǳ�(Button) ���� ���� */
		button=new JPanel() {
			public void paintComponent(Graphics g)
			{
				g.setColor(BGcolor);
				g.fillRect(0,0,2000,100);
				setOpaque(false);
				super.paintComponent(g);
			}
		};;
		
		button.add(cheap3000=new JButton("�ѳ���õ"));
		//cheap3000.setFont(new Font("����", Font.BOLD,15)); ��ư��Ʈ�غ���
		button.add(cheap5000=new JButton("�ѳ���õ"));
		button.add(best=new JButton("Best�޴�"));

		add(button,BorderLayout.SOUTH);
		/* �Ʒ��� �ǳ�(Button) ���� �� */
	}

	private void event() //���� �̺�Ʈ ����Ŭ����
	{
		/* ���� �ǳ�(Search) �̺�Ʈ ���� */
		shoplist.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e) //�޺��ڽ����� �������� �ش� ���� �޴� ��� �̺�Ʈ
			{
				String shopS=(String) shoplist.getSelectedItem();
				String gradeS = (String) gradelist.getSelectedItem();
				menuname.setText("");
				price.setText("");

				if(gradeS=="��ü����")
				{
					if(shopS=="��ü����")
					{
						TabelDisplay("SELECT * FROM food");
					}
					else
					{
						TabelDisplay("SELECT * FROM food WHERE shopname='"+shopS+"'");
					}
				}
				else //������ �� �˻� ����
				{
					if(shopS=="��ü����")
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
		menuname.addActionListener(new ActionListener() //���ĸ޴� �˻� �̺�Ʈ
		{
			public void actionPerformed(ActionEvent event)
			{
				String shopS=(String) shoplist.getSelectedItem();
				String menuS=menuname.getText();
				price.setText("");
				gradelist.setSelectedIndex(0);

				if(shopS=="��ü����")
				{
					TabelDisplay("SELECT * FROM food WHERE menu Like '%"+menuS+"%'");
				}
				else //������ �� �˻� ����
				{
					TabelDisplay("SELECT * FROM food WHERE shopname='"+shopS+"' and menu Like '%"+menuS+"%'");
				}
			}
		});
		price.addActionListener(new ActionListener() //���İ��� �˻� �̺�Ʈ
		{
			public void actionPerformed(ActionEvent event)
			{
				String shopS=(String) shoplist.getSelectedItem();
				String priceS=price.getText();
				menuname.setText("");
				gradelist.setSelectedIndex(0);

				if(shopS=="��ü����")
				{
					TabelDisplay("SELECT * FROM food WHERE price<="+priceS);
				}
				else //������ �� �˻� ����
				{
					TabelDisplay("SELECT * FROM food WHERE shopname='"+shopS+"' and price<="+priceS);
				}
			}
		});
		gradelist.addActionListener(new ActionListener() //�������� �˻� �̺�Ʈ
		{
			public void actionPerformed(ActionEvent e)
			{
				String shopS=(String) shoplist.getSelectedItem();
				String gradeS = (String) gradelist.getSelectedItem();
				menuname.setText("");
				price.setText("");

				if(shopS=="��ü����")
				{
					if(gradeS=="��ü����")
					{
						TabelDisplay("SELECT * FROM food");
					}
					else
					{
						TabelDisplay("SELECT * FROM food WHERE grade='"+gradeS+"'");
					}
				}
				else //������ �� �˻� ����
				{
					if(gradeS=="��ü����")
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
		/* ���� �ǳ�(Search) �̺�Ʈ �� */

		/* �߾��� �ǳ�(display) �̺�Ʈ ���� */
		menulist.getModel().addTableModelListener(new TableModelListener() //�������� ���� �̺�Ʈ
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
		/* �߾��� �ǳ�(display) �̺�Ʈ �� */

		/* �Ʒ��� �ǳ�(Button) �̺�Ʈ ���� */
		cheap3000.addActionListener(new ActionListener() //��õ���� �޴��� ���
		{
			public void actionPerformed(ActionEvent event)
			{
				shoplist.setSelectedIndex(0);
				gradelist.setSelectedIndex(0);
				JOptionPane.showMessageDialog(null, "�ΰ���������� 1��2�� ���̵�~~", " ��õ���ȳ�",JOptionPane.INFORMATION_MESSAGE);
				TabelDisplay("SELECT * FROM food WHERE price>=2500 and price<4000");
			}
		});
		cheap5000.addActionListener(new ActionListener() //��õ���� �޴��� ���
		{
			public void actionPerformed(ActionEvent event)
			{
				shoplist.setSelectedIndex(0);
				gradelist.setSelectedIndex(0);
				JOptionPane.showMessageDialog(null, "��õ����ó �޴��� ������ �˷��帱�Կ�~!", " ��õ���ҵ�",JOptionPane.INFORMATION_MESSAGE);
				TabelDisplay("SELECT * FROM food WHERE price>=4500 and price<6000");
			}
		});
		best.addActionListener(new ActionListener() //���� ��5�� �޴��� ���
		{
			public void actionPerformed(ActionEvent event)
			{
				shoplist.setSelectedIndex(0);
				gradelist.setSelectedIndex(0);
				JOptionPane.showMessageDialog(null, "���ټ��� ���߸޴��� ��Ƹ��~!!!", " ���ְ���?",JOptionPane.INFORMATION_MESSAGE);
				TabelDisplay("SELECT * FROM food WHERE grade='�ڡڡڡڡ�'");
			}
		});
		/* �Ʒ��� �ǳ�(Button) �̺�Ʈ �� */
	}
	private void TabelDisplay(String query) //���̺� ��� ȭ��
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
			System.out.println("������ �ε� ����");
			stmt.close();
			con.close();
			System.out.println("�����ͺ��̽� ���� ����\n");
		}
		catch (Exception err)
		{
			System.out.println("������ �ε� ����");
			err.printStackTrace();
		}
	}
	private void ReGrade(String query) //���� ���� �޼ҵ�
	{
		try
		{
			con=makeConnection();
			stmt=con.createStatement();
			int j=stmt.executeUpdate(query);
			if(j==1)
			{
				System.out.println("���� ���� ����");
				JOptionPane.showMessageDialog(this, "������ �����Ǿ����ϴ�.", " �˸�",JOptionPane.INFORMATION_MESSAGE);
			}
			else
				System.out.println("���� ���� ����"); 
			stmt.close();
			con.close();
			System.out.println("�����ͺ��̽� ���� ����\n"); 
		}
		catch (SQLException err)
		{
			System.out.println("���� ���� ����");
			err.printStackTrace();
		}
	}
	public static Connection makeConnection() //DB���� �޼ҵ�
	{
		String url="jdbc:mysql://localhost/YOUR_DATABASE_NAME";
		String id="YOUR_ID";
		String password="YOUR_PASSWORD";
		Connection con=null;

		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("����̹� ���� ����");
			con=DriverManager.getConnection(url, id, password);
			System.out.println("�����ͺ��̽� ���� ����");
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("����̹��� ã�� �� �����ϴ�.");
		}
		catch (SQLException e)
		{
			System.out.println("����̹��� ã�� �� �����ϴ�.");
		}
		return con;
	}
}