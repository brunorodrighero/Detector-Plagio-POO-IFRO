package com.detectorplagiopoo.gui;

import com.detectorplagiopoo.config.AppConfig;
import com.detectorplagiopoo.model.TextInfo;
import com.detectorplagiopoo.model.PlagiarismResult;
import com.detectorplagiopoo.model.SimilarityMetric;
import com.detectorplagiopoo.processing.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PlagiarismDetectorFrame extends JFrame {
    private final AppConfig config = new AppConfig();
    private final JTextField folderField;
    private final JTextField reportField;
    private final JTextField thresholdField;
    private final JTextArea resultArea;
    private final JButton startButton;
    private final JComboBox<Integer> ngramBox;
    private final JComboBox<String> metricCombo;
    private final Map<String, Double> defaultThresholds = Map.of(
            "Jaccard", 0.04,  // 4%
            "Dice", 0.10      // 10%
    );

    public PlagiarismDetectorFrame() {
        super();
        setTitle("Detector de Plágio");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel ctrl = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        ctrl.add(new JLabel("Pasta para análise:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        folderField = new JTextField(20);
        ctrl.add(folderField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        JButton btnFolder = new JButton("Selecionar Pasta");
        btnFolder.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                folderField.setText(fc.getSelectedFile().getAbsolutePath());
        });
        ctrl.add(btnFolder, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        ctrl.add(new JLabel("Salvar relatório em:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        reportField = new JTextField(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "RelatorioPlagio.txt", 20);
        ctrl.add(reportField, gbc);
        gbc.gridx = 2;
        gbc.weightx = 0;
        JButton btnReport = new JButton("Selecionar Local");
        btnReport.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
                reportField.setText(fc.getSelectedFile().getAbsolutePath());
        });
        ctrl.add(btnReport, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        ctrl.add(new JLabel("Limiar de similaridade (%):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0;
        thresholdField = new JTextField(5);
        ctrl.add(thresholdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        ctrl.add(new JLabel("Tamanho do n-grama:"), gbc);
        gbc.gridx = 1;
        ngramBox = new JComboBox<>(new Integer[]{3, 5, 7, 10});
        ngramBox.setSelectedItem(config.getNGramSize());
        ngramBox.addActionListener(e -> config.setNGramSize((Integer) ngramBox.getSelectedItem()));
        ctrl.add(ngramBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        ctrl.add(new JLabel("Métrica de similaridade:"), gbc);
        gbc.gridx = 1;
        String[] metrics = {"Jaccard", "Dice"};
        metricCombo = new JComboBox<>(metrics);
        metricCombo.setSelectedItem("Jaccard");
        metricCombo.addActionListener(e -> {
            String selectedMetric = (String) metricCombo.getSelectedItem();
            double defaultThr = defaultThresholds.get(selectedMetric);
            thresholdField.setText(String.valueOf(defaultThr * 100));
        });
        ctrl.add(metricCombo, gbc);

        // Definir o limiar inicial com base na métrica padrão "Jaccard"
        String initialMetric = (String) metricCombo.getSelectedItem();
        double initialThr = defaultThresholds.get(initialMetric);
        thresholdField.setText(String.valueOf(initialThr * 100));

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        startButton = new JButton("Iniciar Análise");
        startButton.addActionListener(e -> startAnalysis());
        ctrl.add(startButton, gbc);

        add(ctrl, BorderLayout.NORTH);
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
        setVisible(true);
    }

    private void startAnalysis() {
        resultArea.setText("");
        double thr;
        try {
            thr = Double.parseDouble(thresholdField.getText()) / 100;
            if (thr < 0 || thr > 1) throw new NumberFormatException();
        } catch (Exception ex) {
            String selectedMetric = (String) metricCombo.getSelectedItem();
            double defaultThr = defaultThresholds.get(selectedMetric);
            JOptionPane.showMessageDialog(this, "Valor inválido; usando " + (defaultThr * 100) + "%", "Erro", JOptionPane.ERROR_MESSAGE);
            thr = defaultThr;
            thresholdField.setText(String.valueOf(thr * 100));
        }
        File folder = new File(folderField.getText());
        if (!folder.exists() || !folder.isDirectory()) {
            JOptionPane.showMessageDialog(this, "Pasta inválida", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        File rpt = new File(reportField.getText());
        if (rpt.getParentFile() == null || (!rpt.getParentFile().exists() && !rpt.getParentFile().mkdirs())) {
            JOptionPane.showMessageDialog(this, "Diretório inválido", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        startButton.setEnabled(false);
        resultArea.append("Iniciando análise...\n");

        double finalThr = thr;
        new SwingWorker<Void, String>() {
            private boolean reportGenerated = false;

            @Override
            protected Void doInBackground() throws Exception {
                TextProcessor proc = new TextProcessor(new BreakIteratorTokenizer(), new NGramGenerator(config.getNGramSize()));
                List<TextInfo> infos = proc.processFolder(folder);
                if (infos.isEmpty()) {
                    publish("Nenhum arquivo de texto encontrado na pasta ou subpastas.");
                    return null;
                }
                List<SimilarityMetric> sel = new ArrayList<>();
                String selectedMetric = (String) metricCombo.getSelectedItem();
                switch (Objects.requireNonNull(selectedMetric)) {
                    case "Jaccard":
                        sel.add(new JaccardMetric());
                        break;
                    case "Dice":
                        sel.add(new DiceMetric());
                        break;
                }
                if (sel.isEmpty()) {
                    publish("Nenhuma métrica selecionada.");
                    return null;
                }
                PlagiarismAnalyzer an = new PlagiarismAnalyzer(sel, finalThr);
                List<PlagiarismResult> res = an.analyze(infos);

                try (FileWriter w = new FileWriter(rpt)) {
                    w.write("Relatório de Plágio\n===================\n\n");
                    w.write("Arquivos processados:\n");
                    for (TextInfo p : infos) {
                        w.write("- " + p.getFileName() + " (Caminho: " + p.getFilePath() + ")\n");
                    }
                    w.write("\n");

                    w.write("Resultados de Comparação:\n");
                    boolean plagiarismFound = false;
                    for (PlagiarismResult r : res) {
                        if (r.hasPlagiarism()) {
                            plagiarismFound = true;
                            w.write("Possível plágio detectado:\n");
                            w.write("Arquivo 1: " + r.getText1().getFileName() + " (Caminho: " + r.getText1().getFilePath() + ")\n");
                            w.write("Arquivo 2: " + r.getText2().getFileName() + " (Caminho: " + r.getText2().getFilePath() + ")\n");
                            w.write("Similaridades:\n");
                            for (Map.Entry<String, Double> entry : r.getSimilarities().entrySet()) {
                                if (entry.getValue() >= finalThr) {
                                    w.write(String.format("- %s: %.2f%%\n", entry.getKey(), entry.getValue() * 100));
                                    List<String> excerpts = r.getExcerpts().get(entry.getKey());
                                    if (!excerpts.isEmpty()) {
                                        w.write("Trechos copiados (segundo " + entry.getKey() + "):\n");
                                        int displayedExcerpts = 0;
                                        for (String excerpt : excerpts) {
                                            if (displayedExcerpts >= 3) {
                                                w.write("(... mais trechos idênticos encontrados)\n");
                                                break;
                                            }
                                            w.write("- " + excerpt + "\n");
                                            displayedExcerpts++;
                                        }
                                    }
                                }
                            }
                            w.write("----------------------------------------\n\n");

                            StringBuilder summary = new StringBuilder();
                            summary.append(String.format("Plágio detectado entre %s e %s:\n", r.getText1().getFileName(), r.getText2().getFileName()));
                            for (Map.Entry<String, Double> entry : r.getSimilarities().entrySet()) {
                                if (entry.getValue() >= finalThr) {
                                    summary.append(String.format("- %s: %.2f%%\n", entry.getKey(), entry.getValue() * 100));
                                }
                            }
                            publish(summary.toString());
                        } else {
                            w.write(String.format("Nenhum plágio detectado entre %s e %s:\n", r.getText1().getFileName(), r.getText2().getFileName()));
                            for (Map.Entry<String, Double> entry : r.getSimilarities().entrySet()) {
                                w.write(String.format("- %s: %.2f%%\n", entry.getKey(), entry.getValue() * 100));
                            }
                            w.write("----------------------------------------\n\n");
                        }
                    }

                    if (!plagiarismFound) {
                        w.write("Nenhum caso de plágio foi detectado.\n");
                        publish("Nenhum caso de plágio foi detectado.");
                    }
                    reportGenerated = true;
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String s : chunks) resultArea.append(s + "\n");
            }

            @Override
            protected void done() {
                if (reportGenerated) {
                    try {
                        Desktop.getDesktop().open(rpt);
                    } catch (IOException e) {
                        resultArea.append("Erro ao abrir o relatório: " + e.getMessage() + "\n");
                    }
                }
                startButton.setEnabled(true);
            }
        }.execute();
    }
}