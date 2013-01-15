package justf0rfun.cybersoccer.gui.swing
import javax.swing.JComponent
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent

class SimpleLayeredPane extends JComponent {

	addComponentListener(new ComponentAdapter {
		override def componentResized(e: ComponentEvent) = {
			resizeChildren
		}
		override def componentShown(e: ComponentEvent) = {
			resizeChildren
		}
	})

	private def resizeChildren = {
		getComponents().foreach(_.setBounds(getBounds()))
	}

}