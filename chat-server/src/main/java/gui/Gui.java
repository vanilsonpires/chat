/**
 * @author Vanilson Pires
 * Date 6 de mai de 2018
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.BindException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import engine.SocketServer;

/**
 * Representa a tela principal do chat
 * 
 * @author Vanilson Pires Date 6 de mai de 2018
 *
 */
@SuppressWarnings("serial")
public class Gui extends JFrame implements Observer {

	public static final Font DEFAULT_FONT_LABEL = new Font("Arial", Font.BOLD, 14);
	private SocketServer server;
	@SuppressWarnings("unused")
	private Status status;

	// Componentes..
	private JTextField porta;
	private JLabel labelStatus;
	private JPanel panelCenter;
	private JButton buttonPower;
	private JPanel panelButton;
	private JTextArea jTextAreaLogs;
	private JLabel qtdClientes;

	// Utilitarios
	private SimpleDateFormat formatHora;

	public static enum Status {

		DISCONNECTED("Desconectado"), CONNECTED("Conectado"), DOOR_IN_USE("Porta em uso"), ERROR("Erro desconhecido");

		private String nome;

		/**
		 * @author Vanilson Pires Date 12 de mai de 2018
		 */
		private Status(String name) {
			this.nome = name;
		}

		/**
		 * @author Vanilson Pires Date 12 de mai de 2018
		 * @return the nome
		 */
		public String getNome() {
			return nome;
		}
	};

	/**
	 * @author Vanilson Pires Date 12 de mai de 2018
	 */
	public Gui() {
		super("Chat Server");
		this.setIconImage(new ImageIcon(this.getClass().getResource("/server.png")).getImage());
		this.setLayout(new BorderLayout());
		setMinimumSize(new Dimension(800, 600));
		setMaximumSize(getMinimumSize());
		setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		labelStatus = new JLabel();
		qtdClientes = new JLabel("0");
		setStatus(Status.DISCONNECTED);
		createPanelCenter();
		createPanelButton();
		this.pack();
		setVisible(true);
		server = new SocketServer(this);
		server.addObserver(this);
		addLog("Sistema inicializado e pronto para uso");
	}

	/**
	 * Inicializa o servidor
	 * 
	 * @author Vanilson Pires Date 12 de mai de 2018
	 */
	public void ligarServer() {
		try {
			server.listen(Integer.valueOf(porta.getText()));
		} catch (BindException e) {
			setStatus(Status.DOOR_IN_USE);
			showWarning("Esta porta já está em uso!");
		} catch (Exception e) {
			e.printStackTrace();
			setStatus(Status.ERROR);
			showMsg(e);
		}
	}

	/**
	 * Notifica um erro ao usuário
	 * 
	 * @author Vanilson Pires Date 12 de mai de 2018
	 * @param exception
	 */
	public void showMsg(Exception exception) {
		if (!server.isConected()) {
			setStatus(Status.DISCONNECTED);
		}
		addLog(exception.getClass().getName()+": " + exception.getMessage());
		JOptionPane.showMessageDialog(this, exception.getMessage(), "ERRO", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Notifica um erro ao usuário
	 * 
	 * @author Vanilson Pires Date 12 de mai de 2018
	 * @param exception
	 */
	public void showWarning(String msg) {
		JOptionPane.showMessageDialog(this, msg, "ATENÇÃO", JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * @author @autor Vanilson Pires Date 12 de mai de 2018
	 * @param status
	 *            the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
		labelStatus.setText(status.getNome());
		labelStatus.repaint();
	}

	/**
	 * Cria o painel central
	 * 
	 * @author Vanilson Pires Date 12 de mai de 2018
	 */
	private void createPanelCenter() {
		this.panelCenter = new JPanel();
		this.panelCenter.setLayout(new BoxLayout(panelCenter, BoxLayout.PAGE_AXIS));

		JPanel panelButton = new JPanel();
		panelButton.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPower = new JButton();
		buttonPower.setIcon(new ImageIcon(this.getClass().getResource("/power-button.png")));
		buttonPower.setToolTipText("On/Off");
		buttonPower.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onOffServer();
			}
		});
		panelButton.add(buttonPower);

		Border border = panelButton.getBorder();
		Border margin = new EmptyBorder(70, 0, 0, 0);
		panelButton.setBorder(new CompoundBorder(border, margin));

		JPanel panelFlow = new JPanel();
		panelFlow.setLayout(new FlowLayout(FlowLayout.CENTER));
		JPanel panelTexts = new JPanel();
		panelTexts.setLayout(new GridLayout(3, 2, 2, 2));
		JLabel labelStatus = new JLabel("Status: ");
		labelStatus.setFont(DEFAULT_FONT_LABEL);
		panelTexts.add(labelStatus);
		panelTexts.add(this.labelStatus);

		JLabel labelClientes = new JLabel("Qtd. Conexões: ");
		labelClientes.setFont(DEFAULT_FONT_LABEL);
		panelTexts.add(labelClientes);
		panelTexts.add(qtdClientes);

		JLabel labelPorta = new JLabel("Porta: ");
		labelPorta.setFont(DEFAULT_FONT_LABEL);
		panelTexts.add(labelPorta);
		panelTexts.add(this.porta = new JTextField("5050"));

		labelStatus.setHorizontalAlignment(SwingConstants.RIGHT);
		this.labelStatus.setHorizontalAlignment(SwingConstants.LEFT);
		labelClientes.setHorizontalAlignment(SwingConstants.RIGHT);
		qtdClientes.setHorizontalAlignment(SwingConstants.LEFT);
		labelPorta.setHorizontalAlignment(SwingConstants.RIGHT);

		panelFlow.add(panelTexts);

		panelCenter.add(panelButton);
		panelCenter.add(panelFlow);

		this.add(panelCenter, BorderLayout.CENTER);
	}

	/**
	 * Cria o painel do fundo
	 * 
	 * @author Vanilson Pires Date 12 de mai de 2018
	 */
	private void createPanelButton() {
		this.panelButton = new JPanel();
		panelButton.setLayout(new BorderLayout());

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JLabel labelLogs = new JLabel("Logs: ");
		labelLogs.setFont(DEFAULT_FONT_LABEL);
		labelLogs.setHorizontalAlignment(JLabel.CENTER);
		panel.add(labelLogs, BorderLayout.NORTH);

		JPanel panelTextLogs = new JPanel();
		panelTextLogs.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.jTextAreaLogs = new JTextArea(11, 70);
		this.jTextAreaLogs.setText("");
		this.jTextAreaLogs.setAutoscrolls(true);
		// jTextAreaLogs.setPreferredSize(new Dimension(750, 150));
		jTextAreaLogs.setEditable(false);
		JScrollPane scroll = new JScrollPane(jTextAreaLogs);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		panelTextLogs.add(scroll);

		panel.add(panelTextLogs, BorderLayout.CENTER);

		panelButton.add(panel, BorderLayout.CENTER);
		this.add(panelButton, BorderLayout.SOUTH);
	}

	// Adiciona um log
	public void addLog(String msg) {
		this.jTextAreaLogs
				.setText(jTextAreaLogs.getText().concat("\n ".concat(getHoraAtual()).concat(" " + msg.trim())));
		jTextAreaLogs.setCaretPosition(jTextAreaLogs.getText().length());
		this.jTextAreaLogs.repaint();
	}

	/**
	 * Retorna a data/hora atual formatado
	 * 
	 * @author Vanilson Pires Date 12 de mai de 2018
	 * @return
	 */
	private String getHoraAtual() {
		if (this.formatHora == null)
			this.formatHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
		return "[" + this.formatHora.format(new Date()) + "]";
	}

	/*
	 * @author Vanilson Pires Date 12 de mai de 2018
	 */
	public void update(Observable o, Object arg) {

		if (arg != null && arg instanceof Exception) {
			showMsg((Exception) arg);
			return;
		}

		// Quando receber uma notificações, escreve no log
		if (arg != null)

			addLog(String.valueOf(arg));

		// Atualizando a qtd de clientes
		qtdClientes.setText(String.valueOf(server.getClientes().size()));
		qtdClientes.repaint();
	}

	public void onOffServer() {
		try {
			if (server.isConected()) {
				server.close();
				setStatus(Status.DISCONNECTED);
				addLog("Servidor desconectado");
			} else {
				ligarServer();
				setStatus(Status.CONNECTED);
				addLog("Conectando servidor na porta: " + porta.getText());
			}
		} catch (Exception e) {
			showMsg(e);
		}
	}
}
