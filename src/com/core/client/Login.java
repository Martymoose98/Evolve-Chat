package com.core.client;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

public class Login extends JFrame
{
	private static final long serialVersionUID = 6660112921631338153L;
	private JPanel contentPane;
	private JTextField nicknameTextBox;
	private JTextField addressTextBox;
	private JTextField portTextBox;
	private JLabel lblInfo;
	private JButton connectButton;

	public Login()
	{
		this.getLookAndFeel();
		this.setResizable(false);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Login.class.getResource("/textures/icon32.png")));
		this.setTitle("Ev0lve Chat | Login");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setSize(350, 450);
		this.setLocationRelativeTo(null);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(this.contentPane);
		this.contentPane.setLayout(null);
		this.nicknameTextBox = new JTextField();
		this.nicknameTextBox.setHorizontalAlignment(0);
		this.nicknameTextBox.setText("Nickname");
		this.nicknameTextBox.setBounds(60, 62, 223, 20);
		this.contentPane.add(this.nicknameTextBox);
		this.nicknameTextBox.setColumns(10);
		JLabel lblName = new JLabel("Name:");
		lblName.setHorizontalAlignment(0);
		lblName.setBounds(149, 37, 46, 14);
		this.contentPane.add(lblName);
		this.addressTextBox = new JTextField();
		this.addressTextBox.setText("127.0.0.1");
		this.addressTextBox.setHorizontalAlignment(0);
		this.addressTextBox.setBounds(60, 142, 223, 20);
		this.contentPane.add(this.addressTextBox);
		this.addressTextBox.setColumns(10);
		JLabel lblIp = new JLabel("IP Address:");
		lblIp.setHorizontalAlignment(0);
		lblIp.setBounds(130, 117, 83, 14);
		this.contentPane.add(lblIp);
		this.portTextBox = new JTextField();
		this.portTextBox.setHorizontalAlignment(0);
		this.portTextBox.setText("666");
		this.portTextBox.setBounds(60, 222, 223, 20);
		this.contentPane.add(this.portTextBox);
		this.portTextBox.setColumns(10);
		JLabel lblPort = new JLabel("Port:");
		lblPort.setHorizontalAlignment(0);
		lblPort.setBounds(149, 197, 46, 14);
		this.contentPane.add(lblPort);
		this.connectButton = new JButton("Connect");
		this.connectButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String name = nicknameTextBox.getText();
				String address = addressTextBox.getText();
				int port = Integer.parseInt(portTextBox.getText());
				login(name, address, port);
			}
		});
		this.connectButton.setMnemonic('c');
		this.connectButton.setBounds(95, 282, 153, 23);
		this.contentPane.add(this.connectButton);
		this.lblInfo = new JLabel("Copyright \u24b8 Ev0lve Corperation 2022. All Rights Reserved\u00ae");
		this.lblInfo.setBounds(16, 392, 312, 18);
		this.contentPane.add(this.lblInfo);
	}

	private boolean login(String name, String address, int port)
	{
		this.dispose();
		System.out.println("Logging in with Name: " + name + ", Address: " + address + ", Port: " + port + " as credentials!");
		new ClientInterface(name, address, port);
		return true;
	}

	private boolean getLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void newInstance()
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Login frame = new Login();
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void main(String[] args)
	{
		Login.newInstance();
	}
}
