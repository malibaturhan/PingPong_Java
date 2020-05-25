package batur.pingpong;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.event.KeyListener;
import java.util.Random;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//Rectangle is needed for collision detection
import java.awt.Rectangle;
public class Board extends JPanel implements KeyListener, ActionListener{
	final private Color darkGreen = new Color(0,53,20);
	final private Color darkGreenTitle = new Color(50,94,55);
	private Color currentBoardColor = darkGreen;
	//paddles will be 7x32;
	private int paddle1xPos = 40, paddle1yPos = 275;
	private int paddle2xPos = 760, paddle2yPos = 275;
	private final int initialpaddleWidth = 7;
	private final int initialpaddleHeigth = 32;
	private final int PADDLESPEED= 7;
	private final int PADDLEBOTTOMLIMIT = 548;
	private final int PADDLETOPLIMIT = 20;
	private PaddleWay p1paddleway = PaddleWay.STOP;
	private PaddleWay p2paddleway = PaddleWay.STOP;
	private final int BALLLATERALMAXSPEED = 8;
	private final int BALLVERTICALMAXSPEED = 6;
	private int ballxspeed =  5;      //BALLLATERALMAXSPEED / 4; //check these for disableGame method too
	private int ballyspeed = 5;   //BALLVERTICALMAXSPEED / 4;
	private int ballxposition = 393, ballyposition = 293;
	private int p1Score = 0, p2Score = 0;
	private Rectangle ballCollider = new Rectangle(ballxposition, ballyposition, 14, 14);
	private final Rectangle TOPBORDER = new Rectangle(10, 10, 780, 10);
	private final Rectangle BOTTOMBORDER = new Rectangle(10, 580, 780, 10);
	private Rectangle p1PaddleCollider = new Rectangle(paddle1xPos, paddle1yPos, 7, 32);
	private Rectangle p2PaddleCollider = new Rectangle(paddle2xPos, paddle2yPos, 7, 32);
	Timer timer;
	private boolean isGameRunning = false;
	private final static BasicStroke MIDDLEDASH = 
		new BasicStroke(1.0f,
			BasicStroke.CAP_SQUARE,
            BasicStroke.JOIN_ROUND,
            10.0f, new float[] {15.0f}, 0.0f);
	private final int DELAY = 20; 
	
	public Board() {
		super();
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		timer = new Timer(DELAY, this);
	}
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D)g;
		BasicStroke defaultStroke = (BasicStroke) g2.getStroke();
		//arrange the board(background)
		this.setBackground(currentBoardColor);
		g.setColor(Color.WHITE);
		//top border
		g.fillRect(10, 10, 780, 10);
		//right border
		g.fillRect(780, 20, 10, 570);
		//bottom border
		g.fillRect(10, 580, 780, 10);
		//left border
		g.fillRect(10, 10, 10, 580);
		g.setColor(darkGreenTitle);
		g2.drawString("malibaturhan", 30, 588);
		g.setColor(Color.WHITE);
//		g2.drawString(String.format("Ball X: %d", ballxposition), 500, 602);//DEBUG PURPOSES
//		g2.drawString(String.format("Ball Y: %d", ballyposition), 580, 602);//DEBUG PURPOSES
		g2.drawString(String.format("Score: %d", p1Score), 30, 602);
		g2.drawString(String.format("Score: %d", p2Score), 720, 602);
		if(!isGameRunning) {
			g2.drawString("Press Space to Start", 330, 602);
		}
		else {
			g2.drawString("Press R to Restart", 334, 602);
		}
		g2.setStroke(MIDDLEDASH);
		g2.drawLine(399, 20, 399, 580);
		
		//draw paddle 1(control with a-z)
		g.setColor(Color.WHITE);
		g.fillRect(paddle1xPos, paddle1yPos, initialpaddleWidth, initialpaddleHeigth);
		
		//draw paddle 2(control with o-k)
		g.fillRect(paddle2xPos, paddle2yPos, initialpaddleWidth, initialpaddleHeigth);
		
		//draw ball
		g.setColor(Color.RED);
		g.fillOval(ballxposition, ballyposition, 14, 14);
		
		g.dispose();
	}
	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		//p1 inputs
		case KeyEvent.VK_A:
			p1paddleway = PaddleWay.UP;
			break;
		case KeyEvent.VK_Z:
			p1paddleway = PaddleWay.DOWN;
			break;
		//p2 inputs
		case KeyEvent.VK_O:
			p2paddleway = PaddleWay.UP;
			break;
		case KeyEvent.VK_K:
			p2paddleway = PaddleWay.DOWN;
			break;
		case KeyEvent.VK_SPACE:
			enableGame();
			break;
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		switch(e.getKeyCode()) {
		//p1 inputs
			case KeyEvent.VK_A:
				p1paddleway = PaddleWay.STOP;
				break;
			case KeyEvent.VK_Z:
				p1paddleway = PaddleWay.STOP;
				break;
		//p2 inputs
			case KeyEvent.VK_O:
				p2paddleway = PaddleWay.STOP;
				break;
			case KeyEvent.VK_K:
				p2paddleway = PaddleWay.STOP;
				break;
			//Debug inputs
			case KeyEvent.VK_R:
				if(isGameRunning) {
					disableGame();
				}
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void actionPerformed(ActionEvent e) {
		handleMovement();
		checkScore();
		repaint();
	}
	private void handleMovement(){
		//check if paddle1 beyond bottom limit
		if(paddle1yPos>=PADDLEBOTTOMLIMIT) {
			paddle1yPos = PADDLEBOTTOMLIMIT;
		}
		//check if paddle1 beyond top limit
		if(paddle1yPos<=PADDLETOPLIMIT) {
			paddle1yPos = PADDLETOPLIMIT;
		}
		//check if paddle2 beyond bottom limit
		if(paddle2yPos>=PADDLEBOTTOMLIMIT) {
			paddle2yPos = PADDLEBOTTOMLIMIT;
		}
		//check if paddle2 beyond top limit
		if(paddle2yPos<=PADDLETOPLIMIT) {
			paddle2yPos = PADDLETOPLIMIT;
		}
		if(p1paddleway == PaddleWay.STOP) {
			paddle1yPos = paddle1yPos;
		}
		if(p2paddleway == PaddleWay.STOP) {
			paddle2yPos = paddle2yPos;
		}
		if(p1paddleway == PaddleWay.UP) {
			paddle1yPos -= PADDLESPEED;
		}
		if(p1paddleway == PaddleWay.DOWN) {
			paddle1yPos += PADDLESPEED;
		}
		if(p2paddleway == PaddleWay.UP) {
			paddle2yPos -= PADDLESPEED;
		}
		if(p2paddleway == PaddleWay.DOWN) {
			paddle2yPos += PADDLESPEED;
		}
		//handle ball movement
		if(ballCollider.intersects(p1PaddleCollider)) {
			ballxspeed = -ballxspeed;
			if(p1paddleway == PaddleWay.DOWN && ballyspeed <= BALLVERTICALMAXSPEED) {
				ballyspeed += 1;
			}
			if(p1paddleway == PaddleWay.UP && ballyspeed >= -BALLVERTICALMAXSPEED) {
				ballyspeed -= 1;
			}
		}
		//Paddle 1 collision handling
		if(ballxposition + 7 < paddle1xPos + initialpaddleWidth +2 &&  ballxposition + 7 > paddle1xPos + -5) { //check
			if(ballyposition + 7 > paddle1yPos -1 && ballyposition + 7 < paddle1yPos + initialpaddleHeigth +1) {
				if(p1paddleway == PaddleWay.DOWN  && ballyspeed <= BALLVERTICALMAXSPEED) {
					ballyspeed += 1;
					ballxspeed = -ballxspeed;
				}
				if(p1paddleway == PaddleWay.UP  && ballyspeed <= BALLVERTICALMAXSPEED) {
					ballyspeed -= 1;
					ballxspeed = -ballxspeed;
				}
				else {
					ballxspeed = -ballxspeed;
				}
			}
		}
		//end of paddle 1 collision handling
		//Paddle 2 collision handling
		if(ballxposition + 7 > paddle2xPos-2 &&  ballxposition + 7 < paddle2xPos +5) { //check
			if(ballyposition + 7 > paddle2yPos -1 && ballyposition + 7 < paddle2yPos + initialpaddleHeigth +1) {// check
				if(p2paddleway == PaddleWay.DOWN  && ballyspeed <= BALLVERTICALMAXSPEED) {
					ballyspeed += 1;
					ballxspeed = -ballxspeed;
				}
				if(p2paddleway == PaddleWay.UP  && ballyspeed <= BALLVERTICALMAXSPEED) {
					ballyspeed -= 1;
					ballxspeed = -ballxspeed;
				}
				else {
					ballxspeed = -ballxspeed;
				}
			}
		}
		//end of paddle 2 collision handling
		if(ballyposition>=580) {
			ballyspeed = -ballyspeed;
		}
		if(ballyposition<=20) {
			ballyspeed = -ballyspeed;
		}
		ballxposition += ballxspeed;
		ballyposition += ballyspeed;
	}
	private void enableGame() {
		isGameRunning = true;
		timer.start();
	}
	private void disableGame() {
		isGameRunning = false;
		timer.stop();
		ballxspeed =  5;
		ballyspeed = 5; 
		ballxposition = 393;
		ballyposition = 293;
		p1Score = 0;
		p2Score = 0;
		paddle1xPos = 40;
		paddle1yPos = 275;
		paddle2xPos = 760;
		paddle2yPos = 275;
		super.repaint();
	}
	private void checkScore() {
		Random rand = new Random();
		if(ballxposition <= 0) {
			p2Score += 1;
			timer.stop();
			isGameRunning = false;
		}
		if(ballxposition >= 800) {
			p1Score += 1;
			timer.stop();
			isGameRunning = false;
		}
		if(!isGameRunning) {
			if(p2Score> p1Score) {
				ballxspeed =  5;
			}
			if(p1Score > p2Score) {
				ballxspeed =  -5;
			}
			else {
				if(rand.nextInt() > 0) {
					ballxspeed = 6;
				}
				else {
					ballxspeed = -6;
				}
			}
			ballyspeed = 5;
			ballxposition = 393;
			ballyposition = 293;
		}
	}
}