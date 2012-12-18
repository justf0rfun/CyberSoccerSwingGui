package justf0rfun.cybersoccer.gui.swing
import javax.swing.JFrame
import javax.swing.JPanel
import justf0rfun.cybersoccer.model.MatchState

//private case object Refresh

class CyberSoccerFrame(refreshTimeInterval: Long) {

  val frame = new JFrame
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  frame.setSize(800, 600)
  val fieldPane = new JPanel
  frame.setContentPane(fieldPane)
  frame.setVisible(true)
  
//  context.system.scheduler.schedule(Duration.Zero, Duration.create(refreshTimeInterval, TimeUnit.MILLISECONDS), self, Refresh)
  
//  def receive = {
//    case matchState: MatchState => fieldPane.repaint()
//  }
  
}