package com.detectorplagiopoo.gui;

import com.detectorplagiopoo.config.AppConfig;
import com.detectorplagiopoo.model.PDFInfo;
import com.detectorplagiopoo.model.PlagiarismResult;
import com.detectorplagiopoo.model.SimilarityMetric;
import com.detectorplagiopoo.processing.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PlagiarismDetectorFrame extends JFrame {
    private final AppConfig config = new AppConfig();
    private final JTextField folderField;
    private final JTextField reportField;
    private final JTextField thresholdField;
    private final JTextArea resultArea;
    private final JButton startButton;
    private final JComboBox<Integer> ngramBox;
    private final Map<String, JCheckBox> metricChecks = new LinkedHashMap<>();

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

        gbc.gridx = 0; gbc.gridy = 0;
        ctrl.add(new JLabel("Pasta para análise:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        folderField = new JTextField(20);
        ctrl.add(folderField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        JButton btnFolder = new JButton("Selecionar Pasta");
        btnFolder.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                folderField.setText(fc.getSelectedFile().getAbsolutePath());
        });
        ctrl.add(btnFolder, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        ctrl.add(new JLabel("Salvar relatório em:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        reportField = new JTextField(System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "RelatorioPlagio.txt", 20);
        ctrl.add(reportField, gbc);
        gbc.gridx = 2; gbc.weightx = 0;
        JButton btnReport = new JButton("Selecionar Local");
        btnReport.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
                reportField.setText(fc.getSelectedFile().getAbsolutePath());
        });
        ctrl.add(btnReport, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        ctrl.add(new JLabel("Limiar de similaridade (%):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0;
        thresholdField = new JTextField(String.valueOf(config.getSimilarityThreshold() * 100), 5);
        ctrl.add(thresholdField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        ctrl.add(new JLabel("Tamanho do n-grama:"), gbc);
        gbc.gridx = 1;
        ngramBox = new JComboBox<>(new Integer[]{3, 5, 7, 10});
        ngramBox.setSelectedItem(config.getNGramSize());
        ngramBox.addActionListener(e -> config.setNGramSize((Integer) ngramBox.getSelectedItem()));
        ctrl.add(ngramBox, gbc);

        // Métricas de similaridade
        List<SimilarityMetric> mets = List.of(new JaccardMetric(), new CosineMetric(), new LevenshteinMetric());
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3;
        ctrl.add(new JLabel("Métricas:"), gbc);
        gbc.gridwidth = 1;
        for (int i = 0; i < mets.size(); i++) {
            SimilarityMetric m = mets.get(i);
            JCheckBox cb = new JCheckBox(m.getName(), true);
            metricChecks.put(m.getName(), cb);
            gbc.gridx = i; gbc.gridy = 5;
            ctrl.add(cb, gbc);
        }

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 3;
        startButton = new JButton("Iniciar Análise");
        startButton.addActionListener(e -> startAnalysis());
        ctrl.add(startButton, gbc);

        add(ctrl, BorderLayout.NORTH);
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
        setVisible(true);
        Logger.getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.SEVERE);
    }

    private void startAnalysis() {
        resultArea.setText("");
        double thr;
        try {
            thr = Double.parseDouble(thresholdField.getText()) / 100;
            if (thr < 0 || thr > 1) throw new NumberFormatException();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido; usando 4%", "Erro", JOptionPane.ERROR_MESSAGE);
            thr = config.getSimilarityThreshold();
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
            @Override
            protected Void doInBackground() throws Exception {
                PDFProcessor proc = new PDFProcessor(new BreakIteratorTokenizer(), new NGramGenerator(config.getNGramSize()));
                List<PDFInfo> infos = proc.processFolder(folder);
                if (infos.isEmpty()) {
                    publish("Nenhum arquivo PDF encontrado na pasta ou subpastas.");
                    return null;
                }
                List<SimilarityMetric> sel = new ArrayList<>();
                for (SimilarityMetric m : List.of(new JaccardMetric(), new CosineMetric(), new LevenshteinMetric())) {
                    if (metricChecks.get(m.getName()).isSelected()) sel.add(m);
                }
                if (sel.isEmpty()) {
                    publish("Nenhuma métrica selecionada. Selecione pelo menos uma métrica para prosseguir.");
                    return null;
                }
                PlagiarismAnalyzer an = new PlagiarismAnalyzer(sel, finalThr);
                List<PlagiarismResult> res = an.analyze(infos);

                // Gerar relatório
                try (FileWriter w = new FileWriter(rpt)) {
                    w.write("Relatório de Plágio\n===================\n\n");
                    w.write("Arquivos processados:\n");
                    for (PDFInfo p : infos) {
                        w.write("- " + p.getFileName() + " (Caminho: " + p.getFilePath() + ")\n");
                    }
                    w.write("\n");

                    w.write("Resultados de Comparação:\n");
                    boolean plagiarismFound = false;
                    for (PlagiarismResult r : res) {
                        if (r.hasPlagiarism()) {
                            plagiarismFound = true;
                            w.write("Possível plágio detectado:\n");
                            w.write("Arquivo 1: " + r.getPdf1().getFileName() + " (Caminho: " + r.getPdf1().getFilePath() + ")\n");
                            w.write("Autor: " + r.getPdf1().getAuthor() + ", Título: " + r.getPdf1().getTitle() + "\n");
                            w.write("Arquivo 2: " + r.getPdf2().getFileName() + " (Caminho: " + r.getPdf2().getFilePath() + ")\n");
                            w.write("Autor: " + r.getPdf2().getAuthor() + ", Título: " + r.getPdf2().getTitle() + "\n");
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

                            // Exibir no sistema
                            StringBuilder summary = new StringBuilder();
                            summary.append(String.format("Plágio detectado entre %s e %s:\n", r.getPdf1().getFileName(), r.getPdf2().getFileName()));
                            for (Map.Entry<String, Double> entry : r.getSimilarities().entrySet()) {
                                if (entry.getValue() >= finalThr) {
                                    summary.append(String.format("- %s: %.2f%%\n", entry.getKey(), entry.getValue() * 100));
                                }
                            }
                            publish(summary.toString());
                        } else {
                            w.write(String.format("Nenhum plágio detectado entre %s e %s:\n", r.getPdf1().getFileName(), r.getPdf2().getFileName()));
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
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String s : chunks) resultArea.append(s + "\n");
            }

            @Override
            protected void done() {
                try {
                    Desktop.getDesktop().open(rpt);
                } catch (IOException ignored) {}
                startButton.setEnabled(true);
            }
        }.execute();
    }
}