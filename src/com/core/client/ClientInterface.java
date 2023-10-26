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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.core.shared.BroadcastPacket;
import com.core.shared.DisconnectPacket;

public class ClientInterface extends JFrame
{
	private static final long serialVersionUID = -3997400817304440729L;

	private static final String TITLE = "Ev0lve Chat Client | Build 0.16";
	private static final String DEFAULT_MSG = "What are you thinking?";
	
	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextPane chatlog;
	private JMenuBar menuBar;
	private JMenu menu;
	private JSplitPane splitPane;
	
	private DefaultCaret caret;
	private Client client;

	public ClientInterface(String name, String address, int port)
	{
		this.client = new Client(this, name);	
		this.createWindow();
			
		if (!this.client.openConnection(address, port))
		{
			System.err.println("Could not create a connection! IPv4 Address: " + address + ":" + port);
		}
	
	}

	private void createWindow()
	{
		this.getLookAndFeel();
		this.setTitle(TITLE);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(Client.class.getResource("/textures/icon32.png")));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(880, 600);
		this.setLocationRelativeTo(null);
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				if (client.getConnection().isValid())
				{
					client.send(new DisconnectPacket(client.getConnection(), client.getId(), DisconnectPacket.REASON_USER_REQUESTED));
				}
				client.setRunning(false);
			}
		});
		
		this.menuBar = new JMenuBar();
		this.menu = new JMenu("New");
		this.menu.addMenuListener(new MenuListener()
		{
			// FIXME: This is super broken! The menu
			// only opens the login dialog once per ClientInterface instance!
			// I think this is relatively low priority. I would much 
			// rather get TCP/UDP/Handshake communications sorted!
			@Override
			public void menuSelected(MenuEvent e)
			{
				// TODO Auto-generated method stub
				//System.out.println(e);
				Login.newInstance();
				//delete();
			}

			@Override
			public void menuDeselected(MenuEvent e)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void menuCanceled(MenuEvent e)
			{
				// TODO Auto-generated method stub

			}
		});
		
		this.menuBar.add(this.menu);
		this.setJMenuBar(this.menuBar);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(this.contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 30, 800, 13, 30, 7 };
		gbl_contentPane.rowHeights = new int[] { 25, 535, 40 };
		this.contentPane.setLayout(gbl_contentPane);

		this.computeLayoutSize(gbl_contentPane.columnWidths, gbl_contentPane.rowHeights);
		this.chatlog = new JTextPane();
		this.chatlog.setForeground(Color.BLACK);
		
		// FIXME: find out how to do this for JTextPane
		//this.chatlog.setWrapStyleWord(true);
		//this.chatlog.setLineWrap(true);
		
		this.caret = (DefaultCaret) this.chatlog.getCaret();
		this.caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		JScrollPane scroll = new JScrollPane(this.chatlog);
		scroll.setPreferredSize(new Dimension(700, 450));
		
		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, this.client.getOnlineUsers());
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
				if (txtMessage.getText().equals(DEFAULT_MSG))
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
					txtMessage.setText(DEFAULT_MSG);
				}
			}
		});
		
		this.txtMessage.setForeground(Color.LIGHT_GRAY);
		this.txtMessage.setText(DEFAULT_MSG);
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
				txtMessage.setText(DEFAULT_MSG);
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
	// than if (message.equals(defaultMsg)) return;
	private void send(String message)
	{
		if (message.isEmpty() || message.equals(DEFAULT_MSG))
			return;
	
		this.client.send(new BroadcastPacket(this.client.getConnection(), this.client.getId(), message));
	}

	public void append(Color userColor, Color color, String message)
	{
		int iColon = message.indexOf(':');
		String vocalizer = message.substring(0, iColon);
		String msg = message.substring(iColon);
		
		StyledDocument doc = this.chatlog.getStyledDocument();
		StyleContext sc = StyleContext.getDefaultStyleContext();
	    AttributeSet assetUsrSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, userColor);
	    AttributeSet assetSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
	    
	    this.chatlog.setCaretPosition(doc.getLength());
	    this.chatlog.setCharacterAttributes(assetUsrSet, false);
	    this.chatlog.replaceSelection(vocalizer);
	    this.chatlog.setCaretPosition(doc.getLength());
	    this.chatlog.setCharacterAttributes(assetSet, false);
	    this.chatlog.replaceSelection(msg);
	}
	
	public void append(Color color, String message)
	{
		StyledDocument doc = this.chatlog.getStyledDocument();
		StyleContext sc = StyleContext.getDefaultStyleContext();
	    AttributeSet assetSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
	    
	    this.chatlog.setCaretPosition(doc.getLength());
	    this.chatlog.setCharacterAttributes(assetSet, false);
	    this.chatlog.replaceSelection(message);
	}
	
	public void consolefln(Color color, String format, Object... args)
	{
		consoleln(color, String.format(format, args));
	}
	
	public void consoleln(Color color, String message)
	{
		console(color, message + "\n");
	}
	
	public void consolef(Color color, String format, Object... args)
	{
		console(color, String.format(format, args));
	}
	
	public void console(Color color, String message)
	{
		append(color, message);
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

		System.out.println("Computed layout size is: " + widthSum + "x" + heightSum);
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
