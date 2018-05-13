/**
 * @author Vanilson Pires
 * Date 12 de mai de 2018
 */
package engine;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * @author Vanilson Pires
 * Date 12 de mai de 2018
 *
 */
public class ServerConnHandler extends Thread{
	
	private SocketServer servidor;
	private Socket socket;
	private ObjectOutputStream dout;

	/**
	 * Construtor padrão da classe
	 * @author Vanilson Pires
	 * Date 12 de mai de 2018
	 * @param servidor
	 * @param cliente
	 * @throws IOException
	 */
	public ServerConnHandler(SocketServer servidor, Socket cliente) throws IOException {
		this.servidor = servidor;
		this.socket = cliente;
		dout = new ObjectOutputStream(socket.getOutputStream());
		start();
	}
	
	/**
	 * @author Vanilson Pires
	 * Date 12 de mai de 2018
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

	@Override
	public void run() {
		
		ObjectInputStream din = null;
		
		try {
			// Captura a strean de leitura e escrita
			din = new ObjectInputStream(socket.getInputStream());
			String mensagem = null;
			while (true) {

				try {
					mensagem = din.readUTF();
					System.err.println("LIDO: " + mensagem);
				} catch (java.io.EOFException e) {

				} finally {
					din.close();
				}

				/**
				 * Caso receba uma notificão FINISH, remove o cliente do servidor
				 * Significa que o cliente está saindo do bate papo
				 */
				if (mensagem != null && mensagem.equals("FINISH")) {
					servidor.removeConnection(socket);
					System.out.println("Socket removido do server");
				}

				// servidor.replicarMensagem(mensagem);
			}
		} catch (EOFException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if(din!=null)
				try {
					din.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			servidor.removeConnection(socket);
		}
	}

	public void enviarMensagem(String mensagem) throws IOException {
		dout.writeUTF(mensagem);
	}

	public void enviarMensagem(Serializable obj) {
		try {
			if (!socket.isClosed() && socket.isConnected()){
				dout.writeUTF(JsonUtil.objectToJson(obj));
				dout.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void fechar() throws IOException {
		socket.close();
		dout.close();
	}

}
