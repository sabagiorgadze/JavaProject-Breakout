/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;
	
//I declared paddle, ball, brick vx,vy and other variables as instances, because I needed to use them in many methods
//at the same time.
	public GRect paddle;
	public GOval ball;
	public GRect brick;
	private double vx;
	private double vy = 3;
	private RandomGenerator rgen = RandomGenerator.getInstance();
	private int count = 0;
	private int destroyedBricks = 0;
	
	

/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		addMouseListeners();
		drawVizualization();
		ballMover();
	}
	
//This method draws visual aspects of the game on the canvas	
	private void drawVizualization(){
		drawBricks();
		drawBall();
		drawPaddle();
	}

//This method is drawing bricks on 10X10, integer "i" is responsible for drawing bricks in column
//integer "j" is responsible for drawing rows, in other words it draws 10 bricks on one line.
	private void drawBricks(){
		for(int i = 0; i < NBRICK_ROWS; i++){
			for(int j = 0; j < NBRICKS_PER_ROW ; j++){
				brick = new GRect(BRICK_WIDTH, BRICK_HEIGHT);
				brick.setFilled(true);
				if(i < 2){
					brick.setColor(Color.RED);
				}else if(i >= 2 && i < 4){
					brick.setColor(Color.ORANGE);
				}else if(i >= 4 && i < 6){
					brick.setColor(Color.YELLOW);
				}else if(i >= 6 && i < 8){
					brick.setColor(Color.GREEN);
				}else if(i >= 8 && i < 10){
					brick.setColor(Color.CYAN);
				}
				add(brick,j * (BRICK_WIDTH + BRICK_SEP), i * (BRICK_HEIGHT + BRICK_SEP) + BRICK_Y_OFFSET);
			}
		}
	}
	
	
//This method draws ball, which is located on the center of the canvas
	private void drawBall(){
		ball = new GOval(BALL_RADIUS * 2,BALL_RADIUS * 2);
		ball.setFilled(true);
		add(ball,WIDTH / 2 - (BALL_RADIUS), HEIGHT / 2 - (BALL_RADIUS));
	}
	

//This method draws paddle, which is used for giving ball a direction
	private void drawPaddle(){
		paddle = new GRect(PADDLE_WIDTH,PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle, (WIDTH / 2) - (PADDLE_WIDTH / 2), HEIGHT - (PADDLE_Y_OFFSET * 2));
		//I had to multiply PADDLE_Y_OFFSET by 2, because my window was a little bit bigger, so otherwise
		//paddle was appearing on the very bottom of the windows Y coordinate.
	}

	
//This is one of the mouse listeners method and it is used when mouse is moved on canvas
//Mouse movement is controlling paddle's location, because I gave paddle location of mouse's movement
	public void mouseMoved(MouseEvent mouse){
		if(mouse.getX() < WIDTH - PADDLE_WIDTH && mouse.getX() > 0){
			paddle.setLocation(mouse.getX(), HEIGHT - (PADDLE_Y_OFFSET * 2));
		}else if(mouse.getX() >= WIDTH - PADDLE_WIDTH){
			paddle.setLocation(WIDTH - PADDLE_WIDTH, HEIGHT - (PADDLE_Y_OFFSET * 2));
		}
	}
	
	

/*This method is responsible for moving ball, the ball changes direction and changes X and Y velocity based on which
*wall it hits. when ball hits right or left side of the wall, changes X velocity and if ball hits upper and bottom
*wall, changes Y velocity. The ball removes after the ball passes the paddle and hits bottom side of the canvas for 3 times.
*and draws a lose message on the center of the canvas.
*/
	public void ballMover(){
//This random generator method is used to randomly change vx movement
		vx = rgen.nextDouble(1.0,3.0);
		if(rgen.nextBoolean(0.5)){
			vx = -vx;
		}
		while(count != NTURNS){
			ball.move(vx, vy);
			pause(8);	
			if(ball.getX() > WIDTH - (BALL_RADIUS * 2)){
				vx = -vx;
			}else if(ball.getX() < 0){
				vx = -vx;
			}else if(ball.getY() > HEIGHT - PADDLE_Y_OFFSET - BALL_RADIUS){
				count++;
				add(ball,WIDTH / 2 - (BALL_RADIUS), HEIGHT / 2 - (BALL_RADIUS));
				pause(8);
				vx = rgen.nextDouble(1,3);
				if(rgen.nextBoolean(0.5)){
					vx = -vx;
				}	
			}else if(ball.getY() < 0){
				vy = -vy;
			}
			destroyBricks();
		}
		remove(ball);
		remove(paddle);
//I put if statement here, because we need to check if destroyedBricks is equal to 100, if it is equal to 100,
//that means that the user has won the game, so it would be incorrect to use drawLoseMassage, but if it
//is false and destroyedBricks is not equal to 100, we need to use drawLoseMassage, because the program
//exited the while loop and count is equal to 3 and destroyedBricks is not 100.
		if(destroyedBricks != 100){
			drawLoseMassage();
		}
	}
	

	
	
//This method draws a message: "YOU LOST!" on the center of the canvas, this method is used when ball hits
//bottom of the wall 3 times and that means that the game is over.
	private void drawLoseMassage(){
		GLabel loseString = new GLabel("YOU LOST!");
		double stringWidth = loseString.getWidth();
		double stringHeight = loseString.getHeight();
		add(loseString, WIDTH / 2 - (stringWidth / 2), HEIGHT / 2 - (stringHeight / 2));
	}
	
	
//This method is responsible for destroying bricks, when ball, in this case collider hits paddle, it changes Y velocity
//direction and when it hits something other than paddle, I assume that it is brick and invoke remove(collider) method.
	private void destroyBricks(){
		GObject collider = getColldingObject();
		if(collider == paddle && collider != null){
//This if statement prevents the ball to stuck in the paddle.
			if(ball.getY() >= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS * 2 && ball.getY() < getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS * 2 + 5){
				vy = -vy;
			}
		}else if(collider != paddle && collider != null){
			remove(collider);
//if collider is removed, that means that the ball hit the brick and we need to increase destroyedBricks by 1.
			destroyedBricks++;
			if(destroyedBricks == 100){
//if destroyedBricks is equal to 100 that means that user has won the game and I set count to 3
//to remove paddle and ball, because we do not need them anymore and the game draws the win message.
				count = 3;
				drawWinMassage();
			}
			vy = -vy;
		}
	}
	
//This method draws win massage: "YOU WIN!", it is used when destroyBricks equals 100, that means that all bricks are
//hit by ball.
	private void drawWinMassage(){
		GLabel winString = new GLabel("YOU WIN!");
		double stringWidth = winString.getWidth();
		double stringHeight = winString.getHeight();
		add(winString, WIDTH / 2 - (stringWidth / 2), HEIGHT / 2 - (stringHeight / 2));
	}
	
	
	
//This method checks if ball hit something or not, if not is return null, and if yes it returns the location
//of the object the ball hit.The ball is divided into four point, which is based on the square corner.
	private GObject getColldingObject(){
		if(getElementAt(ball.getX(),ball.getY()) != null){
			return getElementAt(ball.getX(),ball.getY());
		}else if(getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS)) != null){
			return getElementAt(ball.getX(), ball.getY() + (2 * BALL_RADIUS));
		}else if(getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY()) != null){
			return getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY());
		}else if(getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS)) != null){
			return getElementAt(ball.getX() + (2 * BALL_RADIUS), ball.getY() + (2 * BALL_RADIUS));
		}else {
			return null;
		}
	}
}