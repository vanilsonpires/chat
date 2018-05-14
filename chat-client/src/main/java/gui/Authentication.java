/**
 * @author Vanilson Pires
 * Date 13 de mai de 2018
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import constraints.Message;
import engine.Session;

/**
 * @author Vanilson Pires Date 13 de mai de 2018
 *
 */
@SuppressWarnings("serial")
public class Authentication extends JFrame implements Observer {

	private JTextField ipServer;
	private JTextField userName;
	private JTextField porta;
	private JButton btnEntrar;

	/**
	 * @author Vanilson Pires Date 13 de mai de 2018
	 */
	public Authentication() {
		
		super("Chat - FLF");
		this.setIconImage(new ImageIcon(this.getClass().getResource("/chatting.png")).getImage());
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(400, 600));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		this.setLayout(new BorderLayout());
		this.add(createPanelLogo(), BorderLayout.NORTH);
		this.add(createPanelDadosServer(), BorderLayout.CENTER);
		Session.getInstane().addObserver(this);
		
		this.pack();
		setVisible(true);
	}

	/**
	 * Retorna um painel com a logomarca
	 * 
	 * @author Vanilson Pires Date 13 de mai de 2018
	 * @return
	 */
	private JPanel createPanelLogo() {

		JPanel panelBase = new JPanel();
		panelBase.setLayout(new BorderLayout());

		JPanel panelLogo = new JPanel();
		panelLogo.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel logo = new JLabel(new ImageIcon(this.getClass().getResource("/chatting128x128.png")));
		panelLogo.add(logo);

		Border border = panelLogo.getBorder();
		Border margin = new EmptyBorder(20, 0, 0, 0);
		panelLogo.setBorder(new CompoundBorder(border, margin));

		panelBase.add(panelLogo, BorderLayout.CENTER);

		JLabel labelTitle = new JLabel("Seja bem vindo ao chat FLF!");
		labelTitle.setFont(new Font("Arial", Font.BOLD, 16));
		labelTitle.setHorizontalAlignment(JLabel.CENTER);
		panelBase.add(labelTitle, BorderLayout.SOUTH);

		return panelBase;
	}

	/**
	 * Retorna um painel com formulário para insersão dos dados do server
	 * 
	 * @author Vanilson Pires Date 13 de mai de 2018
	 * @return
	 */
	private JPanel createPanelDadosServer() {

		JPanel panelBase = new JPanel();
		panelBase.setLayout(new BorderLayout());

		JPanel panelForm = new JPanel();
		panelForm.setLayout(new FlowLayout(FlowLayout.CENTER));
		Border border = panelForm.getBorder();
		Border margin = new EmptyBorder(30, 0, 0, 0);
		panelForm.setBorder(new CompoundBorder(border, margin));

		JPanel panelGrid = new JPanel();
		panelGrid.setLayout(new GridLayout(6, 1));
		panelGrid.setAlignmentX(JPanel.CENTER_ALIGNMENT);
		JLabel labelServer = new JLabel("Servidor:");
		labelServer.setHorizontalAlignment(JLabel.CENTER);

		ipServer = new JTextField();
		ipServer.setPreferredSize(new Dimension(170, 24));
		ipServer.setHorizontalAlignment(JLabel.CENTER);
		ipServer.setText(getIP());

		JLabel labelPorta = new JLabel("Porta:");
		labelPorta.setHorizontalAlignment(JLabel.CENTER);

		this.porta = new JTextField();
		porta.setHorizontalAlignment(JLabel.CENTER);
		porta.setText("5050");

		JLabel labelUserName = new JLabel("Apelido:");
		labelUserName.setHorizontalAlignment(JLabel.CENTER);

		userName = new JTextField();
		userName.setHorizontalAlignment(JLabel.CENTER);
		userName.setText(getNameMachine());

		panelGrid.add(labelServer);
		panelGrid.add(ipServer);
		panelGrid.add(labelPorta);
		panelGrid.add(porta);
		panelGrid.add(labelUserName);
		panelGrid.add(userName);
		panelForm.add(panelGrid);

		panelBase.add(panelForm, BorderLayout.CENTER);

		JPanel panelButton = new JPanel();
		panelButton.setLayout(new FlowLayout(FlowLayout.CENTER));
		panelButton.setPreferredSize(new Dimension(100, 230));

		Authentication authentication = this;

		btnEntrar = new JButton("ENTRAR");
		btnEntrar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {

					if (ipServer.getText().trim().equals("")) {
						JOptionPane.showMessageDialog(null, "O campo 'Servidor' é obrigatório!", "ATENÇÃO",
								JOptionPane.WARNING_MESSAGE);
						return;
					}

					if (userName.getText().trim().equals("")) {
						JOptionPane.showMessageDialog(null, "O campo 'Apelido' é obrigatório!", "ATENÇÃO",
								JOptionPane.WARNING_MESSAGE);
						return;
					}

					btnEntrar.setText("Conectando...");
					btnEntrar.setEnabled(false);
					btnEntrar.updateUI();

					Session.getInstane().login(ipServer.getText(), userName.getText(),
							(porta.getText() != null ? Integer.valueOf(porta.getText().trim()) : null));

				} catch (Exception e2) {

					btnEntrar.setText("ENTRAR");
					btnEntrar.setEnabled(true);
					btnEntrar.updateUI();

					e2.printStackTrace();
					JOptionPane.showMessageDialog(authentication, e2.getMessage(), "ERRO", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		panelButton.add(btnEntrar);

		panelBase.add(panelButton, BorderLayout.SOUTH);

		return panelBase;
	}

	/**
	 * Retorna o IP atual da máquina
	 * 
	 * @autor Vanilson Pires
	 * @date 3 de mai de 2018
	 * @return
	 * @throws SocketException
	 */
	@SuppressWarnings({ "rawtypes" })
	public static String getIP() {
		try {
			boolean preferIpv4 = true;
			boolean preferIPv6 = false;
			Enumeration en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface i = (NetworkInterface) en.nextElement();
				for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements();) {
					InetAddress addr = (InetAddress) en2.nextElement();
					if (!addr.isLoopbackAddress()) {
						if (addr instanceof Inet4Address) {
							if (preferIPv6) {
								continue;
							}
							return addr.getHostAddress();
						}
						if (addr instanceof Inet6Address) {
							if (preferIpv4) {
								continue;
							}
							return addr.getHostAddress();
						}
					}
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	public static String getNameMachine() {
		try {
			boolean preferIpv4 = true;
			boolean preferIPv6 = false;
			Enumeration en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface i = (NetworkInterface) en.nextElement();
				for (Enumeration en2 = i.getInetAddresses(); en2.hasMoreElements();) {
					InetAddress addr = (InetAddress) en2.nextElement();
					if (!addr.isLoopbackAddress()) {
						if (addr instanceof Inet4Address) {
							if (preferIPv6) {
								continue;
							}
							return addr.getHostName();
						}
						if (addr instanceof Inet6Address) {
							if (preferIpv4) {
								continue;
							}
							return addr.getHostName();
						}
					}
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * @author Vanilson Pires Date 13 de mai de 2018
	 */
	@Override
	public void update(Observable o, Object arg) {

		if (arg != null && arg instanceof Message) {

			Message message = (Message) arg;

			switch (message) {

			case CONNECTION_SUCCESSFUL:
				this.dispose();
				new Gui();
				break;

			case CONNECTION_FAIL:
				btnEntrar.setText("ENTRAR");
				btnEntrar.setEnabled(true);
				btnEntrar.updateUI();
				JOptionPane.showMessageDialog(this, "Falha na conexão com o servidor", "ERRO",
						JOptionPane.ERROR_MESSAGE);
				break;
				
			case EXISTING_USER:
				btnEntrar.setText("ENTRAR");
				btnEntrar.setEnabled(true);
				btnEntrar.updateUI();
				JOptionPane.showMessageDialog(this, "Este apelido já está em uso", "ATENÇÃO",
						JOptionPane.WARNING_MESSAGE);
				break;

			default:
				break;
			}
		}
	}

}
