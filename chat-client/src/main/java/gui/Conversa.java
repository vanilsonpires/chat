/**
 * @author Vanilson Pires
 * Date 13 de mai de 2018
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import engine.Session;

/**
 * @author Vanilson Pires Date 13 de mai de 2018
 *
 */
@SuppressWarnings("serial")
public class Conversa extends JFrame implements Observer {

	private String userKey;
	private JTextArea msgs;

	/**
	 * @author Vanilson Pires Date 13 de mai de 2018
	 */
	public Conversa(String userKey) {
		super(userKey + " - Chat - FLF");
		this.userKey = userKey;
		this.setIconImage(new ImageIcon(this.getClass().getResource("/chatting.png")).getImage());
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(650, 650));
		this.setMaximumSize(this.getMinimumSize());
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		init();
		this.pack();
		setVisible(true);
	}

	private void init() {
		this.setLayout(new BorderLayout());
		this.add(createPanelTopo(), BorderLayout.NORTH);
		this.add(createPanelMsgs(), BorderLayout.CENTER);
		this.add(panelEscreverMsg(), BorderLayout.SOUTH);
	}

	private JPanel createPanelTopo() {
		JPanel jPanel = new JPanel();
		Border border = jPanel.getBorder();
		Border margin = new EmptyBorder(5, 5, 5, 5);
		jPanel.setBorder(new CompoundBorder(border, margin));
		jPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel jLabel = new JLabel("Conversa de "+Session.getInstane().getSocketClient().getUserName()+" com "+this.userKey);
		jLabel.setFont(new Font("Arial", Font.BOLD, 16));
		jPanel.add(jLabel);

		return jPanel;
	}

	private JPanel createPanelMsgs() {
		JPanel jPanel = new JPanel();
		Border border = jPanel.getBorder();
		Border margin = new EmptyBorder(10, 5, 10, 5);
		jPanel.setBorder(new CompoundBorder(border, margin));

		jPanel.setLayout(new BorderLayout());

		msgs = new JTextArea();
		msgs.setEditable(false);
		msgs.setBackground(new Color(245, 245, 220));
		JScrollPane scroll = new JScrollPane(msgs);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		jPanel.add(scroll, BorderLayout.CENTER);

		return jPanel;
	}

	private JPanel panelEscreverMsg() {
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BorderLayout());

		JTextArea msg = new JTextArea(7, 67);
		JScrollPane scroll = new JScrollPane(msg);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		Border border = msg.getBorder();
		Border margin = new EmptyBorder(5, 5, 5, 5);
		msg.setBorder(new CompoundBorder(border, margin));

		jPanel.add(scroll, BorderLayout.CENTER);

		JPanel panelButton = new JPanel();
		panelButton.setLayout(new FlowLayout(FlowLayout.RIGHT));

		JButton button = new JButton("Enviar");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (sendMessage(msg.getText())) {
						// Se enviar a msg limpa o texto
						msg.setText("");
					}
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(jPanel, e2.getMessage(), "ERRO",JOptionPane.ERROR_MESSAGE);
					e2.printStackTrace();
				}
			}
		});
		panelButton.add(button);
		jPanel.add(panelButton, BorderLayout.SOUTH);

		return jPanel;
	}

	private boolean sendMessage(String msg) throws IOException {
		return Session.getInstane().sendMessage("%user%"+Session.getInstane().getSocketClient().getUserName()+"%user%"+msg, this.userKey);
	}

	/*
	 * @author Vanilson Pires Date 13 de mai de 2018
	 */
	@Override
	public void update(Observable o, Object arg) {

		if (arg instanceof String) {
			String msg = (String) arg;
			String key = "message:" + this.userKey+":";
			String userKey = "%user%";
			if (msg.contains(key) && msg.contains(userKey)) {
				addMensagem(msg.substring(msg.indexOf(userKey)+userKey.length(), msg.lastIndexOf(userKey)), msg.substring(msg.indexOf(key) + key.length(), msg.length()));
			}
		}
	}
	
	public void addMensagem(String usuario, String msg) {
		
		if(msg.contains("%user%")){
			msg = msg.substring(msg.lastIndexOf("%user%")+"%user%".length(), msg.length());
		}
		
		msgs.setText(msgs.getText() + "\n" + " [" + getHora() + "]" +" "+usuario+" Diz: "+ msg);
		msgs.setCaretPosition(msgs.getText().length());
	}

	private String getHora() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return dateFormat.format(new Date());
	}
}
