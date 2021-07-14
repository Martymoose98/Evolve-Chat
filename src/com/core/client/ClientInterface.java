package com.core.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import com.core.shared.BroadcastPacket;
import com.core.shared.ConnectPacket;
import com.core.shared.ConnectResponsePacket;
import com.core.shared.DisconnectPacket;
import com.core.shared.HeartbeatPacket;
import com.core.shared.UnknownPacket;
import com.core.shared.UserListPacket;

public class ClientInterface extends JFrame
{
	private static final long serialVersionUID = -3997400817304440729L;

	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextArea chatlog;
	private OnlineUserList<String> users;
	private DefaultCaret caret;
	private String defaultMsg = "What are you thinking?";
	private Thread listen;
	private Client client;
	private AtomicBoolean isRunning = new AtomicBoolean(true);
	private JMenuBar menuBar;
	private JSplitPane splitPane;

	public ClientInterface(String name, String address, int port)
	{
		this.client = new Client(name);
		
		if (!this.client.openConnection(address, port, true))
		{
			System.err.println("Could not create a connection! IPv4 Address: " + address + ":" + port);
		}
		
		this.createWindow();
		this.listen();
		this.client.send(new ConnectPacket(this.client.getConnection(), name));
	}

	private void createWindow()
	{
		this.getLookAndFeel();
		this.setTitle("Ev0lve Chat Client | Build 0.07");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Client.class.getResource("/textures/icon32.png")));
		this.setDefaultCloseOperation(3);
		this.setSize(880, 600);
		this.setLocationRelativeTo(null);
		this.menuBar = new JMenuBar();
		this.setJMenuBar(this.menuBar);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(this.contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 30, 800, 13, 30, 7 };
		gbl_contentPane.rowHeights = new int[] { 25, 535, 40 };
		this.contentPane.setLayout(gbl_contentPane);

		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				isRunning.set(false);
				
				if (client.getConnection().isValid())
				{
					client.send(new DisconnectPacket(client.getConnection(), client.getId(), 0));
					client.close();
				}
			}
		});

		this.computeLayoutSize(gbl_contentPane.columnWidths, gbl_contentPane.rowHeights);
		this.chatlog = new JTextArea();
		this.chatlog.setForeground(Color.BLACK);
		this.chatlog.setEditable(false);
		this.caret = (DefaultCaret) this.chatlog.getCaret();
		this.caret.setUpdatePolicy(2);
		JScrollPane scroll = new JScrollPane(this.chatlog);
		scroll.setPreferredSize(new Dimension(700, 450));
		this.users = new OnlineUserList<String>();
		this.splitPane = new JSplitPane(1, scroll, this.users);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.insets = new Insets(0, 5, 5, 5);
		gbc_splitPane.fill = 1;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 1;
		gbc_splitPane.gridwidth = 3;
		gbc_splitPane.gridheight = 1;
		gbc_splitPane.weightx = 1.0;
		gbc_splitPane.weighty = 1.0;
		this.contentPane.add(this.splitPane, gbc_splitPane);
		this.txtMessage = new JTextField();
		this.txtMessage.addKeyListener(new KeyAdapter()
		{

			@Override
			public void keyPressed(KeyEvent e)
			{
				int keycode = e.getExtendedKeyCode();	
				if (keycode == KeyEvent.VK_ENTER)
				{
					send(txtMessage.getText());
					txtMessage.setText(null);
				}
			}
		});
		
		this.txtMessage.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent e)
			{
				if (txtMessage.getText().equals(defaultMsg))
				{
					txtMessage.setText(null);
					txtMessage.setForeground(Color.BLACK);
				}
			}

			@Override
			public void focusLost(FocusEvent e)
			{
				if (txtMessage.getText().equals(""))
				{
					txtMessage.setForeground(Color.LIGHT_GRAY);
					txtMessage.setText(defaultMsg);
				}
			}
		});
		this.txtMessage.setForeground(Color.LIGHT_GRAY);
		this.txtMessage.setText("What are you thinking?");
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = 2;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 2;
		gbc_txtMessage.gridwidth = 2;
		gbc_txtMessage.weightx = 1.0;
		gbc_txtMessage.weighty = 0.0;
		this.contentPane.add(this.txtMessage, gbc_txtMessage);

		JButton btnSend = new JButton("Send");

		btnSend.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				send(txtMessage.getText());
				txtMessage.setForeground(Color.LIGHT_GRAY);
				txtMessage.setText(defaultMsg);
			}
		});

		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		gbc_btnSend.weightx = 0.0;
		gbc_btnSend.weighty = 0.0;
		this.contentPane.add(btnSend, gbc_btnSend);
		this.setVisible(true);
		this.txtMessage.requestFocusInWindow();
	}

	// TODO: add timestamp? Figure out a better way to not say the default filler message
	// than  if (message.equals(defaultMsg)) return;
	private void send(String message)
	{
		if (message.isEmpty() || message.equals(defaultMsg))
			return;

		message = this.client.getName() + ": " + message;
		this.client.send(new BroadcastPacket(this.client.getConnection(), this.client.getId(), message));
	}

	public void listen()
	{
		this.listen = new Thread("Inbound Packets")
		{
			@Override
			public void run()
			{
				while (isRunning.get())
				{
					UnknownPacket packet = client.receive();

					if (packet == null)
						continue;

					switch (packet.getType())
					{
					case UnknownPacket.CONNECT_RESPONSE:
						ConnectResponsePacket response = new ConnectResponsePacket(packet);
						client.setId(response.getId());
						consoleln("Successfully connected to server! IP: " + client.getConnection() + " ID: " + client.getId());
						consoleln("Welcome, " + client.getName());
						break;
					case UnknownPacket.DISCONNECT:
						DisconnectPacket disconnect = new DisconnectPacket(packet);
						client.close();
						consoleln("Successfully disconnected from server! Reason: " + disconnect.getReason() + " IP: " + client.getConnection() + " ID: " + client.getId());
						break;
					case UnknownPacket.MESSAGE_ALL:
						BroadcastPacket broadcast = new BroadcastPacket(packet);
						consoleln(broadcast.getMessage());
						break;
					case UnknownPacket.QUERY_ALL:
						client.send(new HeartbeatPacket(client.getConnection(), client.getId()));
						break;
					case UnknownPacket.USERS_ALL:
						users.update(new UserListPacket(packet).getUsers());
						break;
					}
				}
			}
		};
		this.listen.start();
	}

	public void console(String message)
	{
		this.chatlog.append(message);
	}

	public void consoleln(String message)
	{
		this.chatlog.append(message + "\n");
	}

	private void computeLayoutSize(int[] widths, int[] heights)
	{
		int heightSum = 0;
		int widthSum = 0;
		
		for (int i = 0; i < widths.length; ++i)
		{
			int width = widths[i];
			widthSum += width;
		}
		
		for (int i = 0; i < heights.length; ++i)
		{
			int height = heights[i];
			heightSum += height;
		}

		System.out.println("Computed Layout Size is: " + widthSum + "x" + heightSum);
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
}
