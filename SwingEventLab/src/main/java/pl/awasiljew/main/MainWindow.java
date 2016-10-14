package pl.awasiljew.main;

import org.apache.log4j.Logger;
import pl.awasiljew.panels.TestPanel;

import javax.swing.*;
import java.awt.*;

/**
 * @author Adam Wasiljew
 */
public class MainWindow {

    private static final Logger logger = Logger.getLogger(MainWindow.class);
    private JFrame frame;
    private JPanel panel;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final boolean FULL_SCREEN = true;
    private static final boolean ALWAYS_ON_TOP = false;

    /**
     * Main chargee app
     */
    public MainWindow() {

        setupLookAndFeel();

        // Setup screen
        setupScreen(FULL_SCREEN);

        // Setup main panel
        setupMainPanel();

    }

    private void setupMainPanel() {
        // Create new panel
        panel = new TestPanel();
        // Add new panel to main window
        frame.add(panel);
        frame.validate();
    }

    private void setupScreen(boolean fullscreen) {
        // Create main frame
        frame = new JFrame();
        frame.setUndecorated(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(null);
        frame.setLayout(new BorderLayout());
        frame.setVisible(true);
        frame.setResizable(false);
        // Setup screen
        frame.setAlwaysOnTop(ALWAYS_ON_TOP);


        // Setting up full screen
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gds = ge.getScreenDevices();
        GraphicsDevice gd = gds[0];
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (!fullscreen) {
            int frameWidth = gd.getDisplayMode().getWidth() / 2;
            int frameHeight = gd.getDisplayMode().getHeight() / 2;
            frame.setBounds((int) screenSize.getWidth() - frameWidth, 0, frameWidth, frameHeight);
        } else {
            frame.setBounds(0, 0, WIDTH, HEIGHT);
        }
    }

    private void setupLookAndFeel() {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                } catch (ClassNotFoundException ex) {
                    logger.error(ex, ex);
                } catch (InstantiationException ex) {
                    logger.error(ex, ex);
                } catch (IllegalAccessException ex) {
                    logger.error(ex, ex);
                } catch (UnsupportedLookAndFeelException ex) {
                    logger.error(ex, ex);
                }
            }
        }
    }

}
