/**
 * @author Vanilson Pires
 * Date 13 de mai de 2018
 */
package engine;

import java.io.IOException;
import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

/**
 * Classe que representa a sessão da aplicação utilizando padrão Singleton
 * 
 * @author Vanilson Pires Date 13 de mai de 2018
 *
 */
@SuppressWarnings("serial")
public class Session extends Observable implements Serializable, Observer {

	private SocketClient socketClient;
	private static final Session INSTANCE = new Session();

	/**
	 * Construtor privado impedindo criar instâncias por outras classes
	 * 
	 * @author Vanilson Pires Date 13 de mai de 2018
	 */
	private Session() {
		// TODO Auto-generated constructor stub
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
		clear();
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

	/* @author Vanilson Pires
	 * Date 13 de mai de 2018
	 */
	@Override
	public void update(Observable o, Object arg) {
		//Repassa a notificaçao para os observadores da sessão
		setChanged();
		notifyObservers(arg);
	}
}
