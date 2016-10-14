package pl.awasiljew.panels;

import pl.awasiljew.processors.BlockingQueueProcessor;
import pl.awasiljew.tasks.LongRunningTask;
import pl.awasiljew.tasks.ShortRunningTask;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Adam Wasiljew
 */
public class TestPanel extends JPanel {

    private JButton mainButtonStart;
    private JButton mainButtonStop;
    private JLabel mainLabel;
    private BlockingQueueProcessor processor;

    public TestPanel() {
        initComponents();
    }

    private void initComponents() {

        JPanel internalPanel = new JPanel();
        internalPanel.setLayout(new GridLayout(0, 1));

        add(internalPanel);

        mainButtonStart = new JButton("Start");
        mainButtonStop = new JButton("Stop");
        mainLabel = new JLabel("Init...");

        internalPanel.add(mainButtonStart);
        internalPanel.add(mainButtonStop);
        internalPanel.add(mainLabel);

        mainButtonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startAction(e);
            }
        });
        mainButtonStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopAction(e);
            }
        });

        processor = new BlockingQueueProcessor();
        processor.start();
    }

    private void startAction(ActionEvent evt) {
        processor.start(new LongRunningTask());
    }

    private void stopAction(ActionEvent evt) {
        processor.start(new ShortRunningTask());
    }
}
