/**
 * @author Vanilson Pires
 * Date 12 de mai de 2018
 */
package engine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Observable;

import constraints.Message;

/**
 * @author Vanilson Pires Date 12 de mai de 2018
 *
 */
@SuppressWarnings("serial")
public class SocketClient extends Observable implements Serializable {
	
	// Socket usado para a ligação
	private Socket socket;
	// Streams de leitura e escrita. A de leitura é usada para receber os dados
	// do
	// servidor, enviados pelos outros clientes. A de escrita para enviar os
	// dados
	// para o servidor.
	private ObjectInputStream din;
	private ObjectOutputStream dout;

	private String ipServer;
	private Integer porta;
	private String userName;

	public void ligar(int port, String ip, String name) {
		this.porta = port;
		this.ipServer = ip;
		this.userName = name;

		new Thread(new Runnable() {
			// estamos a usar uma classe anônima...
			public void run() {
				
				try {
					
					// criar o socket
					socket = new Socket(ip, port);
					
					//Se chegar até aqui, então a conexão foi realizada com sucesso
					//Deve se então realizar uma notificação
					setChanged();
					notifyObservers(Message.CONNECTION_SUCCESSFUL);

					// Vamos obter as streams de comunicação fornecidas pelo socket
					din = new ObjectInputStream(socket.getInputStream());
					dout = new ObjectOutputStream(socket.getOutputStream());

					// e iniciar a thread que vai estar constantemente espera de novas
					// mensages. Se não usassemos uma thread, não conseguiamos receber
					// mensagens enquanto estivessemos a escrever e toda a parte gráfica
					// ficaria bloqueada.
					
					while (true) {

						String object = din.readUTF();

						// sequencialmente, ler as mensagens uma a uma e
						// acrescentar ao
						// texto que já recebemos
						// para o utilizador ver
						setChanged();
						notifyObservers(object);
					}
				} catch (Exception e) {
					setChanged();
					notifyObservers(Message.CONNECTION_FAIL);
				}
			}
		}).start();
	}

	public void send(String message) throws IOException {
		try {
			// enviar a mensagem para o servidor.
			dout.writeUTF(message);
		} catch (IOException ex) {
			throw ex;
		}
	}

	public void fechar() throws IOException {
		send("FINISH");
		this.socket.close();
		System.out.println("Socket fechado");
	}

	public boolean isClosed() {
		return socket != null ? socket.isClosed() : true;
	}

	/**
	 * @author Vanilson Pires
	 * Date 13 de mai de 2018
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * @author Vanilson Pires
	 * Date 13 de mai de 2018
	 * @return the ipServer
	 */
	public String getIpServer() {
		return ipServer;
	}

	/**
	 * @author Vanilson Pires
	 * Date 13 de mai de 2018
	 * @return the porta
	 */
	public Integer getPorta() {
		return porta;
	}
	
	/**
	 * @author Vanilson Pires
	 * Date 13 de mai de 2018
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
}
