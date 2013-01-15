package justf0rfun.cybersoccer.gui.swing

import akka.actor.Actor
import javax.swing.JFrame
import javax.swing.JScrollPane
import javax.swing.JTextArea
import justf0rfun.cybersoccer.controller.PublishAndSubscribeProtocol
import justf0rfun.cybersoccer.model.MatchState
import scala.util.Properties

class SoccerEventsFrame extends Actor {

	val frame = new JFrame
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
	val width = 200
	val height = 500
	frame.setSize(width, height)
	frame setTitle "CyberSoccer Events"
	val console = new JTextArea
	console.setEditable(false)
	val consoleScrollPane = new JScrollPane(console)
	frame.setVisible(true)
	
	override def receive = {
		case PublishAndSubscribeProtocol.Publication(matchState: MatchState) => {
			console.append("cycle")
			console.append(Properties.lineSeparator)
			//			Rectangle rect = new Rectangle(0, console.getHeight, 10, 10);
			//			console.scrollRectToVisible(rect);
		} 
	}

}