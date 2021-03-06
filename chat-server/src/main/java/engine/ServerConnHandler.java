/**
 * @author Vanilson Pires
 * Date 12 de mai de 2018
 */
package engine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Vanilson Pires Date 12 de mai de 2018
 *
 */
public class ServerConnHandler extends Thread {

	private SocketServer servidor;
	private Socket socket;
	private DataOutputStream dout;

	/**
	 * Construtor padrão da classe
	 * 
	 * @author Vanilson Pires Date 12 de mai de 2018
	 * @param servidor
	 * @param cliente
	 * @throws IOException
	 */
	public ServerConnHandler(SocketServer servidor, Socket cliente) throws IOException {
		this.servidor = servidor;
		this.socket = cliente;
		dout = new DataOutputStream(socket.getOutputStream());
		start();
	}

	/**
	 * @author Vanilson Pires Date 12 de mai de 2018
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

	@Override
	public void run() {

		DataInputStream din = null;

		try {
			// Captura a strean de leitura e escrita
			din = new DataInputStream(socket.getInputStream());
			String mensagem = null;
			while (true) {

				try {
					mensagem = din.readUTF();				
					servidor.addLog(mensagem);

					// Se for solicitado a listagem de usuários...
					if (mensagem != null && mensagem.trim().equals("GET_USERS")) {
						//Envia uma resposta com os usuários
						enviarMensagem("GET_USERS:"+servidor.getUsers());						
						//Se estiver infornando um usuário que está onine...
					} else if (mensagem.contains("session:user{") 
							&& mensagem.contains("username:") 
							&& mensagem.contains("status:")
							&& mensagem.contains(",")
							&& mensagem.contains("};")) {
						
						if(servidor.addUser(socket, mensagem)){
							enviarMensagem("CONNECTION_SUCCESSFUL");
							servidor.atualizarUsuarios();
						}else{
							enviarMensagem("EXISTING_USER");
							servidor.removeConnection(socket);
						}						
					}else if(mensagem.contains("message:") && mensagem.contains("%user%")){						
						servidor.replicarMensagem(mensagem);
					}
				
				
				} catch (java.io.EOFException e) {
					
				}

				/**
				 * Caso receba uma notificão FINISH, remove o cliente do
				 * servidor Significa que o cliente está saindo do bate papo
				 */
				if (mensagem != null && mensagem.equals("FINISH")) {
					servidor.removeConnection(socket);
					servidor.atualizarUsuarios();
				}

				// servidor.replicarMensagem(mensagem);
			}
		} catch (EOFException ex) {
			//ex.printStackTrace();
		} catch (IOException ex) {
			//ex.printStackTrace();
		} finally {
			if (din != null)
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

	public void fechar() throws IOException {
		socket.close();
		dout.close();
	}

}
