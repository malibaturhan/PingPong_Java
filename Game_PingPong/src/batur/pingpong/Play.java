package batur.pingpong;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Container;
public class Play extends JFrame{
	
	public Play() {
		super("Ping -o- PONG!!");
		Board game = new Board();
		//extra 24 pixels are for compensate header, java calculates header with bounds
		setBounds(0,0,800,630);
		add(game);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

	}
	
	public static void main(String[] args) {
		Play game = new Play();

	}

}
