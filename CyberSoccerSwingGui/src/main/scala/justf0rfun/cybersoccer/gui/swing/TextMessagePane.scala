package justf0rfun.cybersoccer.gui.swing
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.SwingConstants
import javax.swing.JPanel

class TextMessagePane(textDisplayDuration: Long) extends JPanel {

	private val label = new JLabel
	private var displayBeginning: Long = 0
	private def elapsedDisplayDuration: Long = System.nanoTime() - displayBeginning
	private def remaingDiplayDuration: Long = textDisplayDuration - elapsedDisplayDuration
	private def displayDurationQuotient: Double = remaingDiplayDuration.toDouble / textDisplayDuration.toDouble
	private def displayAlpha: Double = 256 * displayDurationQuotient - 1
	private def diplayAlphaQuotient: Double = displayAlpha / 255
	setLayout(new BorderLayout)
	setOpaque(false)
	label.setHorizontalAlignment(SwingConstants.CENTER)
	add(label, BorderLayout.CENTER)

	addComponentListener(new ComponentAdapter {
		override def componentShown(e: ComponentEvent) = resize
		override def componentResized(e: ComponentEvent) = resize
	})

	private def textFormat = label.getFontMetrics(label.getFont()).stringWidth(label.getText()).toDouble / label.getFont().getSize().toDouble

	private def margin = getWidth() * .2
	
	private def componentFormat = (getWidth.toDouble - 2 * margin) / (getHeight.toDouble - 2 * margin)

	private def scaleFactor: Double = if (textFormat <= componentFormat) (getHeight.toDouble - 2 * margin) / label.getFont().getSize().toDouble else (getWidth.toDouble - 2 * margin) / label.getFontMetrics(label.getFont()).stringWidth(label.getText()).toDouble

	private def resize = {
		val font = label.getFont
//		val fontSize = label.getFont.getSize() * scaleFactor
		label.setFont(new Font(font.getName(), Font.PLAIN, (getWidth * .05).toInt));
//		label.setFont(new Font(font.getName(), Font.PLAIN, fontSize.toInt));
//		println("fontSize", fontSize)
	}

	def setText(text: String) = {
		displayBeginning = System.nanoTime()
		label.setText(text)
		resize
		repaint()
	}

	override def paintComponent(graphics: Graphics) = {
		if (0 < remaingDiplayDuration) {
			label.setForeground(new Color(1f, 1f, 1f, scala.math.min(1, displayDurationQuotient).toFloat))
			super.paintComponent(graphics)
		} else {
			label.setText(null)
		}
	}

}