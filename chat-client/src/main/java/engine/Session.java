/**
 * @author Vanilson Pires
 * Date 13 de mai de 2018
 */
package engine;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import constraints.Message;

/**
 * Classe que representa a sessão da aplicação utilizando padrão Singleton
 * 
 * @author Vanilson Pires Date 13 de mai de 2018
 *
 */
@SuppressWarnings("serial")
public class Session extends Observable implements Serializable, Observer {

	public static enum Status {
		ONLINE, OFFILINE, OCUPADO
	};

	private Status status;
	private SocketClient socketClient;
	private static final Session INSTANCE = new Session();
	private Map<String, String> usuarios;

	/**
	 * Construtor privado impedindo criar instâncias por outras classes
	 * 
	 * @author Vanilson Pires Date 13 de mai de 2018
	 */
	private Session() {
		this.usuarios = new HashMap<String, String>();
		this.status = Status.ONLINE;
	}

	/**
	 * Retorna a instância e sessão
	 * 
	 * @author Vanilson Pires Date 13 de mai de 2018
	 * @return
	 */
	public static synchronized Session getInstane() {
		return INSTANCE;
	}

	/**
	 * Retorna um booleano indicando se existe um usuário logado ou não
	 * 
	 * @author Vanilson Pires Date 13 de mai de 2018
	 * @return
	 */
	public boolean isLogged() {
		return socketClient != null && !socketClient.isClosed() && socketClient.getUserName() != null
				&& !socketClient.getUserName().trim().equals("");
	}

	/**
	 * Realiza o login no servidor
	 * 
	 * @author Vanilson Pires Date 13 de mai de 2018
	 * @param ip
	 * @param username
	 * @throws IOException
	 */
	public void login(String ip, String username, Integer porta) throws IOException {
		socketClient = new SocketClient();
		socketClient.addObserver(this);
		socketClient.ligar(porta, ip, username);
	}

	/**
	 * Apaga os dados da sessão
	 * 
	 * @author Vanilson Pires Date 13 de mai de 2018
	 * @throws IOException
	 */
	public void clear() throws IOException {
		if (socketClient != null && !socketClient.isClosed())
			socketClient.fechar();
		socketClient = null;
	}

	/*
	 * @author Vanilson Pires Date 13 de mai de 2018
	 */
	@Override
	public void update(Observable o, Object arg) {

		System.out.println("Mensagem recebida: " + arg);

		try {

			if (arg != null && arg instanceof String) {

				String msg = (String) arg;

				// Se for uma mensagem do servidor informando os usuários..
				if (msg.contains("GET_USERS:session:user{")) {
					salveUsers(msg.substring(msg.indexOf("GET_USERS:"), msg.length()));
					setChanged();
					notifyObservers(Message.UPDATE_USERS);
				} else if (msg.equals("CONNECTION_SUCCESSFUL")) {
					this.status = Status.ONLINE;
					solitUsers();
					setChanged();
					notifyObservers(Message.CONNECTION_SUCCESSFUL);
					return;
				} else if (msg.equals("EXISTING_USER")) {
					setChanged();
					notifyObservers(Message.EXISTING_USER);
					return;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Repassa a notificaçao para os observadores da sessão
		setChanged();
		notifyObservers(arg);
	}

	private void salveUsers(String msg) {

		// Separa os usuários apartir dos delimitadores
		String[] users = msg.split("<next>");

		String keyUser = "username:";
		String keyStatus = "status:";

		for (int index = 0; index < users.length; index++) {

			String usuario = users[index];
			System.out.println(usuario);

			if (usuario.contains(keyUser) && usuario.contains(keyStatus)) {
				String userName = usuario.substring(usuario.indexOf(keyUser) + keyUser.length(), usuario.indexOf(","));
				String status = usuario.substring(usuario.indexOf(keyStatus) + keyStatus.length(),
						usuario.indexOf("};"));
				usuarios.put(userName, status);
			}
		}
	}

	public String getJsonUser() {
		StringBuilder builder = new StringBuilder();
		builder.append("session:user{");
		builder.append("username:");
		builder.append(socketClient.getUserName());
		builder.append(",");
		builder.append("status:");
		builder.append(this.status.name());
		builder.append("};");
		return builder.toString();
	}

	/**
	 * @author Vanilson Pires Date 13 de mai de 2018
	 * @return the socketClient
	 */
	public SocketClient getSocketClient() {
		return socketClient;
	}

	/**
	 * @author Vanilson Pires Date 13 de mai de 2018
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Solicita a listagem de usuários ao servidor
	 * 
	 * @author Vanilson Pires Date 13 de mai de 2018
	 * @throws IOException
	 */
	public void solitUsers() throws IOException {
		socketClient.send(Message.GET_USERS.name());
	}

	/**
	 * @author Vanilson Pires Date 13 de mai de 2018
	 * @return the usuarios
	 */
	public Map<String, String> getUsuarios() {
		return usuarios;
	}
}
