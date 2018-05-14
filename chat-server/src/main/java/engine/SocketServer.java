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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import gui.Gui;

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

	private Gui gui;
	private Map<Socket, String> users;
	private ServerSocket servidor;
	private List<String> userNames;

	// guardar os clientes
	private final ArrayList<ServerConnHandler> clientes;

	public SocketServer(Gui gui) {
		this.gui = gui;
		clientes = new ArrayList<ServerConnHandler>();
		users = new HashMap<Socket, String>();
		this.userNames = new ArrayList<String>();
	}

	public void sendMessage(String origem, String destino, String msg){
		try {
			for(ServerConnHandler connHandler : clientes){
				if(connHandler.getSocket().equals(users.get(destino))){
					connHandler.enviarMensagem(msg);
					return;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public boolean addUser(Socket socket, String msg) {

		String keyUser = "username:";
		String keyStatus = "status:";

		if (msg.contains(keyUser) && msg.contains(keyStatus)) {
			String userName = msg.substring(msg.indexOf(keyUser) + keyUser.length(), msg.indexOf(","));
			if (!userNames.contains(userName)) {
				users.put(socket, msg);
				userNames.add(userName);
				return true;
			} else{
				return false;
			}
		} else {
			return false;
		}
	}

	public void addLog(String log) {
		gui.addLog(log);
	}

	/**
	 * Retorna os usuários em String
	 * 
	 * @author Vanilson Pires Date 13 de mai de 2018
	 * @return
	 */
	public String getUsers() {
		StringBuilder builder = new StringBuilder();
		for (Socket socket : users.keySet()) {
			String json = users.get(socket);
			builder.append(json);
			builder.append("<next>");
		}
		return builder.toString();
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
					// conexões
					while (servidor != null) {
						Socket cliente = servidor.accept();
						clientes.add(new ServerConnHandler(socketServer, cliente));
						setChanged();
						notifyObservers("Novo cliente conectado: [" + cliente.getInetAddress().getHostName() + "] IP: "
								+ cliente.getInetAddress().getHostAddress());						
					}
				} catch (Exception e) {
					if (!e.getMessage().equals("socket closed")) {
						setChanged();
						notifyObservers(e);
					}
				}
			}
		};
		task.execute();
	}
	
	public void atualizarUsuarios() throws IOException{
		replicarMensagem("GET_USERS:" + getUsers());
	}

	/**
	 * Envia mensagem para todos clientes
	 * 
	 * @author Vanilson Pires Date 12 de mai de 2018
	 * @param mensagem
	 * @throws IOException 
	 */
	public void replicarMensagem(String mensagem) throws IOException {
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

	public void close() throws IOException {
		if (servidor != null) {
			servidor.close();
			this.servidor = null;
		}
	}
}
