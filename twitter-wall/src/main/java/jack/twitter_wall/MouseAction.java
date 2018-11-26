package jack.twitter_wall;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import twitter4j.TwitterException;

public class MouseAction implements MouseListener {

	private App app;

	public MouseAction(App app) {
		this.app = app;
	}

	public void mouseClicked(MouseEvent e) {
		app.addBody();
		try {
			System.out.println(app.twitterDealer.nextTweetString());
		}
		catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

}
