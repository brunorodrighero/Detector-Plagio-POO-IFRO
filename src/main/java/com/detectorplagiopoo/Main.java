package com.detectorplagiopoo;
import com.detectorplagiopoo.gui.PlagiarismDetectorFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(PlagiarismDetectorFrame::new);
    }
}