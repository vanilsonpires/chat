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

	public void ligar(int port, String ip) {
		try {

			// criar o socket
			socket = new Socket(ip, port);

			// Vamos obter as streams de comunicação fornecidas pelo socket
			din = new ObjectInputStream(socket.getInputStream());
			dout = new ObjectOutputStream(socket.getOutputStream());

			// e iniciar a thread que vai estar constantemente espera de novas
			// mensages. Se não usassemos uma thread, não conseguiamos receber
			// mensagens enquanto estivessemos a escrever e toda a parte gráfica
			// ficaria bloqueada.
			new Thread(new Runnable() {
				// estamos a usar uma classe anônima...
				public void run() {
					try {
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
						// TODO: handle exception
					}
				}
			}).start();

		} catch (IOException ex) {
			setChanged();
			notifyObservers(ex);
		}
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
		return socket.isClosed();
	}
}
