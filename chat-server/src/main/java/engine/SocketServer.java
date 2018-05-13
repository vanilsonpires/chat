/**
 * @author Vanilson Pires
 * Date 12 de mai de 2018
 */
package engine;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Classe que representa um serviço socket
 * 
 * @author Vanilson Pires Date 12 de mai de 2018
 */
public class SocketServer extends Observable implements Serializable {

	/**
	 * @author Vanilson Pires Date 12 de mai de 2018
	 */
	private static final long serialVersionUID = 3473134304114251612L;

	private ServerSocket servidor;

	// guardar os clientes
	private final ArrayList<ServerConnHandler> clientes;

	public SocketServer() {
		clientes = new ArrayList<ServerConnHandler>();
	}

	/**
	 * Inicia uma thread para ficar escultado uma determinada porta
	 * 
	 * @author Vanilson Pires Date 12 de mai de 2018
	 * @param port
	 * @throws Exception 
	 */
	public void listen(final int port) throws Exception {

		final SocketServer socketServer = this;

		// Instância uma tarefa runnable
		Task task = new Task() {
			@Override
			public void run() {
				try {
					// Instância um serviço socket
					servidor = new ServerSocket(port);

					// Loop infinito para sempre ficar esperando por novas
					// conexõe
					while (servidor != null) {
						Socket cliente = servidor.accept();
						clientes.add(new ServerConnHandler(socketServer, cliente));
						setChanged();
						notifyObservers("Novo cliente conectado: ["+cliente.getInetAddress().getHostName()+"] IP: "+cliente.getInetAddress().getHostAddress());
					}
				} catch (Exception e) {
					if(!e.getMessage().equals("socket closed")){
						setChanged();
						notifyObservers(e);
					}
				}
			}
		};
		task.execute();
	}

	/**
	 * Envia mensagem para todos clientes
	 * 
	 * @author Vanilson Pires Date 12 de mai de 2018
	 * @param mensagem
	 */
	public void replicarMensagem(Serializable mensagem) {
		synchronized (clientes) {
			for (ServerConnHandler cl : clientes) {
				cl.enviarMensagem(mensagem);
			}
		}
	}

	/**
	 * Remove um cliente do servidor
	 * 
	 * @author Vanilson Pires Date 12 de mai de 2018
	 * @param cliente
	 */
	public void removerCliente(ServerConnHandler cliente) {
		synchronized (clientes) {
			clientes.remove(cliente);
			try {
				cliente.fechar();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * Remove um cliente do servidor
	 * 
	 * @author Vanilson Pires Date 12 de mai de 2018
	 * @param cliente
	 */
	public void removeConnection(Socket cliente) {
		for (int i = 0; i < clientes.size(); i++) {
			ServerConnHandler c = clientes.get(i);
			if (c.getSocket().equals(cliente)) {
				removerCliente(c);
				break;
			}
		}
	}

	/**
	 * @author Vanilson Pires Date 12 de mai de 2018
	 * @return the clientes
	 */
	public ArrayList<ServerConnHandler> getClientes() {
		return clientes;
	}

	public boolean isConected() {
		if (servidor == null)
			return false;
		return !servidor.isClosed();
	}
	
	public void close() throws IOException{
		if(servidor!=null){
			servidor.close();
			this.servidor = null;
		}	
	}
}
