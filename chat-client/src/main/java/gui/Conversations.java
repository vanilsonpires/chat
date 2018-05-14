/**
 * @author Vanilson Pires
 * Date 13 de mai de 2018
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.Serializable;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import constraints.Message;
import engine.Session;

/**
 * @author Vanilson Pires Date 13 de mai de 2018
 *
 */
@SuppressWarnings("serial")
public class Conversations extends JPanel implements Serializable , Observer{

	private JPanel panelBase;

	/**
	 * @author Vanilson Pires Date 13 de mai de 2018
	 */
	public Conversations() {
		this.setLayout(new BorderLayout());
		this.add(createPanelTopo(), BorderLayout.NORTH);
	
		panelBase = new JPanel();
		createPanelUsers();
		JScrollPane jScrollPane = new JScrollPane(panelBase);
		jScrollPane.setBorder(BorderFactory.createEmptyBorder());
		jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
		this.add(jScrollPane, BorderLayout.CENTER);
	}

	private JPanel createPanelTopo() {
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BorderLayout());
		jPanel.add(createPanelImageUser(), BorderLayout.LINE_START);
		jPanel.add(createPanelTitleUser(), BorderLayout.CENTER);
		return jPanel;
	}

	private JPanel createPanelImageUser() {
		JPanel jPanel = new JPanel();
		Border border = jPanel.getBorder();
		Border margin = new EmptyBorder(5, 5, 5, 5);
		jPanel.setBorder(new CompoundBorder(border, margin));

		jPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JLabel labelIconUser = new JLabel(new ImageIcon(this.getClass().getResource("/user64x64.png")));
		jPanel.add(labelIconUser);
		return jPanel;
	}

	private JPanel createPanelTitleUser() {
		
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel panelTitleMsg = new JPanel();
		panelTitleMsg.setLayout(new BoxLayout(panelTitleMsg, BoxLayout.PAGE_AXIS));

		JLabel nameUser = new JLabel(Session.getInstane().getSocketClient().getUserName());
		nameUser.setFont(new Font("Arial", Font.BOLD, 16));

		Border border = nameUser.getBorder();
		Border margin = new EmptyBorder(15, 0, 3, 5);
		nameUser.setBorder(new CompoundBorder(border, margin));

		panelTitleMsg.add(nameUser);

		JLabel status = new JLabel(Session.getInstane().getStatus().name());
		status.setFont(new Font("Arial", Font.PLAIN, 11));
		panelTitleMsg.add(status);

		jPanel.add(panelTitleMsg);

		return jPanel;
	}

	private void createPanelUsers() {
		
		panelBase.removeAll();
		panelBase.setLayout(new FlowLayout(FlowLayout.CENTER));
		panelBase.setBorder(BorderFactory.createTitledBorder("Pessoas conectadas:"));
		
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.PAGE_AXIS));		
		Border border = jPanel.getBorder();
		Border margin = new EmptyBorder(10, 5, 10, 5);
		jPanel.setBorder(new CompoundBorder(border, margin));
		
		Map<String,String> usuarios = Session.getInstane().getUsuarios();
		for(String s : usuarios.keySet()){
			
			JPanel panelUs = new JPanel();
			panelUs.addMouseListener(new java.awt.event.MouseAdapter() {
			    public void mouseEntered(java.awt.event.MouseEvent evt) {
			    	panelUs.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			    	panelUs.setCursor(new Cursor(Cursor.HAND_CURSOR));
			    }
			    public void mouseExited(java.awt.event.MouseEvent evt) {
			    	panelUs.setBorder(BorderFactory.createEmptyBorder());
			    	panelUs.setCursor(Cursor.getDefaultCursor());
			    }
				public void mouseClicked(java.awt.event.MouseEvent evt) {
					Session.getInstane().showConversa(s);
				}
			});
			
			panelUs.setLayout(new BorderLayout());
			panelUs.setPreferredSize(new Dimension(370, 45));
			
			JPanel panelImage = new JPanel();
			panelImage.setLayout(new FlowLayout(FlowLayout.CENTER));
			JLabel iconUser = new JLabel(new ImageIcon(this.getClass().getResource("/user.png")));
			panelImage.add(iconUser);			
			border = panelImage.getBorder();
			margin = new EmptyBorder(5, 5, 5, 5);
			panelImage.setBorder(new CompoundBorder(border, margin));			
			panelUs.add(panelImage, BorderLayout.LINE_START);	
			
			JPanel panelNameUser = new JPanel();
			panelNameUser.setLayout(new BoxLayout(panelNameUser, BoxLayout.PAGE_AXIS));
			
			border = panelNameUser.getBorder();
			margin = new EmptyBorder(10, 0, 3, 5);
			panelNameUser.setBorder(new CompoundBorder(border, margin));
			
			JLabel nomeUser = new JLabel(s);
			nomeUser.setFont(new Font("Arial", Font.BOLD, 11));
			
			JLabel statusUser = new JLabel(usuarios.get(s));
			statusUser.setFont(new Font("Arial", Font.PLAIN, 9));
			
			panelNameUser.add(nomeUser);
			panelNameUser.add(statusUser);
			
			panelUs.add(panelNameUser,BorderLayout.CENTER);
			
			jPanel.add(panelUs);
		}
		
		panelBase.add(jPanel);
		
	}

	/* @author Vanilson Pires
	 * Date 13 de mai de 2018
	 */
	@Override
	public void update(Observable o, Object arg) {
		
		if(arg!=null && arg instanceof Message){
			Message msg = (Message) arg;
			
			if(msg==Message.UPDATE_USERS){
				createPanelUsers();
				panelBase.updateUI();
				System.out.println("Atualizou os usuários:");
			}
		}
	}
}
