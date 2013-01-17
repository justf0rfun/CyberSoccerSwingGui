package justf0rfun.cybersoccer.gui.swing
import java.awt.BorderLayout
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.Date
import akka.actor.Actor
import akka.actor.ActorRef
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import justf0rfun.cybersoccer.controller.PublishAndSubscribeProtocol
import justf0rfun.cybersoccer.model.MatchConfiguration
import justf0rfun.cybersoccer.model.MatchState
import justf0rfun.cybersoccer.model.Team
import justf0rfun.cybersoccer.model.RefereeDecision
import justf0rfun.cybersoccer.controller.TimeMeasurement
import java.awt.Font

class CyberSoccerFrame(matchConfiguration: MatchConfiguration, matchController: ActorRef) extends Actor {

	private var matchState: MatchState = null
	private val frame = new JFrame
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
	private val width = 800
	private val height = (matchConfiguration.field.height * width / matchConfiguration.field.width).toInt + 60
	frame.setSize(width, height)
	frame setTitle "CyberSoccer"
	frame.getContentPane.setLayout(new BorderLayout)

	//	private val score = scala.collection.mutable.Map[Team, Int]()
	private val scorePanel = new JPanel
	private val hostScore = new JLabel("0")
	private val guestScore = new JLabel("0")
	private val hostNameLabel = new JLabel("Host")
	private val guestNameLabel = new JLabel("Guest")
	private val hostColor = Color.BLUE.brighter().brighter()
	private val guestColor = Color.RED
	private val hostColorBox = new JPanel
	private val guestColorBox = new JPanel
	private val dividerLabel = new JLabel("-")
	private val plainFont = new Font(hostScore.getFont.getName(), Font.PLAIN, (frame.getHeight * .03).toInt)
	private val boldFont = new Font(plainFont.getName(), Font.BOLD, plainFont.getSize())
	hostScore.setFont(boldFont)
	dividerLabel.setFont(boldFont)
	guestScore.setFont(boldFont)
	hostNameLabel.setFont(plainFont)
	guestNameLabel.setFont(plainFont)
	hostColorBox.setBackground(hostColor)
	guestColorBox.setBackground(guestColor)
	scorePanel.add(hostColorBox)
	scorePanel.add(hostNameLabel)
	scorePanel.add(hostScore)
	scorePanel.add(new JLabel("-"))
	scorePanel.add(guestScore)
	scorePanel.add(guestNameLabel)
	scorePanel.add(guestColorBox)

	private lazy val matchTimeMeasurement = new TimeMeasurement
	private val elapsedTimeLabel = new JLabel
	private val remainingTimeLabel = new JLabel
	private val timePanel = new JPanel
	elapsedTimeLabel.setFont(plainFont)
	remainingTimeLabel.setFont(plainFont)
	timePanel.add(elapsedTimeLabel)
	timePanel.add(remainingTimeLabel)

	private val bottomPanel = new JPanel
	bottomPanel.add(scorePanel)
	bottomPanel.add(timePanel)
	frame.getContentPane.add(bottomPanel, BorderLayout.SOUTH)

	private val margin = 20
	private val layeredPane = new SimpleLayeredPane
	private val fieldView = new ScalingSoccerFieldView(matchConfiguration.field, margin)
	private val matchView = new ScalingMatchView(hostColor, guestColor, margin)
	matchView.setOpaque(false)
	private val textMessagePane = new TextMessagePane(3000000000l)
	layeredPane.add(textMessagePane)
	layeredPane.add(matchView)
	layeredPane.add(fieldView)
	frame.getContentPane.add(layeredPane, BorderLayout.CENTER)

	frame.setVisible(true)

	def receive = {
		case PublishAndSubscribeProtocol.Publication(matchState: MatchState) => {
			this.matchState = matchState
			matchView.update(matchState)
			//			hostScore.setText(matchState.hostScore.toString())
			//			guestScore.setText(matchState.guestScore.toString())
			hostNameLabel.setText(matchState.host.name)
			guestNameLabel.setText(matchState.guest.name)
			val timeFormat = new SimpleDateFormat("mm:ss")
			elapsedTimeLabel.setText(timeFormat.format(new Date(TimeMeasurement.nanoSecondsToMilliSeconds(matchTimeMeasurement.elapsedTime))))
			//			remainingTimeLabel.setText(timeFormat.format(new Date(TimeMeasurement.nanoSecondsToMilliSeconds(matchState.remainingTime))))
		}
		case PublishAndSubscribeProtocol.Publication(refereeDecision: RefereeDecision) => textMessagePane.setText(refereeDecision match {
			case RefereeDecision.KickOff(team) => "KICK OFF"
			case RefereeDecision.ThrowIn(team, point) => "THROW IN"
			case RefereeDecision.Goal(team: Team) => {
				team match {
					case team if (team == matchState.host) => hostScore.setText((hostScore.getText.toInt + 1).toString())
					case team if (team == matchState.guest) => guestScore.setText((guestScore.getText.toInt + 1).toString)
				}
				"GOAL"
			}
			case RefereeDecision.GoalKick(team) => "GOAL KICK"
			case RefereeDecision.CornerKick(team, point) => "CORNER KICK"
			case RefereeDecision.Finish(winner) => "FINISH"
			case _ => null
		})

	}

}