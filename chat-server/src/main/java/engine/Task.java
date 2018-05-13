/**
 * @author Vanilson Pires
 * Date 12 de mai de 2018
 */
package engine;

import java.util.concurrent.Executors;

/**
 * @author Vanilson Pires Date 12 de mai de 2018
 *
 */
public abstract class Task implements Runnable{

	public Exception err;

	/**
	 * Executa a tarefa em segundo plano
	 */
	public void execute() {
		Executors.newSingleThreadExecutor().execute(this);
	}

	/**
	 * @author Vanilson Pires
	 * Date 12 de mai de 2018
	 */
	public abstract void run();
}
