/**
 * @author Vanilson Pires
 * Date 12 de mai de 2018
 */
package gui;

import engine.Session;

/**
 * @author Vanilson Pires Date 12 de mai de 2018
 *
 */
public class Chat {

	public static void main(String[] args) {
		if (Session.getInstane().isLogged())
			new Gui();
		else
			new Authentication();
	}

}
