/**
 * @author Vanilson Pires
 * Date 12 de mai de 2018
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * Representa o frame principal
 * 
 * @author Vanilson Pires Date 12 de mai de 2018
 *
 */
@SuppressWarnings("serial")
public class Gui extends JFrame {

	/**
	 * @author Vanilson Pires Date 13 de mai de 2018
	 */
	public Gui() {
		super("Chat - FLF");
		this.setIconImage(new ImageIcon(this.getClass().getResource("/chatting.png")).getImage());
		this.setLayout(new BorderLayout());
		this.setMinimumSize(new Dimension(400, 600));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.add(new Conversations(), BorderLayout.CENTER);

		this.pack();
		setVisible(true);
	}

	/**
	 * Altera entre a tela de autenticação para tela de conversas
	 * 
	 * @author Vanilson Pires Date 13 de mai de 2018
	 */
	public void connect() {
		this.removeAll();
		this.add(new Conversations(), BorderLayout.CENTER);
		this.repaint();
		this.validate();
	}

}
