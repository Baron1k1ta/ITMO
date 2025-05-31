package output;

import java.awt.BorderLayout;
import javax.swing.JFrame;

public class Output {

    public static void show(final GraphicPanel panel) throws Exception {
        final JFrame frame = new JFrame();

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(GraphicPanel.START_WIDTH, GraphicPanel.START_HEIGHT);
        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

}
